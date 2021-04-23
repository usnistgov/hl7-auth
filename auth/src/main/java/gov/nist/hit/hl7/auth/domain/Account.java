package gov.nist.hit.hl7.auth.domain;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@Document
public class Account {

	@Id
	private String id;
	private Long accountId; // only for legacy
	private String username;
	private String password;
	private String email;
	private boolean pending = false;
	private String fullName;
	private String organization;
	private boolean old;
	private Boolean signedConfidentialityAgreement = false;
	@DBRef
	private Set<Privilege> privileges;

    private List<AccountLog> accountLogs;
    private HashMap<String, String> activities;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public Boolean getSignedConfidentialityAgreement() {
		return signedConfidentialityAgreement;
	}

	public void setSignedConfidentialityAgreement(Boolean signedConfidentialityAgreement) {
		this.signedConfidentialityAgreement = signedConfidentialityAgreement;
	}

	public Set<Privilege> getPrivileges() {
		return privileges;
	}

	public List<String> getPrivilegesStr() {
		if (this.privileges != null) {
			List<String> result = new ArrayList<String>();
			this.privileges.forEach(p -> {
				result.add(p.getRole());
			});
			return result;
		}
		return null;
	}

	public void setPrivileges(Set<Privilege> privileges) {
		this.privileges = privileges;
	}

    public List<AccountLog> getAccountLogs() {
        return accountLogs;
    }

    public void setAccountLogs(List<AccountLog> accountLogs) {
        this.accountLogs = accountLogs;
    }

    public HashMap<String, String> getActivities() {
        return activities;
    }

    public void setActivities(HashMap<String, String> activities) {
        this.activities = activities;
    }

	@Transient
	public UserDetails userDetails() {
		return new User(getUsername(), getPassword(), !isPending(), true, true, true, privileges);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public boolean isOld() {
		return old;
	}

	public void setOld(boolean old) {
		this.old = old;
	}

}
