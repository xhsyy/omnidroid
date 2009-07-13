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
package edu.nyu.cs.omnidroid.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import edu.nyu.cs.omnidroid.R;
import edu.nyu.cs.omnidroid.model.db.DataFilterDbAdapter;
import edu.nyu.cs.omnidroid.model.db.DataTypeDbAdapter;
import edu.nyu.cs.omnidroid.model.db.DbHelper;
import edu.nyu.cs.omnidroid.model.db.RegisteredActionDbAdapter;
import edu.nyu.cs.omnidroid.model.db.RegisteredAppDbAdapter;
import edu.nyu.cs.omnidroid.model.db.RegisteredEventAttributeDbAdapter;
import edu.nyu.cs.omnidroid.model.db.RegisteredEventDbAdapter;
import edu.nyu.cs.omnidroid.model.db.RuleActionDbAdapter;
import edu.nyu.cs.omnidroid.model.db.RuleDbAdapter;
import edu.nyu.cs.omnidroid.model.db.RuleFilterDbAdapter;
import edu.nyu.cs.omnidroid.ui.simple.model.ModelAction;
import edu.nyu.cs.omnidroid.ui.simple.model.ModelApplication;
import edu.nyu.cs.omnidroid.ui.simple.model.ModelAttribute;
import edu.nyu.cs.omnidroid.ui.simple.model.ModelEvent;
import edu.nyu.cs.omnidroid.ui.simple.model.ModelFilter;
import edu.nyu.cs.omnidroid.ui.simple.model.Rule;
import edu.nyu.cs.omnidroid.ui.simple.model.RuleNode;
import edu.nyu.cs.omnidroid.ui.simple.model.RuleSparse;

public class UIDbHelper {
  
  private DataTypeDbAdapter dataTypeDbAdapter;
  private DataFilterDbAdapter dataFilterDbAdapter;
  private RegisteredAppDbAdapter registeredAppDbAdapter;
  private RegisteredEventDbAdapter registeredEventDbAdapter;
  private RegisteredActionDbAdapter registeredActionDbAdapter;
  private RegisteredEventAttributeDbAdapter registeredEventAttributeDbAdapter;
  private RuleFilterDbAdapter ruleFilterDbAdpater; 
  private RuleActionDbAdapter ruleActionDbAdpater; 
  private RuleDbAdapter ruleDbAdapter;
  private DbHelper omnidroidDbHelper;
  
  private Map<Integer, String> dataTypeNames;
  private Map<Integer, String> dataFilterNames;
  private Map<Integer, ModelApplication> applications;
  private Map<Integer, ModelEvent> events;
  private Map<Integer, ModelAction> actions;
  private Map<Integer, ModelAttribute> attributes;
  
  public UIDbHelper(Context context) {
    omnidroidDbHelper = new DbHelper(context);
    SQLiteDatabase database = omnidroidDbHelper.getWritableDatabase();
    
    // Initialize db adapters
    dataTypeDbAdapter = new DataTypeDbAdapter(database);
    dataFilterDbAdapter = new DataFilterDbAdapter(database);
    registeredAppDbAdapter = new RegisteredAppDbAdapter(database);
    registeredEventDbAdapter = new RegisteredEventDbAdapter(database);
    registeredActionDbAdapter = new RegisteredActionDbAdapter(database);
    registeredEventAttributeDbAdapter = new RegisteredEventAttributeDbAdapter(database);
    ruleDbAdapter = new RuleDbAdapter(database);
    
    // Initialize db cache
    dataTypeNames = new HashMap<Integer, String>();
    dataFilterNames = new HashMap<Integer, String>();
    applications = new HashMap<Integer, ModelApplication>();
    events = new HashMap<Integer, ModelEvent>();
    actions = new HashMap<Integer, ModelAction>();
    attributes = new HashMap<Integer, ModelAttribute>();
    
    // Load db cache maps
    loadDbCache();
  }
  
  public void close() {
    omnidroidDbHelper.close();
  }
  
