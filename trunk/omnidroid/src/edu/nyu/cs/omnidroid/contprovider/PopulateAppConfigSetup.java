/*******************************************************************************
 * Copyright 2009 OmniDroid - http://code.google.com/p/omnidroid 
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
 *******************************************************************************/
package edu.nyu.cs.omnidroid.contprovider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.widget.Toast;
import edu.nyu.cs.omnidroid.util.AGParser;

public class PopulateAppConfigSetup {
  private Context context;
  AGParser ag;

  public PopulateAppConfigSetup(Context a) {

    this.context = a;
    ag = new AGParser(a);
  }

  public void populate() {
    ag.delete_all();
    ag.write("Application:SMS");
    ag.write("EventName:SMS_RECEIVED,RECEIVED SMS");
    ag.write("Filters:S_Name,S_Ph_No,Text");
    ag.write("ActionName:SMS_SEND,SEND SMS");
    ag.write("ContentMap:");
    ag.write("S_Name,SENDER NAME,STRING");
    ag.write("S_Ph_No,SENDER PHONE NUMBER,INT");
    ag.write("Text,Text,STRING");
  }

  public void retrieveAppCfg() {

    ArrayList<HashMap<String, String>> eArrayList = ag.readEvents("SMS");
    Iterator<HashMap<String, String>> i1 = eArrayList.iterator();
    while (i1.hasNext()) {
      HashMap<String, String> HM1 = i1.next();
      Toast.makeText(context, HM1.toString(), 5).show();
    }

    ArrayList<HashMap<String, String>> aArrayList = ag.readActions("SMS");
    Iterator<HashMap<String, String>> i2 = aArrayList.iterator();
    while (i2.hasNext()) {
      HashMap<String, String> HM1 = i2.next();
      Toast.makeText(context, HM1.toString(), 5).show();
    }

    ArrayList<String> FilterList = ag.readFilters("SMS", "SMS_RECEIVED");
    Iterator<String> i3 = FilterList.iterator();
    while (i3.hasNext()) {
      String filter = i3.next();
      Toast.makeText(context, filter.toString(), 5).show();
    }

  }
}