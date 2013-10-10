package com.tradehero.th.api.competition;

import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 10/10/13 Time: 6:05 PM To change this template use File | Settings | File Templates. */
public class CompetitionFormDTO
{
    public int ProviderId;
    public String OutboundName;
    public int UserId;
    public String ObjectType;
    public Integer AdId;

    public List<String> PropertiesToRecord; // TODO not sure this is part of the DTO
}
