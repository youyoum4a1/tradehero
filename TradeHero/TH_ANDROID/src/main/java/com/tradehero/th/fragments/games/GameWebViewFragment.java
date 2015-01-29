package com.tradehero.th.fragments.games;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.api.games.GameScore;
import com.tradehero.th.api.games.MiniGameDefDTO;
import com.tradehero.th.api.games.MiniGameDefKey;
import com.tradehero.th.api.games.MiniGameScoreResponseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.MiniGameServiceWrapper;
import com.tradehero.th.persistence.games.MiniGameDefCache;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.AnalyticsDuration;
import com.tradehero.th.utils.metrics.events.AttributesEvent;
import com.tradehero.th.utils.route.THRouter;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

@Routable({
        "games/:" + GameWebViewFragment.GAME_ID_KEY
                + "/recordScore/:" + GameWebViewFragment.GAME_SCORE_KEY
                + "/level/:" + GameWebViewFragment.GAME_LEVEL_KEY
})
public class GameWebViewFragment extends BaseWebViewFragment
{
    static final String GAME_ID_ARG_KEY = GameWebViewFragment.class.getName() + ".gameId";
    static final String GAME_ID_KEY = "gameId";
    static final String GAME_SCORE_KEY = "scoreNum";
    static final String GAME_LEVEL_KEY = "levelNum";

    @Inject THRouter thRouter;
    @Inject MiniGameDefCache miniGameDefCache;
    @Inject MiniGameServiceWrapper gamesServiceWrapper;
    @Inject Analytics analytics;

    protected MiniGameDefKey miniGameDefKey;
    @RouteProperty(GAME_ID_KEY) protected Integer gameId;
    @RouteProperty(GAME_SCORE_KEY) protected Integer score;
    @RouteProperty(GAME_LEVEL_KEY) protected Integer level;

    private long beginTime;
    @Nullable Subscription miniGameDefSubscription;
    protected MiniGameDefDTO miniGameDefDTO;
    protected boolean showedHowToPlay = false;
    @Nullable Subscription scoreSubmitSubscription;
    @Nullable Subscription scoreWindowSubscription;

    public static void putUrl(@NonNull Bundle args, @NonNull MiniGameDefDTO miniGameDefDTO, @NonNull UserBaseKey userBaseKey)
    {
        putUrl(args, miniGameDefDTO.url + "?userId=" + userBaseKey.getUserId());
    }

    public static void putGameId(@NonNull Bundle args, @NonNull MiniGameDefKey miniGameDefKey)
    {
        args.putBundle(GAME_ID_ARG_KEY, miniGameDefKey.getArgs());
    }

    @NonNull public static MiniGameDefKey getGameId(@NonNull Bundle args)
    {
        return new MiniGameDefKey(args.getBundle(GAME_ID_ARG_KEY));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(this);
        thRouter.inject(this);
        miniGameDefKey = getGameId(getArguments());
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.mini_game_webview_menu, menu);
    }

    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        displayName();
        boolean hasHowTo = miniGameDefDTO != null && miniGameDefDTO.howToPlayUrl != null;
        menu.findItem(R.id.how_to_menu).setVisible(hasHowTo);
    }

    @Override public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.how_to_menu:
                displayHowToPlay();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initViews(View v)
    {
        super.initViews(v);
        webView.getSettings().setUseWideViewPort(false);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSupportZoom(false);
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchMiniGameDef();
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
        webView.clearCache(true);
        reportAnalytics();
        super.onPause();
    }

    @Override public void onStop()
    {
        webView.clearCache(true);
        unsubscribe(miniGameDefSubscription);
        miniGameDefSubscription = null;
        unsubscribe(scoreSubmitSubscription);
        scoreSubmitSubscription = null;
        unsubscribe(scoreWindowSubscription);
        scoreWindowSubscription = null;
        super.onStop();
    }

    private void reportAnalytics()
    {
        AnalyticsDuration duration = AnalyticsDuration.sinceTimeMillis(beginTime);
        Map<String, String> map = new HashMap<>();
        map.put(AnalyticsConstants.GamePlayed, miniGameDefKey.key.toString());
        map.put(AnalyticsConstants.TimeInGame, duration.toString());
        analytics.fireEvent(new AttributesEvent(AnalyticsConstants.GamePlaySummary, map));
    }

    protected void fetchMiniGameDef()
    {
        unsubscribe(miniGameDefSubscription);
        miniGameDefSubscription = AndroidObservable.bindFragment(
                this,
                miniGameDefCache.get(miniGameDefKey))
                .map(new PairGetSecond<>())
                .subscribe(
                        this::linkWith,
                        this::handleFailedDef);
    }

    protected void linkWith(@NonNull MiniGameDefDTO miniGameDefDTO)
    {
        this.miniGameDefDTO = miniGameDefDTO;
        displayName();
        if (miniGameDefDTO.howToPlayUrl != null && !showedHowToPlay)
        {
            displayHowToPlay();
        }
    }

    protected void handleFailedDef(@NonNull Throwable e)
    {
        Timber.e(e, "Failed to get %s", miniGameDefKey);
    }

    protected void displayName()
    {
        if (miniGameDefDTO != null)
        {
            setActionBarTitle(miniGameDefDTO.name);
        }
    }

    protected void displayHowToPlay()
    {
        HowToPlayDialogFragment.newInstance(miniGameDefKey)
                .show(getFragmentManager(),
                        HowToPlayDialogFragment.class.getName());
        showedHowToPlay = true;
    }

    protected void submitScore()
    {
        if (gameId != null && score != null && level != null)
        {
            if (!gameId.equals(miniGameDefKey.key))
            {
                Timber.e(new IllegalArgumentException("Got gameId " + gameId + ", while it is for " + miniGameDefKey.key),
                        "Got gameId %d, while it is for %d", gameId, miniGameDefKey.key);
                THToast.show(R.string.error_submit_score_game_id_mismatch);
            }
            else
            {
                unsubscribe(scoreSubmitSubscription);
                scoreSubmitSubscription = AndroidObservable.bindFragment(
                        this,
                        gamesServiceWrapper.recordScore(new MiniGameDefKey(gameId), new GameScore(score, level)))
                        .subscribe(
                                this::showScore,
                                this::showFailedReportScore,
                                this::clearScore);
            }
        }
    }

    protected void showScore(@NonNull MiniGameScoreResponseDTO scoreResponse)
    {
        Timber.d("Received %s", scoreResponse);
        MiniGameScoreDialogFragment dialog = MiniGameScoreDialogFragment.newInstance();
        unsubscribe(scoreWindowSubscription);
        scoreWindowSubscription = dialog.getButtonClickedObservable().subscribe(
                this::handleScoreWindowClicked,
                this::handleScoreWindowError);
        dialog.display(scoreResponse);
        dialog.show(getActivity().getFragmentManager(), MiniGameScoreDialogFragment.class.getName());
    }

    protected void handleScoreWindowClicked(@NonNull @IdRes Integer buttonId)
    {
        switch (buttonId)
        {
            case MiniGameScoreDialogFragment.PLAY_AGAIN_BUTTON_ID:
                loadUrl(getLoadingUrl());
                break;
        }
    }

    protected void handleScoreWindowError(@NonNull Throwable e)
    {
        Timber.e(e, "Error from score window");
    }

    protected void showFailedReportScore(@NonNull Throwable e)
    {
        Timber.e(e, "Failed to report score");
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
