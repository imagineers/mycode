package testing;

import static org.mockito.Mockito.*;
import org.apache.hadoop.io.*;
import org.junit.*;

import processors.*;

import java.io.IOException;

public class TestDataMapper {
	@Test
	public void processesValidRecord() throws IOException, InterruptedException {
		DataMapper data_mapper = new DataMapper();
		Text value = new Text("1	Divyanshu");
		
		DataMapper.Context context = mock(DataMapper.Context.class);
		
		//data_mapper.map(null, value, context);
		//verify(context).write(new Text("Divyanshu"), new IntWritable(1));
	}
	
	@Test
	public void ignoresMissingRecord() throws IOException, InterruptedException {
		DataMapper data_mapper = new DataMapper();
		Text value = new Text("2");
		
		DataMapper.Context context = mock(DataMapper.Context.class);
		
		//data_mapper.map(null, value, context);
		//verify(context, never()).write(any(Text.class), any(IntWritable.class));
	}
}
