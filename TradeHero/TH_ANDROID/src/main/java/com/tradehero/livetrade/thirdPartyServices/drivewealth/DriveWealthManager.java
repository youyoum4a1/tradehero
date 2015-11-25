package com.tradehero.livetrade.thirdPartyServices.drivewealth;

import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupFormDTO;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */

@Singleton public class DriveWealthManager {

    private DriveWealthSignupFormDTO mSignupFormDTO;
    private String mUserID;
    private String mSessionKey;

    @Inject public DriveWealthManager() {
        mSignupFormDTO = new DriveWealthSignupFormDTO();
    }

    public DriveWealthSignupFormDTO getSignupFormDTO() {
        return mSignupFormDTO;
    }

    public String getSessionKey() {
        return mSessionKey;
    }

    public void setSessionKey(String mSessionKey) {
        this.mSessionKey = mSessionKey;
    }

    public String getUserID() {
        return mUserID;
    }

    public void setUserID(String mUserID) {
        this.mUserID = mUserID;
    }
}
