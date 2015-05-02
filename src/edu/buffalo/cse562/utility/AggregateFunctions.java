package edu.buffalo.cse562.utility;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.LeafValue;
import net.sf.jsqlparser.expression.LeafValue.InvalidLeaf;
import net.sf.jsqlparser.expression.LongValue;

public class AggregateFunctions {

	/**
	 * Function that accepts two LeafValues and returns their sum
	 * @param a
	 * @param b
	 * @return
	 */
	public static LeafValue calculateSum(LeafValue a, LeafValue b){
		double sum = 0.0;
		try {
			if(b == null)
				sum = a.toDouble();
			else
				sum = a.toDouble() + b.toDouble();
		} catch (InvalidLeaf e) {
			e.printStackTrace();
		}
		return new DoubleValue(sum);
	}

	/**
	 * Function to accept two LeafValues and return the smaller one
	 * @param a
	 * @param b
	 * @return
	 */
	public static LeafValue getMinimum(LeafValue a,	LeafValue b) {
		double minimum = 0.0;
		try {
			if(b == null)
				minimum = a.toDouble();
			else
				minimum = Math.min(a.toDouble(), b.toDouble());
		} catch (InvalidLeaf e) {
			e.printStackTrace();
		}
		return new DoubleValue(minimum);
	}

	/**
	 * Function to accept two LeafValues and return the larger one
	 * @param a
	 * @param b
	 * @return
	 */
	public static LeafValue getMaximum(LeafValue a,	LeafValue b) {
		double maximum = 0.0;
		try {
			if(b == null)
				maximum = a.toDouble();
			else
				maximum = Math.max(a.toDouble(), b.toDouble());
		} catch (InvalidLeaf e) {
			e.printStackTrace();
		}
		return new DoubleValue(maximum);
	}

	/**
	 * Function to return count
	 * @param column
	 * @return
	 */
	public static LeafValue getCount(LeafValue column) {
		long count = 1L;
		if(column != null){
			try {
				count = column.toLong() + 1;
			} catch (InvalidLeaf e) {
				e.printStackTrace();
			}//end catch	
		}//end if
		return new LongValue(count);
	}//end getCount
}
