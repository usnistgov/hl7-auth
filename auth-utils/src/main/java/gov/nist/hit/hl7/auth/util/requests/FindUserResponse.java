package gov.nist.hit.hl7.auth.util.requests;

public class FindUserResponse {
    private String username;
    private boolean exists;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }
}