  private void loadDbCache() {
    
    // Load DataTypes
    Cursor cursorDataTypes = dataTypeDbAdapter.fetchAll();
    for (int i = 0; i < cursorDataTypes.getCount(); i++) {
      cursorDataTypes.moveToNext();
      dataTypeNames.put(
          cursorDataTypes.getInt(cursorDataTypes.getColumnIndex(
              DataTypeDbAdapter.KEY_DATATYPEID)), 
          cursorDataTypes.getString(cursorDataTypes.getColumnIndex(
              DataTypeDbAdapter.KEY_DATATYPENAME)));
    }
    cursorDataTypes.close();
    
    // Load Filters
    Cursor cursorDataFilters = dataFilterDbAdapter.fetchAll();
    for (int i = 0; i < cursorDataFilters.getCount(); i++) {
      cursorDataFilters.moveToNext();
      dataFilterNames.put(
          cursorDataFilters.getInt(cursorDataFilters.getColumnIndex(
              DataFilterDbAdapter.KEY_DATAFILTERID)), 
          cursorDataFilters.getString(cursorDataFilters.getColumnIndex(
              DataFilterDbAdapter.KEY_DATAFILTERNAME)));
    }
    cursorDataFilters.close();
    
    // Load applications
    Cursor cursorApplications = registeredAppDbAdapter.fetchAll();
    for (int i = 0; i < cursorApplications.getCount(); i++) {
      cursorApplications.moveToNext();
      ModelApplication application = new ModelApplication(
          cursorApplications.getInt(cursorApplications.getColumnIndex(
              RegisteredAppDbAdapter.KEY_APPID)),
          cursorApplications.getString(cursorApplications.getColumnIndex(
              RegisteredAppDbAdapter.KEY_APPNAME)), "",
          R.drawable.icon_event_unknown);
      applications.put(application.getDatabaseId(), application);
    }
    cursorApplications.close();
    
    // Load Events
    Cursor cursorEvents = registeredEventDbAdapter.fetchAll();
    for (int i = 0; i < cursorEvents.getCount(); i++) {
      cursorEvents.moveToNext();
      ModelEvent event = new ModelEvent(
          cursorEvents.getInt(cursorEvents.getColumnIndex(
              RegisteredEventDbAdapter.KEY_EVENTID)), 
          cursorEvents.getString(cursorEvents.getColumnIndex(
              RegisteredEventDbAdapter.KEY_EVENTNAME))
          , "", R.drawable.icon_event_unknown);
      events.put(event.getDatabaseId(), event);
    }
    cursorEvents.close();
    
    // Load Actions
    Cursor cursorActions = registeredActionDbAdapter.fetchAll();
    for (int i = 0; i < cursorActions.getCount(); i++) {
      cursorActions.moveToNext();
      
      ModelApplication application = applications.get(
          cursorActions.getInt(cursorActions.getColumnIndex(RegisteredActionDbAdapter.KEY_APPID))); 
      
      ModelAction action = new ModelAction(
          cursorActions.getInt(cursorActions.getColumnIndex(
              RegisteredActionDbAdapter.KEY_ACTIONID)), 
          cursorActions.getString(cursorActions.getColumnIndex(
              RegisteredActionDbAdapter.KEY_ACTIONNAME)), 
          "", R.drawable.icon_event_unknown, application);
      
      actions.put(cursorActions.getInt(cursorActions.getColumnIndex(
              RegisteredActionDbAdapter.KEY_ACTIONID)), action);
    }
    cursorActions.close();
    
    // Load Attributes
    Cursor cursorAttributes = registeredEventAttributeDbAdapter.fetchAll();
    for (int i = 0; i < cursorAttributes.getCount(); i++) {
      cursorAttributes.moveToNext();
      
      ModelAttribute attribute = new ModelAttribute(
          cursorAttributes.getInt(cursorAttributes.getColumnIndex(
              RegisteredEventAttributeDbAdapter.KEY_EVENTATTRIBUTEID)), 
          cursorAttributes.getInt(cursorAttributes.getColumnIndex(
              RegisteredEventAttributeDbAdapter.KEY_EVENTID)), 
          cursorAttributes.getInt(cursorAttributes.getColumnIndex(
              RegisteredEventAttributeDbAdapter.KEY_DATATYPEID)), 
          cursorAttributes.getString(cursorAttributes.getColumnIndex(
              RegisteredEventAttributeDbAdapter.KEY_EVENTATTRIBUTENAME)), 
          "", R.drawable.icon_attribute_unknown);
      
      attributes.put(attribute.getDatabaseId(), attribute);
    }
    cursorAttributes.close();
    
    // TODO load others
  }
  
