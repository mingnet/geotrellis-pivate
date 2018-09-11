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

package geotrellis.spark.merge

import geotrellis.raster._
import geotrellis.raster.merge._
import geotrellis.raster.prototype._
import geotrellis.spark._
import geotrellis.spark.tiling.LayoutDefinition
import geotrellis.util._

import org.apache.spark.rdd.RDD

import scala.reflect.ClassTag

class RDDLayoutMergeMethods[
  K: SpatialComponent: ClassTag,
  V <: CellGrid: ClassTag: ? => TileMergeMethods[V]: ? => TilePrototypeMethods[V],
  M: (? => LayoutDefinition)
](val self: RDD[(K, V)] with Metadata[M]) extends MethodExtensions[RDD[(K, V)] with Metadata[M]] {

 def merge(other: RDD[(K, V)] with Metadata[M]) =
   RDDLayoutMerge.merge(self, other)
}
