package com.tradehero.th.fragments.timeline;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.activities.UpdateCenterActivity;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.DummyFxDisplayablePortfolioDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.fxonboard.FxOnBoardDialogFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.persistence.DTOCacheUtilImpl;
import com.tradehero.th.rx.ToastOnErrorAction;
import javax.inject.Inject;
import rx.functions.Action1;

@Routable({
        "user/me", "profiles/me"
})
public class MeTimelineFragment extends TimelineFragment
        implements WithTutorial
{
    @Inject protected CurrentUserId currentUserId;
    @Inject DTOCacheUtilImpl dtoCacheUtil;

    TextView unreadCountView;
    @Nullable FxOnBoardDialogFragment onBoardDialogFragment;

    @Nullable @Override protected UserBaseKey getShownUserBaseKey()
    {
        return currentUserId.toUserBaseKey();
    }

    @Override public void onResume()
    {
        super.onResume();
        dtoCacheUtil.anonymousPrefetches();
        dtoCacheUtil.initialPrefetches();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarSubtitle(null);
        inflater.inflate(R.menu.me_timeline_menu, menu);

        View unreadActionView = menu.findItem(R.id.btn_notification).getActionView();
        unreadCountView = (TextView) unreadActionView.findViewById(R.id.unread_count);
        displayUnreadCount();
        unreadActionView.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                navigator.get().launchActivity(UpdateCenterActivity.class);
            }
        });
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.btn_notification)
        {
            thRouter.open("notifications");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_timeline;
    }

    @Override protected void linkWith(@NonNull UserProfileDTO userProfileDTO)
    {
        super.linkWith(userProfileDTO);
        displayUnreadCount();
    }

    protected void displayUnreadCount()
    {
        if (shownProfile != null && unreadCountView != null)
        {
            unreadCountView.setVisibility(shownProfile.unreadMessageThreadsCount > 0 ? View.VISIBLE : View.GONE);
            unreadCountView.setText(THSignedNumber.builder(shownProfile.unreadMessageThreadsCount)
                    .build().toString());
        }
    }

    @Override protected void onMainItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Object item = adapterView.getItemAtPosition(i);
        if (item instanceof DummyFxDisplayablePortfolioDTO)
        {
            popEnrollFx();
        }
        else
        {
            super.onMainItemClick(adapterView, view, i, l);
        }
    }

    private void popEnrollFx()
    {
        if (onBoardDialogFragment == null)
        {
            onBoardDialogFragment = FxOnBoardDialogFragment.showOnBoardDialog(getActivity().getFragmentManager());
            onBoardDialogFragment.getDismissedObservable()
                    .subscribe(
                            new Action1<DialogInterface>()
                            {
                                @Override public void call(DialogInterface dialog)
                                {
                                    onBoardDialogFragment = null;
                                    swipeRefreshContainer.setRefreshing(true);
                                    portfolioCompactListCache.invalidate(shownUserBaseKey);
                                    portfolioCompactListCache.get(shownUserBaseKey);
                                    userProfileCache.get().invalidate(shownUserBaseKey);
                                    userProfileCache.get().get(shownUserBaseKey);
                                    swipeRefreshContainer.setRefreshing(false);
                                }
                            },
                            new ToastOnErrorAction()
                    );
        }
    }
}
