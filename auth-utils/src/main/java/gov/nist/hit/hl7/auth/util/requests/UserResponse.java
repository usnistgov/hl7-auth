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
package gov.nist.hit.hl7.auth.util.requests;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author ena3
 *
 */
public class UserResponse {
  /**
   * @param username2
   */
  public UserResponse(String username) {
    // TODO Auto-generated constructor stub
    this.username = username;
  }

  public UserResponse() {
    // TODO Auto-generated constructor stub
    super();
  }

  public List<String> getAuthorities() {
    return authorities;
  }

  public void setAuthorities(List<String> authorities) {
    this.authorities = authorities;
  }

  private String username;
  private List<String> authorities = new ArrayList<String>();

  private String email;
  private String fullName;
  private String organization;
  private boolean old;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void addAuthority(GrantedAuthority auth) {
    authorities.add(auth.getAuthority());
  }

  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getOrganization() {
    return organization;
  }
  public void setOrganization(String organization) {
    this.organization = organization;
  }
  public String getFullName() {
    return fullName;
  }
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

public boolean isOld() {
	return old;
}

public void setOld(boolean old) {
	this.old = old;
}
}
