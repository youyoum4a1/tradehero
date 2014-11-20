package com.tradehero.th.fragments.games;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.metrics.Analytics;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.games.GameScore;
import com.tradehero.th.api.games.MiniGameDefDTO;
import com.tradehero.th.api.games.MiniGameDefKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.MiniGameServiceWrapper;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.AttributesEvent;
import com.tradehero.th.utils.route.THRouter;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import rx.Observer;
import timber.log.Timber;

@Routable({
        "games/:" + GameWebViewFragment.GAME_ID_KEY
                + "/recordScore/:" + GameWebViewFragment.GAME_SCORE_KEY
                + "/level/:" + GameWebViewFragment.GAME_LEVEL_KEY
})
public class GameWebViewFragment extends BaseWebViewFragment
{
    static final String GAME_ID_KEY = "gameId";
    static final String GAME_SCORE_KEY = "scoreNum";
    static final String GAME_LEVEL_KEY = "levelNum";
    static final String GAME_NAME_KEY = "gameName";

    @Inject THRouter thRouter;
    @Inject MiniGameServiceWrapper gamesServiceWrapper;
    @Inject Analytics analytics;

    @RouteProperty(GAME_ID_KEY) protected Integer gameId;
    @RouteProperty(GAME_SCORE_KEY) protected Integer score;
    @RouteProperty(GAME_LEVEL_KEY) protected Integer level;

    private long beginTime;
    private String gameName;
    
    public static void putUrl(@NonNull Bundle args, @NonNull MiniGameDefDTO miniGameDefDTO, @NonNull UserBaseKey userBaseKey)
    {
        putUrl(args, miniGameDefDTO.url + "?userId=" + userBaseKey.getUserId());
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(this);
        thRouter.inject(this);
        gameName = getGameName(getArguments());
    }

    @Override protected int getLayoutResId()
    {
        return R.layout.fragment_webview;
    }

    @Override public void onResume()
    {
        super.onResume();
        thRouter.inject(this);
        submitScore();
        beginTime = System.currentTimeMillis();
    }

    @Override public void onPause()
    {
        reportAnalytics();
        super.onPause();
    }

    private void reportAnalytics() {
        long duration = (System.currentTimeMillis()-beginTime)/1000;
        String s = AnalyticsConstants.Time10M;
        if (duration <= 10)
        {
            s = AnalyticsConstants.Time1T10S;
        }
        else if (duration <= 30)
        {
            s = AnalyticsConstants.Time11T30S;
        }
        else if (duration <= 60)
        {
            s = AnalyticsConstants.Time31T60S;
        }
        else if (duration <= 180)
        {
            s = AnalyticsConstants.Time1T3M;
        }
        else if (duration <= 600)
        {
            s = AnalyticsConstants.Time3T10M;
        }
        Map<String, String> map = new HashMap<>();
        map.put(AnalyticsConstants.GamePlayed, gameName);
        map.put(AnalyticsConstants.TimeInGame, s);
        analytics.fireEvent(new AttributesEvent(AnalyticsConstants.GamePlaySummary, map));
    }

    protected void submitScore()
    {
        if (gameId != null && score != null && level != null)
        {
            gamesServiceWrapper.recordScore(new MiniGameDefKey(gameId), new GameScore(score, level))
                    .subscribe(new Observer<BaseResponseDTO>()
                    {
                        @Override public void onNext(BaseResponseDTO baseResponseDTO)
                        {
                            Timber.d("Received %s", baseResponseDTO);
                        }

                        @Override public void onCompleted()
                        {
                            clearScore();
                        }

                        @Override public void onError(Throwable e)
                        {
                            Timber.e(e, "Failed to report score");
                        }
                    });
        }
    }

    protected void clearScore()
    {
        this.gameId = null;
        this.score = null;
        this.level = null;
        getArguments().remove(GAME_ID_KEY);
        getArguments().remove(GAME_SCORE_KEY);
        getArguments().remove(GAME_LEVEL_KEY);
    }

    public static void putGameName(@NonNull Bundle args, @NonNull String url)
    {
        args.putString(GAME_NAME_KEY, url);
    }


    public static String getGameName(@Nullable Bundle args)
    {
        if (args != null)
        {
            return args.getString(GAME_NAME_KEY);
        }
        return "";
    }
}
