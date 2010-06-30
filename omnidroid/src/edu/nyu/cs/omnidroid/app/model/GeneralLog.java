/*******************************************************************************
 * Copyright 2010 OmniDroid - http://code.google.com/p/omnidroid
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
package edu.nyu.cs.omnidroid.app.model;

import android.content.Context;

/**
 * This class represents an General{@code Log}. Logs are displayed on the ActivityLogs for
 * users to see what is going on.
 */
public class GeneralLog extends Log {
  public static final String TAG = GeneralLog.class.getSimpleName();

  /**
   * @param context
   *          application context for the db connection
   * @param text
   *          to create an OmniLogEvent out of
   * 
   */
  public GeneralLog(Context context, String text) {
    super();
    this.context = context;
    this.text = text;
  }

  /**
   * Copy constructor
   * 
   * @param log
   *          GeneralLog to duplicate
   * 
   */
  public GeneralLog(GeneralLog log) {
    super(log);
    this.context = log.context;
  }

  public GeneralLog(Context context, long id, long timestamp, String text) {
    super(context, id, timestamp, text);
  }

  public String toString() {
    return "ID: " + id + "\n" + "Timestamp: " + timestamp + "\n" + "\nText: " + text;
  }

  @Override
  public long insert() {
    CoreLogsDbHelper dbHelper = new CoreGeneralLogsDbHelper(context);
    long rowid = dbHelper.insert(this);
    dbHelper.close();
    return rowid;
  }
}