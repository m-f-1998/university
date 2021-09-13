
package uk.co.coryalexander.pedalpay.uk.co.coryalexander.pedalpay.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoginResponse {

    @SerializedName("error")
    @Expose
    private boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("userDetails")
    @Expose
    private List<UserDetail> userDetails = null;
    @SerializedName("admin")
    @Expose
    private boolean admin;

    /**
     * No args constructor for use in serialization
     *
     */
    public LoginResponse() {
    }

    /**
     *
     * @param message
     * @param error
     * @param admin
     * @param userDetails
     */
    public LoginResponse(boolean error, String message, List<UserDetail> userDetails, boolean admin) {
        super();
        this.error = error;
        this.message = message;
        this.userDetails = userDetails;
        this.admin = admin;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<UserDetail> getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(List<UserDetail> userDetails) {
        this.userDetails = userDetails;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

}


