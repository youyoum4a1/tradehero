package com.tradehero.livetrade.thirdPartyServices.drivewealth.data;

import android.net.Uri;
import com.tradehero.th.BuildConfig;

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
    public int genderIdx;
    public int martialStatusIdx;
    public String countryName;
    public String idNO;
    public String dob;
    public String address;
    public int employmentStatusIdx;
    public int employerBusinessIdx;
    public String employerCompany;
    public boolean employerIsBroker;
    public boolean director;
    public String directorOf;
    public boolean politicallyExposed;
    public String politicallyExposedNames;
    public int investmentObjectivesIdx;
    public int investmentExperienceIdx;
    public int annualIncomeIdx;
    public int networthLiquidIdx;
    public int networthTotalIdx;
    public int riskToleranceIdx;
    public int timeHorizonIdx;
    public int liquidityNeedsIdx;
    public String ackSignedBy;
    public transient Uri idcardFront;
    public transient Uri idcardBack;

    public DriveWealthSignupFormDTO() {
        if (BuildConfig.DEBUG) {
            phoneNumber = "13816672325";
            phoneVerificationToken = "2516";
            email = "sam@tradehero.mobi";
            userName = "samyu";
            password = "welcome0";
            firstName = "三";
            lastName = "张";
            firstNameInEng = "Zheng";
            lastNameInEng = "Yu";
            employerCompany = "MyHero";
            ackSignedBy = "张三";
            idNO = "310101198210121074";
            address = "哈尔滨路160号";
        }
    }
}
