package com.tradehero.th.fragments.portfolio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
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
    implements FetchAssistant.OnInfoFetchedListener<UserBaseKey, OwnedPortfolioId>
{
    public static final String TAG = PortfolioListFragment.class.getSimpleName();

    private PortfolioListView portfolioListView;

    private PortfolioListItemAdapter portfolioListAdapter;

    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;
    // We need to populate the PortfolioDTOs in order to sort them appropriately
    private Map<OwnedPortfolioId, DisplayablePortfolioDTO> displayablePortfolios;
    private UserPortfolioFetchAssistant otherPortfolioFetchAssistant;
    private boolean areOthersComplete = false;

    @Inject Lazy<PortfolioCompactListCache> portfolioListCache;
    @Inject Lazy<PortfolioCache> portfolioCache;
    @Inject Lazy<UserProfileCache> userProfileCache;

    private PortfolioCompactListCache.Listener<UserBaseKey, OwnedPortfolioIdList> ownPortfolioListListener;
    private DTOCache.GetOrFetchTask<OwnedPortfolioIdList> fetchOwnPortfolioListFetchTask;

    private Map<OwnedPortfolioId, UserProfileCache.Listener<UserBaseKey, UserProfileDTO>> userProfileDTOListeners = new HashMap<>();
    private Map<OwnedPortfolioId, PortfolioCache.Listener<OwnedPortfolioId, PortfolioDTO>> portfolioDTOListeners = new HashMap<>();

    private Map<Integer /* userId */, DTOCache.GetOrFetchTask<UserProfileDTO>> fetchUserTaskMap = new HashMap<>();
    private Map<Integer /* portfolioId */, DTOCache.GetOrFetchTask<PortfolioDTO>> fetchPortfolioTaskMap = new HashMap<>();

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
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setDisplayHomeAsUpEnabled(isDisplayHomeAsUpEnabled());
        displayActionBarTitle();
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean isDisplayHomeAsUpEnabled()
    {
        return false;
    }

    @Override public void onResume()
    {
        super.onResume();
        displayablePortfolios = new HashMap<>();
        fetchListOwn();
        fetchListOther();
    }

    @Override public void onPause()
    {
        ownPortfolioListListener = null;
        if (fetchOwnPortfolioListFetchTask != null)
        {
            fetchOwnPortfolioListFetchTask.cancel(false);
        }
        fetchOwnPortfolioListFetchTask = null;

        if (otherPortfolioFetchAssistant != null)
        {
            otherPortfolioFetchAssistant.clear();
        }
        otherPortfolioFetchAssistant = null;
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        if (portfolioListView != null)
        {
            portfolioListView.setOnItemClickListener(null);
        }
        portfolioListView = null;

        if (fetchOwnPortfolioListFetchTask != null)
        {
            fetchOwnPortfolioListFetchTask.forgetListener(true);
        }
        fetchOwnPortfolioListFetchTask = null;
        ownPortfolioListListener = null;

        if (fetchUserTaskMap != null)
        {
            for (DTOCache.GetOrFetchTask<UserProfileDTO> task: fetchUserTaskMap.values())
            {
                if (task != null)
                {
                    task.forgetListener(true);
                }
            }
            fetchUserTaskMap.clear();
        }
        if (userProfileDTOListeners != null)
        {
            userProfileDTOListeners.clear();
        }

        if (fetchPortfolioTaskMap != null)
        {
            for (DTOCache.GetOrFetchTask<PortfolioDTO> task: fetchPortfolioTaskMap.values())
            {
                if (task != null)
                {
                    task.forgetListener(true);
                }
            }
            fetchPortfolioTaskMap.clear();
        }
        if (portfolioDTOListeners != null)
        {
            portfolioDTOListeners.clear();
        }

        super.onDestroyView();
    }

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
        if (ownPortfolioListListener == null)
        {
            ownPortfolioListListener = createOwnPortfolioListListener();
        }
        if (fetchOwnPortfolioListFetchTask != null)
        {
            fetchOwnPortfolioListFetchTask.forgetListener(true);
        }
        fetchOwnPortfolioListFetchTask = portfolioListCache.get().getOrFetch(currentUserBaseKeyHolder.getCurrentUserBaseKey(), ownPortfolioListListener);
        fetchOwnPortfolioListFetchTask.execute();
    }

    private PortfolioCompactListCache.Listener<UserBaseKey, OwnedPortfolioIdList> createOwnPortfolioListListener()
    {
        return new PortfolioCompactListCache.Listener<UserBaseKey, OwnedPortfolioIdList>()
        {
            @Override public void onDTOReceived(UserBaseKey key, OwnedPortfolioIdList value)
            {
                if (key.equals(currentUserBaseKeyHolder.getCurrentUserBaseKey()))
                {
                    linkWithOwn(value, true, (OwnedPortfolioId) null);
                }
            }

            @Override public void onErrorThrown(UserBaseKey key, Throwable error)
            {
                THToast.show(getString(R.string.error_fetch_portfolio_list_info));
                THLog.e(TAG, "Error fetching the portfolio id list " + key, error);
            }
        };
    }

    protected void linkWithOwn(List<OwnedPortfolioId> ownedPortfolioIds, boolean andDisplay, OwnedPortfolioId typeQualifier)
    {
        if (displayablePortfolios == null)
        {
            displayablePortfolios = new HashMap<>();
        }
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
        if (otherPortfolioFetchAssistant != null)
        {
            otherPortfolioFetchAssistant.clear();
        }

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
        if (displayablePortfolios == null)
        {
            displayablePortfolios = new HashMap<>();
        }
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
            DTOCache.GetOrFetchTask<UserProfileDTO> fetchTask = fetchUserTaskMap.get(displayablePortfolioDTO.ownedPortfolioId.userId);
            if (fetchTask != null)
            {
                fetchTask.forgetListener(true);
            }
        }

        DTOCache.Listener<UserBaseKey, UserProfileDTO> fetchListener = userProfileDTOListeners.get(displayablePortfolioDTO.ownedPortfolioId);
        if (fetchListener == null)
        {
            fetchListener = new DTOCache.Listener<UserBaseKey, UserProfileDTO>()
            {
                @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value)
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

        DTOCache.GetOrFetchTask<UserProfileDTO> fetchTask = userProfileCache.get().getOrFetch(
                displayablePortfolioDTO.ownedPortfolioId.getUserBaseKey(), fetchListener);
        fetchUserTaskMap.put(displayablePortfolioDTO.ownedPortfolioId.userId, fetchTask);
        fetchTask.execute();
    }

    private void launchFetchTaskForPortfolio(final DisplayablePortfolioDTO displayablePortfolioDTO)
    {
        if (fetchPortfolioTaskMap.containsKey(displayablePortfolioDTO.ownedPortfolioId.portfolioId))
        {
            DTOCache.GetOrFetchTask<PortfolioDTO> fetchTask = fetchPortfolioTaskMap.get(displayablePortfolioDTO.ownedPortfolioId.portfolioId);
            if (fetchTask != null)
            {
                fetchTask.forgetListener(true);
            }
        }

        DTOCache.Listener<OwnedPortfolioId, PortfolioDTO> fetchListener = portfolioDTOListeners.get(displayablePortfolioDTO.ownedPortfolioId);
        if (fetchListener == null)
        {
            fetchListener = new DTOCache.Listener<OwnedPortfolioId, PortfolioDTO>()
            {
                @Override public void onDTOReceived(OwnedPortfolioId key, PortfolioDTO value)
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

        DTOCache.GetOrFetchTask<PortfolioDTO> fetchTask = portfolioCache.get().getOrFetch(
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
        if (displayablePortfolios != null && portfolioListAdapter != null)
        {
            portfolioListAdapter.setItems(getAllPortfolios());
            getView().post(new Runnable()
            {
                @Override public void run()
                {
                    // We save it in a variable to avoid it disappearing between the 2 accesses
                    PortfolioListItemAdapter adapter = portfolioListAdapter;
                    if (adapter != null)
                    {
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public void displayActionBarTitle()
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        if (displayablePortfolios == null || areOthersComplete)
        {
            actionBar.setTitle(getString(R.string.topbar_portfolios_title));
        }
        else
        {
            actionBar.setTitle(String.format(
                    getString(R.string.portfolio_loading_count),
                    getDisplayablePortfoliosValidCount(),
                    getDisplayablePortfoliosCount()));
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
        if (view instanceof PortfolioListItemView)
        {
            Bundle args = new Bundle();
            args.putBundle(PositionListFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE, ((PortfolioListItemView) view).getDisplayablePortfolioDTO().ownedPortfolioId.getArgs());
            navigator.pushFragment(PositionListFragment.class, args);
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
    //</editor-fold>
}
