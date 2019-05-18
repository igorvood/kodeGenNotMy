package com.valapay.test


import com.mchange.v2.c3p0.ComboPooledDataSource
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.beans.PropertyVetoException
import java.util.*
import javax.sql.DataSource


@Configuration
@EnableJpaRepositories("com.valapay")
@EnableTransactionManagement
@PropertySource("\${propertySource:application.properties}")
@ComponentScan("com.valapay")
open class RunnerConfiguration {

    private val logger = LoggerFactory.getLogger(RunnerConfiguration::class.java)

    @Bean
    open fun dataSource(env: Environment): DataSource {
        val dataSource = ComboPooledDataSource()
        try {
            dataSource.driverClass = env.getProperty("jdbc.driverClassName")
        } catch (e: PropertyVetoException) {
            e.printStackTrace()
        }

        dataSource.jdbcUrl = env.getProperty("jdbc.url")
        dataSource.user = env.getProperty("jdbc.username")
        dataSource.password = env.getProperty("jdbc.password")
        dataSource.maxPoolSize = Integer.parseInt(env.getProperty("jdbc.max.pool.size")!!)
        dataSource.isTestConnectionOnCheckin = true
        dataSource.isTestConnectionOnCheckout = true
        return dataSource
    }


    @Bean(name = ["entityManagerFactory"])
    @Autowired
    open fun entityManagerFactory(dataSource: DataSource, env: Environment): LocalContainerEntityManagerFactoryBean {
        val factory = LocalContainerEntityManagerFactoryBean()
        factory.dataSource = dataSource
        val vendorAdapter = HibernateJpaVendorAdapter()
        factory.dataSource = dataSource
        factory.jpaVendorAdapter = vendorAdapter
        factory.setPackagesToScan("com.valapay")
        val jpaProperties = Properties()
        jpaProperties["hibernate.hbm2ddl.auto"] = env.getProperty("hibernate.hbm2ddl.auto")
        jpaProperties["hibernate.show_sql"] = env.getProperty("hibernate.show_sql")
        jpaProperties["hibernate.dialect"] = env.getProperty("hibernate.dialect")
        factory.setJpaProperties(jpaProperties)
        return factory
    }

    @Bean
    @Autowired
    open fun transactionManager(
        entityManagerFactory: LocalContainerEntityManagerFactoryBean
    ): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory.getObject()!!)
    }

    @Bean
    open fun vertx(): Vertx {
        return Vertx.vertx()
    }

}

