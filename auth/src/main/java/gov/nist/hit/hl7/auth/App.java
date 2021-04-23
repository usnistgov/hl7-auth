package gov.nist.hit.hl7.auth;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.opencsv.exceptions.CsvValidationException;

import gov.nist.hit.hl7.auth.repository.PrivilegeRepository;
import gov.nist.hit.hl7.auth.service.AccountService;

@SpringBootApplication
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, MongoAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
@EnableMongoRepositories("gov.nist.hit.hl7")
@ComponentScan({ "gov.nist.hit.hl7" })
public class App extends SpringBootServletInitializer {

	private static final String EMAIL_PORT = "email.port";
	private static final String EMAIL_PROTOCOL = "email.protocol";
	private static final String EMAIL_HOST = "email.host";
	private static final String EMAIL_ADMIN = "email.admin";
	private static final String EMAIL_FROM = "email.from";
	private static final String EMAIL_SUBJECT = "email.subject";
	private static final String EMAIL_SMTP_AUTH = "email.smtp.auth";
	private static final String EMAIL_DEBUG = "email.debug";

	@Autowired
	PrivilegeRepository priviliges;

	@Autowired
	AccountService accountService;

	@Autowired
	Environment env;

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Bean
	public JavaMailSenderImpl mailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(env.getProperty(EMAIL_HOST));
		mailSender.setPort(Integer.valueOf(env.getProperty(EMAIL_PORT)));
		mailSender.setProtocol(env.getProperty(EMAIL_PROTOCOL));
		Properties javaMailProperties = new Properties();
		mailSender.setJavaMailProperties(javaMailProperties);
		return mailSender;
	}

	@Bean
	public org.springframework.mail.SimpleMailMessage templateMessage() {
		org.springframework.mail.SimpleMailMessage templateMessage = new org.springframework.mail.SimpleMailMessage();
		templateMessage.setFrom(env.getProperty(EMAIL_FROM));
		templateMessage.setSubject(env.getProperty(EMAIL_SUBJECT));
		return templateMessage;
	}

//	@Override
//	public void run(String... arg0) throws Exception {
//
//	}

	@PostConstruct
	void converAccounts() throws IOException, CsvValidationException {

		accountService.createAccountsFromLegacy();
	}
}
