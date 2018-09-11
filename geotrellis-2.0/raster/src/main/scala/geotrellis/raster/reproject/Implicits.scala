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

package geotrellis.raster.reproject

import geotrellis.raster._

object Implicits extends Implicits

trait Implicits {
  implicit class withProjectedRasterReprojectMethods[T <: CellGrid](self: ProjectedRaster[T])(implicit ev: Raster[T] => RasterReprojectMethods[Raster[T]])
    extends ProjectedRasterReprojectMethods[T](self)

  implicit class withTileFeatureReprojectMethods[
    T <: CellGrid : (? => TileReprojectMethods[T]),
    D
  ](self: TileFeature[T,D]) extends TileFeatureReprojectMethods[T, D](self)

  implicit class withRasterTileFeatureReprojectMethods[
    T <: CellGrid : (? => TileReprojectMethods[T]),
    D
  ](self: TileFeature[Raster[T], D]) extends RasterTileFeatureReprojectMethods[T, D](self)
}
