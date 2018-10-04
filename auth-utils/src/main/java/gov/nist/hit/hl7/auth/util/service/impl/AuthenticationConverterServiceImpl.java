/**
 * This software was developed at the National Institute of Standards and Technology by employees of
 * the Federal Government in the course of their official duties. Pursuant to title 17 Section 105
 * of the United States Code this software is not subject to copyright protection and is in the
 * public domain. This is an experimental system. NIST assumes no responsibility whatsoever for its
 * use by other parties, and makes no guarantees, expressed or implied, about its quality,
 * reliability, or any other characteristic. We would appreciate acknowledgement if the software is
 * used. This software can be redistributed and/or modified freely provided that any derivative
 * works bear some notice that they are derived from it, and any modified versions bear some notice
 * that they have been modified.
 */
package gov.nist.hit.hl7.auth.util.service.impl;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import gov.nist.hit.hl7.auth.util.requests.UserResponse;
import gov.nist.hit.hl7.auth.util.service.AuthenticationConverterService;

/**
 * @author ena3
 *
 */
@Service
public class AuthenticationConverterServiceImpl implements AuthenticationConverterService {

  /*
   * (non-Javadoc)
   * 
   * @see gov.nist.hit.hl7.auth.util.service.AuthenticationConverterService#getAuthentication(org.
   * springframework.security.core.Authentication)
   */
  @Override
  public UserResponse getAuthentication(Authentication auth) {
    UserResponse response = new UserResponse();

    if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
      response.setUsername(auth.getName());
      for (GrantedAuthority a : auth.getAuthorities()) {
        response.addAuthority(a);
      }
    } else {
      response.setUsername("Guest");
    }
    return response;
  }

}
