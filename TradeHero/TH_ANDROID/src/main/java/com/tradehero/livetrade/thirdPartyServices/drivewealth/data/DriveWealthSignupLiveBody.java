package com.tradehero.livetrade.thirdPartyServices.drivewealth.data;

import java.util.Calendar;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class DriveWealthSignupLiveBody {
    private String userID;
    private String tradingType;
    private String ownershipType;
    private String languageID;

    private String firstName;
    private String lastName;
    private String dob;
    private String gender;
    private String martialStatus;
    private String idNo;
    private String addressLine1;
    private String emailAddress1;
    private boolean usCitizen;
    private String countryID;
    private String citizenship;

    private String employmentStatus;
    private String employerBusiness;
    private String employerCompany;
    private boolean employerIsBroker;
    private boolean director;
    private String directorOf;
    private boolean politicallyExposed;
    private String politicallyExposedNames;

    private String investmentObjectives;
    private String investmentExperience;
    private String annualIncome;
    private String networthLiquid;
    private String networthTotal;
    private String riskTolerance;
    private String timeHorizon;
    private String liquidityNeeds;
    
    private boolean disclosureAck;
    private boolean disclosureRule14b;
    private boolean ackCustomerAgreement;
    private boolean ackMarketData;
    private boolean ackSweep;
    private boolean ackFindersFee;
    private boolean ackSigned;
    private String ackSignedBy;
    private String ackSignedWhen;
    private String referralCode;

    public DriveWealthSignupLiveBody(String userID, String firstName, String lastName,
                                     String dob, String gender, String martialStatus,
                                     String idNo, String addressLine1, String emailAddress,
                                     String employmentStatus, String employerBusiness,
                                     String employerCompany, boolean employerIsBroker,
                                     boolean director, String directorOf,
                                     boolean politicallyExposed, String politicallyExposedNames,
                                     String investmentObjectives, String investmentExperience,
                                     String annualIncome, String networthLiquid,
                                     String networthTotal, String riskTolerance,
                                     String timeHorizon, String liquidityNeeds,
                                     String ackSignedBy) {
        this.userID = userID;
        this.tradingType = "m";
        this.ownershipType = "Individual";
        this.languageID = "zh_CN";
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.gender = gender;
        this.martialStatus = martialStatus;
        this.idNo = idNo;
        this.addressLine1 = addressLine1;
        this.emailAddress1 = emailAddress;
        this.usCitizen = false;
        this.countryID = "CHN";
        this.citizenship = "CHN";
        this.employmentStatus = employmentStatus;
        this.employerBusiness = employerBusiness;
        this.employerCompany = employerCompany;
        this.employerIsBroker = employerIsBroker;
        this.director = director;
        this.directorOf = directorOf;
        this.politicallyExposed = politicallyExposed;
        this.politicallyExposedNames = politicallyExposedNames;
        this.investmentObjectives = investmentObjectives;
        this.investmentExperience = investmentExperience;
        this.annualIncome = annualIncome;
        this.networthLiquid = networthLiquid;
        this.networthTotal = networthTotal;
        this.riskTolerance = riskTolerance;
        this.timeHorizon = timeHorizon;
        this.liquidityNeeds = liquidityNeeds;
        this.disclosureAck = true;
        this.disclosureRule14b = true;
        this.ackCustomerAgreement = true;
        this.ackMarketData = true;
        this.ackSweep = true;
        this.ackFindersFee = true;
        this.ackSigned = true;
        this.ackSignedBy = ackSignedBy;

        java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        this.ackSignedWhen = format.format(Calendar.getInstance().getTime());

        referralCode = "RUA4RR";
    }
}
