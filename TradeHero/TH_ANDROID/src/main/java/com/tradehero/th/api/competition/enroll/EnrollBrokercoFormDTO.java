package com.tradehero.th.api.competition.enroll;

import com.tradehero.th.api.competition.CompetitionFormDTO;
import java.util.List;

public class EnrollBrokercoFormDTO extends CompetitionFormDTO
{
    public String PreviousTrades;
    public List<String> Subscription;
    public boolean AcceptEula;
}
