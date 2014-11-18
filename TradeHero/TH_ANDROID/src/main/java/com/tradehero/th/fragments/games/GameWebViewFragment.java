package com.tradehero.th.fragments.games;

import android.os.Bundle;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.games.GameId;
import com.tradehero.th.api.games.GameScore;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.GamesServiceWrapper;
import com.tradehero.th.utils.route.THRouter;
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

    @Inject THRouter thRouter;
    @Inject GamesServiceWrapper gamesServiceWrapper;

    @RouteProperty(GAME_ID_KEY) protected Integer gameId;
    @RouteProperty(GAME_SCORE_KEY) protected Integer score;
    @RouteProperty(GAME_LEVEL_KEY) protected Integer level;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(this);
        thRouter.inject(this);
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
    }

    protected void submitScore()
    {
        if (gameId != null && score != null && level != null)
        {
            gamesServiceWrapper.recordScore(new GameId(gameId), new GameScore(score, level))
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
}
