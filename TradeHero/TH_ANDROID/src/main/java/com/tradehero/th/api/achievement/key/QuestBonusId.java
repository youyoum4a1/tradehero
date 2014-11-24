package com.tradehero.th.api.achievement.key;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.th.api.achievement.QuestBonusDTO;

public class QuestBonusId extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY = QuestBonusDTO.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public QuestBonusId(@NonNull Integer key)
    {
        super(key);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }
}
