package models;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import processors.CSVSchemaDiscovery;
import processors.ISchemaDiscoverable;

public class DataSource {
	private static final String _SUPER_KEY = "SuperKey";
	private static final String _CANDIDATE_KEY = "CandidateKey";
	private static final String _PIVOT = "Pivot";
	private static final String _DS_TYPE = "csv";
	private static final String CONNECTION_STRING_SEPARATOR = "/";
	
	private ArrayList<Column> _columns;
	private String _query;	
	private String _dataSourceType;
	private String _connectionString;
	private int _preference;

	private int _pivotIndex;
	private ArrayList<Column> _candidateKeys;
	private ArrayList<Column> _superKeys;

	public DataSource() {
		this._columns = new ArrayList<Column>();
		this._candidateKeys = new ArrayList<Column>();
		this._superKeys = new ArrayList<Column>();
	}

	public int get_preference() {
		return _preference;
	}

	@XmlAttribute(name = "preference")
	public void set_preference(int preference) {
		this._preference = preference;
	}

	public ArrayList<Column> get_candidateKeys() {
		return _candidateKeys;
	}

	public void set_candidateKeys(ArrayList<Column> keys) {
		_candidateKeys = keys;
	}

	public ArrayList<Column> get_superKeys() {
		return _superKeys;
	}

	public void set_superKeys(ArrayList<Column> keys) {
		_superKeys = keys;
	}

	public int get_pivotIndex() {
		return _pivotIndex;
	}

	public void set_pivotIndex(int index) {
		_pivotIndex = index;
	}

	public ArrayList<Column> get_columns() {
		return _columns;
	}

	@XmlElementWrapper(name = "Columns")
	@XmlElement(name = "Column")
	public void set_columns(ArrayList<Column> _columns) {
		this._columns = _columns;
	}

	public String get_connectionString() {
		return _connectionString;
	}

	@XmlAttribute(name = "connectionString")
	public void set_connectionString(String connectionString) {
		_connectionString = connectionString;
	}

	public String get_query() {
		return _query;
	}

	@XmlAttribute(name = "query")
	public void set_query(String query) {
		_query = query;
	}

	public String get_dataSourceType() {
		return _dataSourceType;
	}

	@XmlAttribute(name = "dataSourceType")
	public void set_dataSourceType(String dataSourceType) {
		_dataSourceType = dataSourceType;
	}
	
	/*Infer the schema of all the data sources.*/
	public void inferSchema() {
		ISchemaDiscoverable schemaDiscovery = null;
		if(this._dataSourceType.equals(_DS_TYPE)) {
			schemaDiscovery = new CSVSchemaDiscovery();
		}
		if(schemaDiscovery == null) {
			return;
		}
		int index = 0;
		/*Get the list of column names for a particular data source.*/
		ArrayList<Column> allColumns = schemaDiscovery.discover(this._connectionString);
		for(Column allCol : allColumns) {
			for(Column col : this.get_columns()) {
				/*If extra information is present for a particular column in the configuration file, store it.*/
				if(col.get_columnName().equals(allCol.get_columnName())) {
					
					allCol.set_normalizedColumnName(col.get_normalizedColumnName());
					allCol.set_columnType(col.get_columnType());
					allCol.set_weight(col.get_weight());
					allCol.set_mergeAction(col.get_mergeAction());
					
					/*Set pivot index.*/
					if(col.get_columnType().equals(_PIVOT)) {
						this.set_pivotIndex(index);
					}
					/*Add candidate key columns.*/
					else if(col.get_columnType().equals(_CANDIDATE_KEY)) {
						this._candidateKeys.add(col);
					}
					/*Add super key columns.*/
					else if(col.get_columnType().equals(_SUPER_KEY)) {
						this._superKeys.add(col);
					}
				}
			}
			index++;
		}
		this.set_columns(allColumns);
	}
	
	/*Check if the connection sting contains the data source file name.*/
	public boolean doesDatasourceMatch(String datasourceName){
		
		if(this._dataSourceType.equals(_DS_TYPE)){
			return this._connectionString.contains(datasourceName);
		}
		return false;
	}
	
	/*Extract file name from the connection string.*/
	public String getDataSourceFileName(String connectionString) {
		String dsName[] = connectionString.split(CONNECTION_STRING_SEPARATOR);
		return dsName[dsName.length-1];
	}
	
	/*Get a particular super key column.*/
	public Column getSuperKeyColumn(String normalizedName) {
		Column superKeyColumn = null;
		
		for(Column eachSuperKeyColumn : this.get_superKeys()) {
			if(eachSuperKeyColumn.get_normalizedColumnName().equals(normalizedName)) {
				superKeyColumn = eachSuperKeyColumn;
				break;
			}
		}
		return superKeyColumn;
	}
}
