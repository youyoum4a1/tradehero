package com.ayondo.academy.persistence.achievement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import com.ayondo.academyRobolectric;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.api.achievement.AchievementDefDTO;
import com.ayondo.academy.api.achievement.UserAchievementDTO;
import com.ayondo.academy.base.TestTHApp;
import com.ayondo.academy.utils.broadcast.BroadcastTaskNew;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;

import static com.ayondo.academy.utils.broadcast.BroadcastConstants.ACHIEVEMENT_INTENT_FILTER;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class UserAchievementCacheTest
{
    @Inject UserAchievementCacheRx userAchievementCache;
    LocalBroadcastManager localBroadcastManager;

    @Before
    public void setUp()
    {
        TestTHApp.staticInject(this);
        localBroadcastManager = LocalBroadcastManager.getInstance(TestTHApp.context());
    }

    @Test
    public void testWhenNoReceiverItShouldRepeat() throws Exception
    {
        UserAchievementDTO userAchievementDTO = new UserAchievementDTO();
        userAchievementDTO.id = 1;
        userAchievementDTO.achievementDef = new AchievementDefDTO();
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
        assertThat(broadcastTask.getCurrentTry()).isLessThanOrEqualTo(1);
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
        userAchievementDTO.achievementDef = new AchievementDefDTO();
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
        userAchievementDTO.achievementDef = new AchievementDefDTO();
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
