package processors;

import java.io.File;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import models.Column;
import models.DataSource;
import models.SmartJoinConfig;
import models.Column.MergeAction;

import mainclass.SmartJoin;

public class XmlJoinConfiguration implements IJoinConfigurable {
	private SmartJoinConfig _smartJoinConfigObj = null;
	private String _filePath = "";
	
	/*Initialize the _filePath variable.*/
	public XmlJoinConfiguration(String filePath) {
		_filePath = filePath;
	}
	
	public SmartJoinConfig getSmartJoinConfig() {
		return this._smartJoinConfigObj;
	}
	
	/*Submit the xml file for parsing.*/
	public void load() {
		try {
			JAXBContext jc = JAXBContext.newInstance(SmartJoinConfig.class);
			Unmarshaller um = jc.createUnmarshaller();
			_smartJoinConfigObj = (SmartJoinConfig) um.unmarshal(new File(_filePath));
			
			for(DataSource eachDataSource : _smartJoinConfigObj.get_dataSources()) {
				eachDataSource.inferSchema();
			}
		}
		
		catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<DataSource> getAllDataSources() {
		return _smartJoinConfigObj.get_dataSources();
	}
	
	/*Get list containing every column name, where column names have been modified according to their merge type.*/
	public ArrayList<String> getEveryColumnName() {
		ArrayList<String> everyColumnName = new ArrayList<String>();
		
		for(DataSource ds : _smartJoinConfigObj.get_dataSources()) {
			String dsName = ds.getDataSourceFileName(ds.get_connectionString());
			
			for(Column col : ds.get_columns()) {
				/*If merge action is merge, then store normalized name.*/
				if(col.get_mergeAction() == MergeAction.MERGE) {
					if(everyColumnName.contains(col.get_normalizedColumnName()) == false) {
						everyColumnName.add(col.get_normalizedColumnName());
					}
				}
				/*If merge action is unmerge and if normalized name is not null, store it with data source name.*/
				else if(col.get_mergeAction() == MergeAction.UNMERGE) {
					everyColumnName.add(dsName + SmartJoin.NAME_SEPARATOR + col.get_normalizedColumnName());
				}
				/*If merge action is unmerge and if normalized name is null, store the column name with data source name.*/
				else if(col.get_mergeAction() == null) {
					everyColumnName.add(dsName + SmartJoin.NAME_SEPARATOR + col.get_columnName());
				}
			}
			/*Add an extra field it identify the data source file name.*/
			everyColumnName.add(dsName + SmartJoin.NAME_SEPARATOR + SmartJoin.DS);
		}
		
		//System.out.println("everyColumnName: " + everyColumnName.toString());
		return everyColumnName;
	}
}
