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

package geotrellis.spark.io.hadoop.cog

import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.cog._
import geotrellis.spark.io.hadoop._
import geotrellis.util.UriUtils
import org.apache.hadoop.fs.Path
import org.apache.hadoop.conf.Configuration
import org.apache.spark.SparkContext
import java.net.URI

/**
 * Provides [[HadoopAttributeStore]] instance for URI with `hdfs`, `hdfs+file`, `s3n`, `s3a`, `wasb` and `wasbs` schemes.
 * The uri represents Hadoop [[Path]] of catalog root.
 * `wasb` and `wasbs` provide support for the Hadoop Azure connector. Additional
 * configuration is required for this.
 * This Provider intentinally does not handle the `s3` scheme because the Hadoop implemintation is poor.
 * That support is provided by [[HadoopAttributeStore]]
 */
class HadoopCOGLayerProvider extends AttributeStoreProvider
    with COGLayerReaderProvider with COGLayerWriterProvider with COGValueReaderProvider with COGCollectionLayerReaderProvider {
  val schemes: Array[String] = Array("hdfs", "hdfs+file", "s3n", "s3a", "wasb", "wasbs")

  private def trim(uri: URI): URI =
    if (uri.getScheme.startsWith("hdfs+"))
      new URI(uri.toString.stripPrefix("hdfs+"))
    else uri

  def canProcess(uri: URI): Boolean = uri.getScheme match {
    case str: String => schemes contains str.toLowerCase
    case null => false
  }

  def attributeStore(uri: URI): AttributeStore = {
    val path = new Path(trim(uri))
    val conf = new Configuration()
    HadoopAttributeStore(path, conf)
  }

  def layerReader(uri: URI, store: AttributeStore, sc: SparkContext): COGLayerReader[LayerId] = {
    // don't need uri because HadoopLayerHeader contains full path of the layer
    new HadoopCOGLayerReader(store)(sc)
  }

  def layerWriter(uri: URI, store: AttributeStore): COGLayerWriter = {
    val _uri = trim(uri)
    val path = new Path(_uri)
    new HadoopCOGLayerWriter(path.toString, store)
  }

  def valueReader(uri: URI, store: AttributeStore): COGValueReader[LayerId] = {
    val _uri = trim(uri)
    val path = new Path(_uri)
    val params = UriUtils.getParams(_uri)
    val conf = new Configuration()
    new HadoopCOGValueReader(store, conf)
  }

  def collectionLayerReader(uri: URI, store: AttributeStore) = {
    val _uri = trim(uri)
    val path = new Path(_uri)
    val conf = new Configuration()
    HadoopCOGCollectionLayerReader(path, conf)
  }
}
