package gov.nist.hit.hl7.auth.filter;

import java.io.IOException;
import java.security.Key;
import java.util.Collections;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nist.hit.hl7.auth.util.crypto.CryptoUtil;
import gov.nist.hit.hl7.auth.util.requests.ConnectionResponseMessage;
import gov.nist.hit.hl7.auth.util.requests.ConnectionResponseMessage.Status;
import gov.nist.hit.hl7.auth.util.requests.LoginRequest;
import gov.nist.hit.hl7.auth.util.requests.UserResponse;
import gov.nist.hit.hl7.auth.util.service.AuthenticationConverterService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTAuthenticationProcessingFilter extends AbstractAuthenticationProcessingFilter {


  @Autowired
  DaoAuthenticationProvider provider;

  @Autowired
  private CryptoUtil crypto;

  @Autowired
  private PasswordEncoder encoder;
  @Autowired
  AuthenticationConverterService authConverter;

  @Autowired
  Environment env;

  public JWTAuthenticationProcessingFilter(String url, AuthenticationManager authenticationManager) {
    super(new AntPathRequestMatcher(url));
    setAuthenticationManager(authenticationManager);

  }


  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response)
      throws AuthenticationException, JsonParseException, JsonMappingException, IOException {
    try {

      ObjectMapper mapper = new ObjectMapper().configure(
          com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

      LoginRequest creds = new LoginRequest();

      creds = mapper.readValue(request.getInputStream(), LoginRequest.class);
      Authentication ret =
          provider.authenticate(new UsernamePasswordAuthenticationToken(creds.getUsername(),
              creds.getPassword(), Collections.emptyList()));

      ObjectMapper mapper_ = new ObjectMapper();

      ConnectionResponseMessage<UserResponse> resp =
          new ConnectionResponseMessage<UserResponse>(Status.SUCCESS, "Login Successfull",
              "Login Successfull", null, false, new Date(), authConverter.getAuthentication(ret));
      String responseString1 = mapper_.writeValueAsString(resp);

      response.getWriter().write(responseString1);
      response.setContentType("application/json");
      return ret;
    } catch (Exception e) {
      response.setStatus(403);
      ConnectionResponseMessage<UserResponse> responseMessage =
          new ConnectionResponseMessage<UserResponse>(Status.FAILED, "Authentication Error",
              e.getLocalizedMessage(), null, false, new Date(), null);


      ObjectMapper mapper = new ObjectMapper();
      String responseString = mapper.writeValueAsString(responseMessage);
      response.getWriter().write(responseString);
      response.setContentType("application/json");
      return null;

    }


  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain, Authentication authResult) throws IOException, ServletException {
    User springUser = (User) authResult.getPrincipal();

    String jwt;
    try {
      Key privateKey = crypto.priv(env.getProperty("key.private"));
      jwt = Jwts.builder().setSubject(springUser.getUsername())
          .setExpiration(new Date(System.currentTimeMillis()
              + gov.nist.hit.hl7.auth.util.crypto.SecurityConstants.EXPIRATION_DATE))
          .signWith(SignatureAlgorithm.RS256, privateKey)
          .claim("roles", springUser.getAuthorities()).compact();
      response.addHeader("Authorization", jwt);

    } catch (Exception e) {
      ConnectionResponseMessage<UserResponse> responseMessage =
          new ConnectionResponseMessage<UserResponse>(Status.FAILED, "Authentication Error",
              e.getLocalizedMessage(), null, false, new Date(), null);


      ObjectMapper mapper = new ObjectMapper();
      String responseString = mapper.writeValueAsString(responseMessage);
      response.reset();
      response.setStatus(403);

      response.getWriter().write(responseString);
      response.setContentType("application/json");

    }
  }
}
