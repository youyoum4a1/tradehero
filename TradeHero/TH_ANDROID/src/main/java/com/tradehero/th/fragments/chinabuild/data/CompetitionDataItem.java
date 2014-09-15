package com.tradehero.th.fragments.chinabuild.data;

public class CompetitionDataItem implements CompetitionInterface
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
