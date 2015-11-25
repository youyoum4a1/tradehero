package com.tradehero.th.api.users.password;

public class PhoneNumberVerifyDTO {
    public boolean success;
    public String reason;

    @Override
    public String toString() {
        return "PhoneNumberVerifyDTO{" +
                "success=" + success +
                ", reason='" + reason + '\'' +
                '}';
    }
}
