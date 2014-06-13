package com.tradehero.th.api.competition;

import java.util.List;

public class CompetitionFormDTO
{
    public int ProviderId;
    public String OutboundName;
    public int UserId;
    public String ObjectType;
    public Integer AdId;

    public List<String> PropertiesToRecord; // TODO not sure this is part of the DTO
}
