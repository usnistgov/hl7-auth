package gov.nist.hit.hl7.auth.controller;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.nist.hit.hl7.auth.domain.Account;
import gov.nist.hit.hl7.auth.domain.AccountLog;
import gov.nist.hit.hl7.auth.domain.PasswordResetToken;
import gov.nist.hit.hl7.auth.exception.PasswordChangeException;
import gov.nist.hit.hl7.auth.exception.RegistrationException;
import gov.nist.hit.hl7.auth.service.AccountService;
import gov.nist.hit.hl7.auth.util.requests.ChangePasswordConfirmRequest;
import gov.nist.hit.hl7.auth.util.requests.ChangePasswordRequest;
import gov.nist.hit.hl7.auth.util.requests.ConnectionResponseMessage;
import gov.nist.hit.hl7.auth.util.requests.ConnectionResponseMessage.Status;
import gov.nist.hit.hl7.auth.util.requests.PasswordResetTokenResponse;
import gov.nist.hit.hl7.auth.util.requests.RegistrationRequest;
import gov.nist.hit.hl7.auth.util.requests.UserListResponse;
import gov.nist.hit.hl7.auth.util.requests.UserResponse;
import gov.nist.hit.hl7.auth.util.requests.AccountLogRequest;

@Controller
public class AccountController {


  @Autowired
  private AccountService accountService;

  @Autowired
  private SimpleMailMessage templateMessage;


  @Autowired
  private JavaMailSender mailSender;


  @RequestMapping(value = "/api/tool/register", method = RequestMethod.POST, produces = {"application/json"})

  public @ResponseBody ConnectionResponseMessage<UserResponse> register(
      @RequestBody RegistrationRequest user, HttpServletResponse response) throws Exception {
    Account a = new Account();


    if (accountService.emailExist(user.getEmail())) {

      throw new RegistrationException("e-mail: " + user.getEmail() + "is Already used");

    } else if (accountService.userNameExist(user.getUsername())) {

      throw new Exception("username: " + user.getUsername() + "is Already used");


    } else {
      a.setFullName(user.getFullName());
      a.setEmail(user.getEmail());
      a.setUsername(user.getUsername());
      a.setOrganization(user.getOrganization());
      a.setPassword(user.getPassword());
      a.setSignedConfidentialityAgreement(user.getSignedConfidentialityAgreement());
      accountService.createNoramlUser(a);
      UserResponse userResponse = new UserResponse(user.getUsername());
      return new ConnectionResponseMessage<UserResponse>(Status.SUCCESS, null,
          "Registration successfull", null, false, new Date(), userResponse);
    }


  }

  @RequestMapping(value = "/api/tool/password/reset", method = RequestMethod.POST)
  @ResponseBody
  public ConnectionResponseMessage<PasswordResetTokenResponse> getResetTokenString(
      HttpServletRequest request, @RequestBody ChangePasswordRequest requestObject,
      HttpServletResponse response) throws PasswordChangeException {
    Account user = accountService.findByEmail(requestObject.getEmail());
    if (user == null) {
      throw new PasswordChangeException(
          "Could not found an account with E-mail :" + requestObject.getEmail());
    }
    try {
      String token = UUID.randomUUID().toString();
      PasswordResetToken tokenObject = accountService.createPasswordResetTokenForUser(user, token);

      PasswordResetTokenResponse tokenResponse = convertToken(tokenObject);


      return new ConnectionResponseMessage<PasswordResetTokenResponse>(Status.SUCCESS, null,
          "Password change request success", null, false, new Date(), tokenResponse);
    } catch (Exception e) {
      throw new PasswordChangeException(e.getMessage());
    }
  }

  
  /**
   * @param tokenObject
   * @return
   */
  private PasswordResetTokenResponse convertToken(PasswordResetToken tokenObject) {
    // TODO Auto-generated method stub
    PasswordResetTokenResponse response = new PasswordResetTokenResponse();
    response.setEmail(tokenObject.getEmail());
    response.setFullName(tokenObject.getFullname());

    response.setUsername(tokenObject.getUsername());
    response.setToken(tokenObject.getToken());
    response.setExpiryDate(tokenObject.getExpiryDate());
    response.setToolName(tokenObject.getToolName());
    return response;
  }

  @RequestMapping(value = "/api/tool/password/reset/confirm", method = RequestMethod.POST)
  @ResponseBody
  public ConnectionResponseMessage<PasswordResetTokenResponse> ConfirmResetPassword(
      HttpServletRequest request, @RequestBody ChangePasswordConfirmRequest requestObject,
      HttpServletResponse response) throws Exception {
    try {
      PasswordResetToken responseToken =
          accountService.changePassword(requestObject.getPassword(), requestObject.getToken());
      PasswordResetTokenResponse tokenResponse = convertToken(responseToken);
      return new ConnectionResponseMessage<PasswordResetTokenResponse>(Status.SUCCESS, null,
          "Password successfull changed", null, false, new Date(), tokenResponse);
    } catch (Exception e) {
      throw new PasswordChangeException(e.getMessage());
    }

  }


