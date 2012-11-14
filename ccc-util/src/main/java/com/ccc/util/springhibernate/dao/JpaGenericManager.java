package com.ccc.util.springhibernate.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class JpaGenericManager<E> implements Manager<E> {

	public JpaGenericManager(Class<? extends E> theClass) {
		this.clazz=theClass;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)

	public boolean saveE(E e) throws IllegalArgumentException {
		entityManager.persist(e);
		return true;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)

	public List<E> allElements() {
		Query q = entityManager.createQuery("SELECT h FROM " +
				clazz.getName() + " h");
		
		if(log.isDebugEnabled()) {
			log.debug("Getting all from: {}",clazz.getName());
		}
		return q.getResultList();
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)

	public boolean deleteE(E e) throws IllegalArgumentException {
		try {
			entityManager.detach(e);
			if(log.isDebugEnabled()) {
				log.debug("Successfully deleted entity: {}", clazz.getName());
			}
			return true;
		}
		catch(Exception e1) {
			log.error("Error deleting from: {} ",clazz.getName(),e);
			return false;
		}
		
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)

	public boolean updateE(E e) throws IllegalArgumentException {
		try {
		entityManager.refresh(e);
		return true;
		}catch(Exception e1) {
			log.error("Error udpating entity: {} ",clazz.getName(),e1);
			return false;
		}
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)

	public List<E> elementsForColumn(String columnName)
			throws IllegalArgumentException {
		Query q = entityManager.createQuery("SELECT h FROM " +
				clazz.getName() + " h");
		return q.getResultList();
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)

	public List<E> elementsWithValue(String columnName, String value)
			throws IllegalArgumentException {
		Query q = entityManager.createQuery("SELECT " + columnName + " FROM " +
				clazz.getName() + " h");
		return q.getResultList();
	}
	@Override
	public E getElementWithId(String id) {
		return getElementWithId(id,"id");
	}

	@Override
	public E getElementWithId(String id, String idColumnName) {
		List<E> elements=elementsWithValue(idColumnName,id);
		if(elements!=null && !elements.isEmpty())
			return elements.get(0);
		return null;
	}
	@Override
	@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)

	public void deleteAll() {

		entityManager.clear();
		entityManager.flush();
		if(log.isDebugEnabled()) {
			log.debug("Deleted all entities for: {}	",clazz.getClass());
		}
	}

	//This is a helper variable for getting the class.
	protected Class<? extends E> clazz;
	@Autowired(required=false)
	private EntityManager  entityManager;

	private static Logger log=LoggerFactory.getLogger(JpaGenericManager.class);




}
