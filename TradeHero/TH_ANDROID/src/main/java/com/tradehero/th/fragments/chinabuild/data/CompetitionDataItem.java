package com.tradehero.th.fragments.chinabuild.data;

import java.io.Serializable;

public class CompetitionDataItem implements CompetitionInterface,Serializable
{
    public UserCompetitionDTO userCompetitionDTO;

    public CompetitionDataItem()
    {
    }

    public CompetitionDataItem(UserCompetitionDTO userCompetitionDTO)
    {
        this.userCompetitionDTO = userCompetitionDTO;
    }
}
