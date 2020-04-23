package gov.nist.hit.hl7.auth.util.requests;

import java.util.ArrayList;
import java.util.List;

public class UserListResponse {
	private List<UserResponse> users;

	public List<UserResponse> getUsers() {
		return users;
	}

	public void setUsers(List<UserResponse> users) {
		this.users = users;
	}

	public UserListResponse(List<UserResponse> users) {
		super();
		this.users = users;
	}
	
	public UserListResponse() {
		super();
		users = new ArrayList<UserResponse>();
	}

	
}
