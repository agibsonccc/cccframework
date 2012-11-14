package com.ccc.util.springhibernate.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.lang.annotation.*;
import java.lang.reflect.Field;

import org.hibernate.LockMode;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.util.Assert;

import com.ccc.util.springhibernate.model.AbstractModel;

import javax.persistence.Table;
/**
 * This is a generic dao class that supports basic operations on a database.
 * @author Adam Gibson
 *
 * @param <E> the type of element to manipulate in the database
 */
public abstract class GenericManager<E> extends HibernateDaoSupport 
implements Manager <E>, java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1079145750839299694L;

	public GenericManager(Class<? extends E> theClass){
		this.clazz = theClass;
	}


	public List<E> elementsWithValue(String value) {
		if(!(clazz.isAssignableFrom(AbstractModel.class)))
			throw new IllegalStateException("Model managed must extend AbstractModel");
		List<E> elements=allElements();
		List<E> ret = new ArrayList<E>();
		//find the column name
		String columnName=guessColumnName(value,elements);
		if(columnName==null)
			return null;
		return elementsWithValue(columnName,value);

	}

	private String guessColumnName(Object value,List<E> elements) {
		String columnName=null;
		for(E e : elements) {
			AbstractModel a=(AbstractModel) e;
			try {
				Field f=a.fieldWithValue(value);
				if(f==null)
					continue;
				columnName=a.columnNameForValue(value);


			} catch (IllegalArgumentException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return columnName;
	}

	/**
	 * Gets a unique element with an id, assumes column name is id
	 * @param id the value of the id
	 * @return a unique element or null if none exists
	 */
	public E getElementWithId(String id) {
		return getElementWithId(id,"id");
	}
	public E getElementWithId(String id,String idColumnName) {
		List<E> elements=elementsWithValue(idColumnName,id);
		if(elements!=null && !elements.isEmpty())
			return elements.get(0);
		return null;
	}

	public Class<? extends E> getType() {
		return this.clazz;
	}

	@Autowired(required=false)
	public void init(SessionFactory factory) {
		setSessionFactory(factory);
		HibernateUtil.setSessionFactory(factory);
	}
	/**
	 * This will return the most recent auto increment value
	 * relative to the class this manager is for.
	 * 
	 * @return -1 if a valid value isn't found,otherwise
	 * the most recent id for auto increment
	 */
	public int getLastAutoIncrement() {
		Session session=super.getSession();
		Object ret  =  session.createSQLQuery("SELECT LAST_INSERT_ID()")
				.uniqueResult();

		if(ret instanceof BigInteger) {
			BigInteger id=(BigInteger) ret;
			int lastId=id.intValue();
			return lastId;
		}
		else if(ret instanceof Integer) {
			return (Integer) ret;
		}
		return -1;
	}

	/**
	 * This creates the beginning of a query based on the class of 
	 * the generic manager
	 * @param selection the selection string (table name)
	 * @return select {select} from clazz table name
	 */
	protected String beginSelect(String selection) {
		Annotation[] anns=clazz.getAnnotations();
		String ret="select " + selection + " from ";
		for(Annotation ann : anns) {
			if(ann instanceof Table) {
				Table t=(Table) ann;
				String name=t.name();
				ret+=name;
			}
		}
		return ret;
	}//end beginSelect

	/**
	 * This saves the given element to the database.
	 * @param brand the brand to save
	 * @return true if successful, false otherwise
	 * @throws IllegalArgumentException if element is null
	 */

	public boolean saveE(E e) throws IllegalArgumentException {
		Assert.notNull(e);
		Assert.notNull(getHibernateTemplate());
		try {
			if(log.isDebugEnabled()) {
				log.debug("Saving {}",e);
			}
			getHibernateTemplate().saveOrUpdate(e);
			return true;
		}catch(Exception e1){
			log.error("Unable to save entity",e1);
			e1.printStackTrace();
			return false;
		}
	}//end saveE

	/**
	 * This executes the given sql query on the server.
	 * WARNING: This assumes the existence of a session factory. 
	 * @param query the query to execute
	 * @throws IllegalArgumentException if query is null or empty
	 */

	public Object executeSQLQuery(String query,int type) throws IllegalArgumentException  {
		Assert.notNull(query);
		Assert.hasLength(query);
		SessionFactory s=HibernateUtil.getSessionFactory();
		if(log.isDebugEnabled()) {
			log.debug("Obtained session factory");
		}

		Session session =
				s.openSession();
		if(log.isDebugEnabled()) {
			log.debug("Obtained session");
		}

		Transaction tx = session.beginTransaction();
		SQLQuery query2=session.createSQLQuery(query);
		switch(type) {
		case INSERT: 
			int update=query2.executeUpdate();
			return update;

		case UPDATE:
			int update2=query2.executeUpdate();
			return update2;

		case DELETE:
			int delete=query2.executeUpdate();
			return delete;
		case READ:
			return query2.list();
		}


		tx.commit();
		if(log.isDebugEnabled()) {
			log.debug("Commited transaction");
		}
		//error
		return null;
	}//end executeSQLQuery

	/**
	 * This deletes all of the elements from a managed table.
	 */

	public void deleteAll() {
		getHibernateTemplate().deleteAll(allElements());
	}//end deleteAll

	/**
	 * This retrieves a list of elements for a given column
	 * @param columnName the column name to grab
	 * @return the elements from the given column
	 * @throws IllegalArgumentException if columnName is null or empty
	 */

	public List<E> elementsForColumn(String columnName) throws IllegalArgumentException {
		String query="select " + columnName + "from " + clazz.getSimpleName();
		return getHibernateTemplate().find(query);	
	}


	/**
	 * This retrieves a list of elements for a given column
	 * @param columnName the column name to grab
	 * @param value the value to check for
	 * @return the elements from the given column
	 * @throws IllegalArgumentException if columnName  or value is null or empty
	 */
	@SuppressWarnings("unchecked")

	public List<E> elementsWithValue(String columnName,String value) throws IllegalArgumentException {
		String query="from " + clazz.getSimpleName() + " where " +  columnName + "='" + value + "'"; 
		try {
			List<E> ret= getHibernateTemplate().find(query);	
			return ret;
		}catch(Exception e) {
			if(log.isDebugEnabled()) {
				log.debug("Exception attempting query for column name {} and value {}  with exception {}returning null",new Object[]{columnName,value,e});
			}
			e.printStackTrace();

			return null;
		}
	}

	/**
	 * This loads all of the elements from the given table from the database.
	 * 
	 * @return a list of all the elements in a table specified by the model.
	 */
	@SuppressWarnings("unchecked")

	public List<E> allElements() {
		String query="from " + clazz.getSimpleName();
		return getHibernateTemplate().find(query);
	}//end allElements

	/**
	 * This deletes the given element from the database.
	 * @param e the element to delete
	 * @return true if the element was saved, false otherwise
	 * @throws IllegalArgumentException if e is null
	 */

	public boolean deleteE(E e) throws IllegalArgumentException {
		Assert.notNull(e);
		try {
			getHibernateTemplate().delete(e);
			return true;
		}catch(Exception e1){
			e1.printStackTrace();
			return false;
		}
	}//end deleteE
	/**
	 * This updates the given element in the database.
	 * @param e the element to update
	 * @return true if the element was update, false otherwise
	 * @throws IllegalArgumentException if e is null
	 */

	public boolean updateE(E e) throws IllegalArgumentException {
		Assert.notNull(e);
		try {
			getHibernateTemplate().update(e, LockMode.PESSIMISTIC_WRITE);
			return true;
		}
		catch(Exception e1){
			e1.printStackTrace();
			return false;
		}
	}//end updateE

	//This is a helper variable for getting the class.
	protected Class<? extends E> clazz;
	private static Logger log=LoggerFactory.getLogger(GenericManager.class);
	@Autowired(required=false)
	private HibernateTemplate hibernateTemplate;

}//end GenericManager