  public ArrayList<ModelApplication> getAllApplications() {
    ArrayList<ModelApplication> applicationList = new ArrayList<ModelApplication>();
    for(Entry<Integer, ModelApplication> entry : applications.entrySet() ) {
      applicationList.add(entry.getValue());
    }
    return applicationList;
  }
  
  public ArrayList<ModelEvent> getAllEvents() {
    ArrayList<ModelEvent> eventList = new ArrayList<ModelEvent>();
    for(Entry<Integer, ModelEvent> entry : events.entrySet()) {
      eventList.add(entry.getValue());
    }
    return eventList;
  }

  public ArrayList<ModelAction> getActionsForApplication(ModelApplication application) {
    ArrayList<ModelAction> actionList = new ArrayList<ModelAction>();
    for(Entry<Integer, ModelAction> entry : actions.entrySet()){
      if(entry.getValue().getApplication().getDatabaseId() == application.getDatabaseId()) {
        actionList.add(entry.getValue());  
      }
    }
    return actionList;
  }

  public ArrayList<ModelAttribute> getAttributesForEvent(ModelEvent event) {
    ArrayList<ModelAttribute> attributesList = new ArrayList<ModelAttribute>();
    
    Cursor cursorAttribute = registeredEventAttributeDbAdapter.fetchAll(null, 
        Long.valueOf(event.getDatabaseId()), null);
    
    for (int i = 0; i < cursorAttribute.getCount(); i++) {
      cursorAttribute.moveToNext();
      ModelAttribute attribute = attributes.get(cursorAttribute.getInt(
          cursorAttribute.getColumnIndex(RegisteredEventAttributeDbAdapter.KEY_EVENTATTRIBUTEID)));
      attributesList.add(attribute);
    }
    
    cursorAttribute.close();
    return attributesList;
  }
  
  public ArrayList<ModelFilter> getFiltersForAttribute(ModelAttribute attribute) {
    ArrayList<ModelFilter> filterList = new ArrayList<ModelFilter>();
    Cursor cursor = dataFilterDbAdapter.fetchAll(null, Long.valueOf(attribute.getDatatype()));
    for (int i = 0; i < cursor.getCount(); i++) {
      cursor.moveToNext();
      
      int filterID = cursor.getInt(cursor.getColumnIndex(DataFilterDbAdapter.KEY_DATAFILTERID));
      String filterName = dataFilterNames.get(filterID);
      
      filterList.add(new ModelFilter(
          filterID, filterName, "", R.drawable.icon_filter_unknown, attribute, null));
    }
    cursor.close();
    return filterList;
  }
  
  public ArrayList<RuleSparse> getRules() {
	  ArrayList<RuleSparse> rules = new ArrayList<RuleSparse>();
	  Cursor cursor = ruleDbAdapter.fetchAll();
	  for (int i = 0; i < cursor.getCount(); i++) {
	    rules.add(new RuleSparse(
	        cursor.getInt(cursor.getColumnIndex(RuleDbAdapter.KEY_RULEID)),
	        cursor.getString(cursor.getColumnIndex(RuleDbAdapter.KEY_RULENAME)),
	        cursor.getInt(cursor.getColumnIndex(RuleDbAdapter.KEY_ENABLED)) == 1 ));
	  }
	  cursor.close();
	  return rules;
  }
  
