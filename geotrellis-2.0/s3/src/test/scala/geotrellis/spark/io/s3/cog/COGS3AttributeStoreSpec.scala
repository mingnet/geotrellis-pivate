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

package geotrellis.spark.io.s3.cog

import geotrellis.spark._
import geotrellis.spark.io._
import geotrellis.spark.io.cog._
import geotrellis.spark.io.s3._
import geotrellis.spark.io.s3.testkit._

class COGS3AttributeStoreSpec extends COGAttributeStoreSpec {
  val bucket = "attribute-store-test-mock-bucket"
  val prefix = "catalog"

  lazy val header = S3LayerHeader("geotrellis.spark.SpatialKey", "geotrellis.raster.Tile", bucket, prefix, COGLayerType)
  lazy val attributeStore: AttributeStore = new S3AttributeStore(bucket, prefix) {
    override val s3Client = new MockS3Client()
  }
}
