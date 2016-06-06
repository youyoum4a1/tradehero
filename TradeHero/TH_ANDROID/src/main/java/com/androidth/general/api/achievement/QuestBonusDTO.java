package com.androidth.general.api.achievement;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.achievement.key.QuestBonusId;

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
