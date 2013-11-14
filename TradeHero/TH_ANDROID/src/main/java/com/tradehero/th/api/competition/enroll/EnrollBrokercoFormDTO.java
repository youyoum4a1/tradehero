package com.tradehero.th.api.competition.enroll;

import com.tradehero.th.api.competition.CompetitionFormDTO;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 7:48 PM To change this template use File | Settings | File Templates. */
public class EnrollBrokercoFormDTO extends CompetitionFormDTO
{
    public static final String TAG = EnrollBrokercoFormDTO.class.getSimpleName();

    public String PreviousTrades;
    public List<String> Subscription;
    public boolean AcceptEula;
}
