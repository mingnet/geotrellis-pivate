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

package geotrellis.spark.io.hbase

import geotrellis.spark.io.{LayerHeader, LayerType, AvroLayerType}

import spray.json._

case class HBaseLayerHeader(
  keyClass: String,
  valueClass: String,
  tileTable: String,
  layerType: LayerType = AvroLayerType
) extends LayerHeader {
  def format = "hbase"
}

object HBaseLayerHeader {
  implicit object CassandraLayerMetadataFormat extends RootJsonFormat[HBaseLayerHeader] {
    def write(md: HBaseLayerHeader) =
      JsObject(
        "format" -> JsString(md.format),
        "keyClass" -> JsString(md.keyClass),
        "valueClass" -> JsString(md.valueClass),
        "tileTable" -> JsString(md.tileTable),
        "layerType" -> md.layerType.toJson
      )

    def read(value: JsValue): HBaseLayerHeader =
      value.asJsObject.getFields("keyClass", "valueClass", "tileTable", "layerType") match {
        case Seq(JsString(keyClass), JsString(valueClass), JsString(tileTable), layerType) =>
          HBaseLayerHeader(
            keyClass,
            valueClass,
            tileTable,
            layerType.convertTo[LayerType]
          )
        case Seq(JsString(keyClass), JsString(valueClass), JsString(tileTable)) =>
          HBaseLayerHeader(
            keyClass,
            valueClass,
            tileTable,
            AvroLayerType
          )
        case _ =>
          throw new DeserializationException(s"HBaseLayerHeader expected, got: $value")
      }
  }
}
