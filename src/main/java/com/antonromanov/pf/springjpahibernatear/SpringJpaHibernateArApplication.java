package com.antonromanov.pf.springjpahibernatear;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Create schema:
 *
 * CREATE SCHEMA arduino AUTHORIZATION postgres;
 *
 * Create table:
 * CREATE TABLE arduino.temperature (id INTEGER DEFAULT nextval('arduino.temp_id_seq'::regclass) NOT NULL,  name VARCHAR(255),  temperature DOUBLE PRECISION, datecreated TIMESTAMP(0) WITHOUT TIME ZONE  ) WITH (oids = false);
 *
 * Trigger:
 *
 * CREATE TRIGGER update_user_date_oncreate BEFORE INSERT  ON arduino.temperature FOR EACH ROW  EXECUTE PROCEDURE public.update_user_created_date();
 *
 * Function for trigger:
 *
 * CREATE OR REPLACE FUNCTION arduino.update_user_created_date ()
 * RETURNS trigger AS
 * $body$
 * BEGIN
 * IF TG_OP='INSERT' THEN
 * NEW.datecreated = now();
 * END IF;
 * RETURN NEW;
 * END;
 * $body$
 * LANGUAGE 'plpgsql'
 * VOLATILE
 * CALLED ON NULL INPUT
 * SECURITY INVOKER
 * COST 100;
 *
 * Request:
 * http://localhost:8080//rest/users/add
 *
 * {
 * "temp": 2
 * }
 */



@EnableJpaRepositories(basePackages = "com.antonromanov.pf.springjpahibernatear.repository")
@SpringBootApplication
public class SpringJpaHibernateArApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringJpaHibernateArApplication.class, args);
	}
}
