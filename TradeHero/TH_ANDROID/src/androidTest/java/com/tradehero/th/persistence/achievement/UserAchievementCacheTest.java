package com.tradehero.th.persistence.achievement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.tradehero.THRobolectric;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.api.achievement.AchievementDefDTO;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.base.TestTHApp;
import com.tradehero.th.utils.broadcast.BroadcastTaskNew;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.tradehero.th.utils.broadcast.BroadcastConstants.ACHIEVEMENT_INTENT_FILTER;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
public class UserAchievementCacheTest
{
    @Inject UserAchievementCacheRx userAchievementCache;
    @Inject LocalBroadcastManager localBroadcastManager;

    @Before
    public void setUp()
    {
        TestTHApp.staticInject(this);
    }

    @Test
    public void testWhenNoReceiverItShouldRepeat() throws Exception
    {
        UserAchievementDTO userAchievementDTO = new UserAchievementDTO();
        userAchievementDTO.id = 1;
        BroadcastTaskNew broadcastTask = userAchievementCache.onNextAndBroadcast(userAchievementDTO);
        assertThat(broadcastTask).isNotNull();
        assertThat(broadcastTask.getCurrentTry()).isEqualTo(1);

        Thread.sleep(10000);
        THRobolectric.runBgUiTasks(3);

        assertThat(broadcastTask.getCurrentTry()).isGreaterThan(1);
    }

    @Test
    public void testWhenMaxIsReachedShouldStop() throws Exception
    {
        UserAchievementDTO userAchievementDTO = new UserAchievementDTO();
        userAchievementDTO.id = 1;
        userAchievementDTO.achievementDef = new AchievementDefDTO();
        BroadcastTaskNew broadcastTask = userAchievementCache.onNextAndBroadcast(userAchievementDTO);
        assertThat(broadcastTask).isNotNull();
        assertThat(broadcastTask.getCurrentTry()).isEqualTo(1);
        assertThat(broadcastTask.isRunning).isTrue();

        Thread.sleep(25000);
        THRobolectric.runBgUiTasks(5);

        assertThat(broadcastTask.getCurrentTry()).isEqualTo(4);
        assertThat(broadcastTask.isRunning).isFalse();
    }

    @Test
    public void testWhenReceiverExistShouldStop() throws Exception
    {
        localBroadcastManager.registerReceiver(new BroadcastReceiver()
        {
            @Override public void onReceive(Context context, Intent intent)
            {
            }
        }, ACHIEVEMENT_INTENT_FILTER);

        UserAchievementDTO userAchievementDTO = new UserAchievementDTO();
        userAchievementDTO.id = 1;
        BroadcastTaskNew broadcastTask = userAchievementCache.onNextAndBroadcast(userAchievementDTO);
        assertThat(broadcastTask).isNotNull();
        assertThat(broadcastTask.getCurrentTry()).isEqualTo(0);

        assertThat(broadcastTask.isRunning).isFalse();
    }

    @Test
    public void testWhenReceiverExistAfterFirstTryShouldStop() throws Exception
    {
        UserAchievementDTO userAchievementDTO = new UserAchievementDTO();
        userAchievementDTO.id = 1;
        BroadcastTaskNew broadcastTask = userAchievementCache.onNextAndBroadcast(userAchievementDTO);
        assertThat(broadcastTask).isNotNull();
        assertThat(broadcastTask.getCurrentTry()).isEqualTo(1);
        assertThat(broadcastTask.isRunning).isTrue();

        Thread.sleep(10000);
        THRobolectric.runBgUiTasks(2);

        localBroadcastManager.registerReceiver(new BroadcastReceiver()
        {
            @Override public void onReceive(Context context, Intent intent)
            {

            }
        }, ACHIEVEMENT_INTENT_FILTER);

        Thread.sleep(5000);
        THRobolectric.runBgUiTasks(1);
        assertThat(broadcastTask.getCurrentTry()).isEqualTo(3);
        assertThat(broadcastTask.isRunning).isFalse();

    }
}
