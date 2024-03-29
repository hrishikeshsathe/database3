package edu.buffalo.cse562.operators;

import java.util.ArrayList;
import java.util.HashMap;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.schema.Table;
import edu.buffalo.cse562.utility.Schema;
import edu.buffalo.cse562.utility.StringUtility;
import edu.buffalo.cse562.utility.Tuple;
import edu.buffalo.cse562.utility.Utility;

public class HashJoinOperator implements Operator{

	Operator leftChild;
	Operator rightChild;
	Operator parent;
	Expression leftColumn;
	Expression rightColumn;
	ArrayList<Tuple> leftTuples;
	Tuple rightTuple;
	Schema leftTableSchema;
	Schema rightTableSchema;
	Table table;
	private HashMap<String,ArrayList<Tuple>> hashIndex;
	static int index = 0;

	public HashJoinOperator(Operator leftOperator,
			Operator rightOperator,Expression leftColumn, Expression rightColumn) {

		this.leftChild = leftOperator;
		this.rightChild = rightOperator;
		this.leftColumn = leftColumn;
		this.rightColumn = rightColumn;
		this.leftTableSchema = Utility.tableSchemas.get(leftOperator.getTable().getName());
		this.rightTableSchema = Utility.tableSchemas.get(rightOperator.getTable().getName());
		createNewJoinSchema(leftOperator.getTable(), rightOperator.getTable());

	}
	private void populateHashIndex() {

		hashIndex=new HashMap<String, ArrayList<Tuple>>();
		Tuple leftTuple;
		String key;
		String columnName = leftColumn.toString();
		int columnIndex = leftTableSchema.getColumns().get(columnName);
		leftTuple = leftChild.readOneTuple();


		while(leftTuple != null)
		{

			key = leftTuple.get(columnIndex).toString();
			ArrayList<Tuple> tuples;
			if(!hashIndex.containsKey(key))
			{
				tuples = new ArrayList<Tuple>();
				tuples.add(leftTuple);
				hashIndex.put(key, tuples);
			}
			else
			{
				tuples = hashIndex.get(key);
				tuples.add(leftTuple);
				hashIndex.put(key, tuples);
			}
			leftTuple = leftChild.readOneTuple();
		}//end of else

	}
	@Override
	public void reset() {
		leftChild.reset();
		rightChild.reset();
	}

	@Override
	public Tuple readOneTuple() {

		if(hashIndex == null)
			populateHashIndex();
		String key;
		String columnName = rightColumn.toString();
		int columnIndex = rightTableSchema.getColumns().get(columnName);
		if(leftTuples != null){
			index++;
			if(index < leftTuples.size())
				return joinTuple(leftTuples.get(index), rightTuple);
			else
				index = 0;
		}
		rightTuple = rightChild.readOneTuple();
		while(rightTuple != null){

			key = rightTuple.get(columnIndex).toString();
			if(hashIndex.containsKey(key))
			{
				leftTuples = hashIndex.get(key);
				return joinTuple(leftTuples.get(index),rightTuple);
			}
			rightTuple = rightChild.readOneTuple();
		}
		return null;
	}

	private Tuple joinTuple(Tuple leftTuple, Tuple rightTuple) {

		ArrayList<LeafValue> newTuple = new ArrayList<LeafValue>();
		newTuple.addAll(leftTuple.getTuple());
		newTuple.addAll(rightTuple.getTuple());
		return new Tuple(newTuple);
	}
	@Override
	public Table getTable() {
		return table;
	}

	private void createNewJoinSchema(Table leftTable, Table rightTable) {
		this.table = new Table();
		String newTableName = leftTable.getAlias() + StringUtility.JOIN + rightTable.getAlias();
		this.table.setName(newTableName);
		this.table.setAlias(newTableName);
		Schema newSchema = new Schema(table);
		HashMap<String, Integer> columns = new HashMap<String, Integer>();

		for(String column: leftTableSchema.getColumns().keySet()){
			if(column.contains(StringUtility.DOT))
				columns.put(column, leftTableSchema.getColumns().get(column));
			else
				columns.put(leftTable.getAlias() + StringUtility.DOT + column, leftTableSchema.getColumns().get(column));
		}

		for(String column: rightTableSchema.getColumns().keySet()){
			if(column.contains(StringUtility.DOT))
				columns.put(column, rightTableSchema.getColumns().get(column) + leftTableSchema.getColumns().size());
			else
				columns.put(rightTable.getAlias() + StringUtility.DOT + column, rightTableSchema.getColumns().get(column) + leftTableSchema.getColumns().size());
		}

		newSchema.setColumns(columns);
		Utility.tableSchemas.put(table.getName(), newSchema);
	}


	@Override
	public Operator getLeftChild() {
		return this.leftChild;
	}
	@Override
	public Operator getRightChild() {
		return this.rightChild;
	}
	@Override
	public Operator getParent() {
		return this.parent;
	}
	@Override
	public void setLeftChild(Operator leftChild) {
		this.leftChild = leftChild;
	}
	@Override
	public void setRightChild(Operator rightChild) {
		this.rightChild = rightChild;
	}
	@Override
	public void setParent(Operator parent) {
		this.parent = parent;
	}
	public Expression getLeftColumn() {
		return this.leftColumn;
	}
	public Expression getRightColumn() {
		return this.rightColumn;
	}	

}
