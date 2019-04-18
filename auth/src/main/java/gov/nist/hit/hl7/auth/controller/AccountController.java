package gov.nist.hit.hl7.auth.controller;

import java.util.Date;
import java.util.UUID;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.nist.hit.hl7.auth.domain.Account;
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
import gov.nist.hit.hl7.auth.util.requests.UserResponse;

@Controller
public class AccountController {


  @Autowired
  private AccountService accountService;

  @Autowired
  private SimpleMailMessage templateMessage;


  @Autowired
  private JavaMailSender mailSender;


  @RequestMapping(value = "/register", method = RequestMethod.POST, produces = {"application/json"})

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

  @RequestMapping(value = "password/reset", method = RequestMethod.POST)
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

  @RequestMapping(value = "password/reset/confirm", method = RequestMethod.POST)
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


  @RequestMapping(value = "password/validatetoken", method = RequestMethod.POST)
  @ResponseBody
  public boolean resetPassword(HttpServletRequest request, @RequestBody String token)
      throws AuthenticationException {

    return accountService.validateToken(token);

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
