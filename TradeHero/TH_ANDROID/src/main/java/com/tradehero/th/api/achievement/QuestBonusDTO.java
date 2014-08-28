package com.tradehero.th.api.achievement;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.achievement.key.QuestBonusId;

public class QuestBonusDTO implements DTO
{
    public int id;
    public int level;
    public double bonus;

    public QuestBonusId getQuestBonusId()
    {
        return new QuestBonusId(id);
    }

    @Override public String toString()
    {
        return "QuestBonusDTO{" +
                "id=" + id +
                ", level=" + level +
                ", bonus=" + bonus +
                '}';
    }
}
