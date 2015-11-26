package com.tradehero.th.api.users.password;

public class PhoneNumberVerifyDTO {

    public static final int CODE_PARAM_ERROR = -1;
    public static final int CODE_NO_SUCH_ACCOUNT = 0;
    public static final int CODE_SIGNUP_PENDING = 1;
    public static final int CODE_SIGNUP_APPROVED = 2;
    public static final int CODE_SIGNUP_REJECTED = 3;
    public static final int CODE_SIGNUP_REVOKED = 4;
    public static final int CODE_SIGNUP_CLOSED = 5;

    public int code;
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
