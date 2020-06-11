package gov.nist.hit.hl7.auth.client.emails.service;

import gov.nist.hit.hl7.auth.client.exception.ErrorEmailException;

public interface ErrorEmailService {

	
	void reportError(String url, String message, String trace,String usrname) throws ErrorEmailException;
	
	
}
