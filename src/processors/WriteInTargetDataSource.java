package processors;

import java.util.ArrayList;

import helpers.MatchHelper;

import mainclass.SmartJoin;
import models.Column;
import models.DataSource;
import models.TargetDataSource;

import org.json.simple.JSONObject;

public class WriteInTargetDataSource {
	private static final String TARGET_DS_TYPE = "csv";
	
	public String writeInCSV(JSONObject joinedObject, IJoinConfigurable iConfig) {
		ArrayList<TargetDataSource> targetDSList = iConfig.getSmartJoinConfig().get_targetDataSources();
		MatchHelper mh = new MatchHelper();
		String outputString = "";
		TargetDataSource targetDataSource = new TargetDataSource();
		
		/*Get the target data source for the target data source type.*/
		for(TargetDataSource targetDS : targetDSList) {
			if(targetDS.get_type().equals(TARGET_DS_TYPE)) {
				targetDataSource = targetDS;
				break;
			}
		}
		
		/*Iterate for every column of the target data source.*/
		for(Column col : targetDataSource.get_columns()) {
			Column column = new Column();
			/*Get the data source object from the target data source name.*/
			DataSource ds = mh.getDataSource(col.get_dataSourceName(), iConfig);
			
			/*Get the desired column form the data source object.*/
			for(Column referCol : ds.get_columns()) {
				if(referCol.get_columnName().equals(col.get_sourceColumnName())) {
					column = referCol;
					break;
				}
			}
			
			String normalizedColumnName = column.get_normalizedColumnName();
			String columnName = column.get_columnName();
			String targetDSName = col.get_dataSourceName();
			
			/*Store the value in the outputString variable.*/
			if(normalizedColumnName != null && joinedObject != null && joinedObject.containsKey(targetDSName + SmartJoin.NAME_SEPARATOR + normalizedColumnName)) {
				outputString = outputString + SmartJoin.VALUES_SEPARATOR + joinedObject.get(targetDSName + SmartJoin.NAME_SEPARATOR + normalizedColumnName);
			}
			else if(normalizedColumnName != null && joinedObject != null && joinedObject.containsKey(normalizedColumnName)) {
				outputString = outputString + SmartJoin.VALUES_SEPARATOR + joinedObject.get(normalizedColumnName);
			}
			else if(normalizedColumnName == null && joinedObject != null && joinedObject.containsKey(targetDSName + SmartJoin.NAME_SEPARATOR + columnName)) {
				outputString = outputString + SmartJoin.VALUES_SEPARATOR + joinedObject.get(targetDSName + SmartJoin.NAME_SEPARATOR + columnName);
			}
		}
		return outputString;
	}
}
