/*
 * Copyright 2016 Azavea
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package geotrellis.raster.render.png

import geotrellis.raster._

import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.util.zip.CRC32
import java.util.zip.CheckedOutputStream
import java.util.zip.Deflater
import java.util.zip.DeflaterOutputStream

import scala.math.abs

import Util._

case class PngEncoder(settings: Settings) {
  // magic numbers from the PNG spec
  final val SIGNATURE: Array[Byte] = Array[Byte](137.asInstanceOf[Byte], 80, 78, 71, 13, 10, 26, 10)
  final val IHDR: Int = 0x49484452
  final val BKGD: Int = 0x624b4744
  final val PLTE: Int = 0x504c5445
  final val TRNS: Int = 0x74524e53
  final val IDAT: Int = 0x49444154
  final val IEND: Int = 0x49454E44

  // filter byte signature
  final val FILTER: Byte = settings.filter.n

  // pixel depth in bytes
  final val DEPTH: Int = settings.colorType.depth

  // how many bits to shift to get the uppermost byte.
  final val SHIFT: Int = (DEPTH - 1) * 8

  def writeHeader(dos: DataOutputStream, raster: Tile) {
    val width = raster.cols
    val height = raster.rows

    dos.write(SIGNATURE)

    // write some basic metadata about the file.
    val cIHDR = new Chunk(IHDR)
    cIHDR.writeInt(width)
    cIHDR.writeInt(height)
    cIHDR.writeByte(8); // 8 bits per component
    cIHDR.writeByte(settings.colorType.n) // color type (0, 2, 3, 4, 6)
    cIHDR.writeByte(0) // DEFLATE compression
    cIHDR.writeByte(0) // adaptive filtering
    cIHDR.writeByte(0) // no interlacing
    cIHDR.writeTo(dos)
  }

  def writeBackgroundInfo(dos: DataOutputStream) {
    settings.colorType match {
      case GreyPngEncoding(Some(ndColor)) =>
        // write a single 2-byte color value
        // NoData color is presumed to be in RGBA
        val cTRNS = new Chunk(TRNS)
        cTRNS.writeByte(0x00)
        cTRNS.writeByte(shift(ndColor, 8))
        cTRNS.writeTo(dos)

      case RgbPngEncoding(Some(ndColor)) =>
        // write three 2-byte color values
        // NoData color is presumed to be in RGBA
        val cTRNS = new Chunk(TRNS)
        cTRNS.writeByte(0x00)
        cTRNS.writeByte(shift(ndColor, 24))
        cTRNS.writeByte(0x00)
        cTRNS.writeByte(shift(ndColor, 16))
        cTRNS.writeByte(0x00)
        cTRNS.writeByte(shift(ndColor, 8))
        cTRNS.writeTo(dos)

      case IndexedPngEncoding(rgbs, alphas) =>
        // write 3-byte RGB values for every index entry
        val cPLTE = new Chunk(PLTE)
        rgbs.foreach {
          rgb =>
          cPLTE.writeByte(shift(rgb, 16))
          cPLTE.writeByte(shift(rgb, 8))
          cPLTE.writeByte(byte(rgb))
        }
        cPLTE.writeTo(dos)

        // write 1-byte color values for every index entry
        val cTRNS = new Chunk(TRNS)
        alphas.foreach(a => cTRNS.writeByte(byte(a)))
        cTRNS.writeTo(dos)

      case _ =>
    }
  }

  def createByteBuffer(raster: Tile) = {
    val size = raster.size
    val data = raster.toArray
    val bb = ByteBuffer.allocate(size * DEPTH)

    if (DEPTH == 4) initByteBuffer32(bb, data, size)
    else if (DEPTH == 3) initByteBuffer24(bb, data, size)
    else if (DEPTH == 2) initByteBuffer16(bb, data, size)
    else if (DEPTH == 1) initByteBuffer8(bb, data, size)
    else sys.error(s"unsupported depth: $DEPTH")

    bb
  }

  // TODO: figure out how to share code without impacting performance
  def writePixelData(dos: DataOutputStream, raster: Tile) {
    settings.filter match {
      case PaethFilter => writePixelDataPaeth(dos, raster)
      case NoFilter => writePixelDataNoFilter(dos, raster)
      case _ => sys.error("filter %s not supported")
    }
  }

  def writePixelDataNoFilter(dos: DataOutputStream, raster: Tile) {
    // dereference some useful information from the raster
    val cols = raster.cols
    val size = cols * raster.rows

    // allocate a data chunk for our pixel data
    val cIDAT = new Chunk(IDAT)

    // allocate a byte buffer
    val bb = createByteBuffer(raster)

    // wrap the chunk's output stream to apply the DEFLATE compression
    val dfos = new DeflaterOutputStream(cIDAT.cos, new Deflater(Deflater.BEST_SPEED))

    val byteWidth = cols * DEPTH

    // allocate a buffer for one row's worth of bytes
    var currLine = Array.ofDim[Byte](byteWidth)

    var yspan = 0

    // loop over lines of the image. each line will contain 'cols' pixels.
    while (yspan < size) {
      bb.position(yspan * DEPTH)
      bb.get(currLine)

      // write the "filter type" for this line, followed by the line itself.
      dfos.write(FILTER)
      dfos.write(currLine)

      // proceed to the next row.
      yspan += cols
    }

    // now that we've written all the data, actually do the DEFLATE compression
    // and write the result to our output stream.
    dfos.finish()
    cIDAT.writeTo(dos)
  }

  def writePixelDataPaeth(dos: DataOutputStream, raster: Tile) {
    // dereference some useful information from the raster
    val cols = raster.cols
    val size = cols * raster.rows
//    val data = raster.toArray

    // allocate a data chunk for our pixel data
    val cIDAT = new Chunk(IDAT)

    var j = 0

    // allocate a byte buffer
    val bb = createByteBuffer(raster)

    // wrap the chunk's output stream to apply the DEFLATE compression
    val dfos = new DeflaterOutputStream(cIDAT.cos, new Deflater(Deflater.BEST_SPEED))

    val byteWidth = cols * DEPTH

    // allocate a buffer for one row's worth of bytes
    val lineOut = Array.ofDim[Byte](byteWidth)
    var prevLine = Array.ofDim[Byte](byteWidth)
    var currLine = Array.ofDim[Byte](byteWidth)
    var tmp: Array[Byte] = null

    var yspan = 0

    // loop over lines of the image. each line will contain 'cols' pixels.
    while (yspan < size) {
      bb.position(yspan * DEPTH)
      bb.get(currLine)

      j = 0
      while (j < DEPTH) {
        // for the first DEPTH bytes, there is no left neighbor so 'c' is
        // always the neighbor above (from the previous line).
        lineOut(j) = byte(currLine(j) - prevLine(j))
        j += 1
      }

      while (j < byteWidth) {
        // the names a, b, c and p are actually used in the PNG spec, so we
        // use them here. they correspond to three neighbors.
        val a: Int = currLine(j - DEPTH) & 0xff // left
        val b: Int = prevLine(j) & 0xff // above
        var c: Int = prevLine(j - DEPTH) & 0xff // above + left

        // find the distance of a, b, c from "p" (p = a + b - c)
        var pa: Int = b - c
        var pb: Int = a - c
        var pc: Int = pa + pb
        if (pa < 0) pa = -pa
        if (pb < 0) pb = -pb
        if (pc < 0) pc = -pc

        // find closest neighbor; assign that neighbor's value to 'c'
        if (pa <= pb && pa <= pc) c = a else if(pb <= pc) c = b

        // apply PAETH: store the current byte's value minus c's value
        lineOut(j) = byte(currLine(j) - c)
        j += 1
      }

      // write the "filter type" for this line, followed by the line itself.
      dfos.write(FILTER)
      dfos.write(lineOut)

      // swap the buffers
      tmp = prevLine
      prevLine = currLine
      currLine = tmp

      // proceed to the next row.
      yspan += cols
    }

    // now that we've written all the data, actually do the DEFLATE compression
    // and write the result to our output stream.
    dfos.finish()
    cIDAT.writeTo(dos)
  }

  // signal the end of the PNG data
  def writeEnd(dos: DataOutputStream) {
    val cIEND = new Chunk(IEND)
    cIEND.writeTo(dos)
  }

  def writeOutputStream(os: OutputStream, raster: Tile) {
    // wrap our actual OutputStream to enable us to write bytes and such.
    val dos = new DataOutputStream(os)

    // write the header first
    writeHeader(dos, raster)

    // if we have background/transparency info, then write it
    writeBackgroundInfo(dos)

    // write the actual data
    writePixelData(dos, raster)

    // and we're done
    writeEnd(dos)
    dos.flush()
  }

  def writeByteArray(raster: Tile) = {
    val baos = new ByteArrayOutputStream()
    writeOutputStream(baos, raster)
    baos.toByteArray
  }

  def writePath(path: String, raster: Tile) {
    val fos = new FileOutputStream(new File(path))
    writeOutputStream(fos, raster)
    fos.close()
  }
}
