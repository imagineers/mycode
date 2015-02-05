package processors;

import java.util.ArrayList;

import models.DataSource;
import models.SmartJoinConfig;

public interface IJoinConfigurable {
	public void load();
	public ArrayList<DataSource> getAllDataSources();
	public ArrayList<String> getEveryColumnName();
	public SmartJoinConfig getSmartJoinConfig();
}
