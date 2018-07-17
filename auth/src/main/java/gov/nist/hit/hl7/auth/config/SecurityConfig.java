package gov.nist.hit.hl7.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import gov.nist.hit.hl7.auth.filter.JWTAuthenticationFilter;
import gov.nist.hit.hl7.auth.filter.JWTAuthorizationFilter;
import gov.nist.hit.hl7.auth.service.AccountService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private AccountService authenticationService;
  @Autowired
  private PasswordEncoder encoder;
  @Autowired

  AuthenticationManager authenticationManager;


  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.authenticationProvider(authProvider());
  }

  @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Bean
  public DaoAuthenticationProvider authProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

    authProvider.setUserDetailsService(authenticationService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }



  @Bean
  public PasswordEncoder passwordEncoder() {


    PasswordEncoder encoder = new BCryptPasswordEncoder(11);
    return encoder;
  }


  @Bean
  protected JWTAuthenticationFilter loginFilter() throws Exception {
    return new JWTAuthenticationFilter("/api/login", authenticationManager);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // TODO Auto-generated method stub
    http.csrf().disable();
    http.formLogin().disable();
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.authorizeRequests().antMatchers("/api/login", "/register/").permitAll();
    http.authorizeRequests().antMatchers("/password/**").permitAll();



    // http.authorizeRequests().antMatchers("/api/**").fullyAuthenticated();

    http.addFilterBefore(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    http.addFilterBefore(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);


    // super.configure(http);
  }



}
