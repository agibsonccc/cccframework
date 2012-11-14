package com.ccc.util.springhibernate.dao;

import java.util.List;
/**
 * This is a generic dao interface for a database.
 * @author Adam Gibson
 *
 * @param <E>
 */
public interface Manager<E> {
	/**
	 * This will delete all of the managed elements
	 */
	public void deleteAll();
	/**
	 * Gets a unique element with an id, assumes column name is id
	 * @param id the value of the id
	 * @return a unique element or null if none exists
	 */
	public E getElementWithId(String id);
	/**
	 * Gets a unique element with an id
	 * @param id the value of the id
	 * @param idColumnName the column name of the id
	 * @return a unique element or null if none exists
	 */
	public E getElementWithId(String id,String idColumnName);
	/**
	 * This saves the given element to the database.
	 * @param brand the brand to save
	 * @return true if successful, false otherwise
	 * @throws IllegalArgumentException if element is null
	 */
	public boolean saveE(E e) throws IllegalArgumentException;


	/**
	 * This loads all of the elements from the given table from the database.
	 * 
	 * @return a list of all the elements in a table specified by the model.
	 */
	public List<E> allElements();

	/**
	 * This deletes the given element from the database.
	 * @param e the element to delete
	 * @return true if the element was saved, false otherwise
	 * @throws IllegalArgumentException if e is null
	 */
	public boolean deleteE(E e) throws IllegalArgumentException;
	/**
	 * This updates the given element in the database.
	 * @param e the element to update
	 * @return true if the element was update, false otherwise
	 * @throws IllegalArgumentException if e is null
	 */
	public boolean updateE(E e) throws IllegalArgumentException;	//This is a helper variable for getting the class.

	/**
	 * This retrieves a list of elements for a given column
	 * @param columnName the column name to grab
	 * @return the elements from the given column
	 * @throws IllegalArgumentException if columnName is null or empty
	 */
	public List<E> elementsForColumn(String columnName) throws IllegalArgumentException;


	/**
	 * This retrieves a list of elements for a given column
	 * @param columnName the column name to grab
	 * @param value the value to check for
	 * @return the elements from the given column
	 * @throws IllegalArgumentException if columnName  or value is null or empty
	 */
	public List<E> elementsWithValue(String columnName,String value) throws IllegalArgumentException;


	public static int UPDATE=1;
	public static int INSERT=2;
	public static int DELETE=3;
	public static int READ=4;
}//end Manager