  @RequestMapping(value = "/api/tool/password/validatetoken", method = RequestMethod.POST)
  @ResponseBody
  public boolean resetPassword(HttpServletRequest request, @RequestBody String token)
      throws AuthenticationException {

    return accountService.validateToken(token);

  }

  @RequestMapping(value = "/api/tool/users", method = RequestMethod.GET)
  @ResponseBody
  public UserListResponse getAllUsers(HttpServletResponse request)
      throws IOException {

	  UserListResponse results = new UserListResponse();
	  accountService.findAll().forEach(a -> {
		  UserResponse u = new UserResponse();
		  u.setUsername(a.getUsername());
		  u.setEmail(a.getEmail());
		  u.setFullName(a.getFullName());
		  u.setOrganization(a.getOrganization());
		  u.setAuthorities(a.getPrivilegesStr());
		  results.getUsers().add(u);
	  });

    return results;
  }

  @RequestMapping(value = "/api/tool/user/{username}", method = RequestMethod.GET)
  @ResponseBody
  public UserResponse getCurrentUser(@PathVariable("username") String username, HttpServletResponse request)
      throws Exception {

    Account account = accountService.getAccountByUsername(username);
    if (account == null) {

        throw new Exception("username: " + username + "is not found");

    } else {
        UserResponse u = new UserResponse();
        u.setUsername(username);
        u.setFullName(account.getFullName());
        u.setEmail(account.getEmail());
        u.setOrganization(account.getOrganization());

        return u;
    }
  }

  @RequestMapping(value = "/api/tool/user", method = RequestMethod.POST, produces = {"application/json"})

  public @ResponseBody ConnectionResponseMessage<UserResponse> update(
      @RequestBody RegistrationRequest user, HttpServletResponse response) throws Exception {

    Account a = accountService.getAccountByUsername(user.getUsername());
    if (a == null) {

        throw new Exception("username: " + user.getUsername() + "is not found");

        
    } else if (!a.getEmail().equals(user.getEmail()) &&  accountService.emailExist(user.getEmail())) {

      throw new Exception("e-mail: " + user.getEmail() + "is Already used");

    } else {
        UserResponse u = new UserResponse(user.getUsername());

        if (user.getFullName() != null) {
            a.setFullName(user.getFullName());
            u.setFullName(user.getFullName());
        }
        if (user.getEmail() != null) {
            a.setEmail(user.getEmail());
            u.setEmail(user.getEmail());
        }
        if (user.getOrganization() != null) {
            a.setOrganization(user.getOrganization());
            u.setOrganization(user.getOrganization());
        }

        accountService.updateNoramlUser(a);

        return new ConnectionResponseMessage<UserResponse>(Status.SUCCESS, null,
          "UserProfileUpdate successfull", null, false, new Date(), u);
    }
  }

  @RequestMapping(value = "/api/tool/accountlog", method = RequestMethod.POST, produces = {"application/json"})

  public @ResponseBody ConnectionResponseMessage<UserResponse> accountlog(
      @RequestBody AccountLogRequest user, HttpServletResponse response) throws Exception {
    AccountLog a = new AccountLog();

    if (accountService.userNameExist(user.getUsername()) == false) {

      throw new Exception("username: " + user.getUsername() + "is not found");

    } else {
      a.setUsername(user.getUsername());
      a.setFrom(user.getFrom());

      accountService.createLog(a);
      UserResponse userResponse = new UserResponse(user.getUsername());
      return new ConnectionResponseMessage<UserResponse>(Status.SUCCESS, null,
          "User Logging successfull", null, false, new Date(), userResponse);
    }
  }

  private void sendAccountPasswordResetRequestNotification(Account acc, String url) {
    SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);

    msg.setTo(acc.getEmail());
    msg.setSubject("IGAMT Password Reset Request Notification");
    msg.setText("Dear " + acc.getUsername() + " \n\n"
        + "**** If you have not requested a password reset, please disregard this email **** \n\n\n"
        + "You password reset request has been processed.\n"
        + "Copy and paste the following url to your browser to initiate the password change:\n"
        + url + " \n\n" + "Sincerely, " + "\n\n" + "The IGAMT Team" + "\n\n"
        + "P.S: If you need help, contact us at '" + "'");

    try {
      this.mailSender.send(msg);
    } catch (MailException ex) {
      ex.printStackTrace();
    }
  }
}
