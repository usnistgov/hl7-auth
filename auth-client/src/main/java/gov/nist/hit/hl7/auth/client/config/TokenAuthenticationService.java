package gov.nist.hit.hl7.auth.client.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import gov.nist.hit.hl7.auth.util.crypto.CryptoUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class TokenAuthenticationService {

  @Autowired
  private CryptoUtil crypto;

  @Autowired
  Environment env;


  public UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request)
      throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException,
      SignatureException, IllegalArgumentException, FileNotFoundException, NoSuchAlgorithmException,
      InvalidKeySpecException, IOException {

    Cookie token = WebUtils.getCookie(request, "authCookie");
	Cookie[] cookies = request.getCookies();
	if (cookies != null) {
		for (Cookie cookie : cookies) {
			System.out.println("==========================================");

			System.out.println(cookie.getName() + " ==" + cookie.getValue() );
			if (cookie.getName().equals("authCookie")) {
				System.out.println("Found Cookie");
				System.out.println(cookie.getValue());

			}
		}
	}


    if (token != null && token.getValue() != null && !token.getValue().isEmpty()) {
    	System.out.println("Found Cookie");
      Claims claims = Jwts.parser().setSigningKey(crypto.pub(env.getProperty("key.public")))
          .parseClaimsJws(token.getValue()).getBody();
      String username = claims.getSubject();
      System.out.println(username);
      ArrayList<Map<String, String>> roles = (ArrayList<Map<String, String>>) claims.get("roles");

      Collection<GrantedAuthority> authorities = new ArrayList<>();
      roles.forEach(r -> {
        System.out.println(r);
        authorities.add(new SimpleGrantedAuthority(r.get("authority")));
      });
      UsernamePasswordAuthenticationToken authenticatedUser =
          new UsernamePasswordAuthenticationToken(username, token.getValue(), authorities);
      return authenticatedUser;
    } else {
    	System.out.println("=========================NOT authenticated================================== ");
      return null;
    }
  }



}
