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

package geotrellis.raster.io.geotiff.tags

import ProjectionTypesMap._
import geotrellis.raster.io.geotiff.reader.MalformedGeoTiffException
import geotrellis.raster.io.geotiff.util._

import monocle.syntax._
import monocle.macros.Lenses

import scala.collection.immutable.HashMap

object GeoKeys {

  val GTModelTypeGeoKey = 1024
  val GTRasterTypeGeoKey = 1025
  val GTCitationGeoKey = 1026

  val GeogTypeGeoKey = 2048
  val GeogCitationGeoKey = 2049
  val GeogGeodeticDatumGeoKey = 2050
  val GeogPrimeMeridianGeoKey = 2051
  val GeogLinearUnitsGeoKey = 2052
  val GeogLinearUnitSizeGeoKey = 2053
  val GeogAngularUnitsGeoKey = 2054
  val GeogAngularUnitSizeGeoKey = 2055
  val GeogEllipsoidGeoKey = 2056
  val GeogSemiMajorAxisGeoKey = 2057
  val GeogSemiMinorAxisGeoKey = 2058
  val GeogInvFlatteningGeoKey = 2059
  val GeogAzimuthUnitsGeoKey = 2060
  val GeogPrimeMeridianLongGeoKey = 2061

  val ProjectedCSTypeGeoKey = 3072
  val PCSCitationGeoKey = 3073
  val ProjectionGeoKey = 3074
  val ProjCoordTransGeoKey = 3075
  val ProjLinearUnitsGeoKey = 3076
  val ProjLinearUnitSizeGeoKey = 3077
  val ProjStdParallel1GeoKey = 3078
  val ProjStdParallel2GeoKey = 3079
  val ProjNatOriginLongGeoKey = 3080
  val ProjNatOriginLatGeoKey = 3081
  val ProjFalseEastingGeoKey = 3082
  val ProjFalseNorthingGeoKey = 3083
  val ProjFalseOriginLongGeoKey = 3084
  val ProjFalseOriginLatGeoKey = 3085
  val ProjFalseOriginEastingGeoKey = 3086
  val ProjFalseOriginNorthingGeoKey = 3087
  val ProjCenterLongGeoKey = 3088
  val ProjCenterLatGeoKey = 3089
  val ProjCenterEastingGeoKey = 3090
  val ProjCenterNorthingGeoKey = 3091
  val ProjScaleAtNatOriginGeoKey = 3092
  val ProjScaleAtCenterGeoKey = 3093
  val ProjAzimuthAngleGeoKey = 3094
  val ProjStraightVertPoleLongGeoKey = 3095
  val ProjRectifiedGridAngleGeoKey = 3096

  val VerticalCSTypeGeoKey = 4096
  val VerticalCitationGeoKey = 4097
  val VerticalDatumGeoKey = 4098
  val VerticalUnitsGeoKey = 4099

}

object CommonPublicValues {

  val UndefinedCPV = 0
  val UserDefinedCPV = 32767

}

@Lenses("_")
case class ConfigKeys(
  gtModelType: Int = -1,
  gtRasterType: Option[Int] = None,
  gtCitation: Option[Array[String]] = None
)

@Lenses("_")
case class GeogCSParameterKeys(
  geogType: Option[Int] = None,
  geogCitation: Option[Array[String]] = None,
  geogGeodeticDatum: Option[Int] = None,
  geogPrimeMeridian: Option[Int] = None,
  geogLinearUnits: Option[Int] = None,
  geogLinearUnitSize: Option[Double] = None,
  geogAngularUnits: Option[Int] = None,
  geogAngularUnitSize: Option[Double] = None,
  geogEllipsoid: Option[Int] = None,
  geogSemiMajorAxis: Option[Double] = None,
  geogSemiMinorAxis: Option[Double] = None,
  geogInvFlattening: Option[Double] = None,
  geogAzimuthUnits: Option[Int] = None,
  geogPrimeMeridianLong: Option[Double] = None
)

@Lenses("_")
case class ProjectedCSParameterKeys(
  projectedCSType: Int = UserDefinedProjectionType,
  pcsCitation: Option[Array[String]] = None,
  projection: Option[Int] = None,
  projCoordTrans: Option[Int] = None,
  projLinearUnits: Option[Int] = None,
  projLinearUnitSize: Option[Double] = None,
  projStdParallel1: Option[Double] = None,
  projStdParallel2: Option[Double] = None,
  projNatOriginLong: Option[Double] = None,
  projNatOriginLat: Option[Double] = None,
  projectedFalsings: ProjectedFalsings = ProjectedFalsings(),
  projCenterLong: Option[Double] = None,
  projCenterLat: Option[Double] = None,
  projCenterEasting: Option[Double] = None,
  projCenterNorthing: Option[Double] = None,
  projScaleAtNatOrigin: Option[Double] = None,
  projScaleAtCenter: Option[Double] = None,
  projAzimuthAngle: Option[Double] = None,
  projStraightVertPoleLong: Option[Double] = None,
  projRectifiedGridAngle: Option[Double] = None
)

@Lenses("_")
case class ProjectedFalsings(
  projFalseEasting: Option[Double] = None,
  projFalseNorthing: Option[Double] = None,
  projFalseOriginLong: Option[Double] = None,
  projFalseOriginLat: Option[Double] = None,
  projFalseOriginEasting: Option[Double] = None,
  projFalseOriginNorthing: Option[Double] = None
)

@Lenses("_")
case class VerticalCSKeys(
  verticalCSType: Option[Int] = None,
  verticalCitation: Option[Array[String]] = None,
  verticalDatum: Option[Int] = None,
  verticalUnits: Option[Int] = None
)

@Lenses("_")
case class NonStandardizedKeys(
  shortMap: HashMap[Int, Int] = HashMap[Int, Int](),
  doublesMap: HashMap[Int, Array[Double]] = HashMap[Int, Array[Double]](),
  asciisMap: HashMap[Int, Array[String]] = HashMap[Int, Array[String]]()
)

@Lenses("_")
case class GeoKeyDirectory(
  count: Int,
  configKeys: ConfigKeys = ConfigKeys(),
  geogCSParameterKeys: GeogCSParameterKeys = GeogCSParameterKeys(),
  projectedCSParameterKeys: ProjectedCSParameterKeys = ProjectedCSParameterKeys(),
  verticalCSKeys: VerticalCSKeys = VerticalCSKeys(),
  nonStandardizedKeys: NonStandardizedKeys = NonStandardizedKeys()
) {

  def getDoublesArrayFromMaps(key: Int) = {
    val doublesMap = nonStandardizedKeys.doublesMap

    if (!doublesMap.contains(key)) {
      val shortMap = nonStandardizedKeys.shortMap
      if (!shortMap.contains(key)) throw new MalformedGeoTiffException(
        s"couldn't find geokey $key"
      )
      else Array(shortMap(key).toDouble)
    } else doublesMap(key)
  }

  def getDoubleFromMaps(key: Int) = getDoublesArrayFromMaps(key)(0)

}

case class GeoKeyDirectoryMetadata(
  version: Int,
  keyRevision: Int,
  minorRevision: Int,
  numberOfKeys: Int
)

case class GeoKeyMetadata(
  keyID: Int,
  tiffTagLocation: Int,
  count: Int,
  valueOffset: Int
)
