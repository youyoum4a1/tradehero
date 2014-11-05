package com.tradehero.th.api.achievement;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.achievement.key.QuestBonusId;
import android.support.annotation.NonNull;

public class QuestBonusDTO implements DTO
{
    public int id;
    public int level;
    public double bonus;
    public String levelStr;
    public String bonusStr;

    @NonNull public QuestBonusId getQuestBonusId()
    {
        return new QuestBonusId(id);
    }

    @Override public String toString()
    {
        return "QuestBonusDTO{" +
                "id=" + id +
                ", level=" + level +
                ", bonus=" + bonus +
                ", levelStr='" + levelStr + '\'' +
                ", bonusStr='" + bonusStr + '\'' +
                '}';
    }
}
