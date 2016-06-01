package com.ayondo.academy.api;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.achievement.key.MockQuestBonusId;
import javax.inject.Inject;

public class ValidMockerDebug
{
    public final ValidMocker validMocker;

    //<editor-fold desc="Construction">
    @Inject public ValidMockerDebug(ValidMocker validMocker)
    {
        super();
        this.validMocker = validMocker;
    }
    //</editor-fold>

    //<editor-fold desc="Create valid parameters">
    public Object mockValidParameter(@NonNull Class<?> type)
    {
        if (type.equals(MockQuestBonusId.class))
        {
            return new MockQuestBonusId(1, 2, 3);
        }

        return validMocker.mockValidParameter(type);
    }
    //</editor-fold>
}
