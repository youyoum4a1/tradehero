package com.tradehero.th.api.users.password;


public class ResetPasswordFormDTO
{
    public static final String TAG = ResetPasswordFormDTO.class.getSimpleName();

    public String newPassword;
    public String newPasswordConfirmation;
    public String forgotEmailUrlGuid;

    public ResetPasswordFormDTO()
    {
        super();
    }

    public boolean isValid()
    {
        return newPassword != null && newPassword.length() >= 6 &&
                newPasswordConfirmation != null && newPasswordConfirmation.length() >= 6 &&
                newPasswordConfirmation.equals(newPassword);
    }
}
