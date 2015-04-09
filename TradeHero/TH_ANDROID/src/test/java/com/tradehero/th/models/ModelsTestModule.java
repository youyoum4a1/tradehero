package com.tradehero.th.models;

import com.tradehero.th.models.leaderboard.ModelsLeaderboardTestModule;
import com.tradehero.th.models.level.LevelTestModule;
import com.tradehero.th.models.position.PositionDTOUtilsTest;
import com.tradehero.th.models.push.PushTestModule;
import dagger.Module;

@Module(
        injects = {
            PositionDTOUtilsTest.class,
        },
        includes = {
                ModelsLeaderboardTestModule.class,
                PushTestModule.class,
                LevelTestModule.class,
        },
        complete = false,
        library = true
)
public class ModelsTestModule
{
}
