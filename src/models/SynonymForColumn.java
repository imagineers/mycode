package models;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class SynonymForColumn {	
	private ArrayList<Group> _group;
	private String _normalizedNames;
	
	public SynonymForColumn() {
		this._group = new ArrayList<Group>();
	}
	
	public ArrayList<Group> get_group() {
		return _group;
	}
	
	@XmlElement(name = "Group")
	public void set_group(ArrayList<Group> group) {
		this._group = group;
	}
	
	public String get_normalizedNames() {
		return _normalizedNames;
	}
	
	@XmlAttribute(name = "normalizedNames")
	public void set_normalizedNames(String name) {
		this._normalizedNames = name;
	}
}
