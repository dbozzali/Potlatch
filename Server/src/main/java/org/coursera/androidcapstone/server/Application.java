package org.coursera.androidcapstone.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.coursera.androidcapstone.common.repository.Gift;
import org.coursera.androidcapstone.common.repository.User;
import org.coursera.androidcapstone.server.oauth.OAuth2SecurityConfiguration;
import org.coursera.androidcapstone.server.json.ResourcesMapper;
import org.coursera.androidcapstone.server.repository.GiftRepository;
import org.coursera.androidcapstone.server.repository.UserRepository;

import org.hibernate.cfg.Environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

// Tell Spring to automatically inject any dependencies that are marked in
// our classes with @Autowired.
@EnableAutoConfiguration
// Tell Spring to automatically create a JPA implementation of our
// GiftRepository.
@EnableJpaRepositories(basePackageClasses = {GiftRepository.class, UserRepository.class})
// Tell Spring to turn on WebMVC (e.g., it should enable the DispatcherServlet
// so that requests can be routed to our Controllers).
@EnableWebMvc
// Tell Spring that this object represents a Configuration for the
// application.
@Configuration
// Tell Spring to go and scan our controller package (and all sub packages) to
// find any Controllers or other components that are part of our application.
// Any class in this package that is annotated with @Controller is going to be
// automatically discovered and connected to the DispatcherServlet.
@ComponentScan
// We use the @Import annotation to include our OAuth2SecurityConfiguration
// as part of this configuration so that we can have security and oauth
// setup by Spring.
@Import(OAuth2SecurityConfiguration.class)
// We use to Enable database transaction management for methods advised with the
// @Transactional annotation
@EnableTransactionManagement
public class Application extends RepositoryRestMvcConfiguration {
	// The app now requires that you pass the location of the keystore and
	// the password for your private key that you would like to setup HTTPS
	// with. In Eclipse, you can set these options by going to:
	//    1. Run->Run Configurations
	//    2. Under Java Applications, select your run configuration for this app
	//    3. Open the Arguments tab
	//    4. In VM Arguments, provide the following information to use the
	//       default keystore provided with the sample code:
	//
	//       -Dkeystore.file=src/main/resources/private/keystore -Dkeystore.pass=changeit
	//
	//    5. Note, this keystore is highly insecure! If you want more securtiy, you 
	//       should obtain a real SSL certificate:
	//
	//       http://tomcat.apache.org/tomcat-7.0-doc/ssl-howto.html
	//
	// Tell Spring to launch our app!
	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		Application.populateUsersRepository(context);
	}

    // This version uses the Tomcat web container and configures it to
	// support HTTPS. The code below performs the configuration of Tomcat
	// for HTTPS. Each web container has a different API for configuring
	// HTTPS. 
	//
	// The app now requires that you pass the location of the keystore and
	// the password for your private key that you would like to setup HTTPS
	// with. In Eclipse, you can set these options by going to:
	//    1. Run->Run Configurations
	//    2. Under Java Applications, select your run configuration for this app
	//    3. Open the Arguments tab
	//    4. In VM Arguments, provide the following information to use the
	//       default keystore provided with the sample code:
	//
	//       -Dkeystore.file=src/main/resources/private/keystore -Dkeystore.pass=changeit
	//
	//    5. Note, this keystore is highly insecure! If you want more securtiy, you 
	//       should obtain a real SSL certificate:
	//
	//       http://tomcat.apache.org/tomcat-7.0-doc/ssl-howto.html
	//
    @Bean
    EmbeddedServletContainerCustomizer containerCustomizer(
    		@Value("${keystore.file:src/main/resources/private/keystore}") String keystoreFile,
            @Value("${keystore.pass:changeit}") final String keystorePass) throws Exception {

		// If you were going to reuse this class in another
		// application, this is one of the key sections that you
		// would want to change
    	
        final String absoluteKeystoreFile = new File(keystoreFile).getAbsolutePath();

        return new EmbeddedServletContainerCustomizer () {
			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
				TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
		        tomcat.addConnectorCustomizers(
                    new TomcatConnectorCustomizer() {
						@Override
						public void customize(Connector connector) {
							connector.setPort(8443);
	                        connector.setSecure(true);
	                        connector.setScheme("https");

	                        Http11NioProtocol proto = (Http11NioProtocol) connector.getProtocolHandler();
	                        proto.setSSLEnabled(true);
	                        proto.setKeystoreFile(absoluteKeystoreFile);
	                        proto.setKeystorePass(keystorePass);
	                        proto.setKeystoreType("JKS");
	                        proto.setKeyAlias("tomcat");
						}
                    }
		        );
			}
        };
    }

	// We are overriding the bean that RepositoryRestMvcConfiguration 
	// is using to convert our objects into JSON so that we can control
	// the format. The Spring dependency injection will inject our instance
	// of ObjectMapper in all of the spring data rest classes that rely
	// on the ObjectMapper. This is an example of how Spring dependency
	// injection allows us to easily configure dependencies in code that
	// we don't have easy control over otherwise.
	@Override
	public ObjectMapper halObjectMapper(){
		return new ResourcesMapper();
	}

	@Override
	protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		config.exposeIdsFor(Gift.class, User.class);
	}

	private static final String DB_NAME = "potlatchdb";

	@Bean(destroyMethod="shutdown")
	public DataSource dataSource() {
		return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).setName(DB_NAME).build();
	}

	@Bean(destroyMethod="close")
	public EntityManagerFactory entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean factory =
			new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource());
		factory.setPersistenceUnitName(DB_NAME);
		//factory.setPackagesToScan(getClass().getPackage().getName());
		factory.setPackagesToScan("org.coursera.androidcapstone.server.repository",
								  "org.coursera.androidcapstone.common.repository",
								  "org.coursera.androidcapstone.common.client");
		factory.setJpaVendorAdapter(jpaAdapter());
		factory.setJpaProperties(jpaProperties());
		factory.afterPropertiesSet();
		return factory.getObject();
	}

	@Bean
	public JpaVendorAdapter jpaAdapter() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabase(Database.H2);
		// Database tables will be created/updated automatically due to this
		adapter.setGenerateDdl(true);
		adapter.setShowSql(true);
		return adapter;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new JpaTransactionManager(entityManagerFactory());
	}

	@Bean
	public HibernateExceptionTranslator exceptionTranslator() {
		return new HibernateExceptionTranslator();
	}

	private static void populateUsersRepository(ConfigurableApplicationContext context) {
		UserRepository userRepository = context.getBean(UserRepository.class);
		try {
			User aliceUser = new User("alice", "");
			Resource resource = context.getResource("classpath:icons/alice-avatar.png");
			InputStream inputStream = resource.getInputStream();
		    BufferedImage bufferedImage = ImageIO.read(inputStream);
		    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        ImageIO.write(bufferedImage, "png", bos);
	        byte[] avatarBytes = bos.toByteArray();
		    aliceUser.avatarFromByteArray(avatarBytes);
			userRepository.save(aliceUser);
			bos.close();
		}
		catch (IOException e) {
            e.printStackTrace();
        }
		try {
			User bobUser = new User("bob", "");
			Resource resource = context.getResource("classpath:icons/bob-avatar.png");
			InputStream inputStream = resource.getInputStream();
		    BufferedImage bufferedImage = ImageIO.read(inputStream);
		    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        ImageIO.write(bufferedImage, "png", bos);
	        byte[] avatarBytes = bos.toByteArray();
		    bobUser.avatarFromByteArray(avatarBytes);
			userRepository.save(bobUser);
			bos.close();
		}
		catch (IOException e) {
            e.printStackTrace();
        }
		try {
			User carolUser = new User("carol", "");
			Resource resource = context.getResource("classpath:icons/carol-avatar.png");
			InputStream inputStream = resource.getInputStream();
		    BufferedImage bufferedImage = ImageIO.read(inputStream);
		    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        ImageIO.write(bufferedImage, "png", bos);
	        byte[] avatarBytes = bos.toByteArray();
		    carolUser.avatarFromByteArray(avatarBytes);
			userRepository.save(carolUser);
			bos.close();
		}
		catch (IOException e) {
            e.printStackTrace();
        }
	}

	private Properties jpaProperties() {
		Properties properties = new Properties();
		properties.put(Environment.HBM2DDL_AUTO, "create");
		properties.put(Environment.HBM2DDL_IMPORT_FILES, "data.sql");
		properties.put(Environment.DIALECT, "org.hibernate.dialect.H2Dialect");
		properties.put(Environment.DRIVER,"org.h2.Driver");
		properties.put(Environment.SHOW_SQL, "true");
        properties.put(Environment.FORMAT_SQL, "true");
        properties.put(Environment.USE_SQL_COMMENTS, "true");
		properties.put("javax.persistence.provider", "org.hibernate.ejb.HibernatePersistence");
        properties.put("javax.persistence.transactionType", "RESOURCE_LOCAL");
		properties.put("javax.persistence.schema-generation.create-database-schemas", "true");
		properties.put("javax.persistence.schema-generation.scripts.action", "create");
		properties.put("javax.persistence.schema-generation.scripts.create-target", "sql/schema.sql");
		properties.put("javax.persistence.database-product-name", "H2");
		return properties;
	}
}
