package gov.nist.hit.hl7.auth;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import gov.nist.hit.hl7.auth.domain.Account;
import gov.nist.hit.hl7.auth.domain.Privilege;
import gov.nist.hit.hl7.auth.repository.AccountRepository;
import gov.nist.hit.hl7.auth.repository.PrivilegeRepository;
import gov.nist.hit.hl7.auth.service.AccountService;


@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, MongoAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableMongoRepositories("gov.nist.hit.hl7")
@ComponentScan({"gov.nist.hit.hl7"})
public class App implements CommandLineRunner {

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
  private AccountRepository accountRepository;
  
  
  @Autowired
  AccountService accountService;
  
  @Autowired
  Environment env;
  
  @Autowired
  private PasswordEncoder encoder;

  
  @Autowired
  PrivilegeRepository privilegeRepository;

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
    org.springframework.mail.SimpleMailMessage templateMessage =
        new org.springframework.mail.SimpleMailMessage();
    templateMessage.setFrom(env.getProperty(EMAIL_FROM));
    templateMessage.setSubject(env.getProperty(EMAIL_SUBJECT));
    return templateMessage;
  }

  @Override
  public void run(String... arg0) throws Exception {

  }

  @PostConstruct
  void createTestAccounts() {
//	  for(int i = 0; i < 77; i ++) {
//		  Account a = new Account();
//	      Privilege userPrevilege = privilegeRepository.findByRole("USER");
//	      if(userPrevilege == null) {
//	    	  userPrevilege = new Privilege();
//	    	  userPrevilege.setRole("USER");
//	    	  privilegeRepository.save(userPrevilege);
//	      }
//	      Privilege adminPrevilege = privilegeRepository.findByRole("ADMIN");
//	      if(adminPrevilege == null) {
//	    	  adminPrevilege = new Privilege();
//	    	  adminPrevilege.setRole("ADMIN");
//	    	  privilegeRepository.save(adminPrevilege);
//	      }
//	      
//		  a.setEmail("anyone" + i + "@nist.gov");
//		  a.setFullName("unknown Last" + i);
//		  a.setOrganization("ANYCOMPANY");
//		  a.setPassword(encoder.encode("Q1w2e3r4"));
//		  a.setPending(false);
//		  Set<Privilege> roles = new HashSet<Privilege>();
//		  roles.add(userPrevilege);
//		  a.setPrivileges(roles);
//		  a.setSignedConfidentialityAgreement(true);
//		  a.setUsername("testuser" + i);
//		  
//		  this.accountRepository.save(a);
//	}
  }
 
}
