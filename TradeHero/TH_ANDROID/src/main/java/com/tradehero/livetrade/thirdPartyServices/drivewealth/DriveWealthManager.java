package com.tradehero.livetrade.thirdPartyServices.drivewealth;

import android.content.Context;

import com.tradehero.chinabuild.data.db.THDatabaseHelper;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupFormDTO;

import java.io.IOException;

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

    public void storeSignupInfo(Context context){
        THDatabaseHelper dbHelper = new THDatabaseHelper(context);
        try {
            dbHelper.storeDWSignupInfo(mSignupFormDTO.phoneNumber, THJsonAdapter.getInstance().toStringBody(mSignupFormDTO));
        } catch (IOException e) {
            // Do nothing just skip store.
        }
    }

    public void retriveSignupInfo(Context context) {
        THDatabaseHelper dbHelper = new THDatabaseHelper(context);
        String phoneNumber = mSignupFormDTO.phoneNumber;
        String phoneVerificationToken = mSignupFormDTO.phoneVerificationToken;
        String infoString = dbHelper.retrieveDWSignupInfo(phoneNumber);
        if (infoString.length() > 0) {
            mSignupFormDTO = (DriveWealthSignupFormDTO) THJsonAdapter.getInstance().fromBody(infoString, DriveWealthSignupFormDTO.class);
        } else {
            mSignupFormDTO = new DriveWealthSignupFormDTO();
        }
        mSignupFormDTO.phoneNumber = phoneNumber;
        mSignupFormDTO.phoneVerificationToken = phoneVerificationToken;
    }
}
