/*******************************************************************************
 * Copyright 2009, 2010 Omnidroid - http://code.google.com/p/omnidroid 
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
package edu.nyu.cs.omnidroid.app.controller.external.actions;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.net.smtp.SMTPClient;
import org.apache.commons.net.smtp.SMTPReply;
import org.apache.commons.net.smtp.SimpleSMTPHeader;
import edu.nyu.cs.omnidroid.app.controller.actions.SendGmailAction;
import edu.nyu.cs.omnidroid.app.model.db.DbHelper;
import edu.nyu.cs.omnidroid.app.model.db.RegisteredAppDbAdapter;
import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.widget.Toast;

/**
 * This service can be used to send a GMail when receiving an intent created by
 * {@link SendGmailAction}. It utilize a SMTP library from Apache Commons project.
 */
public class GMailService extends Service {

  /**
   * attributes field names
   */
  private RegisteredAppDbAdapter.AccountCredentials account;
  private String to;
  private String subject;
  private String body;

  /**
   * @return null because client can't bind to this service
   */
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
  
  /**
   * Get the username and password for the Gmail account 
   */
  private void extractUserCredentials() {
    DbHelper omniDbHelper = new DbHelper(this);
    SQLiteDatabase database = omniDbHelper.getWritableDatabase();
    RegisteredAppDbAdapter registeredAppDbAdapter = new RegisteredAppDbAdapter(database);
    
    account = registeredAppDbAdapter.getAccountCredentials(DbHelper.AppName.GMAIL, "");
        
    database.close();
    omniDbHelper.close(); 
  }
  
  @Override
  public void onStart(Intent intent, int startId) {
    super.onStart(intent, startId);

    extractUserCredentials();
    to = intent.getStringExtra(SendGmailAction.PARAM_TO);
    subject = intent.getStringExtra(SendGmailAction.PARAM_SUBJECT);
    body = intent.getStringExtra(SendGmailAction.PARAM_BODY);

    send();
  }

  /**
   * Send a GMail
   */
  private void send() {
    Toast.makeText(this, "GMail Service Started", Toast.LENGTH_LONG).show();

    SMTPClient client = new SMTPClient("UTF-8");
    client.setDefaultTimeout(60 * 1000);

    client.setRequireStartTLS(true); // requires STARTTLS
    client.setUseAuth(true); // use SMTP AUTH

    try {
      client.connect("smtp.gmail.com", 587);
      checkReply(client);
    } catch (IOException e) {
      Toast.makeText(this, "Send GMail failed, No network", Toast.LENGTH_LONG).show();
      return;
    }

    try {
      client.login("localhost", account.accountName, account.credential);
      checkReply(client);
    } catch (IOException e) {
      Toast.makeText(this, "Send GMail failed, Authentication error", Toast.LENGTH_LONG).show();
      return;
    }

    try {
      client.setSender(account.accountName);
      checkReply(client);

      client.addRecipient(to);
      checkReply(client);

      Writer writer = client.sendMessageData();

      if (writer != null) {
        SimpleSMTPHeader header = new SimpleSMTPHeader(account.accountName, to, subject);
        writer.write(header.toString());
        writer.write(body);
        writer.close();
        client.completePendingCommand();
        checkReply(client);
      }

      client.logout();
      client.disconnect();

    } catch (IOException e) {
      Toast.makeText(this, "Send GMail failed, Server error", Toast.LENGTH_LONG).show();
      return;
    }

    Toast.makeText(this, "GMail Sended", Toast.LENGTH_LONG).show();
  }

  /**
   * Check the response from the SMTP connection
   */
  private void checkReply(SMTPClient sc) throws IOException {
    if (SMTPReply.isNegativeTransient(sc.getReplyCode())) {
      sc.disconnect();
      throw new IOException("Transient SMTP error " + sc.getReplyCode());
    } else if (SMTPReply.isNegativePermanent(sc.getReplyCode())) {
      sc.disconnect();
      throw new IOException("Permanent SMTP error " + sc.getReplyCode());
    }
  }

}
