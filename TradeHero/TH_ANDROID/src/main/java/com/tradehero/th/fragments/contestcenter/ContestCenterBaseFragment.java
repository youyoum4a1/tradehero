package com.tradehero.th.fragments.contestcenter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import com.tradehero.th.fragments.competition.MainCompetitionFragment;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.models.intent.competition.ProviderIntent;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.persistence.competition.ProviderListCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import timber.log.Timber;

/**
 * Created by huhaiping on 14-7-18.
 */
public abstract class ContestCenterBaseFragment extends DashboardFragment implements View.OnClickListener
{
    @Inject Lazy<ProviderListCache> providerListCache;
    @InjectView(R.id.contest_center_content_screen) BetterViewAnimator contest_center_content_screen;
    @Inject protected PortfolioCompactListCache portfolioCompactListCache;
    @Inject CurrentUserId currentUserId;
    @Inject ProviderUtil providerUtil;
    @InjectView(android.R.id.list) StickyListHeadersListView contestListView;

    public ContestItemAdapter contestListAdapter;
    private int currentDisplayedChildLayoutId;
    public ProviderDTOList providerDTOs;
    private DTOCacheNew.Listener<ProviderListKey, ProviderDTOList> providerListFetchListener;
    private BaseWebViewFragment webFragment;
    private THIntentPassedListener thIntentPassedListener;
    protected UserProfileDTO currentUserProfileDTO;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.thIntentPassedListener = new LeaderboardCommunityTHIntentPassedListener();
        providerListFetchListener = createProviderIdListListener();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.contest_center_content_screen, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    protected DTOCacheNew.Listener<ProviderListKey, ProviderDTOList> createProviderIdListListener()
    {
        return new LeaderboardCommunityProviderListListener();
    }

    protected class LeaderboardCommunityProviderListListener implements DTOCacheNew.Listener<ProviderListKey, ProviderDTOList>
    {
        @Override public void onDTOReceived(@NotNull ProviderListKey key, @NotNull ProviderDTOList value)
        {
            providerDTOs = value;
            recreateAdapter();
        }

        @Override public void onErrorThrown(@NotNull ProviderListKey key, @NotNull Throwable error)
        {
            handleFailToReceiveLeaderboardDefKeyList();
            THToast.show(getString(R.string.error_fetch_provider_info_list));
            Timber.e("Failed retrieving the list of competition providers", error);
        }
    }

    protected ContestItemAdapter createAdapter()
    {
        return new ContestItemAdapter(
                getActivity(),
                R.layout.leaderboard_competition_item_view,
                R.layout.contest_content_item_view);
    }

    /**
     * TODO to show user detail of the error
     */
    private void handleFailToReceiveLeaderboardDefKeyList()
    {
        setContestCenterScreen(R.id.error);
        View displayedChild = contest_center_content_screen.getChildAt(contest_center_content_screen.getDisplayedChild());
        displayedChild.setOnClickListener(this);
    }

    @Override public void onClick(View v)
    {
        if (v.getId() == R.id.error)
        {
            setContestCenterScreen(R.id.progress);
            loadContestData();
        }
    }

    public void setContestCenterScreen(int viewId)
    {
        if (contest_center_content_screen != null)
        {
            contest_center_content_screen.setDisplayedChildByLayoutId(viewId);
        }
    }

    private void loadContestData()
    {
        // get the data
        fetchProviderIdList();
    }

    private void fetchProviderIdList()
    {
        detachProviderListFetchTask();
        providerListCache.get().register(new ProviderListKey(), providerListFetchListener);
        providerListCache.get().getOrFetchAsync(new ProviderListKey());
    }

    private void detachProviderListFetchTask()
    {
        providerListCache.get().unregister(providerListFetchListener);
    }

    @Override public void onStart()
    {
        super.onStart();
        contestListView.setOnItemClickListener(createItemClickListener());
        if (currentDisplayedChildLayoutId != 0)
        {
            setContestCenterScreen(currentDisplayedChildLayoutId);
        }
    }

    @Override public void onStop()
    {
        detachProviderListFetchTask();
        currentDisplayedChildLayoutId = contest_center_content_screen.getDisplayedChildLayoutId();
        contestListView.setOnItemClickListener(null);
        super.onStop();
    }

    @Override public void onDestroy()
    {
        providerListFetchListener = null;
        this.thIntentPassedListener = null;
        detachWebFragment();
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
        loadContestData();
        detachWebFragment();
    }

    private void detachWebFragment()
    {
        if (this.webFragment != null)
        {
            this.webFragment.setThIntentPassedListener(null);
        }
        this.webFragment = null;
    }

    protected AdapterView.OnItemClickListener createItemClickListener()
    {
        return new ContestOnItemClickListener();
    }

    protected class ContestOnItemClickListener implements AdapterView.OnItemClickListener
    {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
        {
            Object item = adapterView.getItemAtPosition(position);
            if (item instanceof ProviderContestPageDTO)
            {
                handleCompetitionItemClicked(((ProviderContestPageDTO) item).providerDTO);
            }
            else
            {
                throw new IllegalArgumentException("Unhandled item type " + item);
            }
        }
    }

    private void handleCompetitionItemClicked(ProviderDTO providerDTO)
    {
        if (providerDTO != null && providerDTO.isUserEnrolled)
        {
            Bundle args = new Bundle();
            MainCompetitionFragment.putProviderId(args, providerDTO.getProviderId());
            getDashboardNavigator().pushFragment(MainCompetitionFragment.class, args);
        }
        else if (providerDTO != null)
        {
            // HACK Just in case the user eventually enrolls
            portfolioCompactListCache.invalidate(currentUserId.toUserBaseKey());
            Bundle args = new Bundle();
            CompetitionWebViewFragment.putUrl(args, providerUtil.getLandingPage(
                    providerDTO.getProviderId(),
                    currentUserId.toUserBaseKey()));
            CompetitionWebViewFragment.putIsOptionMenuVisible(args, true);
            webFragment = getDashboardNavigator().pushFragment(CompetitionWebViewFragment.class, args);
            webFragment.setThIntentPassedListener(thIntentPassedListener);
        }
    }

    protected class LeaderboardCommunityTHIntentPassedListener implements THIntentPassedListener
    {
        @Override public void onIntentPassed(THIntent thIntent)
        {
            Timber.d("LeaderboardCommunityTHIntentPassedListener " + thIntent);
            if (thIntent instanceof ProviderIntent)
            {
                // Just in case the user has enrolled
                portfolioCompactListCache.invalidate(currentUserId.toUserBaseKey());
            }

            if (thIntent instanceof ProviderPageIntent)
            {
                Timber.d("Intent is ProviderPageIntent");
                if (webFragment != null)
                {
                    Timber.d("Passing on %s", ((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
                    webFragment.loadUrl(((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
                }
                else
                {
                    Timber.d("WebFragment is null");
                }
            }
            else
            {
                Timber.w("Unhandled intent %s", thIntent);
            }
        }
    }

    abstract public void recreateAdapter();

    public abstract ContestCenterTabType getCCTabType();
}
