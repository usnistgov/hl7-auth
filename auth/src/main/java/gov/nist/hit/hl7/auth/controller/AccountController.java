package gov.nist.hit.hl7.auth.controller;

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
import gov.nist.hit.hl7.auth.service.AccountService;
import gov.nist.hit.hl7.auth.util.requests.ChangePasswordConfirmRequest;
import gov.nist.hit.hl7.auth.util.requests.ChangePasswordRequest;
import gov.nist.hit.hl7.auth.util.requests.RegistrationRequest;

@Controller
public class AccountController {


  @Autowired
  private AccountService accountService;

  @Autowired
  private SimpleMailMessage templateMessage;


  @Autowired
  private JavaMailSender mailSender;


  @RequestMapping(value = "/register", method = RequestMethod.POST, produces = {"application/json"})

  public @ResponseBody Account register(@RequestBody RegistrationRequest user,
      HttpServletResponse response) throws Exception {
    Account a = new Account();


    if (accountService.emailExist(user.getEmail())) {

      response.sendError(response.SC_BAD_REQUEST, "Email Already Used");
      return null;

    } else if (accountService.userNameExist(user.getUsername())) {

      response.sendError(response.SC_BAD_REQUEST, "username Already Used");
      return null;

    } else {
      a.setFullName(user.getFullName());
      a.setEmail(user.getEmail());
      a.setUsername(user.getUsername());
      a.setOrganization(user.getOrganization());
      a.setPassword(user.getPassword());
      a.setSignedConfidentialityAgreement(user.getSignedConfidentialityAgreement());

      accountService.createNoramlUser(a);
      return a;
    }


  }

  @RequestMapping(value = "password/reset", method = RequestMethod.POST)
  @ResponseBody
  public boolean resetPassword(HttpServletRequest request,
      @RequestBody ChangePasswordRequest requestObject, HttpServletResponse response)
      throws Exception {
    Account user = accountService.findByEmail(requestObject.getEmail());
    if (user == null) {

      response.sendError(response.SC_BAD_REQUEST,
          "Could not found an account with E-mail :" + requestObject.getEmail());
      return false;
    }
    String token = UUID.randomUUID().toString();
    accountService.createPasswordResetTokenForUser(user.getUsername(), token);
    String url = requestObject.getUrl() + "/" + token;


    sendAccountPasswordResetRequestNotification(user, url);
    return true;

  }

  @RequestMapping(value = "password/reset/confirm", method = RequestMethod.POST)
  @ResponseBody
  public boolean ConfirmResetPassword(HttpServletRequest request,
      @RequestBody ChangePasswordConfirmRequest requestObject, HttpServletResponse response)
      throws Exception {
    try {

      return accountService.changePassword(requestObject.getPassword(), requestObject.getToken());

    } catch (Exception e) {
      response.sendError(response.SC_BAD_REQUEST, "Could not change the password");
      throw e;
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
