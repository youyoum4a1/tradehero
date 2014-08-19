package com.tradehero.th.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.achievement.UserAchievementId;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.position.GetPositionsDTO;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.utils.achievement.ForAchievement;
import com.tradehero.th.utils.achievement.UserAchievementDTOUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(RobolectricMavenTestRunner.class)
public class ObjectMapperWrapperTest extends BaseApiTest
{
    @Inject @ForApp ObjectMapper objectMapper;
    @Inject UserAchievementDTOUtil userAchievementDTOUtil;
    @Inject Context context;

    private InputStream positionDTOBody1Stream;
    private InputStream positionDTOBody2Stream;
    private InputStream leaderboardDefDTOStream;

    @Inject LocalBroadcastManager localBroadcastManager;
    @Inject @ForAchievement IntentFilter achievementIntentFilter;

    @Before
    public void setUp() throws IOException
    {
        positionDTOBody1Stream = PositionDTO.class.getResourceAsStream(getPackagePath() + "/position/GetPositionsDTOWithAchievementBody1.json");
        positionDTOBody2Stream = PositionDTO.class.getResourceAsStream(getPackagePath() + "/position/GetPositionsDTOWithoutAchievementBody1.json");
        leaderboardDefDTOStream = LeaderboardDefDTO.class.getResourceAsStream(getPackagePath() + "/leaderboard/def/LeaderboardDefDTOListBody1.json");
    }

    @Test public void mapperIsWrapper()
    {
        assertEquals(((Object) objectMapper).getClass(), ObjectMapperWrapper.class);
    }

    @Test public void mapperWorksAsRegular() throws IOException
    {
        GetPositionsDTO getPositionsDTO = objectMapper.readValue(positionDTOBody1Stream, GetPositionsDTO.class);
        assertThat(getPositionsDTO.securities.size()).isEqualTo(10);
        assertThat(getPositionsDTO.securities.get(1).id).isEqualTo(9256);

        GetPositionsDTO getPositionsDTO2 = objectMapper.readValue(positionDTOBody2Stream, GetPositionsDTO.class);
        assertThat(getPositionsDTO2.securities.size()).isEqualTo(10);
        assertThat(getPositionsDTO2.securities.get(1).id).isEqualTo(9256);

        List<LeaderboardDefDTO> leaderboardDefDTOList = objectMapper.readValue(leaderboardDefDTOStream, new TypeReference<List<LeaderboardDefDTO>>()
        {
        });

        assertThat(leaderboardDefDTOList).isNotEmpty();
    }

    @Test public void testMapperShouldPickUpAchievement() throws IOException
    {
        objectMapper.readValue(positionDTOBody1Stream, GetPositionsDTO.class);

        UserAchievementDTO userAchievementDTO = userAchievementDTOUtil.get(new UserAchievementId(1));
        assertThat(userAchievementDTO).isNotNull();
        assertThat(userAchievementDTO.id).isEqualTo(1);
    }

    @Test public void testMapperShouldTriggerReceiver() throws IOException
    {
        final List<Intent> receivedIntents = new ArrayList<>();

        localBroadcastManager.registerReceiver(new BroadcastReceiver()
        {
            @Override public void onReceive(Context context, Intent intent)
            {
                receivedIntents.add(intent);
            }
        }, achievementIntentFilter);

        objectMapper.readValue(positionDTOBody1Stream, GetPositionsDTO.class);

        Robolectric.runBackgroundTasks();
        Robolectric.runUiThreadTasksIncludingDelayedTasks();

        assertThat(receivedIntents).isNotEmpty();

        Intent aReceivedIntent = receivedIntents.get(0);

        assertThat(aReceivedIntent).isNotNull();

        Bundle bundle = aReceivedIntent.getBundleExtra(UserAchievementDTO.class.getName());
        assertThat(bundle).isNotNull();

        UserAchievementId userAchievementId = new UserAchievementId(bundle);
        assertThat(userAchievementId).isNotNull();
        assertThat(userAchievementId.key).isEqualTo(1);

        UserAchievementDTO userAchievementDTO = userAchievementDTOUtil.get(userAchievementId);

        assertThat(userAchievementDTO.id).isEqualTo(1);
    }
}
