package processors;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import models.Column;

import mainclass.SmartJoin;

public class CSVSchemaDiscovery implements ISchemaDiscoverable {
	/*Get the list of column names for a particular data source.*/
	public ArrayList<Column> discover(String connectionString) {
		DataInputStream dis = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		ArrayList<Column> columns = new ArrayList<Column>();

		try {
			fis = new FileInputStream(connectionString);
			bis = new BufferedInputStream(fis);
			dis = new DataInputStream(bis);

			/*Read the first line of the data source, as it contains the column names and store each column name in an array of strings.*/
			String columnTokens[] = dis.readLine().split(SmartJoin.VALUES_SEPARATOR);
			
			/*Store each column name in a Column variable, which has several other properties like normalized column name, type etc.*/
			for(int index = 0; index < columnTokens.length; index++) {
				Column column = new Column();
				column.set_columnName(columnTokens[index]);
				columns.add(column);
			}
		}

		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		catch (IOException e) {
			e.printStackTrace();
		}

		finally {
			try {
				fis.close();
				bis.close();
				dis.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return columns;
	}

}
