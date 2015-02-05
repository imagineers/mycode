/*
 * Steps followed in reducer to get the desired output:
 * 1) Initialise the object which would be the empty json object with modified column names and corresponding values as null.
 *    This output object will contain records like:
 *    	a) Normalized column name for column whose merge action is MERGE.
 *    	b) Data Source name concatenated with normalized column name separated by underscore for column whose merge action is UNMERGE.
 *   	c) Data Source name concatenated with simple column name (if normalized name == null) separated by underscore for column whose merge action is UNMERGE.
 * 2) Get the matching records.
 * 3) Send the records for join based on join type.
 * 4) Then, send the joined records to be formatted in the target data source format.
 * 5) Writing this formatted record in the context.
 */

package processors;

import helpers.FullOuterJoin;
import helpers.InnerJoin;
import helpers.LeftAndRightOuterJoin;
import helpers.MatchHelper;

import java.io.IOException;
import java.util.ArrayList;

import models.SmartJoinConfig.JoinType;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class DataReducer extends Reducer<Text, Text, Text, Text> {
	private static IJoinConfigurable _iConfig;
	/*Initialise the configuration variable.*/
	public static void setIJoinConfigurable(IJoinConfigurable iConfig) {
		_iConfig = iConfig;
	}

	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		MatchHelper mh = new MatchHelper();
		ArrayList<ArrayList<Text>> matchingRecords = null;
		
		try {
			/*Get the list of matching records for a particular key.*/
			matchingRecords = mh.getMatchingRecords(values, _iConfig);
		}

		catch (ParseException e) {
			e.printStackTrace();
		}

		/*Return if there are no matching records.*/
		if(matchingRecords == null) {
			return;
		}

		for(int outerIndex = 0; outerIndex < matchingRecords.size(); outerIndex++) {
			/*Get the list of matching records.*/
			ArrayList<Text> matchedRecordsSet = matchingRecords.get(outerIndex);
			
			JSONObject joinedObject = null;
			
			/*Perform the join based on the join type mentioned in the configuration file.*/
			if(_iConfig.getSmartJoinConfig().get_joinType() == JoinType.FULL_OUTER_JOIN) {
				joinedObject = FullOuterJoin.join(matchedRecordsSet, _iConfig);
			}
			else if(_iConfig.getSmartJoinConfig().get_joinType() == JoinType.LEFT_OUTER_JOIN || _iConfig.getSmartJoinConfig().get_joinType() == JoinType.RIGHT_OUTER_JOIN) {
				joinedObject = LeftAndRightOuterJoin.join(matchedRecordsSet, _iConfig);
			}
			else if(_iConfig.getSmartJoinConfig().get_joinType() == JoinType.INNER_JOIN) {
				joinedObject = InnerJoin.join(matchedRecordsSet, _iConfig);
			}			
			
			WriteInTargetDataSource writeInTargetDS = new WriteInTargetDataSource();
			
			/*Convert the joined records into the target data source format.*/
			String outputString = writeInTargetDS.writeInCSV(joinedObject, _iConfig);
			
			if(joinedObject != null) {
				/*Write the converted joined records in the context.*/
				context.write(key, new Text(outputString));
				
			}
		}
	}
}