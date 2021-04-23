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

import gov.nist.hit.hl7.auth.filter.JWTAuthenticationProcessingFilter;
import gov.nist.hit.hl7.auth.client.config.JWTAuthenticationFilter;
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
  @Autowired
  private JWTAuthenticationFilter authFilter;

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
  protected JWTAuthenticationProcessingFilter loginFilter() throws Exception {
    return new JWTAuthenticationProcessingFilter("/api/tool/login", authenticationManager);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    // TODO Auto-generated method stub
    /*
    http.csrf().disable();
    http.formLogin().disable();
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    http.authorizeRequests().antMatchers("/api/tool/login", "/api/tool/register/").permitAll();
    http.authorizeRequests().antMatchers("/api/tool/password/**").permitAll();
    http.addFilterBefore(
            loginFilter(), UsernamePasswordAuthenticationFilter.class);
    http.addFilterBefore(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    */

    http.
        csrf().disable()
        .formLogin().disable()
        .authorizeRequests()
            .antMatchers("/api/tool/login").permitAll()
            .antMatchers("/api/tool/register").permitAll()
            .antMatchers("/api/tool/password/**").permitAll()
            .antMatchers("/api/tool/accountlog/**").permitAll()
            .antMatchers("/api/tool/activity/**").permitAll()
            .antMatchers("/api/tool/users").permitAll()
            .antMatchers("/api/tool/user/**").permitAll()
            .antMatchers("/api/tool/user/method/**").permitAll()
            .antMatchers("/api/account/**").permitAll()
            .antMatchers("/api/accountLog/**").permitAll()
            .antMatchers("/api/tool/adminUpdate").permitAll()
            .antMatchers("/api/login").permitAll()
            .antMatchers("/api/register").permitAll()
            .antMatchers("/api/password/**").permitAll()
            .antMatchers("/api/**").fullyAuthenticated()
            .and()
        .addFilterBefore(loginFilter(), UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class);

        /*
        .addFilterBefore(new JWTAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        */
  }



}
