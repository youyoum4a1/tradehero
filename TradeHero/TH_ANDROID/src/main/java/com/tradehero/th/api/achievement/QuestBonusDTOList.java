package com.ayondo.academy.api.achievement;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class QuestBonusDTOList extends BaseArrayList<QuestBonusDTO> implements DTO
{
    /**
     * Get <i>next</i> number of {@link com.ayondo.academy.api.achievement.QuestBonusDTO} inclusive of <i>currentLevel</i>
     *
     * <p> If the <i>currentLevel</i> + <i>next</i> is greater than list's size, it will return a list with size of less than <i>next</i> </p>
     *
     * @param currentLevel currentLevel
     * @param next number of {@link com.ayondo.academy.api.achievement.QuestBonusDTO} to be retrieved
     * @return {@link java.util.List} of {@link com.ayondo.academy.api.achievement.QuestBonusDTO}
     */
    @NonNull public List<QuestBonusDTO> getNextInclusive(int currentLevel, int next)
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
     * Get <i>numOfItems</i> - or less if the size of the list is less than <i>numOfItems</i> - {@link com.ayondo.academy.api.achievement.QuestBonusDTO}
     * which includes the {@link com.ayondo.academy.api.achievement.QuestBonusDTO} with <i>currentLevel</i>
     *
     * @param currentLevel currentLevel
     * @return {@link java.util.List} of {@link com.ayondo.academy.api.achievement.QuestBonusDTO}
     */
    public List<QuestBonusDTO> getInclusive(int currentLevel, int numOfItems)
    {
        List<QuestBonusDTO> list = getNextInclusive(currentLevel, numOfItems);
        if (list.size() < numOfItems && size() >= numOfItems)
        {
            for (int i = size() - 1 - list.size(); i >= 0 && list.size() < numOfItems; i--)
            {
                list.add(0, get(i));
            }
        }
        return list;
    }

    @Nullable public QuestBonusDTO getPrevious(int currentLevel)
    {
        ListIterator<QuestBonusDTO> iterator = listIterator();
        QuestBonusDTO questBonusDTO = null;
        while (iterator.hasNext() && currentLevel >= 0)
        {
            QuestBonusDTO dto = iterator.next();
            if (dto.level < currentLevel)
            {
                questBonusDTO = dto;
            }
            else
            {
                break;
            }
        }
        return questBonusDTO;
    }

    @NonNull public List<QuestBonusDTO> getPrevious(int currentLevel, int numOfItems)
    {
        List<QuestBonusDTO> questBonusDTOList = new ArrayList<>();
        if (numOfItems > 0)
        {
            for (int i = size() - 1 ; i >= 0 && questBonusDTOList.size() < numOfItems; i--)
            {
                QuestBonusDTO dto = get(i);
                if(dto.level < currentLevel)
                {
                    questBonusDTOList.add(0, dto);
                }
            }
        }
        return questBonusDTOList;
    }
}
