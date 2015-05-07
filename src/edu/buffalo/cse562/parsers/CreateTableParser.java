package edu.buffalo.cse562.parsers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import edu.buffalo.cse562.utility.Schema;
import edu.buffalo.cse562.utility.StringUtility;
import edu.buffalo.cse562.utility.Utility;

public class CreateTableParser {

	/**Create Table Schema and store in a hash map as (TableName, Columns) 
	 * Also create the data type schema for the table
	 * @param statement
	 */
	public static void parseStatement(Statement statement){
		Table table = ((CreateTable) statement).getTable();
		table.setName(table.getName().toUpperCase());
		Utility.checkAndSetTableAlias(table);
		Schema schema = new Schema(table);
		HashMap<String, Integer> cols = new HashMap<String, Integer>();
		ArrayList<Integer> dataType = new ArrayList<Integer>();
		
		if(Utility.tableSchemas != null){
			@SuppressWarnings(StringUtility.UNCHECKED)
			List<ColumnDefinition> list = ((CreateTable)statement).getColumnDefinitions();
			for(int colIndex = 0; colIndex < list.size(); colIndex++){
				cols.put(table.getAlias() + StringUtility.DOT + list.get(colIndex).getColumnName(), colIndex);
				dataType.add(getDataType(list.get(colIndex).getColDataType().toString()));
			}
			list = null;
			schema.setColumns(cols);
			Utility.tableSchemas.put(table.getName(), schema);
			Utility.tableDataTypes.put(table.getName(), dataType);
		}//end if
	}//end parseStatement
	
	private static int getDataType(String dataType){
		switch(dataType){
		case StringUtility.INT:
			return 1;
		case StringUtility.DECIMAL:
		case StringUtility.DOUBLE:
			return 2;
		case StringUtility.DATE1: 
			return 3;
		case StringUtility.CHAR2: 
		case StringUtility.STRING: 
		case StringUtility.VARCHAR:
			return 4;
		default:
		{
			if(dataType.contains(StringUtility.CHAR1) || dataType.contains(StringUtility.CHAR2)){
				return 4;
			}
		}//default
		}
		return 0;
	}
}//end of class
