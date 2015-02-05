package models;

import javax.xml.bind.annotation.XmlElement;

public class Group {
	private String _synonyms;

	public String get_synonyms() {
		return _synonyms;
	}
	
	@XmlElement(name = "Synonyms")
	public void set_synonyms(String synonyms) {
		this._synonyms = synonyms;
	}
}
