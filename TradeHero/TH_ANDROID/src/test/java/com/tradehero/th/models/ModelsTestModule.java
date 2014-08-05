package com.tradehero.th.models;

import com.tradehero.th.models.leaderboard.ModelsLeaderboardTestModule;
import com.tradehero.th.models.position.PositionDTOUtilsTest;
import com.tradehero.th.models.push.PushTestModule;
import com.tradehero.th.models.share.ModelsShareTestModule;
import com.tradehero.th.models.user.ModelsUserTestModule;
import dagger.Module;

@Module(
        injects = {
            PositionDTOUtilsTest.class,
        },
        includes = {
                ModelsLeaderboardTestModule.class,
                PushTestModule.class,
                ModelsShareTestModule.class,
                ModelsUserTestModule.class,
        },
        complete = false,
        library = true
)
public class ModelsTestModule
{
}
