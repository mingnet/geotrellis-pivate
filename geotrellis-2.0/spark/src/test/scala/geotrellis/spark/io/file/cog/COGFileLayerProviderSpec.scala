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

package geotrellis.spark.io.file.cog

import geotrellis.spark.io._
import geotrellis.spark.io.cog._
import geotrellis.spark.io.file._
import geotrellis.spark.testkit.TestEnvironment
import org.scalatest._

class FileCOGLayerProviderSpec extends FunSpec with TestEnvironment {
  val uri = new java.net.URI("file:/tmp/catalog")

  it("construct FileCOGLayerReader from URI") {
    val reader = COGLayerReader(uri)
    assert(reader.isInstanceOf[FileCOGLayerReader])
  }

  it("construct FileCOGLayerWriter from URI") {
    val reader = COGLayerWriter(uri)
    assert(reader.isInstanceOf[FileCOGLayerWriter])
  }

  it("construct FileValueReader from URI") {
    val reader = COGValueReader(uri)
    assert(reader.isInstanceOf[FileCOGValueReader])
  }

  it("should be able to process a URI without a scheme") {
    val badURI = new java.net.URI("/tmp/catalog")
    val provider = new FileCOGLayerProvider

    provider.canProcess(badURI) should be (true)
  }
}
