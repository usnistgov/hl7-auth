package gov.nist.hit.hl7.auth.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.security.sasl.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.nist.hit.hl7.auth.converter.OldAccount;
import gov.nist.hit.hl7.auth.converter.OldUser;
import gov.nist.hit.hl7.auth.domain.Account;
import gov.nist.hit.hl7.auth.domain.AccountLog;
import gov.nist.hit.hl7.auth.domain.PasswordResetToken;
import gov.nist.hit.hl7.auth.domain.Privilege;
import gov.nist.hit.hl7.auth.repository.AccountRepository;
import gov.nist.hit.hl7.auth.repository.PasswordResetTokenRepository;
import gov.nist.hit.hl7.auth.repository.PrivilegeRepository;
import gov.nist.hit.hl7.auth.repository.AccountLogRepository;
import gov.nist.hit.hl7.auth.service.AccountService;

@Service("accountService")
public class AccountServiceImpl implements AccountService {

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private PasswordResetTokenRepository passwordResetTokenRepository;

  @Autowired
  private PrivilegeRepository privilegeRepository;

  @Autowired
  private PasswordEncoder encoder;

  @Autowired
  private AccountLogRepository accountLogRepository;

  @Override
  public Account getAccountByUsername(String username) {
    return accountRepository.findByUsername(username);
  }

  @Override
  public Account createAdmin(Account account) {

    if (!account.getUsername().isEmpty() && !account.getPassword().isEmpty()) {
      account.setPassword(encoder.encode(account.getPassword()));
      Set<Privilege> roles = new HashSet<Privilege>(privilegeRepository.findAll());
      account.setPrivileges(roles);
      accountRepository.save(account);
      return account;
    }
    return null;
  }

  @Override
  public Account createNoramlUser(Account account) {
    if (!account.getUsername().isEmpty() && !account.getPassword().isEmpty()) {
      account.setPassword(encoder.encode(account.getPassword()));
      Set<Privilege> roles = new HashSet<Privilege>();
      roles.add(privilegeRepository.findByRole("USER"));
      account.setPrivileges(roles);
      accountRepository.save(account);
      return account;
    }
    return null;
  }
  @Override
  public Account updateNoramlUser(Account account) {
    if (!account.getUsername().isEmpty() && !account.getPassword().isEmpty()) {
      accountRepository.save(account);
      return account;
    }
    return null;
  }

  @Override
  public void deleteAll() {

    accountRepository.deleteAll();
  }

