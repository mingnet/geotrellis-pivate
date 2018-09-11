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

package geotrellis.vector.io.json

import geotrellis.vector._
import spray.json._

/** A trait that implements Spray JsonFormats for Geometry objects.
  * @note Import or extend this object directly to use them with default spray-json (un)marshaller
  */
trait GeometryFormats {
  /** Writes point to JsArray as [x, y] */
  private def writePointCoords(point: Point): JsArray =
    JsArray(JsNumber(point.x), JsNumber(point.y))

  /** JsArray of [x, y] arrays */
  private def writeLineCoords(line: Line): JsArray =
    JsArray(line.points.map(writePointCoords).toVector)

  /** JsArray of Lines for the polygin, first line is exterior, rest are holes*/
  private def writePolygonCoords(polygon: Polygon): JsArray =
    JsArray(writeLineCoords(polygon.exterior) +: polygon.holes.map(writeLineCoords).toVector)


  /** Reads Point from JsArray of [x, y] */
  private def readPointCoords(value: JsValue): Point = value match {
    case arr: JsArray =>
      arr.elements match {
        case Seq(JsNumber(x), JsNumber(y)) =>
          Point(x.toDouble, y.toDouble)
        case Seq(JsNumber(x), JsNumber(y), _) =>
          Point(x.toDouble, y.toDouble)
        case _ => throw new DeserializationException("Point [x,y] or [x,y,_] coordinates expected")
      }
    case _ => throw new DeserializationException("Point [x,y] coordinates expected")
  }

  /** Reads Line as JsArray of [x, y] point elements */
  private def readLineCoords(value: JsValue): Line = value match {
    case arr: JsArray =>
      Line( arr.elements.map(readPointCoords) )
    case _ => throw new DeserializationException("Line coordinates array expected")
  }

  /** Reads Polygon from JsArray containg Lines for polygon */
  private def readPolygonCoords(value: JsValue): Polygon = value match {
    case arr: JsArray =>
      val lines: Vector[Line] = 
        arr.elements
           .map(readLineCoords)
           .map(_.closed)

      Polygon(lines.head, lines.tail.toSet)
    case _ => throw new DeserializationException("Polygon coordinates array expected")
  }

  implicit object PointFormat extends RootJsonFormat[Point] {
    def write(p: Point) = JsObject(
      "type" -> JsString("Point"),
      "coordinates" -> JsArray(JsNumber(p.x), JsNumber(p.y))
    )

    def read(value: JsValue) = value.asJsObject.getFields("type", "coordinates") match {
      case Seq(JsString("Point"), point) =>      
        readPointCoords(point)
      case Seq(JsString("Feature")) => 
        read(unwrapFeature(value))
      case _ => throw new DeserializationException("Point geometry expected")
    }
  }

  implicit object LineFormat extends RootJsonFormat[Line] {
    def write(line: Line) = JsObject(
      "type" -> JsString("LineString"),
      "coordinates" -> writeLineCoords(line)
    )

    def read(value: JsValue) = value.asJsObject.getFields("type", "coordinates") match {
      case Seq(JsString("LineString"), points) =>
        readLineCoords(points)
      case Seq(JsString("Feature")) => 
        read(unwrapFeature(value))        
      case _ => throw new DeserializationException("LineString geometry expected")
    }
  }

  implicit object PolygonFormat extends RootJsonFormat[Polygon] {
    override def read(json: JsValue): Polygon = json.asJsObject.getFields("type", "coordinates") match {
      case Seq(JsString("Polygon"), linesArray) =>
        readPolygonCoords(linesArray)
      case Seq(JsString("Feature")) => 
        read(unwrapFeature(json))
      case _ => throw new DeserializationException("Polygon geometry expected")
    }

    override def write(obj: Polygon): JsValue = JsObject(
      "type" -> JsString("Polygon"),
      "coordinates" -> writePolygonCoords(obj)
    )
  }

  /** Extent gets it's own non-GeoJson JSON representation.
    * If you're using the Extent as a geometry, however, it gets converted
    * to a Polygon and written out in GeoJson as a Polygon
    */
  implicit object ExtentFormat extends RootJsonFormat[Extent] {
    def write(extent: Extent) = 
      JsObject(
        "xmin" -> JsNumber(extent.xmin),
        "ymin" -> JsNumber(extent.ymin),
        "xmax" -> JsNumber(extent.xmax),
        "ymax" -> JsNumber(extent.ymax)
      )

    def read(value: JsValue): Extent =
      value.asJsObject.getFields("xmin", "ymin", "xmax", "ymax") match {
        case Seq(JsNumber(xmin), JsNumber(ymin), JsNumber(xmax), JsNumber(ymax)) =>
          Extent(xmin.toDouble, ymin.toDouble, xmax.toDouble, ymax.toDouble)
        case _ =>
          throw new DeserializationException(s"Extent [xmin,ymin,xmax,ymax] expected: $value")
      }
  }

