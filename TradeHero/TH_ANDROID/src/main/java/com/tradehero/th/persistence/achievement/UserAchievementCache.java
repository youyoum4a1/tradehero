package com.tradehero.th.persistence.achievement;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.achievement.key.UserAchievementId;
import com.tradehero.th.network.service.AchievementServiceWrapper;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class UserAchievementCache extends StraightDTOCacheNew<UserAchievementId, UserAchievementDTO>
{
    //TODO implements CutDTO when AchievementsDTO has its own cache?
    public static final String KEY_USER_ACHIEVEMENT_ID = UserAchievementCache.class.getName() + ".achievementId";
    public static final String INTENT_ACTION_NAME = "com.tradehero.th.achievement.ALERT";
    public static final String KEY_ACHIEVEMENT_NODE = "achievements";

    public static final int DEFAULT_SIZE = 20;

    private static final int DELAY_INTERVAL = 5000;

    @NotNull private final AchievementServiceWrapper achievementServiceWrapper;
    @NotNull private final LocalBroadcastManager localBroadcastManager;

    @Inject public UserAchievementCache(@NotNull AchievementServiceWrapper achievementServiceWrapper,
            @NotNull LocalBroadcastManager localBroadcastManager)
    {
        super(DEFAULT_SIZE);
        this.achievementServiceWrapper = achievementServiceWrapper;
        this.localBroadcastManager = localBroadcastManager;
    }

    @NotNull @Override public UserAchievementDTO fetch(@NotNull UserAchievementId key) throws Throwable
    {
        return achievementServiceWrapper.getUserAchievementDetails(key);
    }

    @Nullable public UserAchievementDTO pop(@NotNull UserAchievementId userAchievementId)
    {
        UserAchievementDTO userAchievementDTO = get(userAchievementId);
        if (userAchievementDTO != null)
        {
            remove(userAchievementId);
        }
        return userAchievementDTO;
    }

    public void remove(@NotNull UserAchievementId userAchievementId)
    {
        invalidate(userAchievementId);
    }

    public boolean shouldShow(@NotNull UserAchievementId userAchievementId)
    {
        UserAchievementDTO userAchievementDTO = get(userAchievementId);
        return userAchievementDTO != null &&
                !userAchievementDTO.shouldShow();
    }

    public void put(@NotNull List<? extends UserAchievementDTO> userAchievementDTOs)
    {
        for (UserAchievementDTO userAchievementDTO : userAchievementDTOs)
        {
            putAndBroadcast(userAchievementDTO);
        }
    }

    public BroadcastTask putAndBroadcast(@NotNull UserAchievementDTO userAchievementDTO)
    {
        put(userAchievementDTO.getUserAchievementId(), userAchievementDTO);
        final UserAchievementId userAchievementId = userAchievementDTO.getUserAchievementId();
        BroadcastTask broadcastTask = new BroadcastTask(userAchievementId, localBroadcastManager);
        broadcastTask.start();
        return broadcastTask;
    }

    protected static class BroadcastTask
    {
        private static final int MAX_BROADCAST_TRY = 4;

        private int mDelayInterval = DELAY_INTERVAL;
        private UserAchievementId mUserAchievementId;
        private Handler mHandler;
        private LocalBroadcastManager mLocalBroadcastManager;
        private volatile int mTry;
        public volatile boolean isRunning;

        private Runnable mTask = new Runnable()
        {
            @Override public void run()
            {
                if (mTry >= MAX_BROADCAST_TRY)
                {
                    stop();
                }
                else if (!broadcast(mUserAchievementId))
                {
                    mHandler.postDelayed(mTask, mDelayInterval);
                    mTry++;
                }
                else
                {
                    stop();
                }
            }
        };

        public BroadcastTask(UserAchievementId mUserAchievementId, LocalBroadcastManager mLocalBroadcastManager)
        {
            this.mUserAchievementId = mUserAchievementId;
            this.mLocalBroadcastManager = mLocalBroadcastManager;
            if (Looper.myLooper() == null)
            {
                Looper.prepare();
            }
            mHandler = new Handler();
        }

        public int getCurrentTry()
        {
            return mTry;
        }

        private boolean broadcast(UserAchievementId userAchievementId)
        {
            Intent i = new Intent(INTENT_ACTION_NAME);
            i.putExtra(KEY_USER_ACHIEVEMENT_ID, userAchievementId.getArgs());
            return mLocalBroadcastManager.sendBroadcast(i);
        }

        public void start()
        {
            isRunning = true;
            mTask.run();
        }

        public void stop()
        {
            mHandler.removeCallbacks(mTask);
            isRunning = false;
        }
    }
}
