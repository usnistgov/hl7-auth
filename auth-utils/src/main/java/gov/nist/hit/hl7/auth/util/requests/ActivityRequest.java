package gov.nist.hit.hl7.auth.util.requests;

import java.util.HashMap;

public class ActivityRequest {
	private String username;
	private HashMap<String, String> activities;
	
	public ActivityRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
    public HashMap<String, String> getActivities() {
        return activities;
    }

    public void setActivities(HashMap<String, String> activities) {
        this.activities = activities;
    }
}
