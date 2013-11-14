package com.tradehero.th.api.competition.outbound;

import com.tradehero.th.api.competition.CompetitionFormDTO;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 7:42 PM To change this template use File | Settings | File Templates. */
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