  implicit object MultiPointFormat extends RootJsonFormat[MultiPoint] {
    override def read(json: JsValue): MultiPoint = json.asJsObject.getFields("type", "coordinates") match {
      case Seq(JsString("MultiPoint"), pointArray: JsArray) =>
        MultiPoint(pointArray.elements.map(readPointCoords))
      case Seq(JsString("Feature")) => 
        read(unwrapFeature(json))
      case _ => throw new DeserializationException("MultiPoint geometry expected")
    }

    override def write(obj: MultiPoint): JsValue = JsObject(
      "type" -> JsString("MultiPoint"),
      "coordinates" -> JsArray(obj.points.map(writePointCoords).toVector)
    )
  }

  implicit object MultiLineFormat extends RootJsonFormat[MultiLine] {
    override def read(json: JsValue): MultiLine = json.asJsObject.getFields("type", "coordinates") match {
      case Seq(JsString("MultiLineString"), linesArray: JsArray) =>
        MultiLine(linesArray.elements.map(readLineCoords))
      case Seq(JsString("Feature")) => 
        read(unwrapFeature(json))
      case _ => throw new DeserializationException("MultiLine geometry expected")
    }

    override def write(obj: MultiLine): JsValue = JsObject(
      "type" -> JsString("MultiLineString"),
      "coordinates" -> JsArray(obj.lines.map(writeLineCoords).toVector)
    )
  }

  implicit object MultiPolygonFormat extends RootJsonFormat[MultiPolygon] {
    override def read(json: JsValue): MultiPolygon = json.asJsObject.getFields("type", "coordinates") match {
      case Seq(JsString("MultiPolygon"), polygons: JsArray) =>
        MultiPolygon(polygons.elements.map(readPolygonCoords))
      case Seq(JsString("Feature")) => 
        read(unwrapFeature(json))
      case _ => throw new DeserializationException("MultiPolygon geometry expected")
    }

    override def write(obj: MultiPolygon): JsValue =  JsObject(
      "type" -> JsString("MultiPolygon"),
      "coordinates" -> JsArray(obj.polygons.map(writePolygonCoords).toVector)
    )
  }

  implicit object GeometryCollectionFormat extends RootJsonFormat[GeometryCollection] {
    def write(gc: GeometryCollection) = JsObject(
      "type" -> JsString("GeometryCollection"),
      "geometries" -> JsArray(
        Vector(
          gc.points.map(_.toJson),
          gc.lines.map(_.toJson),
          gc.polygons.map(_.toJson),
          gc.multiPoints.map(_.toJson),
          gc.multiLines.map(_.toJson),
          gc.multiPolygons.map(_.toJson),
          gc.geometryCollections.map(_.toJson)
        ).flatten
      )
    )

    def read(value: JsValue) = value.asJsObject.getFields("type", "geometries") match {
      case Seq(JsString("GeometryCollection"), JsArray(geomsJson)) =>
        GeometryCollection(geomsJson.map(g => GeometryFormat.read(g)))
      case Seq(JsString("Feature")) => 
        read(unwrapFeature(value))
      case _ => throw new DeserializationException("GeometryCollection expected")
    }
  }

  implicit object GeometryFormat extends RootJsonFormat[Geometry] {
    def write(geom: Geometry) = geom match {
      case geom: Point => geom.toJson
      case geom: Line => geom.toJson
      case geom: Polygon => geom.toJson
      case geom: Extent => geom.toPolygon.toJson
      case geom: MultiPolygon => geom.toJson
      case geom: MultiPoint => geom.toJson
      case geom: MultiLine => geom.toJson
      case geom: GeometryCollection => geom.toJson
      case _ => throw new SerializationException("Unknown Geometry type ${geom.getClass.getName}: $geom")
    }

    def read(value: JsValue) = value.asJsObject.getFields("type") match {
      case Seq(JsString("Feature")) => read(unwrapFeature(value))
      case Seq(JsString("Point")) => value.convertTo[Point]
      case Seq(JsString("LineString")) => value.convertTo[Line]
      case Seq(JsString("Polygon")) => value.convertTo[Polygon]
      case Seq(JsString("MultiPolygon")) => value.convertTo[MultiPolygon]
      case Seq(JsString("MultiPoint")) => value.convertTo[MultiPoint]
      case Seq(JsString("MultiLineString")) => value.convertTo[MultiLine]
      case Seq(JsString("GeometryCollection")) => value.convertTo[GeometryCollection]      
      case Seq(JsString(t)) => throw new DeserializationException(s"Unknown Geometry type: $t")
    }
  }

  /** Unwrap feature geometry (ignoring its properties) */
  private def unwrapFeature(value: JsValue): JsValue = {
    value.asJsObject.getFields("type", "geometry") match {
      case Seq(JsString("Feature"), geom) =>  geom
      case _ => value
    }
  }
}

object GeometryFormats extends GeometryFormats
