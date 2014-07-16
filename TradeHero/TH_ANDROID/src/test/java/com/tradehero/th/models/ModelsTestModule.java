package com.tradehero.th.models;

import com.tradehero.th.models.leaderboard.ModelsLeaderboardTestModule;
import com.tradehero.th.models.push.PushTestModule;
import com.tradehero.th.models.share.ModelsShareTestModule;
import dagger.Module;

@Module(
        includes = {
                ModelsLeaderboardTestModule.class,
                PushTestModule.class,
                ModelsShareTestModule.class,
        },
        complete = false,
        library = true
)
public class ModelsTestModule
{
}
