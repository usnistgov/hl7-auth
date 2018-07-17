package gov.nist.hit.hl7.auth.config;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by gkatzioura on 10/5/16.
 */
public class CustomPasswordEncoder implements PasswordEncoder {
  @Override
  public String encode(CharSequence rawPassword) {

    String hashed = rawPassword.toString();
    return hashed;
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {

    return rawPassword.equals(encodedPassword);
  }
}
