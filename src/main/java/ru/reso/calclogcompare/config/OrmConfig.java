package ru.reso.calclogcompare.config;

import java.util.Properties;
import javax.sql.DataSource;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring's configuration for beans that are related to ORM and data access.
 *
 * @author ROMAB
 */
@Configuration
@EnableTransactionManagement
@ComponentScan({ "ru.reso.calclogcompare" })
@EnableJpaRepositories(basePackages = "ru.reso.calclogcompare.repository")
public class OrmConfig {

    /**
     * Дефолтный конструктор.
     */
    public OrmConfig() {
        super();
    }


    /**
     * Hibernate's properties.
     */

    /**
     * Диалект. В нашем случае - Оракл.
     */
    @Value("${hibernate.dialect}")
    private String hibernateDialect;

    @Value("${db.driverClassName}")
    private String driverClassName;

    @Value("${db.url}")
    private String url;

    @Value("${db.username}")
    private String username;
    @Value("${db.password}")
    private String password;


    /**
     * Показывать sql в консоле или нет.
     */
    @Value("${hibernate.show_sql}")
    private String hibernateShowSql;

    /**
     * Дефолтная схема БД.
     */
    @Value("${db.defaultschema}")
    private String defaultSchema;

    /**
     * Мерзкий параметр, который
     * наделал нам делов.
     * Его всегда делать validate,
     * иначе поудаляет к черту
     * все таблицы из БД.
     */
    @Value("${hibernate.hbm2ddl.auto}")
    private String hbm2dll;


   /* *//**
     * Привязываем Датасарс. В данном
     * случае уже вяжемся через JNDI,
     * то есть через GlassFish
     * Connection Pools.
     *
     * @return the data source
     *//*
    @Bean
    public DataSource dataSource() {
        final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
        dsLookup.setResourceRef(true);
        DataSource dataSource = dsLookup.getDataSource("OSAGO");
        return dataSource;
    }*/


    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }



    /**
     * Энтити Менеджер. Нужен для работы
     * JPARepository и в частности
     * '@Query'.
     * @return  - бин фабрики.
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[] {"ru.reso.calclogcompare.model"});

        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(hibernateProperties());

        return em;
    }

    /**
     * Менеджер транзакций. Нужен для
     * Хибернейта.
     * @return - менеджер.
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    /**
     * Не знаю что это. Добавлено
     * для JPARepository.
     *
     * @return - нечто.
     */
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }



    /**
     * Выставляем Hibernate свойства. Все по стандарту.
     *
     * @return the data source
     */
    @Bean
    public Properties hibernateProperties() {
        final Properties properties = new Properties();
        properties.put("hibernate.dialect", hibernateDialect);
        properties.put("hibernate.show_sql", hibernateShowSql);
        properties.put("hibernate.default_schema", defaultSchema);
        properties.put("hibernate.hbm2ddl.auto", hbm2dll);
        return properties;
    }


    /**
     * Создаем фабрику сессий.
     *
     * @param dataSource - датасорс наш.
     * @return - фабрика сессий.
     *//*
    @Bean(name = "sessionFactory")
    public SessionFactory getSessionFactory(final DataSource dataSource) {
        LocalSessionFactoryBuilder sessionBuilder = new LocalSessionFactoryBuilder(dataSource);
        sessionBuilder.addProperties(hibernateProperties());
        sessionBuilder.addAnnotatedClasses(Premium.class);
        return sessionBuilder.buildSessionFactory();
    }

    *//**
     * Транзакции для Хибернейта.
     *
     * @param sessionFactory - фабрика сессий Хибернейта
     * @return - HibernateTransactionManager.
     *//*
    @Bean
    public HibernateTransactionManager transactionManager(final SessionFactory sessionFactory) {
        final HibernateTransactionManager htm =
                new HibernateTransactionManager();
        htm.setSessionFactory(sessionFactory);
        return htm;
    }*/


}
