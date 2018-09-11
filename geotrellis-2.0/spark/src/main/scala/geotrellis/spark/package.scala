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

package geotrellis

import geotrellis.raster._
import geotrellis.util._

import org.apache.spark.rdd._

package object spark extends Implicits {
  /** GeoTiff Layer */
  type RasterRDD[M] = RDD[Raster[Tile]] with Metadata[M]
  type MultibandRasterRDD[M] = RDD[Raster[MultibandTile]] with Metadata[M]

  type RasterCollection[M] = Seq[Raster[Tile]] with Metadata[M]
  type MultibandRasterCollection[M] = Seq[Raster[MultibandTile]] with Metadata[M]
  /** ------------- */

  type TileLayerRDD[K] = RDD[(K, Tile)] with Metadata[TileLayerMetadata[K]]
  object TileLayerRDD {
    def apply[K](rdd: RDD[(K, Tile)], metadata: TileLayerMetadata[K]): TileLayerRDD[K] =
      new ContextRDD(rdd, metadata)
  }

  type TileLayerCollection[K] = Seq[(K, Tile)] with Metadata[TileLayerMetadata[K]]
  object TileLayerCollection {
    def apply[K](seq: Seq[(K, Tile)], metadata: TileLayerMetadata[K]): TileLayerCollection[K] =
      new ContextCollection(seq, metadata)
  }

  type MultibandTileLayerRDD[K] = RDD[(K, MultibandTile)] with Metadata[TileLayerMetadata[K]]
  object MultibandTileLayerRDD {
    def apply[K](rdd: RDD[(K, MultibandTile)], metadata: TileLayerMetadata[K]): MultibandTileLayerRDD[K] =
      new ContextRDD(rdd, metadata)
  }

  type MultibandTileLayerCollection[K] = Seq[(K, MultibandTile)] with Metadata[TileLayerMetadata[K]]
  object MultibandTileLayerCollection {
    def apply[K](seq: Seq[(K, MultibandTile)], metadata: TileLayerMetadata[K]): MultibandTileLayerCollection[K] =
      new ContextCollection(seq, metadata)
  }

  type TileBounds = GridBounds

  type SpatialComponent[K] = Component[K, SpatialKey]
  type TemporalComponent[K] = Component[K, TemporalKey]
}
