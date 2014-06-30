package com.tradehero.th.models;

import com.tradehero.th.models.leaderboard.ModelsLeaderboardTestModule;
import com.tradehero.th.models.push.PushTestModule;
import dagger.Module;

@Module(
        includes = {
                ModelsLeaderboardTestModule.class,
                PushTestModule.class,
        },
        complete = false,
        library = true
)
public class ModelsTestModule
{
}
