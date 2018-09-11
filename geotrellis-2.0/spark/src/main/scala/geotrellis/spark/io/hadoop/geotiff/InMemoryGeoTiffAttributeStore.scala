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

package geotrellis.spark.io.hadoop.geotiff

import geotrellis.vector.ProjectedExtent
import geotrellis.util.annotations.experimental
import java.net.URI

/**
  * @define experimental <span class="badge badge-red" style="float: right;">EXPERIMENTAL</span>@experimental
  */
@experimental abstract class InMemoryGeoTiffAttributeStore extends CollectionAttributeStore[GeoTiffMetadata] {
  val metadataList: List[GeoTiffMetadata]
  lazy val getData: () => GeoTiffMetadataTree[GeoTiffMetadata] = () => GeoTiffMetadataTree.fromGeoTiffMetadataSeq(metadataList)
  lazy val data = getData()
  @experimental def query(layerName: Option[String] = None, extent: Option[ProjectedExtent] = None): Seq[GeoTiffMetadata] = {
    (layerName, extent) match {
      case (Some(name), Some(projectedExtent)) =>
        data.query(name, projectedExtent).filter { md => md.name == name }
      case (_, Some(projectedExtent)) =>
        data.query(projectedExtent)
      case (Some(name), _) => data.query.filter { md => md.name == name }
      case _ => data.query
    }
  }

  @experimental def persist(uri: URI): Unit
}
