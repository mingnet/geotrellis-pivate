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

package geotrellis.raster.mapalgebra.zonal

import geotrellis.raster._

import scala.collection.mutable

import spire.syntax.cfor._

/**
 * Given a raster and a raster representing it's zones, sets all pixels
 * within each zone to the percentage of those pixels having values equal
 * to that of the given pixel.
 *
 * Percentages are integer values from 0 - 100.
 *
 * @note    ZonalPercentage does not currently support Double raster data.
 *          If you use a Raster with a Double CellType (FloatConstantNoDataCellType, DoubleConstantNoDataCellType)
 *          the data values will be rounded to integers.
 */
object ZonalPercentage {
  def apply(r: Tile, zones: Tile): Tile = {
    val zonesToValueCounts = mutable.Map[Int, mutable.Map[Int, Int]]()
    val zoneTotals = mutable.Map[Int, Int]()

    val (cols, rows) = (r.cols, r.rows)
    if(r.cols != zones.cols ||
       r.rows != zones.rows) {
      sys.error(s"The zone raster is not the same dimensions as the data raster.")
    }

    cfor(0)(_ < rows, _ + 1) { row =>
      cfor(0)(_ < cols, _ + 1) { col =>
        val value = r.get(col, row)
        val zone = zones.get(col, row)

        if(!zonesToValueCounts.contains(zone)) {
          zonesToValueCounts(zone) = mutable.Map[Int, Int]()
          zoneTotals(zone) = 0
        }
        zoneTotals(zone) += 1

        val valueCounts = zonesToValueCounts(zone)
        if(!valueCounts.contains(value)) {
          valueCounts(value) = 0
        }
        valueCounts(value) += 1
      }
    }

    val tile = IntArrayTile.empty(cols, rows)

    cfor(0)(_ < rows, _ + 1) { row =>
      cfor(0)(_ < cols, _ + 1) { col =>
        val v = r.get(col, row)
        val z = zones.get(col, row)
        val count = zonesToValueCounts(z)(v)
        val total = zoneTotals(z)
        tile.set(col, row, math.round((count / total.toDouble) * 100).toInt)
      }
    }

    tile
  }
}
