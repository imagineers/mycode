package models;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class TargetDataSource {
	private String _connectionString;
	private String _type;
	private ArrayList<Column> _columns;
	
	public String get_connectionString() {
		return _connectionString;
	}
	
	@XmlAttribute(name = "connectionString")
	public void set_connectionString(String string) {
		_connectionString = string;
	}
	
	public String get_type() {
		return _type;
	}
	
	@XmlAttribute(name = "type")
	public void set_type(String _type) {
		this._type = _type;
	}
	
	@XmlElement(name = "Column")
	public ArrayList<Column> get_columns() {
		return _columns;
	}
	
	public void set_columns(ArrayList<Column> _columns) {
		this._columns = _columns;
	}
	
}
