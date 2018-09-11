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

package geotrellis.spark.testkit

import org.scalatest._
import geotrellis.raster._
import geotrellis.raster.testkit._
import geotrellis.spark._
import org.apache.spark.rdd._

import scala.reflect.ClassTag

trait TileLayerRDDMatchers extends RasterMatchers {
  implicit def rddToTile(rdd: RDD[(SpatialKey, Tile)]) = rdd.stitch

  /*
   * Takes a 3-tuple, min, max, and count and checks
   * a. if every tile has a min/max value set to those passed in,
   * b. if number of tiles == count
   */
  def rasterShouldBe[K](rdd: RDD[(K, Tile)], minMax: (Int, Int)): Unit = {
    val res = rdd.map(_._2.findMinMax).collect
    withClue(s"Actual MinMax: ${res.toSeq}; expecting: ${minMax}") {
      res.count(_ == minMax) should be(res.length)
    }
  }

  def rastersShouldHaveSameIdsAndTileCount[K: Ordering: ClassTag](
    first: RDD[(K, Tile)],
    second: RDD[(K, Tile)]): Unit = {

    val firstKeys = first.sortBy(_._1).map(_._1).collect
    val secondKeys = second.sortBy(_._1).map(_._1).collect

    (firstKeys zip secondKeys) foreach { case (key1, key2) => key1 should be(key2) }

    first.count should be(second.count)
  }

  /*
   * Takes a value and a count and checks
   * a. if every pixel == value, and
   * b. if number of tiles == count
   */
  def rasterShouldBe(rdd: RDD[(SpatialKey, Tile)], value: Int, count: Int): Unit = {
    rasterShouldBe(rdd, value)
    rdd.count should be(count)
  }

  def rastersEqual(
    first: RDD[(SpatialKey, Tile)],
    second: RDD[(SpatialKey, Tile)]): Unit = {

    tilesEqual(first, second)
  }

  def rasterShouldBe(rdd: RDD[(SpaceTimeKey, Tile)], value: Int, count: Int)(implicit d: DummyImplicit): Unit = {
    rdd.count should be (count)
    rdd.collect.map { case (_, tile) => rasterShouldBe(tile, value) }
  }

  def rastersEqual[K](
    first: RDD[(K, Tile)],
    second: RDD[(K, Tile)])(implicit d: DummyImplicit): Unit = {
    first.count should be(second.count)

    val ft = first.collect
    val st = second.collect

    val keys1 = ft.map(_._1).toSet
    val keys2 = st.map(_._1).toSet

    val keyDiff1 = keys1 -- keys2
    withClue("First has keys the second doesn't have: ${keyDiff1.toSeq}") {
      keyDiff1.isEmpty should be (true)
    }

    val keyDiff2 = keys2 -- keys1
    withClue("Second has keys the first doesn't have: ${keyDiff2.toSeq}") {
      keyDiff2.isEmpty should be (true)
    }

    val grouped: Map[K, Array[(K, Tile)]] =
      ft.union(st).groupBy(_._1)

    for( (key, tiles) <- grouped) {
      tiles.size should be (2)

      val t1 = tiles(0)._2
      val t2 = tiles(1)._2

      withClue("Filed on key $key") {
        tilesEqual(t1, t2)
      }
    }
  }
}
