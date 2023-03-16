package gov.nist.hit.hl7.auth.util.requests;

public class FindUserRequest {
    // If email = true, then value is the user's email, if email = false then value is the user's username
    private boolean email;
    private String value;

    public boolean isEmail() {
        return email;
    }

    public void setEmail(boolean email) {
        this.email = email;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
