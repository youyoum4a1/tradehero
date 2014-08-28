package com.tradehero.th.api.achievement;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import org.jetbrains.annotations.NotNull;

public class QuestBonusDTOList extends BaseArrayList<QuestBonusDTO> implements DTO
{
    private static final int DEFAULT_SIZE = 5;

    /**
     * Get <i>next</i> number of {@link com.tradehero.th.api.achievement.QuestBonusDTO} inclusive of <i>currentLevel</i>
     *
     * <p> If the <i>currentLevel</i> + <i>next</i> is greater than list's size, it will return a list with size of less than <i>next</i> </p>
     *
     * @param currentLevel currentLevel
     * @param next number of {@link com.tradehero.th.api.achievement.QuestBonusDTO} to be retrieved
     * @return {@link java.util.List} of {@link com.tradehero.th.api.achievement.QuestBonusDTO}
     */
    @NotNull public List<QuestBonusDTO> getNextInclusive(int currentLevel, int next)
    {
        List<QuestBonusDTO> list = new ArrayList<>();
        ListIterator<QuestBonusDTO> iterator = listIterator();
        while (iterator.hasNext() && next > 0 && currentLevel >= 0)
        {
            QuestBonusDTO questBonusDTO = iterator.next();
            if (questBonusDTO.level >= currentLevel)
            {
                list.add(questBonusDTO);
                currentLevel++;
                next--;
            }
        }
        return list;
    }

    /**
     * Get 5 or less {@link com.tradehero.th.api.achievement.QuestBonusDTO} which includes the {@link com.tradehero.th.api.achievement.QuestBonusDTO}
     * with <i>currentLevel</i>
     *
     * @param currentLevel currentLevel
     * @return {@link java.util.List} of {@link com.tradehero.th.api.achievement.QuestBonusDTO}
     */
    public List<QuestBonusDTO> getInclusive(int currentLevel)
    {
        List<QuestBonusDTO> list = getNextInclusive(currentLevel, DEFAULT_SIZE);
        if (list.size() < DEFAULT_SIZE && size() >= DEFAULT_SIZE)
        {
            for (int i = size() - 1 - list.size(); i >= 0 && list.size() < DEFAULT_SIZE; i--)
            {
                list.add(0, get(i));
            }
        }
        return list;
    }
}
