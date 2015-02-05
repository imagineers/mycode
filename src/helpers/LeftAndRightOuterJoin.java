package helpers;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

import models.DataSource;
import models.SmartJoinConfig.JoinType;

import org.apache.hadoop.io.Text;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import processors.IJoinConfigurable;

import mainclass.SmartJoin;

public class LeftAndRightOuterJoin extends InitialiseReducerOutputObject {
	/*Joining the records for Left and Right Outer Join.*/
	public static JSONObject join(ArrayList<Text> matchedRecordsSet, IJoinConfigurable iConfig) {
		boolean containsLeftTable = false;
		ArrayList<JSONObject> matchingRecords = new ArrayList<JSONObject>();
		JSONParser parser = new JSONParser();
		
		/*Get the preference order of all the data sources.*/
		TreeMap<Integer, DataSource> preferenceWiseDS = iConfig.getSmartJoinConfig().get_preferenceOfEachDS();
		DataSource preferenceDS = null;
		
		/*Get the preferred data source according to the join type.*/
		if(iConfig.getSmartJoinConfig().get_joinType() == JoinType.LEFT_OUTER_JOIN) {
			preferenceDS = preferenceWiseDS.get(preferenceWiseDS.firstKey());
		}
		else {
			preferenceDS = preferenceWiseDS.get(preferenceWiseDS.lastKey());
		}
		
		String preferenceDSName = preferenceDS.getDataSourceFileName(preferenceDS.get_connectionString());
		
		/*Convert the records from Text to JSONObject and store it in ArrayList of JSONObjects, only if preferred data source is present.*/
		for(int innerIndex = 0; innerIndex < matchedRecordsSet.size(); innerIndex++) {
			JSONObject record = null;
			
			try {
				record = (JSONObject)parser.parse(matchedRecordsSet.get(innerIndex).toString());
				if(containsLeftTable == false && record.containsValue(preferenceDSName)) {
					containsLeftTable = true;
				}
				matchingRecords.add(record);
			}

			catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		
		if(containsLeftTable == true) {
			JSONObject joinedObject = initJsonObject(iConfig);
			
			/*Merging the records.*/
			for(JSONObject eachMatch : matchingRecords) {
				Set<String> allProperties = eachMatch.keySet();
				
				for(String eachProp : allProperties) {				
					String newEachProp = "";
					
					String modifiedProp = eachMatch.get(SmartJoin.DS).toString() + SmartJoin.NAME_SEPARATOR + eachProp;
					
					/*Check if initialised reducer output object contains the modified property.*/
					if(joinedObject.containsKey(modifiedProp)) {
						newEachProp = modifiedProp;
					}
					/*If initialised reducer output object contains the property without any modification*/
					else {
						newEachProp = eachProp;
					}
					
					Object currentValueInJoined = joinedObject.get(newEachProp);
					Object valueInMatchedObject = eachMatch.get(eachProp);
					
					/*If the value for the corresponding key in the output object is null, store the value in it.*/
					if (currentValueInJoined == null) {
						if(valueInMatchedObject.toString().equals("")) {
							joinedObject.put(newEachProp, null);
						}
						else {
							joinedObject.put(newEachProp, valueInMatchedObject.toString());
						}
					}

					/*If the value for the corresponding key in the output object is not appropriate, store the correct value in it./*/
					else if (currentValueInJoined.toString().contains(valueInMatchedObject.toString()) == false) {
						String newValue = appropriateValue(matchingRecords, eachProp, iConfig);
						
						if(newValue.equals("") == false) {
							joinedObject.put(newEachProp, newValue);
						}
						else {
							joinedObject.put(newEachProp, null);
						}
					}
				}
			}
			
			//System.out.println(joinedObject.toString());
			
			/*Returning the joined records.*/
			return joinedObject;
		}
		
		/*Returning null if record form preferred data source was not present.*/
		return null;
	}
	
	/*Find the next not null value according to the join type.*/
	private static String appropriateValue(ArrayList<JSONObject> matchingRecords, String eachProp, IJoinConfigurable iConfig) {
		if(iConfig.getSmartJoinConfig().get_joinType() == JoinType.LEFT_OUTER_JOIN) {
			for(JSONObject eachRecord : matchingRecords) {
				if(eachRecord.get(eachProp).toString().equals("") == false) {
					return eachRecord.get(eachProp).toString();
				}
			}
		}
		else {
			for(int index = matchingRecords.size()-1 ; index >= 0; index--) {
				JSONObject eachRecord = matchingRecords.get(index);
				if(eachRecord.get(eachProp).toString().equals("") == false) {
					return eachRecord.get(eachProp).toString();
				}
			}
		}
		return "";
	}
}
