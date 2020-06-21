package gov.nist.hit.hl7.auth.util.requests;

public class AccountLogRequest {
	private String username;
	private String from;
	
	public AccountLogRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
}
