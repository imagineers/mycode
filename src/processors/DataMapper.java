/*
 * Steps followed in mapper:
 * 1) First check if the key == 0 (which signifies that the values contains the column names and not the value for the columns), so ignore it.
 * 2) Separate the values with "\t" as the splitter and store the values in the array of strings.
 * 3) Get the pivot column index and use it to identify the output key for mapper.
 * 4) Put thses values in the json object, which will be the output of mapper, along with their column values as key of json object.
 * 5) Add one more pair in the json object, with "DS" as the key and data source file name as value.
 * 6) Write the json object in he context.
 */

package processors;

import java.io.IOException;
import java.net.URI;

import models.DataSource;

import org.json.simple.JSONObject;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import mainclass.SmartJoin;

public class DataMapper extends Mapper<LongWritable, Text, Text, Text> {
	private static IJoinConfigurable _iConfig;
	
	/*Initialise the configuration variable.*/
	public static void setIJoinConfigurable(IJoinConfigurable iConfig) {
		_iConfig = iConfig;
	}
	
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {		
		
		String line = value.toString();		
		/*Get the data source file name for each split.*/
		InputSplit inputSplit = context.getInputSplit();
		FileSplit fileSplit = (FileSplit)inputSplit;
		String fileName = fileSplit.getPath().getName();
		
		URI[] configurFile = context.getCacheFiles();
		if(configurFile!=null){
			System.out.println(configurFile[0]);
		}
		
		DataSource ds = new DataSource();
		
		/*Get the data source object for each data source file name.*/
		for(DataSource eachDataSource : _iConfig.getAllDataSources()) {
			if(eachDataSource.doesDatasourceMatch(fileName) == true) {
				ds = eachDataSource;
				break;
			}
		}
		
		/*Return if key is the first line of the data source, as it contains the column names.*/
		if(key.get() == 0) {
			return;
		}
		
		String columnVals[] = line.split(SmartJoin.VALUES_SEPARATOR);
		String outKey = "";
		
		JSONObject jsonObj = new JSONObject();
		/*Add the key and value pair i.e. "DS" and data source file name in the json object, which is the mapper output.*/
		jsonObj.put(SmartJoin.DS, fileName);
		
		int keyColumnIndex = ds.get_pivotIndex();
		
		/*Store each value extracted from the "value" parameter of the map() method along with its column name into the json object.*/
		
		
		for(int columnIndex = 0; columnIndex < columnVals.length; columnIndex++) {
			String columnName = "";
			if(columnIndex == keyColumnIndex) {
				outKey = columnVals[columnIndex];
			}
			if(ds.get_columns().get(columnIndex).get_normalizedColumnName() == null) {
				columnName = ds.get_columns().get(columnIndex).get_columnName();
			}
			else {
				columnName = ds.get_columns().get(columnIndex).get_normalizedColumnName();
			}
			jsonObj.put(columnName, columnVals[columnIndex]);
			
			
		}
		
//		System.out.println("DataMapper.java\nKey: " + outKey + "\nValue : " + jsonObj.toString());
		
		/*Write the json object into the context.*/
		context.write(new Text(outKey), new Text(jsonObj.toString()));
	}
}