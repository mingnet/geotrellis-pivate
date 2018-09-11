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
 * Determines if values are greater than or equal to other values. Sets to 1 if true, else 0.
 */
object GreaterOrEqual extends LocalTileComparatorOp {
  def compare(z1: Int, z2: Int): Boolean =
    if(z1 >= z2) true else false

  def compare(z1: Double, z2: Double): Boolean =
    if(isNoData(z1)) { false }
    else {
      if(isNoData(z2)) { false }
      else {
        if(z1 >= z2) true
        else false
      }
    }
}

trait GreaterOrEqualMethods extends MethodExtensions[Tile] {
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is greater than or equal to the input
   * integer, else 0.
   */
  def localGreaterOrEqual(i: Int): Tile = GreaterOrEqual(self, i)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is greater than or equal to the input
   * integer, else 0.
   */
  def localGreaterOrEqualRightAssociative(i: Int): Tile = GreaterOrEqual(i, self)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is greater than or equal to the input
    * integer, else 0.
   */
  def >=(i: Int): Tile = localGreaterOrEqual(i)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is greater than or equal to the input
   * integer, else 0.
   */
  def >=:(i: Int): Tile = localGreaterOrEqualRightAssociative(i)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is greater than or equal to the input
   * double, else 0.
   */
  def localGreaterOrEqual(d: Double): Tile = GreaterOrEqual(self, d)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is greater than or equal to the input
   * double, else 0.
   */
  def localGreaterOrEqualRightAssociative(d: Double): Tile = GreaterOrEqual(d, self)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is greater than or equal to the input
   * double, else 0.
   */
  def >=(d: Double): Tile = localGreaterOrEqual(d)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is greater than or equal to the input
   * double, else 0.
   */
  def >=:(d: Double): Tile = localGreaterOrEqualRightAssociative(d)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell valued of the rasters are greater than or equal to the next raster, else 0.
   */
  def localGreaterOrEqual(r: Tile): Tile = GreaterOrEqual(self, r)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell valued of the rasters are greater than or equal to the next raster, else 0.
   */
  def >=(r: Tile): Tile = localGreaterOrEqual(r)
}
