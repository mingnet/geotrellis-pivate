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

package geotrellis.raster.mapalgebra.local

import geotrellis.raster._
import geotrellis.util.MethodExtensions


/**
 * Determines if values are less than or equal to other values. Sets to 1 if true, else 0.
 */
object LessOrEqual extends LocalTileComparatorOp {
  def compare(z1: Int, z2: Int): Boolean =
    if(z1 <= z2) true else false

  def compare(z1: Double, z2: Double): Boolean =
    if(isNoData(z1)) { false }
    else {
      if(isNoData(z2)) { false }
      else {
        if(z1 <= z2) true
        else false
      }
    }
}

trait LessOrEqualMethods extends MethodExtensions[Tile] {
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is less than or equal to the input
   * integer, else 0.
   */
  def localLessOrEqual(i: Int): Tile = LessOrEqual(self, i)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is less than or equal to the input
   * integer, else 0.
   */
  def localLessOrEqualRightAssociative(i: Int): Tile = LessOrEqual(i, self)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is less than or equal to the input
    * integer, else 0.
   */
  def <=(i: Int): Tile = localLessOrEqual(i)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is less than or equal to the input
   * integer, else 0.
   */
  def <=:(i: Int): Tile = localLessOrEqualRightAssociative(i)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is less than or equal to the input
   * double, else 0.
   */
  def localLessOrEqual(d: Double): Tile = LessOrEqual(self, d)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is less than or equal to the input
   * double, else 0.
   */
  def localLessOrEqualRightAssociative(d: Double): Tile = LessOrEqual(d, self)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is less than or equal to the input
   * double, else 0.
   */
  def <=(d: Double): Tile = localLessOrEqual(d)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is less than or equal to the input
   * double, else 0.
   */
  def <=:(d: Double): Tile = localLessOrEqualRightAssociative(d)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell valued of the rasters are less than or equal to the next raster, else 0.
   */
  def localLessOrEqual(r: Tile): Tile = LessOrEqual(self, r)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell valued of the rasters are less than or equal to the next raster, else 0.
   */
  def <=(r: Tile): Tile = localLessOrEqual(r)
}
