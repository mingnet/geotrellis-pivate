/*
 * Copyright 2016 Martin Davis, Azavea
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

package org.osgeo.proj4j.parser;

import java.util.*;
import org.osgeo.proj4j.*;

public class Proj4Keyword 
{

  public static final String a = "a";
  public static final String b = "b";
  public static final String f = "f";
  public static final String alpha = "alpha";
  public static final String datum = "datum";
  public static final String ellps = "ellps";
  public static final String es = "es";
  public static final String axis = "axis";

  public static final String azi = "azi";
  public static final String gamma = "gamma";
  public static final String k = "k";
  public static final String k_0 = "k_0";
  public static final String lat_ts = "lat_ts";
  public static final String lat_0 = "lat_0";
  public static final String lat_1 = "lat_1";
  public static final String lat_2 = "lat_2";
  public static final String lon_0 = "lon_0";
  public static final String lonc = "lonc";
  public static final String pm = "pm";
  
  public static final String proj = "proj";
  
  public static final String R = "R";
  public static final String R_A = "R_A";
  public static final String R_a = "R_a";
  public static final String R_V = "R_V";
  public static final String R_g = "R_g";
  public static final String R_h = "R_h";
  public static final String R_lat_a = "R_lat_a";
  public static final String R_lat_g = "R_lat_g";
  public static final String rf = "rf";
  
  public static final String south = "south";
  public static final String to_meter = "to_meter";
  public static final String towgs84 = "towgs84";
  public static final String units = "units";
  public static final String x_0 = "x_0";
  public static final String y_0 = "y_0";
  public static final String zone = "zone";
  
  public static final String title = "title";
  public static final String nadgrids = "nadgrids";
  public static final String no_defs = "no_defs";
  public static final String wktext = "wktext";
  public static final String no_uoff = "no_uoff"; // TODO: Implement no_uoff parameter


  private static Set<String> supportedParams = null;
  
  public static synchronized Set supportedParameters()
  {
    if (supportedParams == null) {
      supportedParams = new TreeSet<String>();
      
      supportedParams.add(a);
      supportedParams.add(rf);
      supportedParams.add(f);
      supportedParams.add(alpha);
      supportedParams.add(es);
      supportedParams.add(b);
      supportedParams.add(datum);
      supportedParams.add(ellps);
      
      supportedParams.add(R_A);
   
      supportedParams.add(k);
      supportedParams.add(k_0);
      supportedParams.add(lat_ts);
      supportedParams.add(lat_0);
      supportedParams.add(lat_1);
      supportedParams.add(lat_2);
      supportedParams.add(lon_0);
      supportedParams.add(lonc);
      
      supportedParams.add(x_0);
      supportedParams.add(y_0);

      supportedParams.add(proj);
      supportedParams.add(south);
      supportedParams.add(towgs84);
      supportedParams.add(to_meter);
      supportedParams.add(units);
      supportedParams.add(nadgrids);
      supportedParams.add(pm);
      supportedParams.add(axis);

      supportedParams.add(gamma);       // Just for Oblique Mercator projection
      supportedParams.add(zone);        // Just for Transverse Mercator projection
      
      supportedParams.add(title);       // no-op
      supportedParams.add(no_defs);     // no-op
      supportedParams.add(wktext);      // no-op
      supportedParams.add(no_uoff);     // no-op
    }
    return supportedParams;
  }
  
  public static boolean isSupported(String paramKey)
  {
    return supportedParameters().contains(paramKey);
  }
  
  public static void checkUnsupported(String paramKey)
  {
    if (! isSupported(paramKey)) {
      throw new UnsupportedParameterException(paramKey + " parameter is not supported");
    }
  }
  
  public static void checkUnsupported(Collection params)
  {
    for (Object s : params) {
      checkUnsupported((String) s);
    }
  }
}
