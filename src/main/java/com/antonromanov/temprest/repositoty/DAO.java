package com.antonromanov.temprest.repositoty;

import com.antonromanov.temprest.model.Logs;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
@Transactional
public class DAO {

	// Private fields

	// An EntityManager will be automatically injected from entityManagerFactory
	// setup on DatabaseConfig class.
	@PersistenceContext
	private EntityManager entityManager;


	public void create(Logs logs) {
		entityManager.persist(logs);
		return;
	}


}
