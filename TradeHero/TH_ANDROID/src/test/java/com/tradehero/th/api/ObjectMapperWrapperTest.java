package com.ayondo.academy.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ayondo.academyRobolectricTestRunner;
import com.tradehero.common.annotation.ForApp;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.api.achievement.UserAchievementDTO;
import com.ayondo.academy.api.achievement.key.UserAchievementId;
import com.ayondo.academy.api.leaderboard.def.LeaderboardDefDTO;
import com.ayondo.academy.api.position.GetPositionsDTO;
import com.ayondo.academy.api.position.PositionDTO;
import com.ayondo.academy.base.TestTHApp;
import com.ayondo.academy.persistence.achievement.UserAchievementCacheRx;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static com.ayondo.academy.utils.broadcast.BroadcastConstants.ACHIEVEMENT_INTENT_FILTER;
import static com.ayondo.academy.utils.broadcast.BroadcastConstants.KEY_USER_ACHIEVEMENT_ID;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class ObjectMapperWrapperTest extends BaseApiTestClass
{
    @Inject @ForApp ObjectMapper objectMapper;
    @Inject UserAchievementCacheRx userAchievementCache;

    private InputStream positionDTOBody1Stream;
    private InputStream positionDTOBody2Stream;
    private InputStream leaderboardDefDTOStream;

    LocalBroadcastManager localBroadcastManager;

    @Before
    public void setUp() throws IOException
    {
        TestTHApp.staticInject(this);
        localBroadcastManager = LocalBroadcastManager.getInstance(TestTHApp.context());
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

        UserAchievementDTO userAchievementDTO = userAchievementCache.get(new UserAchievementId(1))
                .toBlocking()
                .first()
                .second;
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
        }, ACHIEVEMENT_INTENT_FILTER);

        objectMapper.readValue(positionDTOBody1Stream, GetPositionsDTO.class);

        Robolectric.getBackgroundThreadScheduler().advanceToLastPostedRunnable();
        Robolectric.getForegroundThreadScheduler().advanceToLastPostedRunnable();

        assertThat(receivedIntents).isNotEmpty();

        Intent aReceivedIntent = receivedIntents.get(0);

        assertThat(aReceivedIntent).isNotNull();

        Bundle bundle = aReceivedIntent.getBundleExtra(KEY_USER_ACHIEVEMENT_ID);
        assertThat(bundle).isNotNull();

        UserAchievementId userAchievementId = new UserAchievementId(bundle);
        assertThat(userAchievementId).isNotNull();
        assertThat(userAchievementId.key).isEqualTo(1);

        UserAchievementDTO userAchievementDTO = userAchievementCache.get(userAchievementId)
                .toBlocking()
                .first()
                .second;

        assertThat(userAchievementDTO.id).isEqualTo(1);
    }
}
