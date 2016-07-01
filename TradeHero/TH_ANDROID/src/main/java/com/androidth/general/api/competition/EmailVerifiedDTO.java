package com.androidth.general.api.competition;

/**
 * Created by jeffgan on 1/7/16.
 */
public class EmailVerifiedDTO {
    public boolean IsValidated;
    public String Message;

    public boolean isValidated() {
        return IsValidated;
    }

    public void setValidated(boolean validated) {
        IsValidated = validated;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        this.Message = message;
    }

    @Override
    public String toString() {
        return "EmailVerifiedDTO{" +
                "isValidated=" + IsValidated +
                ", message='" + Message + '\'' +
                '}';
    }
}
