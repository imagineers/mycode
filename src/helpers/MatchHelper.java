package helpers;

import java.util.ArrayList;
import java.util.Iterator;

import models.Column;
import models.DataSource;
import models.Group;
import models.SmartJoinConfig.MatchType;

import org.apache.hadoop.io.Text;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import processors.DataReducer;
import processors.IJoinConfigurable;
import mainclass.SmartJoin;

public class MatchHelper {
	public ArrayList<ArrayList<Text>> getMatchingRecords(Iterable<Text> values, IJoinConfigurable iConfig) throws ParseException {
		JSONParser parser = new JSONParser();
		
		ArrayList<Text> list = new ArrayList<Text>();
		Iterator<Text> valueIterator = values.iterator();
		
		while(valueIterator.hasNext()) {
			Text value = new Text(valueIterator.next().toString());
			list.add(value);
		}
		
		//System.out.println("-------------------------");
		
		ArrayList<Integer> matchedRecords = new ArrayList<Integer>();
		ArrayList<ArrayList<Text>> matches = new ArrayList<ArrayList<Text>>();
		
		/*Finding the matching records.*/
		for(int parentId = 0; parentId < list.size(); parentId++) {
			/*If record has already been considered for matching once in the past then don't consider it for matching again.*/
			if(matchedRecords.contains(parentId) == true) {
				continue;
			}
			
			ArrayList<Text> matchForCurrentParent = new ArrayList<Text>();
			matchForCurrentParent.add(list.get(parentId));

			JSONObject parentRecord = (JSONObject)parser.parse(list.get(parentId).toString());
			
			String parentDSName = parentRecord.get(SmartJoin.DS).toString();
			DataSource parentDS = getDataSource(parentDSName, iConfig);
			
			for(int childId = (parentId + 1); childId < list.size(); childId++) {
				JSONObject childRecord = (JSONObject)parser.parse(list.get(childId).toString());
				
				String childDSName = childRecord.get(SmartJoin.DS).toString();
				
				DataSource childDS = getDataSource(childDSName, iConfig);
				/*Matching the records on candidate key.*/
				if(matchOnCandidateKeys(parentRecord, childRecord, parentDS, childDS, iConfig) == true) {
					/*Add the matching record in the list together with the records with which it matched.*/
					matchForCurrentParent.add(list.get(childId));
					/*Add the child record id so that they are not considered for matching again.*/
					matchedRecords.add(childId);
				}
				else
				{
					/*Get the max score possible between parent and child record.*/
					int maxScore = calcMaxScore(parentDS, childDS);
					/*Get the result for match on super keys.*/
					boolean superKeyMatchResult = matchOnSuperKeys(parentRecord, childRecord, parentDS, childDS, maxScore, iConfig);
					if(superKeyMatchResult == true) {
						/*Add the matching record in the list together with the records with which it matched.*/
						matchForCurrentParent.add(list.get(childId));
						/*Add the child record id so that they are not considered for matching again.*/
						matchedRecords.add(childId);
						
						
					}
				}
			}
			
			Iterator<Text> iterator=matchForCurrentParent.iterator();
			
			ArrayList< Text> listIteraor=new ArrayList<Text>();
			while(iterator.hasNext()){
				
				listIteraor.add(iterator.next());
			}
			
			matchForCurrentParent = sortRecordsOnPreference(listIteraor, iConfig);
			
			/*Add the list of matching and non matching records as separate records in array list*/
			matches.add(matchForCurrentParent);
		}
		
		return matches;
	}
	
	/*Sort the matching records on preference of data source.*/
	private ArrayList<Text> sortRecordsOnPreference(ArrayList<Text> matchingRecords, IJoinConfigurable iConfig) throws ParseException {
		
		JSONParser parser = new JSONParser();
		ArrayList<Text> recordForMatch = new ArrayList<Text>();
		for(Text obj:matchingRecords){
			recordForMatch.add(obj);
		}
		int recordsSize=matchingRecords.size();
		for(int outerIndex = 0; outerIndex < recordsSize; outerIndex++) {
			JSONObject outerJsonRecord = null;
			outerJsonRecord  = (JSONObject)parser.parse(matchingRecords.get(outerIndex).toString());
			DataSource outerRecordDS = getDataSource(outerJsonRecord.get(SmartJoin.DS).toString(), iConfig);
			
			int length=matchingRecords.size();
			for(int innerIndex = outerIndex+1; innerIndex < length; innerIndex++) {
				JSONObject innerJsonRecord = null;
				innerJsonRecord = (JSONObject)parser.parse(matchingRecords.get(innerIndex).toString());
				DataSource innerRecordDS = getDataSource(innerJsonRecord.get(SmartJoin.DS).toString(), iConfig);
				if(outerRecordDS.get_preference() > innerRecordDS.get_preference()) {
					Text tempRecord = matchingRecords.get(outerIndex);
					matchingRecords.add(outerIndex, matchingRecords.get(innerIndex));
					matchingRecords.add(innerIndex, tempRecord);
				}
				innerIndex++;
			}
			outerIndex++;
		}
		return matchingRecords;
	}
	
	/*Get the data source object based on data source name.*/
	public DataSource getDataSource(String dsName, IJoinConfigurable iConfig) {

		DataSource dataSource = null;
		ArrayList<DataSource> dataSourceData = iConfig.getAllDataSources();
		
		for(DataSource eachDataSource : dataSourceData) {
			if(eachDataSource.doesDatasourceMatch(dsName) == true) {
				dataSource = eachDataSource;
				break;
			}
		}
		return dataSource;
	}
	
