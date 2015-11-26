package com.tradehero.livetrade.thirdPartyServices.drivewealth.data;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class DriveWealthSignupBody{
    private String emailAddress1;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean usCitizen;
    private String utm_campaign;

    public DriveWealthSignupBody(String emailAddress1, String firstName, String lastName, String username, String password) {
        this.emailAddress1 = emailAddress1;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.usCitizen = false;
        this.utm_campaign = "Tradehero";
    }
}