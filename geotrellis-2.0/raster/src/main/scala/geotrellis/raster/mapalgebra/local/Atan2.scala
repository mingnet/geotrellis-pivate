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

/**
  * Operation to get the Arc Tangent2 of values.  The first raster
  * holds the y-values, and the second holds the x values. The arctan
  * is calculated from y/x.
  *
  * @note A double raster is always returned.
  */
object Atan2 extends Serializable {
  def apply(r1: Tile, r2: Tile): Tile = {
    (if(r1.cellType.isFloatingPoint) r1
     else r1.convert(DoubleConstantNoDataCellType))
      .combineDouble(r2) { (z1, z2) => math.atan2(z1, z2) }
  }
}