	/*Calculate the max score possible between parent and child data source.*/
	private int calcMaxScore(DataSource parentDS, DataSource childDS) {
		int maxScore = parentDS.get_columns().get(parentDS.get_pivotIndex()).get_weight();
		for(Column eachParentSuperKeyColumn : parentDS.get_superKeys()) {
			Column childsuperKeyColumn = childDS.getSuperKeyColumn(eachParentSuperKeyColumn.get_normalizedColumnName().toString());
			if(childsuperKeyColumn != null && 
				eachParentSuperKeyColumn.get_normalizedColumnName().equals(childsuperKeyColumn.get_normalizedColumnName())) {
				maxScore += eachParentSuperKeyColumn.get_weight();
			}
		}
		//System.out.println("MaxScore: " + maxScore);
		return maxScore;
	}
	
	/*Check for match on synonyms.*/
	private boolean matchOnSynonyms(String parentSuperKeyValueStr, String childSuperKeyValueStr, String normalizedColumnName, IJoinConfigurable iConfig) {
		ArrayList<Group> synonymGroupList = iConfig.getSmartJoinConfig().getSynonymsForColumn(normalizedColumnName);
		if(synonymGroupList != null) {
			for(Group synGroup : synonymGroupList) {
				if(synGroup.get_synonyms().contains(childSuperKeyValueStr) && synGroup.get_synonyms().contains(parentSuperKeyValueStr)) {
					return true;
				}
			}
		}
		
		return false;
	}

	/*Check for match on super keys.*/
	private boolean matchOnSuperKeys(JSONObject parentRecord, JSONObject childRecord, DataSource parentDS, DataSource childDS, int maxScore, 
			IJoinConfigurable iConfig) {
		int score = parentDS.get_columns().get(parentDS.get_pivotIndex()).get_weight();
		int minRequiredScore = (iConfig.getSmartJoinConfig().getMinThresholdPercentage() * maxScore) / 100;
	
		/*Iterate on every super key column for parent data source.*/
		for(Column eachParentSuperKeyColumn : parentDS.get_superKeys()) {
			Object parentSuperKeyValue = parentRecord.get(eachParentSuperKeyColumn.get_normalizedColumnName());
			String parentSuperKeyValueStr = (parentSuperKeyValue == null) ? "" : parentSuperKeyValue.toString();
			
			/*Get the child super key column for corresponding parent super key column.*/
			Column childSuperKeyColumn = childDS.getSuperKeyColumn(eachParentSuperKeyColumn.get_normalizedColumnName().toString());
			if(childSuperKeyColumn != null && eachParentSuperKeyColumn.get_normalizedColumnName().equals(childSuperKeyColumn.get_normalizedColumnName())) {
				Object childSuperKeyValue = childRecord.get(childSuperKeyColumn.get_normalizedColumnName());
				String childSuperKeyValueStr = (childSuperKeyValue == null) ? "" : childSuperKeyValue.toString();
				
				/*Check whether super key column value matches with child super key column value OR if they don't match check if they match on synonyms.*/
				if(parentSuperKeyValueStr.equals(childSuperKeyValueStr) || matchOnSynonyms(parentSuperKeyValueStr, childSuperKeyValueStr,
						eachParentSuperKeyColumn.get_normalizedColumnName(), iConfig)) {
					score += eachParentSuperKeyColumn.get_weight();
					
					/*If match type is FAST_MATCH.*/
					if(iConfig.getSmartJoinConfig().get_matchType() == MatchType.FAST_MATCH && score >= minRequiredScore) {
						//System.out.println("fast match");
						return true;
					}
				}
			}
		}
		
		/*If match type is FULL_MATCH.*/
		if(score >= minRequiredScore) {
			return true;
		}
		
		/*If the records don't match return false.*/
		return false;
	}
	
	/*Check for match on candidate keys.*/
	private boolean matchOnCandidateKeys(JSONObject parentRecord, JSONObject childRecord,  DataSource parentDS, DataSource childDS,
			IJoinConfigurable iConfig) {
		ArrayList<Column> parentCandidateKeys = parentDS.get_candidateKeys();
		ArrayList<Column> childCandidateKeys = childDS.get_candidateKeys();
		
		//Bit Optimization..Since count does not match..we cannot trust candidate key match.
		//Log: the fact that a and b have identical set of candidate keys.
		if(parentCandidateKeys.size() != childCandidateKeys.size()){
			return false;
		}
				
		boolean matchingColumnfound = false;
		
		/*Iterate on every parent candidate key column.*/
		for(Column eachParentCandidateColumn : parentCandidateKeys) {
			/*Iterate on every child key column.*/
			for(Column eachChildCandidateColumn : childCandidateKeys) {
				/*Check if candidate key column for parent and child has same normalized name.*/
				if(eachParentCandidateColumn.get_normalizedColumnName().equals(eachChildCandidateColumn.get_normalizedColumnName())) {
					matchingColumnfound = true;
					
					Object parentVal = parentRecord.get(eachParentCandidateColumn.get_normalizedColumnName());
					String parentValStr = parentVal == null ? "" : parentVal.toString();
					
					Object childVal = childRecord.get(eachChildCandidateColumn.get_normalizedColumnName());
					String childValStr = childVal == null ? "" : childVal.toString();
					
					/*Check if the value for parent and child super key column are equal.*/
					if(parentValStr.equals(childValStr)) {
						break;
					}
					else {
						return false;
					}
				}
			}
			
			/*If parent and child normalized column name are not equal.*/
			if(matchingColumnfound == false) {
				return false;
			}
			
			matchingColumnfound = false;
		}
		
		/*If value for parent and child candidate key column are equal.*/
		return true;
	}
}
