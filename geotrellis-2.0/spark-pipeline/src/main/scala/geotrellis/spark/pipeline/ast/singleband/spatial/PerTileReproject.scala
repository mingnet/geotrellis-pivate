/*
 * Copyright 2018 Azavea
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

package geotrellis.spark.pipeline.ast.singleband.spatial

import io.circe.syntax._

import geotrellis.raster._
import geotrellis.spark.pipeline.ast._
import geotrellis.spark.pipeline.json.transform
import geotrellis.vector._

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

case class PerTileReproject(
  node: Node[RDD[(ProjectedExtent, Tile)]],
  arg: transform.Reproject
) extends Transform[RDD[(ProjectedExtent, Tile)], RDD[(ProjectedExtent, Tile)]] {
  def asJson = node.asJson :+ arg.asJson
  def eval(implicit sc: SparkContext): RDD[(ProjectedExtent, Tile)] = Transform.perTileReproject(arg)(node.eval)
}
