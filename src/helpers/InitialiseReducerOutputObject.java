package helpers;

import java.util.ArrayList;

import org.json.simple.JSONObject;

import processors.IJoinConfigurable;

/*Initialising the output object of reducer.*/
public class InitialiseReducerOutputObject {
	public static JSONObject initJsonObject(IJoinConfigurable iConfig) {
		JSONObject joinedObject = new JSONObject();
		ArrayList<String> everyColumn = iConfig.getEveryColumnName();
		for(String eachColumn : everyColumn) {
			if(joinedObject.containsKey(eachColumn) == false) {
				joinedObject.put(eachColumn, null);
			}
		}
		
		return joinedObject;
	}
}
