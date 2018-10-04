package gov.nist.hit.hl7.auth.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nist.hit.hl7.auth.util.crypto.SecurityConstants;
import gov.nist.hit.hl7.auth.util.requests.ConnectionResponseMessage;
import gov.nist.hit.hl7.auth.util.requests.ConnectionResponseMessage.Status;
import gov.nist.hit.hl7.auth.util.requests.UserResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws ServletException, IOException {
    // TODO Auto-generated method stub
    try {
      String jwt = null;
      Cookie token = WebUtils.getCookie(request, "authCookie");
      if (token != null) {
        jwt = token.getValue();
      }

      if (jwt == null || !jwt.startsWith(SecurityConstants.TOKEN_PREFIX)) {
        chain.doFilter(request, response);
        return;
      }
      Claims claims = Jwts.parser().setSigningKey(SecurityConstants.SECRET)
          .parseClaimsJws(jwt.replace(SecurityConstants.TOKEN_PREFIX, "")).getBody();
      String username = claims.getSubject();
      ArrayList<Map<String, String>> roles = (ArrayList<Map<String, String>>) claims.get("roles");

      Collection<GrantedAuthority> authorities = new ArrayList<>();
      roles.forEach(r -> {
        authorities.add(new SimpleGrantedAuthority(r.get("authority")));
      });

      chain.doFilter(request, response);

    } catch (Exception e) {
      // TODO: handle exception

      response.setStatus(403);
      ConnectionResponseMessage<UserResponse> responseMessage =
          new ConnectionResponseMessage<UserResponse>(Status.FAILED, "Authentication Error",
              e.getLocalizedMessage(), null, false, new Date(), null);


      ObjectMapper mapper = new ObjectMapper();
      String responseString = mapper.writeValueAsString(responseMessage);
      response.getWriter().write(responseString);
      response.setContentType("application/json");
    }
  }
}
