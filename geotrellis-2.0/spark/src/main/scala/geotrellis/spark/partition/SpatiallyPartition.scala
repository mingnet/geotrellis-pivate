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

package geotrellis.spark.partition

import geotrellis.spark._
import geotrellis.util._

import org.apache.spark.rdd._

import scala.reflect.ClassTag

object SpatiallyPartition {
  def apply[
    K: Boundable: PartitionerIndex: ClassTag,
    V: ClassTag,
    M: GetComponent[?, Bounds[K]]
  ](rdd: RDD[(K, V)] with Metadata[M]): RDD[(K, V)] with Metadata[Bounds[K]] =
    apply(rdd, rdd.metadata.getComponent[Bounds[K]])

  def apply[
    K: Boundable: PartitionerIndex: ClassTag,
    V: ClassTag,
    M: GetComponent[?, Bounds[K]]
  ](rdd: RDD[(K, V)] with Metadata[M], filterBounds: Bounds[K]): RDD[(K, V)] with Metadata[Bounds[K]] = {
    val partitioner = SpacePartitioner(filterBounds)
    partitioner(rdd)
  }
}
