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
 * Determines if values are equal. Sets to 1 if true, else 0.
 */
object Unequal extends LocalTileComparatorOp {
  def compare(z1: Int, z2: Int):Boolean =
    if(z1 == z2) false else true

  def compare(z1: Double, z2: Double):Boolean =
    if(isNoData(z1)) { if(isNoData(z2)) false else true }
    else {
      if(isNoData(z2)) { true }
      else {
        if(z1 == z2) false
        else true
      }
    }
}

trait UnequalMethods extends MethodExtensions[Tile] {
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is equal to the input
   * integer, else 0.
   */
  def localUnequal(i: Int): Tile = Unequal(self, i)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is equal to the input
   * integer, else 0.
   */
  def !==(i: Int): Tile = localUnequal(i)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is equal to the input
   * integer, else 0.
   */
  def !==:(i: Int): Tile = localUnequal(i)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is equal to the input
   * intenger, else 0.
   */
  def localUnequal(d: Double): Tile = Unequal(self, d)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is equal to the input
   * double, else 0.
   */
  def !==(d: Double): Tile = localUnequal(d)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell value of the input raster is equal to the input
   * double, else 0.
   */
  def !==:(d: Double): Tile = localUnequal(d)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell valued of the rasters are not equal, else 0.
   */
  def localUnequal(r: Tile): Tile = Unequal(self,r)
  /**
   * Returns a Tile with data of BitCellType, where cell values equal 1 if
   * the corresponding cell valued of the raster are not equal, else 0.
   */
  def !==(r: Tile): Tile = localUnequal(r)
}
