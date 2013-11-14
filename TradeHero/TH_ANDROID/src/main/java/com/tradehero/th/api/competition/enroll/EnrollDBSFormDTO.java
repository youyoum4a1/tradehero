package com.tradehero.th.api.competition.enroll;

import com.tradehero.th.api.competition.CompetitionFormDTO;
import java.util.Date;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 7:49 PM To change this template use File | Settings | File Templates. */
public class EnrollDBSFormDTO extends CompetitionFormDTO
{
    public static final String TAG = EnrollDBSFormDTO.class.getSimpleName();

    public Date Dob;
    public String IncomeRange;
    public String RiskLevel;
    public String ExistingCustomer;
    public boolean AcceptTerms;
}
