package com.tradehero.th.api.achievement.key;

import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.th.api.achievement.QuestBonusDTO;

public class QuestBonusId extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY = QuestBonusDTO.class.getName() + ".key";

    public QuestBonusId(Integer key)
    {
        super(key);
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }
}
