/*
 * Copyright 2017 Azavea
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

package geotrellis.raster.split

import geotrellis.raster._

class TileFeatureSplitMethods[
  T <: CellGrid : (? => SplitMethods[T]), 
  D
](val self: TileFeature[T, D]) extends SplitMethods[TileFeature[T, D]] {
  import Split.Options

  def split(tileLayout: TileLayout, options: Options): Array[TileFeature[T, D]] = {
    val results = self.tile.split(tileLayout, options)
    results.map(t ⇒ TileFeature(t, self.data))
  }
}

