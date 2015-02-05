package models;

import javax.xml.bind.annotation.XmlAttribute;

public class Column {
	/*Enum for merge action.*/
	public enum MergeAction {
		MERGE,
		UNMERGE,
		LATEST;
	}
	
	private String _columnName;
	private String _normalizedColumnName;
	private String _columnType;
	private int _weight;
	private MergeAction _mergeAction;
	
	private String _targetColumnName;
	private String _sourceColumnName;
	private String _dataSourceName;
	
	public MergeAction get_mergeAction() {
		return _mergeAction;
	}

	@XmlAttribute(name = "mergeAction")
	public void set_mergeAction(MergeAction action) {
		_mergeAction = action;
	}

	public String get_normalizedColumnName() {
		return _normalizedColumnName;
	}
	
	@XmlAttribute(name = "normalizedName")
	public void set_normalizedColumnName(String columnName) {
		_normalizedColumnName = columnName;
	}
	
	public String get_columnName() {
		return _columnName;
	}

	@XmlAttribute(name = "name")
	public void set_columnName(String _columnName) {
		this._columnName = _columnName;
	}

	public String get_columnType() {
		return _columnType;
	}

	@XmlAttribute(name = "type")
	public void set_columnType(String _columnType) {
		this._columnType = _columnType;
	}

	public int get_weight() {
		return _weight;
	}
	
	@XmlAttribute(name = "weight")
	public void set_weight(int _weight) {
		this._weight = _weight;
	}

	public String get_targetColumnName() {
		return _targetColumnName;
	}

	@XmlAttribute(name = "targetColumnName")
	public void set_targetColumnName(String columnName) {
		_targetColumnName = columnName;
	}

	public String get_sourceColumnName() {
		return _sourceColumnName;
	}

	@XmlAttribute(name = "sourceColumnName")
	public void set_sourceColumnName(String columnName) {
		_sourceColumnName = columnName;
	}

	public String get_dataSourceName() {
		return _dataSourceName;
	}

	@XmlAttribute(name = "dataSourceName")
	public void set_dataSourceName(String sourceName) {
		_dataSourceName = sourceName;
	}
}
