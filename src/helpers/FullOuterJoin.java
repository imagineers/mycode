package helpers;

import java.util.ArrayList;
import java.util.Set;

import org.apache.hadoop.io.Text;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import processors.IJoinConfigurable;

import mainclass.SmartJoin;

public class FullOuterJoin extends InitialiseReducerOutputObject {
	/*Joining the records for Full Outer Join.*/
	public static JSONObject join(ArrayList<Text> matchedRecordsSet, IJoinConfigurable iConfig) {
		ArrayList<JSONObject> matchingRecords = new ArrayList<JSONObject>();
		JSONParser parser = new JSONParser();
		
		/*Convert the records from Text to JSONObject and store it in ArrayList of JSONObjects.*/
		for(int innerIndex = 0; innerIndex < matchedRecordsSet.size(); innerIndex++) {
			JSONObject record = null;

			try {
				record = (JSONObject)parser.parse(matchedRecordsSet.get(innerIndex).toString());
				matchingRecords.add(record);
			}

			catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		/*Get the initialised reducer output object.*/
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
				if(currentValueInJoined == null) {
					if(valueInMatchedObject.toString().equals("")) {
						joinedObject.put(newEachProp, null);
					}
					else {
						joinedObject.put(newEachProp, valueInMatchedObject.toString());
					}
				}
				
				/*If the value for the corresponding key in the output object is not appropriate, store the correct value in it./*/
				else if(currentValueInJoined.toString().contains(valueInMatchedObject.toString()) == false) {
					String newValue = appropriateValue(matchingRecords, eachProp);
					
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
	
	/*Find the next not null value.*/
	private static String appropriateValue(ArrayList<JSONObject> matchingRecords, String eachProp) {
		for(JSONObject eachRecord : matchingRecords) {
			if(eachRecord.get(eachProp).toString().equals("") == false) {
				return eachRecord.get(eachProp).toString();
			}
		}
		return "";
	}
}
