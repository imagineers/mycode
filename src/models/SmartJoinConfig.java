package models;

import java.util.ArrayList;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "SmartJoinConfig")
public class SmartJoinConfig {
	/*Enum for match type.*/
	public enum MatchType {
		FAST_MATCH,
		FULL_MATCH;
	}
	
	/*Enum for join type.*/
	public enum JoinType {
		FULL_OUTER_JOIN,
		LEFT_OUTER_JOIN,
		RIGHT_OUTER_JOIN,
		INNER_JOIN;
	}
	
	private ArrayList<DataSource> _dataSources;
	private MatchType _matchType;
	private JoinType _joinType;
	private int minThresholdPercentage;
	private ArrayList<SynonymForColumn> _synonymList;
	private ArrayList<TargetDataSource> _targetDataSources;
	
	private ArrayList<String> _everyColumnName;
	private TreeMap<Integer, DataSource> _preferenceOfEachDS;
	
	public SmartJoinConfig() {
		this._dataSources = new ArrayList<DataSource>();
		this._everyColumnName = new ArrayList<String>();
		this._synonymList = new ArrayList<SynonymForColumn>();
		this._preferenceOfEachDS = new TreeMap<Integer, DataSource>();
		this._targetDataSources = new ArrayList<TargetDataSource>();
	}

	public JoinType get_joinType() {
		return _joinType;
	}

	@XmlElement(name = "JoinType")
	public void set_joinType(JoinType type) {
		_joinType = type;
	}

	public ArrayList<TargetDataSource> get_targetDataSources() {
		return _targetDataSources;
	}

	@XmlElementWrapper(name = "TargetDataSources")
	@XmlElement(name = "TargetDataSource")
	public void set_targetDataSources(ArrayList<TargetDataSource> dataSources) {
		_targetDataSources = dataSources;
	}

	public ArrayList<SynonymForColumn> get_synonymList() {
		return _synonymList;
	}

	@XmlElementWrapper(name = "SynonymsList")
	@XmlElement(name = "SynonymForColumn")
	public void set_synonymList(ArrayList<SynonymForColumn> list) {
		_synonymList = list;
	}
	
	public ArrayList<DataSource> get_dataSources() {
		return _dataSources;
	}

	@XmlElementWrapper(name = "DataSources")
	@XmlElement(name = "DataSource")
	public void set_dataSources(ArrayList<DataSource> dataSources) {
		_dataSources = dataSources;
	}

	public MatchType get_matchType() {
		return _matchType;
	}

	@XmlElement(name = "MatchType")
	public void set_matchType(MatchType matchType) {
		_matchType = matchType;
	}

	public int getMinThresholdPercentage() {
		return minThresholdPercentage;
	}

	@XmlElement(name = "MinThresholdPercentage")
	public void setMinThresholdPercentage(int minThresholdPercentage) {
		this.minThresholdPercentage = minThresholdPercentage;
	}

	public ArrayList<String> get_everyColumnName() {
		return _everyColumnName;
	}

	public void set_everyColumnName(ArrayList<String> columnName) {
		_everyColumnName = columnName;
	}
	
	/*Get the list of synonym groups for a particular column.*/
	public ArrayList<Group> getSynonymsForColumn(String normalizedColumnName) {
		for(SynonymForColumn synForCol : _synonymList) {
			if(synForCol.get_normalizedNames().contains(normalizedColumnName)) {
				return synForCol.get_group();
			}
		}
		
		return null;
	}
	
	/*Get the list data sources according to their preferences.*/
	public TreeMap<Integer, DataSource> get_preferenceOfEachDS() {
		for(DataSource ds : this._dataSources) {
			_preferenceOfEachDS.put(ds.get_preference(), ds);
		}
		
		return _preferenceOfEachDS;
	}
}
