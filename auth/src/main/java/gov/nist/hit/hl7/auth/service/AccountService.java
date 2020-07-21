package gov.nist.hit.hl7.auth.service;


import java.io.IOException;
import java.util.List;

import javax.security.sasl.AuthenticationException;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import gov.nist.hit.hl7.auth.domain.Account;
import gov.nist.hit.hl7.auth.domain.PasswordResetToken;
import gov.nist.hit.hl7.auth.domain.Privilege;
import gov.nist.hit.hl7.auth.domain.AccountLog;

@Service
public interface AccountService extends UserDetailsService {

  public Account getCurrentUser();

  public Account getAccountByUsername(String username);

  public Account createAdmin(Account account);

  public Account createNoramlUser(Account account);
  public Account updateNoramlUser(Account account);

  public Account createUser(Account account, Privilege p);

  public Privilege getPrivilegeByRole(String role);

  public Privilege createPrivilegeByRole(String role);

  public List<Account> findAll();

  public void deleteAll();

  boolean emailExist(String email);

  boolean userNameExist(String username);

  void createAccountsFromLegacy() throws IOException;

  public Account findByAccountId(Long accountID);

  public Account findByEmail(String email);

  public PasswordResetToken createPasswordResetTokenForUser(Account user, String token);

  boolean validateToken(String token) throws AuthenticationException;

  PasswordResetToken changePassword(String newPassword, String token)
      throws AuthenticationException;

  void createLog(AccountLog accountLog);

public Account makePending(Account account);

public Account relaxPending(Account account);

Account makeAdmin(Account account);

Account makeNoramlUser(Account account);
}