  public Rule loadRule(int databaseId) {
	  Cursor cursorRule = ruleDbAdapter.fetch(Long.valueOf(databaseId));
	  
	  ModelEvent event = events.get(cursorRule.getInt(cursorRule.getColumnIndex(
	      RuleDbAdapter.KEY_EVENTID)));
	  ArrayList<ModelFilter> filters = getFiltersForRule(databaseId);
	  ArrayList<ModelAction> actions = getActionForRule(databaseId);
	  
	  // TODO: reconstruct rule from filters and actions
	  Rule rule = new Rule();
	  return rule;
  }
  
  private ArrayList<ModelFilter> getFiltersForRule(int ruleId) {
    Cursor cursorRuleFilters = ruleFilterDbAdpater.fetchAll(Long.valueOf(ruleId), null, null, 
        null, null, null);
    ArrayList<ModelFilter> filters = new ArrayList<ModelFilter>();
    for (int i = 0; i < cursorRuleFilters.getCount(); i++) {
      
      ModelAttribute attribute = attributes.get(
          cursorRuleFilters.getInt(cursorRuleFilters.getColumnIndex(
              RuleFilterDbAdapter.KEY_EVENTATTRIBUTEID)));
      
      // TODO needs to deal with datatype later, right now just set them to null
      ModelFilter filter = new ModelFilter(
          cursorRuleFilters.getInt(cursorRuleFilters.getColumnIndex(
              RuleFilterDbAdapter.KEY_RULEFILTERID)),
          "", "", R.drawable.icon_event_unknown, attribute, null);
      
      filters.add(filter);
    }
    cursorRuleFilters.close();
    return filters;
  }
  
  private ArrayList<ModelAction> getActionForRule(int ruleId) {
    Cursor cursorRuleActions = ruleActionDbAdpater.fetchAll(Long.valueOf(ruleId), null);
    ArrayList<ModelAction> actionList = new ArrayList<ModelAction>();
    for (int i = 0; i< cursorRuleActions.getCount(); i++) {
      
      ModelAction action = actions.get(
          cursorRuleActions.getInt(cursorRuleActions.getColumnIndex(
              RuleActionDbAdapter.KEY_ACTIONID)));
      
      actionList.add(action);
    }
    cursorRuleActions.close();
    return actionList;
  }
  
  /**
   * Given a rule, try to save it to the database.
   */
  public void saveRule(Rule rule) throws Exception {
	  ModelEvent event = (ModelEvent)rule.getRootNode().getItem();
	  ArrayList<RuleNode> filterList = rule.getFilterBranches();
	  ArrayList<ModelAction> actionList = rule.getActions();
	  
	  // Save the rule record
	  long ruleID = ruleDbAdapter.insert(
	      Long.valueOf(event.getDatabaseId()), "RuleName", "RuleDesc", true);
	  
	  // Save all rule actions
	  for (int i = 0; i < actionList.size(); i++) {
	    ruleActionDbAdpater.insert(ruleID, Long.valueOf(actionList.get(i).getDatabaseId()));
	  }
	  
	  // Save all rule filters
	  for (int i = 0; i < filterList.size(); i++) {
		  saveRuleNode(ruleID, -1, filterList.get(i));
	  }
  }
  
  /**
   * Recursively write each node of the filter branches to the database.
   */
  private void saveRuleNode(long ruleID, long parentRuleNodeID, RuleNode node) {
    
	  ModelFilter filter = (ModelFilter)node.getItem();
	  
	  long thisRuleNodeID = ruleFilterDbAdpater.insert(ruleID, 
	      Long.valueOf(filter.getAttribute().getDatabaseId()), Long.valueOf(-1), 
	      Long.valueOf(filter.getDatabaseId()), parentRuleNodeID, "");
	  
	  // Now all our children filters:
	  for (int i = 0; i < node.getChildren().size(); i++) {
		  saveRuleNode(ruleID, thisRuleNodeID, node.getChildren().get(i));
	  }
  }
}