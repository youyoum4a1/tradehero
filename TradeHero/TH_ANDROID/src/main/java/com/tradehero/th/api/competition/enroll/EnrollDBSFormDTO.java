package com.tradehero.th.api.competition.enroll;

import com.tradehero.th.api.competition.CompetitionFormDTO;
import java.util.Date;


public class EnrollDBSFormDTO extends CompetitionFormDTO
{
    public static final String TAG = EnrollDBSFormDTO.class.getSimpleName();

    public Date Dob;
    public String IncomeRange;
    public String RiskLevel;
    public String ExistingCustomer;
    public boolean AcceptTerms;
}
