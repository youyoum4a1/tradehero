package com.tradehero.th.fragments.portfolio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.persistence.FetchAssistant;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.fragments.watchlist.WatchlistPositionFragment;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.portfolio.UserPortfolioFetchAssistant;
import com.tradehero.th.persistence.social.VisitedFriendListPrefs;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 11:47 AM To change this template use File | Settings | File Templates. */
public class PortfolioListFragment extends DashboardFragment
    implements FetchAssistant.OnInfoFetchedListener<UserBaseKey, OwnedPortfolioId>, WithTutorial
{
    public static final String TAG = PortfolioListFragment.class.getSimpleName();

    private ProgressBar progressBar;
    private PortfolioListView portfolioListView;
    ActionBar actionBar;

    private PortfolioListItemAdapter portfolioListAdapter;

    @Inject protected CurrentUserId currentUserId;
    // We need to populate the PortfolioDTOs in order to sort them appropriately
    private Map<OwnedPortfolioId, DisplayablePortfolioDTO> displayablePortfolios;
    private UserPortfolioFetchAssistant otherPortfolioFetchAssistant;
    private boolean areOthersComplete = false;

    @Inject Lazy<PortfolioCompactListCache> portfolioListCache;
    private PortfolioCompactListCache.Listener<UserBaseKey, OwnedPortfolioIdList> ownPortfolioListListener;
    private DTOCache.GetOrFetchTask<UserBaseKey, OwnedPortfolioIdList> fetchOwnPortfolioListFetchTask;

    @Inject Lazy<PortfolioCache> portfolioCache;
    @Inject Lazy<UserProfileCache> userProfileCache;

    private Map<OwnedPortfolioId, UserProfileCache.Listener<UserBaseKey, UserProfileDTO>> userProfileDTOListeners = new HashMap<>();
    private Map<OwnedPortfolioId, PortfolioCache.Listener<OwnedPortfolioId, PortfolioDTO>> portfolioDTOListeners = new HashMap<>();

    private Map<Integer /* userId */, DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO>> fetchUserTaskMap = new HashMap<>();
    private Map<Integer /* portfolioId */, DTOCache.GetOrFetchTask<OwnedPortfolioId, PortfolioDTO>> fetchPortfolioTaskMap = new HashMap<>();

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ownPortfolioListListener = createOwnPortfolioListListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_portfolios_list, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        if (view != null)
        {
            progressBar = (ProgressBar) view.findViewById(android.R.id.empty);

            if (portfolioListAdapter == null)
            {
                portfolioListAdapter = new PortfolioListItemAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.portfolio_list_item, R.layout.portfolio_list_header);
            }

            portfolioListView = (PortfolioListView) view.findViewById(R.id.own_portfolios_list);
            if (portfolioListView != null)
            {
                portfolioListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        handlePortfolioItemClicked(view, position, id);
                    }
                });
                portfolioListView.setAdapter(portfolioListAdapter);
            }
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        this.actionBar = getSherlockActivity().getSupportActionBar();
        this.actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        this.actionBar.setDisplayHomeAsUpEnabled(isDisplayHomeAsUpEnabled());
        displayActionBarTitle();
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean isDisplayHomeAsUpEnabled()
    {
        return false;
    }

    @Override public void onStart()
    {
        super.onStart();
        displayablePortfolios = new HashMap<>();
        fetchListOwn();
        fetchListOther();
    }

    @Override public void onDestroyOptionsMenu()
    {
        this.actionBar = null;
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        if (portfolioListView != null)
        {
            portfolioListView.setOnItemClickListener(null);
        }
        portfolioListView = null;

        detachOwnPortfolioListFetchTask();
        detachAllUserProfileFetchTask();
        detachAllUserPortfolioFetchTask();
        detachOtherPortfolioFetchAssistant();

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        ownPortfolioListListener = null;
        if (userProfileDTOListeners != null)
        {
            userProfileDTOListeners.clear();
        }
        if (portfolioDTOListeners != null)
        {
            portfolioDTOListeners.clear();
        }

        super.onDestroy();
    }

    //<editor-fold desc="Detach Methods">
    protected void detachOwnPortfolioListFetchTask()
    {
        if (fetchOwnPortfolioListFetchTask != null)
        {
            fetchOwnPortfolioListFetchTask.setListener(null);
        }
        fetchOwnPortfolioListFetchTask = null;
    }

    protected void detachAllUserProfileFetchTask()
    {
        if (fetchUserTaskMap != null)
        {
            for (DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> task: fetchUserTaskMap.values())
            {
                if (task != null)
                {
                    task.setListener(null);
                }
            }
            fetchUserTaskMap.clear();
        }
    }

    protected void detachAllUserPortfolioFetchTask()
    {
        if (fetchPortfolioTaskMap != null)
        {
            for (DTOCache.GetOrFetchTask<OwnedPortfolioId, PortfolioDTO> task: fetchPortfolioTaskMap.values())
            {
                if (task != null)
                {
                    task.setListener(null);
                }
            }
            fetchPortfolioTaskMap.clear();
        }
    }

    protected void detachOtherPortfolioFetchAssistant()
    {
        if (otherPortfolioFetchAssistant != null)
        {
            otherPortfolioFetchAssistant.clear();
        }
        otherPortfolioFetchAssistant = null;
    }
    //</editor-fold>

    public int getDisplayablePortfoliosCount()
    {
        return displayablePortfolios == null ? 0 : displayablePortfolios.size();
    }

    public int getDisplayablePortfoliosValidCount()
    {
        return getValidPortfoliosCount(displayablePortfolios);
    }

    private int getValidPortfoliosCount(Map<OwnedPortfolioId, DisplayablePortfolioDTO> dtos)
    {
        if (dtos == null)
        {
            return 0;
        }
        int total = 0;
        for (DisplayablePortfolioDTO dto:dtos.values())
        {
            if (dto != null && dto.isValid())
            {
                total++;
            }
        }
        return total;
    }

    private void fetchListOwn()
    {
        detachOwnPortfolioListFetchTask();
        fetchOwnPortfolioListFetchTask = portfolioListCache.get().getOrFetch(currentUserId.toUserBaseKey(), ownPortfolioListListener);
        displayProgress(true);
        fetchOwnPortfolioListFetchTask.execute();
    }

    private PortfolioCompactListCache.Listener<UserBaseKey, OwnedPortfolioIdList> createOwnPortfolioListListener()
    {
        return new PortfolioCompactListCache.Listener<UserBaseKey, OwnedPortfolioIdList>()
        {
            @Override public void onDTOReceived(UserBaseKey key, OwnedPortfolioIdList value, boolean fromCache)
            {
                if (key.equals(currentUserId.toUserBaseKey()))
                {
                    displayProgress(false);
                    linkWithOwn(value, true, (OwnedPortfolioId) null);
                }
            }

            @Override public void onErrorThrown(UserBaseKey key, Throwable error)
            {
                if (key.equals(currentUserId.toUserBaseKey()))
                {
                    displayProgress(false);
                    THToast.show(getString(R.string.error_fetch_portfolio_list_info));
                    THLog.e(TAG, "Error fetching the portfolio id list " + key, error);
                }
            }
        };
    }

    protected void linkWithOwn(List<OwnedPortfolioId> ownedPortfolioIds, boolean andDisplay, OwnedPortfolioId typeQualifier)
    {
        if (ownedPortfolioIds != null)
        {
            DisplayablePortfolioDTO displayablePortfolioDTO;
            for (OwnedPortfolioId ownedPortfolioId: ownedPortfolioIds)
            {
                displayablePortfolioDTO = new DisplayablePortfolioDTO(ownedPortfolioId);
                populatePortfolio(displayablePortfolioDTO, false);
                displayablePortfolios.put(ownedPortfolioId, displayablePortfolioDTO);
            }
        }

        if (andDisplay)
        {
            displayPortfolios();
        }
    }

    private void fetchListOther()
    {
        linkWithOther(VisitedFriendListPrefs.getVisitedIdList(), true, (UserBaseKey) null);
    }

    public void linkWithOther(List<UserBaseKey> otherPeopleUserBaseKeys, boolean andDisplay, UserBaseKey typeQualifier)
    {
        detachOtherPortfolioFetchAssistant();

        otherPortfolioFetchAssistant = new UserPortfolioFetchAssistant(getActivity(), otherPeopleUserBaseKeys);
        otherPortfolioFetchAssistant.setListener(this);
        otherPortfolioFetchAssistant.execute();
    }

    //<editor-fold desc="FetchAssistant.OnInfoFetchedListener<UserBaseKey, OwnedPortfolioId>">
    @Override public void onInfoFetched(Map<UserBaseKey, OwnedPortfolioId> fetched, boolean isDataComplete)
    {
        List<OwnedPortfolioId> ownedPortfolioIds = new ArrayList<>();
        if (fetched != null)
        {
            for (OwnedPortfolioId ownedPortfolioId: fetched.values())
            {
                if (ownedPortfolioId != null)
                {
                    ownedPortfolioIds.add(ownedPortfolioId);
                }
            }
        }
        areOthersComplete = isDataComplete;
        linkWithOther(ownedPortfolioIds, true, (OwnedPortfolioId) null);
    }
    //</editor-fold>

    protected void linkWithOther(List<OwnedPortfolioId> otherPortfolioIds, boolean andDisplay, OwnedPortfolioId typeQualifier)
    {
        if (otherPortfolioIds != null)
        {
            DisplayablePortfolioDTO displayablePortfolioDTO;
            for (OwnedPortfolioId otherPortfolioId: otherPortfolioIds)
            {
                displayablePortfolioDTO = new DisplayablePortfolioDTO(otherPortfolioId);
                populatePortfolio(displayablePortfolioDTO, false);
                displayablePortfolios.put(otherPortfolioId, displayablePortfolioDTO);
            }
        }

        if (andDisplay)
        {
            displayPortfolios();
        }
    }

    public void populatePortfolios(boolean andDisplay)
    {
        populatePortfolios(displayablePortfolios, false);
    }

    public void populatePortfolios(Map<OwnedPortfolioId, DisplayablePortfolioDTO> dtos, boolean andDisplay)
    {
        if (dtos == null)
        {
            return;
        }
        for (DisplayablePortfolioDTO dto: dtos.values())
        {
            if (dto != null)
            {
                populatePortfolio(dto, false);
            }
        }
        if (andDisplay)
        {
            displayPortfolios();
        }
    }

    public void populatePortfolio(DisplayablePortfolioDTO displayablePortfolioDTO, boolean andDisplay)
    {
        if (displayablePortfolioDTO == null)
        {
            return;
        }
        displayablePortfolioDTO.populate(userProfileCache.get());
        displayablePortfolioDTO.populate(portfolioCache.get());

        if (displayablePortfolioDTO.userBaseDTO == null)
        {
            launchFetchTaskForUser(displayablePortfolioDTO);
        }
        if (displayablePortfolioDTO.portfolioDTO == null)
        {
            launchFetchTaskForPortfolio(displayablePortfolioDTO);
        }

        if (andDisplay)
        {
            displayPortfolios();
        }
    }

    private void launchFetchTaskForUser(final DisplayablePortfolioDTO displayablePortfolioDTO)
    {
        if (fetchUserTaskMap.containsKey(displayablePortfolioDTO.ownedPortfolioId.userId))
        {
            DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> fetchTask = fetchUserTaskMap.get(displayablePortfolioDTO.ownedPortfolioId.userId);
            if (fetchTask != null)
            {
                fetchTask.setListener(null);
            }
        }

        DTOCache.Listener<UserBaseKey, UserProfileDTO> fetchListener = userProfileDTOListeners.get(displayablePortfolioDTO.ownedPortfolioId);
        if (fetchListener == null)
        {
            fetchListener = new DTOCache.Listener<UserBaseKey, UserProfileDTO>()
            {
                @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value, boolean fromCache)
                {
                    displayablePortfolioDTO.userBaseDTO = value;
                    displayPortfolios();
                }

                @Override public void onErrorThrown(UserBaseKey key, Throwable error)
                {
                    THToast.show(getString(R.string.error_fetch_user_profile));
                    THLog.e(TAG, "Error fetching the user profile " + key, error);
                }
            };
            userProfileDTOListeners.put(displayablePortfolioDTO.ownedPortfolioId, fetchListener);
        }

        DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> fetchTask = userProfileCache.get().getOrFetch(
                displayablePortfolioDTO.ownedPortfolioId.getUserBaseKey(), fetchListener);
        fetchUserTaskMap.put(displayablePortfolioDTO.ownedPortfolioId.userId, fetchTask);
        fetchTask.execute();
    }

    private void launchFetchTaskForPortfolio(final DisplayablePortfolioDTO displayablePortfolioDTO)
    {
        if (fetchPortfolioTaskMap.containsKey(displayablePortfolioDTO.ownedPortfolioId.portfolioId))
        {
            DTOCache.GetOrFetchTask<OwnedPortfolioId, PortfolioDTO> fetchTask = fetchPortfolioTaskMap.get(displayablePortfolioDTO.ownedPortfolioId.portfolioId);
            if (fetchTask != null)
            {
                fetchTask.setListener(null);
            }
        }

        DTOCache.Listener<OwnedPortfolioId, PortfolioDTO> fetchListener = portfolioDTOListeners.get(displayablePortfolioDTO.ownedPortfolioId);
        if (fetchListener == null)
        {
            fetchListener = new DTOCache.Listener<OwnedPortfolioId, PortfolioDTO>()
            {
                @Override public void onDTOReceived(OwnedPortfolioId key, PortfolioDTO value, boolean fromCache)
                {
                    displayablePortfolioDTO.portfolioDTO = value;
                    displayPortfolios();
                }

                @Override public void onErrorThrown(OwnedPortfolioId key, Throwable error)
                {
                    THToast.show(getString(R.string.error_fetch_portfolio_info));
                    THLog.e(TAG, "Error fetching the portfolio " + key, error);
                }
            };
            portfolioDTOListeners.put(displayablePortfolioDTO.ownedPortfolioId, fetchListener);
        }

        DTOCache.GetOrFetchTask<OwnedPortfolioId, PortfolioDTO> fetchTask = portfolioCache.get().getOrFetch(
                displayablePortfolioDTO.ownedPortfolioId, fetchListener);
        fetchPortfolioTaskMap.put(displayablePortfolioDTO.ownedPortfolioId.portfolioId, fetchTask);
        fetchTask.execute();
    }

    public void display()
    {
        displayActionBarTitle();
        displayPortfolios();
    }

    public void displayPortfolios()
    {
        displayActionBarTitle();
        portfolioListAdapter.setItems(getAllPortfolios());
        portfolioListAdapter.notifyDataSetChanged();
    }

    public void displayActionBarTitle()
    {
        if (this.actionBar != null)
        {
            if (displayablePortfolios == null || areOthersComplete)
            {
                this.actionBar.setTitle(getString(R.string.topbar_portfolios_title));
            }
            else
            {
                this.actionBar.setTitle(String.format(
                        getString(R.string.portfolio_loading_count),
                        getDisplayablePortfoliosValidCount(),
                        getDisplayablePortfoliosCount()));
            }
        }
    }

    public void displayProgress(boolean running)
    {
        if (progressBar != null)
        {
            progressBar.setVisibility(running ? View.VISIBLE : View.GONE);
        }
    }

    private List<DisplayablePortfolioDTO> getAllPortfolios()
    {
        List<DisplayablePortfolioDTO> allPortfolios = new ArrayList<>();
        if (displayablePortfolios != null)
        {
            allPortfolios.addAll(displayablePortfolios.values());
        }
        return allPortfolios;
    }

    private void handlePortfolioItemClicked(View view, int position, long id)
    {
        if (view instanceof PortfolioListItemView && portfolioListAdapter.getItem(position) instanceof DisplayablePortfolioDTO)
        {
            Bundle args = new Bundle();

            DisplayablePortfolioDTO displayablePortfolioDTO = (DisplayablePortfolioDTO) portfolioListAdapter.getItem(position);


            if (displayablePortfolioDTO.portfolioDTO != null && displayablePortfolioDTO.portfolioDTO.isWatchlist)
            {
                args.putBundle(WatchlistPositionFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE, displayablePortfolioDTO.ownedPortfolioId.getArgs());
                navigator.pushFragment(WatchlistPositionFragment.class, args);
            }
            else
            {
                args.putBundle(PositionListFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE, displayablePortfolioDTO.ownedPortfolioId.getArgs());
                navigator.pushFragment(PositionListFragment.class, args);
            }
        }
        else
        {
            THLog.d(TAG, "Not handling portfolioItemClicked " + view.getClass().getName());
        }
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return true;
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_portfolio_list;
    }
    //</editor-fold>
}
