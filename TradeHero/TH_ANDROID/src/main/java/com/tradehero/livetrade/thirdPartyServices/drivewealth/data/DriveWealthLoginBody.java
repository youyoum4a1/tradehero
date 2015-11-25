package com.tradehero.livetrade.thirdPartyServices.drivewealth.data;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class DriveWealthLoginBody {

    private String appTypeID;
    private String appVersion;
    private String languageID;
    private String osType;
    private String osVersion;
    private String scrRes;
    private String username;
    private String password;
    private boolean guest;

    public DriveWealthLoginBody(String username, String password) {
        this.username = username;
        this.password = password;
        appTypeID = "27";
        appVersion = "1.0";
        languageID = "zh_CN";
        osType = "Android";
        osVersion = "Android Device";
        scrRes = "360*480";
        guest = false;
    }
}
