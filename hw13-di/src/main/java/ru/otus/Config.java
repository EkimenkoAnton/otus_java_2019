package ru.otus;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.otus.domain.Account;
import ru.otus.domain.Address;
import ru.otus.domain.Phone;
import ru.otus.domain.User;
import ru.otus.services.dbservice.DBService;
import ru.otus.services.dbservice.HibernateDBService;
import ru.otus.services.executor.Executor;


@Configuration
@ComponentScan
@EnableWebMvc
public class Config implements WebMvcConfigurer {

    private static final String HIBERNATE_CONFIG = "hibernate.cfg.xml";

    private final ApplicationContext applicationContext;

    public Config(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("/")
                .setCachePeriod(0);
    }

    @Bean
    public DBService getDBService() {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration()
                .configure(HIBERNATE_CONFIG);
        StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

        Metadata metadata = new MetadataSources(serviceRegistry)
                .addAnnotatedClass(Account.class)
                .addAnnotatedClass(Address.class)
                .addAnnotatedClass(Phone.class)
                .addAnnotatedClass(User.class)

                .getMetadataBuilder()
                .build();
        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
        Executor executor = new Executor(sessionFactory);
        return new HibernateDBService(executor);
    }
}
