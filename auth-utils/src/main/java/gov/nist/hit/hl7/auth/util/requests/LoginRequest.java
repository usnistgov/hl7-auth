package gov.nist.hit.hl7.auth.util.requests;


public class LoginRequest {
	public LoginRequest() {
	}
	private String username;
	private String password;
	private Boolean oldMethod = false;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Boolean getOldMethod() {
		return oldMethod;
	}
	public void setOldMethod(Boolean oldMethod) {
		this.oldMethod = oldMethod;
	}
}
