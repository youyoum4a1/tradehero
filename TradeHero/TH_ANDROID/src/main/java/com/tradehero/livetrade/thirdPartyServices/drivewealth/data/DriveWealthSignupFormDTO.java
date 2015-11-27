package com.tradehero.livetrade.thirdPartyServices.drivewealth.data;

import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;

import com.tradehero.th.BuildConfig;

import java.io.File;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class DriveWealthSignupFormDTO {
    public String phoneNumber;
    public String phoneVerificationToken;
    public String email;
    public String userName;
    public String password;
    public String firstName;
    public String lastName;
    public String firstNameInEng;
    public String lastNameInEng;
    public String idNO;
    public String address;
    public int employmentStatusIdx;
    public int employerBusinessIdx;
    public String employerCompany;
    public boolean employerIsBroker;
    public boolean director;
    public boolean politicallyExposed;
    public int investmentObjectivesIdx;
    public int investmentExperienceIdx;
    public int annualIncomeIdx;
    public int networthLiquidIdx;
    public int networthTotalIdx;
    public int riskToleranceIdx;
    public int timeHorizonIdx;
    public int liquidityNeedsIdx;
    public String ackSignedBy;
    public Uri idcardFront;
    public Uri idcardBack;

    public DriveWealthSignupFormDTO() {
        if (BuildConfig.DEBUG) {
            phoneNumber = "13816631019";
            phoneVerificationToken = "1234";
            email = "sam@tradehero.mobi";
            userName = "samyu";
            password = "welcome0";
            firstName = "123";
            lastName = "456";
            firstNameInEng = "Zheng";
            lastNameInEng = "Yu";
            employerCompany = "MyHero";
            ackSignedBy = "Yu Zheng";
            idNO = "310101198210121074";
            address = "哈尔滨路160号";
        }
    }
}