  @Override
  public Account getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof Account)) {
      return null;
    }
    return (Account) authentication.getPrincipal();
  }

  @Override
  public Account createUser(Account account, Privilege p) {
    if (p.getRole().equals("ADMIN")) {
      this.createAdmin(account);
    } else {
      this.createNoramlUser(account);
    }
    return null;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Account a = this.getAccountByUsername(username);
    if (a != null) {
      return a.userDetails();
    } else {
      throw new UsernameNotFoundException(username);
    }
  }

  @Override
  public Privilege getPrivilegeByRole(String role) {
    return this.privilegeRepository.findByRole(role);
  }

  @Override
  public Privilege createPrivilegeByRole(String role) {
    Privilege p = new Privilege(role);
    if (this.privilegeRepository.findByRole(role) == null) {
      this.privilegeRepository.save(p);
      return p;
    }
    return this.privilegeRepository.findByRole(role);
  }

  @Override
  public List<Account> findAll() {
    return accountRepository.findAll();
    // TODO Auto-generated method stub
  }


  @Override
  public boolean emailExist(String email) {

    Account user = accountRepository.findByEmail(email);
    if (user != null) {
      return true;
    }
    return false;
  }

  @Override
  public boolean userNameExist(String username) {
    // TODO Auto-generated method stub
    Account user = accountRepository.findByUsername(username);
    if (user != null) {
      return true;
    }
    return false;
  }

  @Override
  public void createAccountsFromLegacy() throws IOException {


    File UserFile = new File("/Users/ena3/hl7-igamt/auth/src/main/resources/json/User.json");


    File AccountFile = new File("/Users/ena3/hl7-igamt/auth/src/main/resources/json/Account.json");

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    List<OldUser> users = objectMapper.readValue(new FileInputStream(UserFile),
        new TypeReference<List<OldUser>>() {});
    Map<String, OldUser> usersMap = users.stream().collect(

        Collectors.groupingBy(OldUser::getUsername,
            Collectors.collectingAndThen(Collectors.toList(), x -> {
              return x.get(0);
            })) // returns a LinkedHashMap, keep order

    );

    List<OldAccount> accounts = objectMapper.readValue(new FileInputStream(AccountFile),
        new TypeReference<List<OldAccount>>() {});
    Map<String, OldAccount> accountsMap = accounts.stream().collect(

        Collectors.groupingBy(OldAccount::getUsername,
            Collectors.collectingAndThen(Collectors.toList(), x -> {
              return x.get(0);
            })));

    for (String user : usersMap.keySet()) {
      OldUser olduser = usersMap.get(user);
      if (accountsMap.containsKey(user)) {
        OldAccount oldAccount = accountsMap.get(user);
        if (oldAccount != null)
          mergeAccunts(accountsMap.get(user), olduser);
      }
    }
  }

  private void mergeAccunts(OldAccount oldAccount, OldUser olduser) {
    Account a = new Account();
    a.setEmail(oldAccount.getEmail());
    a.setFullName(oldAccount.getFullName());
    a.setAccountId(oldAccount.getId());
    a.setOrganization(oldAccount.getEmployer());
    a.setUsername(oldAccount.getUsername());
    if (oldAccount.getPending() != null && oldAccount.getPending().equals("0")) {
      a.setPending(false);
    }
    if (oldAccount.getSignedConfidentialityAgreement() != null
        && oldAccount.getSignedConfidentialityAgreement().equals("1")) {
      a.setSignedConfidentialityAgreement(true);
    }
    a.setPassword(olduser.getPassword());
    Set<Privilege> roles = new HashSet<Privilege>();
    roles.add(privilegeRepository.findByRole("USER"));
    if (oldAccount.getAccountType().equals("admin")) {
      roles.add(privilegeRepository.findByRole("ADMIN"));
    }
    a.setPrivileges(roles);
    accountRepository.save(a);
  }

  @Override
  public Account findByAccountId(Long accountId) {
    // TODO Auto-generated method stub
    return accountRepository.findByAccountId(accountId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nist.hit.hl7.auth.service.AccountService#findUserByEmail(java.lang.String)
   */
  @Override
  public Account findByEmail(String email) {
    return accountRepository.findByEmail(email);
  }
  
  @Override
  public Account findByUsername(String username) {
    // TODO Auto-generated method stub
    return accountRepository.findByUsername(username);
  }


  @Override
  public PasswordResetToken createPasswordResetTokenForUser(Account user, String token) {
    PasswordResetToken mytoken = new PasswordResetToken();
    mytoken.setUsername(user.getUsername());
    mytoken.setEmail(user.getEmail());
    mytoken.setFullname(user.getFullName());
    mytoken.setToken(token);

    Date date = new Date();
    date.setTime(date.getTime() + PasswordResetToken.getExpiration());
    mytoken.setExpiryDate(date);

    passwordResetTokenRepository.save(mytoken);
    return mytoken;
  }

  @Override
  public boolean validateToken(String token) throws AuthenticationException {
    PasswordResetToken prt = passwordResetTokenRepository.findByToken(token);
    if (prt == null) {
      throw new AuthenticationException("No password resset token ");
    } else {

      Date current = new Date();
      if (current.compareTo(prt.getExpiryDate()) < 0) {
        throw new AuthenticationException("Token Expired");
      }

    }
    return true;

  }


  @Override
  public PasswordResetToken changePassword(String newPassword, String token)
      throws AuthenticationException {
    PasswordResetToken prt = passwordResetTokenRepository.findByToken(token);
    if (prt == null) {
      throw new AuthenticationException("No password resset token ");
    } else {

      Date current = new Date();
      if (current.compareTo(prt.getExpiryDate()) < 0) {
        throw new AuthenticationException("Token Expired");
      }
      String username = prt.getUsername();
      Account acc = accountRepository.findByUsername(username);

      if (acc == null) {
        throw new UsernameNotFoundException("User Not found");
      } else {
        acc.setPassword(encoder.encode(newPassword));
        accountRepository.save(acc);
        passwordResetTokenRepository.deleteById(prt.getId());
        return prt;
      }
    }
  }

  @Override
  public void createLog(AccountLog accountLog) {
      accountLog.setDate(new Date());
      accountLogRepository.save(accountLog);
  }

}
