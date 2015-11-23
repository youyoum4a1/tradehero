package com.tradehero.livetrade.thirdPartyServices.drivewealth;

import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupFormDTO;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */

@Singleton public class DriveWealthManager {

    private DriveWealthSignupFormDTO mSignupFormDTO;

    @Inject public DriveWealthManager() {
        mSignupFormDTO = new DriveWealthSignupFormDTO();
    }

    public DriveWealthSignupFormDTO getSignupFormDTO() {
        return mSignupFormDTO;
    }
}
