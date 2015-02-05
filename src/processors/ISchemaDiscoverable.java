package processors;

import java.util.ArrayList;

import models.Column;

public interface ISchemaDiscoverable {
	public ArrayList<Column> discover(String connectionString);
}
