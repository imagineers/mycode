package mainclass;

import java.util.ArrayList;

import models.DataSource;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import processors.DataMapper;
import processors.DataReducer;
import processors.IJoinConfigurable;
import processors.XmlJoinConfiguration;

public class SmartJoin {
	public static final String DS = "DS";
	private static final String _JOB_NAME = "smartjoin";
	private static final String _OUTPUT_PATH = "output/smartjoinoutputBasDadaSource";
	public static final String NAME_SEPARATOR = "_";
	public static final String VALUES_SEPARATOR = "\t";
	
	public static void main(String []args) throws Exception {
		Job job = new Job();
		
		//Taking path of xml file as command line argument : args[0] = "src/processors/MapperConfiguration.xml";
		IJoinConfigurable iConfig = new XmlJoinConfiguration("src/processors/MapperConfiguration.xml");
		/*Parse the xml file.*/
		iConfig.load();
		
		/*Get the list of all data sources.*/
		ArrayList<DataSource> ds = iConfig.getAllDataSources();
		
		job.setJobName(_JOB_NAME);
		job.setJarByClass(SmartJoin.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		/*Set the configuration variable of data mapper class.*/
		DataMapper.setIJoinConfigurable(iConfig);
		/*Set the configuration variable of data reducer class.*/
		DataReducer.setIJoinConfigurable(iConfig);
		
		job.setMapperClass(DataMapper.class);
		job.setReducerClass(DataReducer.class);
		
		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		for(int index = 0; index < ds.size(); index++) {
			FileInputFormat.addInputPath(job, new Path(ds.get(index).get_connectionString()));
		}
		
		FileOutputFormat.setOutputPath(job, new Path(_OUTPUT_PATH));
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
