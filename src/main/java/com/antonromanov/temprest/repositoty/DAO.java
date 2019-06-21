package com.antonromanov.temprest.repositoty;

import com.antonromanov.temprest.model.Logs;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

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
	}

	public void testInsert() {

		StoredProcedureQuery query = entityManager.createStoredProcedureQuery("postgres.arduino.addlog")
				.registerStoredProcedureParameter("addlog", String.class, ParameterMode.OUT)
				.registerStoredProcedureParameter("temp", Long.class, ParameterMode.IN).setParameter("temp", 174L);

		query.execute();
	}




}
