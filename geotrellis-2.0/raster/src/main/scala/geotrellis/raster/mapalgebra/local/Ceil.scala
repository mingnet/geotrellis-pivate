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
 * Operation to get the ceiling of values.
 */
object Ceil extends Serializable {
  /** Takes the Ceiling of each raster cell value. */
  def apply(r: Tile): Tile = 
    r.dualMap { z: Int => z }
              { z: Double => math.ceil(z) } // Note: math.ceil(Double.NaN) == Double.NaN
}
