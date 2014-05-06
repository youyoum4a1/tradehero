package com.tradehero.th.api.competition.outbound;

import com.tradehero.th.api.competition.CompetitionFormDTO;


public class BrokercoOutboundFormDTO extends CompetitionFormDTO
{
    public static final String TAG = BrokercoOutboundFormDTO.class.getSimpleName();

    public String Name;
    public String Email;
    public String PhoneNumber;

    public boolean Criteria1;
    public boolean Criteria2;
    public boolean Criteria3;
}
