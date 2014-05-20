package com.tradehero.th.api.competition.enroll;

import com.tradehero.th.api.competition.CompetitionFormDTO;
import java.util.Date;

public class EnrollDBSFormDTO extends CompetitionFormDTO
{
    public Date Dob;
    public String IncomeRange;
    public String RiskLevel;
    public String ExistingCustomer;
    public boolean AcceptTerms;
}
