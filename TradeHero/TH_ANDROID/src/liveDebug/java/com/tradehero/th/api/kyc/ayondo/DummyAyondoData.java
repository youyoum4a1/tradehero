package com.ayondo.academy.api.kyc.ayondo;

public class DummyAyondoData
{
    public static final int DEFAULT_MAX_ADDRESS_REQUIRED = 3;
    public static final String TERMS_CONDITIONS_URL = "http://www.ayondo.com/en/legal/terms-conditions";
    public static final String RISK_WARNING_DISCLAIMER_URL = "http://www.ayondo.com/en/risk-disclosure";
    public static final String DATA_SHARING_AGREEMENT_URL = "http://ayondo.com/en/manisku-data-sharing-agreement";

    public static final String KYC_OPTIONS = "{\"optionType\":\"AYD\","
            + "\"identityPromptInfo\":{\"image\":\"http://portalvhdskgrrf4wksb8vq.blob.core.windows.net/static/icn-sg.png\",\"prompt\":\"Singapore NRIC/ Driver's License\"},"
            + "\"genders\":[1,2],"
            + "\"allowedMobilePhoneCountries\":[\"SG\"],"
            + "\"allowedNationalityCountries\":[\"AD\",\"AE\",\"AF\",\"AG\",\"AI\",\"AL\",\"AM\",\"AO\",\"AQ\",\"AR\",\"AS\",\"AT\",\"AU\",\"AW\",\"AX\",\"AZ\",\"BA\",\"BB\",\"BD\",\"BE\",\"BF\",\"BG\",\"BH\",\"BI\",\"BJ\",\"BL\",\"BM\",\"BN\",\"BO\",\"BQ\",\"BR\",\"BS\",\"BT\",\"BV\",\"BW\",\"BY\",\"BZ\",\"CA\",\"CC\",\"CD\",\"CF\",\"CG\",\"CH\",\"CI\",\"CK\",\"CL\",\"CM\",\"CN\",\"CO\",\"CR\",\"CS\",\"CU\",\"CV\",\"CW\",\"CX\",\"CY\",\"CZ\",\"DE\",\"DJ\",\"DK\",\"DM\",\"DO\",\"DZ\",\"EC\",\"EE\",\"EG\",\"EH\",\"ER\",\"ES\",\"ET\",\"EU\",\"FI\",\"FJ\",\"FK\",\"FM\",\"FO\",\"FR\",\"GA\",\"GB\",\"GD\",\"GE\",\"GF\",\"GG\",\"GH\",\"GI\",\"GL\",\"GM\",\"GN\",\"GP\",\"GQ\",\"GR\",\"GS\",\"GT\",\"GU\",\"GW\",\"GY\",\"HK\",\"HM\",\"HN\",\"HR\",\"HT\",\"HU\",\"ID\",\"IE\",\"IL\",\"IM\",\"IN\",\"IO\",\"IQ\",\"IR\",\"IS\",\"IT\",\"JE\",\"JM\",\"JO\",\"JP\",\"KE\",\"KG\",\"KH\",\"KI\",\"KM\",\"KN\",\"KP\",\"KR\",\"KW\",\"KY\",\"KZ\",\"LA\",\"LB\",\"LC\",\"LI\",\"LK\",\"LR\",\"LS\",\"LT\",\"LU\",\"LV\",\"LY\",\"MA\",\"MC\",\"MD\",\"ME\",\"MF\",\"MG\",\"MH\",\"MK\",\"ML\",\"MM\",\"MN\",\"MO\",\"MP\",\"MQ\",\"MR\",\"MS\",\"MT\",\"MU\",\"MV\",\"MW\",\"MX\",\"MY\",\"MZ\",\"NA\",\"NC\",\"NE\",\"NF\",\"NG\",\"NI\",\"NL\",\"NO\",\"NP\",\"NR\",\"NU\",\"NZ\",\"OM\",\"PA\",\"PE\",\"PF\",\"PG\",\"PH\",\"PK\",\"PL\",\"PM\",\"PN\",\"PR\",\"PS\",\"PT\",\"PW\",\"PY\",\"QA\",\"RE\",\"RO\",\"RS\",\"RU\",\"RW\",\"SA\",\"SB\",\"SC\",\"SD\",\"SE\",\"SG\",\"SH\",\"SI\",\"SJ\",\"SK\",\"SL\",\"SM\",\"SN\",\"SO\",\"SR\",\"SS\",\"ST\",\"SV\",\"SX\",\"SY\",\"SZ\",\"TC\",\"TD\",\"TF\",\"TG\",\"TH\",\"TJ\",\"TK\",\"TL\",\"TM\",\"TN\",\"TO\",\"TR\",\"TT\",\"TV\",\"TW\",\"TZ\",\"UA\",\"UG\",\"UM\",\"US\",\"UY\",\"UZ\",\"VA\",\"VC\",\"VE\",\"VG\",\"VI\",\"VN\",\"VU\",\"WF\",\"WS\",\"YE\",\"YT\",\"ZA\",\"ZM\",\"ZW\"],"
            + "\"allowedResidencyCountries\":[\"SG\"],"
            + "\"annualIncomeOptions\":[-1,0,15,40,70,100],"
            + "\"netWorthOptions\":[-1,0,15,40,70,100,101],"
            + "\"percentNetWorthOptions\":[-1,0,25,50,75],"
            + "\"employmentStatusOptions\":[\"...\",\"Employed\",\"Self-Employed\",\"Unemployed\",\"Retired\",\"Student\",\"Other\"],"
            + "\"tradingPerQuarterOptions\":[-1,1,2,3,4],"
            + "\"maxAddressRequired\":" + DEFAULT_MAX_ADDRESS_REQUIRED + ","
            + "\"identityDocumentTypes\":[1,2,3],"
            + "\"residenceDocumentTypes\":[1,2],"
            + "\"termsConditionsUrl\":\"" + TERMS_CONDITIONS_URL + "\","
            + "\"riskWarningDisclaimerUrl\":\"" + RISK_WARNING_DISCLAIMER_URL + "\","
            + "\"dataSharingAgreementUrl\":\"" + DATA_SHARING_AGREEMENT_URL + "\","
            + "\"minAge\":21"
            + "}";

    private DummyAyondoData()
    {
        throw new IllegalArgumentException("No constructor");
    }
}
