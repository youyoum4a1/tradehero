package com.androidth.general.fragments.timeline;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.androidth.general.common.persistence.DTOCacheUtilRx;
import com.androidth.general.fragments.competition.MainCompetitionFragment;
import com.androidth.general.utils.Constants;
import com.tradehero.route.Routable;
import com.androidth.general.R;
import com.androidth.general.activities.UpdateCenterActivity;
import com.androidth.general.api.portfolio.DummyFxDisplayablePortfolioDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.fragments.fxonboard.FxOnBoardDialogFragment;
import com.androidth.general.fragments.tutorial.WithTutorial;
import com.androidth.general.models.number.THSignedNumber;
import com.androidth.general.rx.ToastOnErrorAction1;

import javax.inject.Inject;

import rx.functions.Action1;

@Routable({
        "user/me", "profiles/me"
})
public class MeTimelineFragment extends TimelineFragment
        implements WithTutorial {
    @Inject
    protected CurrentUserId currentUserId;
    @Inject
    DTOCacheUtilRx dtoCacheUtil;

    TextView unreadCountView;
    @Nullable
    FxOnBoardDialogFragment onBoardDialogFragment;

    @Nullable
    @Override
    protected UserBaseKey getShownUserBaseKey() {
        return currentUserId.toUserBaseKey();
    }

    @Override
    public void onResume() {
        super.onResume();
        dtoCacheUtil.anonymousPrefetches();
        dtoCacheUtil.initialPrefetches();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        setActionBarSubtitle(null);
//        setActionBarColorSelf(null, null);
        if(getArguments().containsKey(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_NAV_URL)){
            String navigationUrl = getArguments().getString(MainCompetitionFragment.BUNDLE_KEY_ACTION_BAR_NAV_URL);
            setActionBarCustomImage(getActivity(), navigationUrl, false);
        }

        inflater.inflate(R.menu.me_timeline_menu, menu);

        View unreadActionView = menu.findItem(R.id.btn_notification).getActionView();
        unreadCountView = (TextView) unreadActionView.findViewById(R.id.unread_count);
        displayUnreadCount();
        unreadActionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigator.get().launchActivity(UpdateCenterActivity.class);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btn_notification) {
            thRouter.open("notifications");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public int getTutorialLayout() {
        return R.layout.tutorial_timeline;
    }

    @Override
    protected void linkWith(@NonNull UserProfileDTO userProfileDTO) {
        super.linkWith(userProfileDTO);
        displayUnreadCount();
    }

    protected void displayUnreadCount() {
        if (shownProfile != null && unreadCountView != null) {
            unreadCountView.setVisibility(shownProfile.unreadMessageThreadsCount > 0 ? View.VISIBLE : View.GONE);
            unreadCountView.setText(THSignedNumber.builder(shownProfile.unreadMessageThreadsCount)
                    .build().toString());
        }
    }

    @Override
    protected void onMainItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Object item = adapterView.getItemAtPosition(i);
        if (item instanceof DummyFxDisplayablePortfolioDTO) {
            popEnrollFx();
        } else {
            super.onMainItemClick(adapterView, view, i, l);
        }
    }

    private void popEnrollFx() {
        if (onBoardDialogFragment == null && Constants.ONBOARD_OANDA_ENABLED)//better than comment HAHA
        {
            onBoardDialogFragment = FxOnBoardDialogFragment.showOnBoardDialog(getActivity().getSupportFragmentManager());
            onBoardDialogFragment.getDismissedObservable()
                    .subscribe(
                            new Action1<DialogInterface>() {
                                @Override
                                public void call(DialogInterface dialog) {
                                    onBoardDialogFragment = null;
                                    swipeRefreshContainer.setRefreshing(true);
                                    portfolioCompactListCache.invalidate(shownUserBaseKey);
                                    portfolioCompactListCache.get(shownUserBaseKey);
                                    userProfileCache.get().invalidate(shownUserBaseKey);
                                    userProfileCache.get().get(shownUserBaseKey);
                                    swipeRefreshContainer.setRefreshing(false);
                                }
                            },
                            new ToastOnErrorAction1()
                    );
        }
    }
}
