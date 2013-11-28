package com.tradehero.th.fragments.billing.management;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.sun.javaws.progress.Progress;
import com.tradehero.common.billing.googleplay.BaseIABPurchase;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.social.HeroDTOList;
import com.tradehero.th.api.social.HeroId;
import com.tradehero.th.api.social.HeroIdList;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.timeline.TimelineFragment;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.persistence.social.HeroCache;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import java.util.List;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/** Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 11:04 AM To change this template use File | Settings | File Templates. */
public class HeroManagerFragment extends BasePurchaseManagerFragment
{
    public static final String TAG = HeroManagerFragment.class.getSimpleName();

    private TextView followCreditCount;
    private ImageView icnCoinStack;
    private ImageButton btnBuyMore;
    private Button btnGoMostSkilled;
    private ProgressDialog progressDialog;

    private HeroListView heroListView;
    private HeroListItemAdapter heroListAdapter;
    private HeroListItemView.OnHeroStatusButtonClickedListener heroStatusButtonClickedListener;

    // The user whose heroes we are listing
    private UserProfileDTO userProfileDTO;
    private List<HeroDTO> heroDTOs;

    @Inject protected Lazy<UserProfileCache> userProfileCache;
    private DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileListener;
    private DTOCache.GetOrFetchTask<UserProfileDTO> userProfileFetchTask;
    @Inject protected Lazy<HeroListCache> heroListCache;
    @Inject protected Lazy<HeroCache> heroCache;
    private DTOCache.Listener<UserBaseKey, HeroIdList> heroListListener;
    private DTOCache.GetOrFetchTask<HeroIdList> heroListFetchTask;
    @Inject protected Lazy<UserService> userService;
    private Callback<UserProfileDTO> followCallback;

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store_manage_heroes, container, false);
        initViews(view);
        return view;
    }

    protected void initViews(View view)
    {
        followCreditCount = (TextView) view.findViewById(R.id.manage_heroes_follow_credit_count);
        icnCoinStack = (ImageView) view.findViewById(R.id.icn_credit_quantity);
        btnBuyMore = (ImageButton) view.findViewById(R.id.btn_buy_more);
        if (btnBuyMore != null)
        {
            btnBuyMore.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handleBuyMoreClicked();
                }
            });
        }
        btnGoMostSkilled = (Button) view.findViewById(R.id.btn_leaderboard_most_skilled);
        if (btnGoMostSkilled != null)
        {
            btnGoMostSkilled.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handleGoMostSkilled();
                }
            });
        }

        heroListView = (HeroListView) view.findViewById(R.id.heros_list);
        heroStatusButtonClickedListener = new HeroListItemView.OnHeroStatusButtonClickedListener()
        {
            @Override public void onHeroStatusButtonClicked(HeroListItemView heroListItemView, HeroDTO heroDTO)
            {
                handleHeroStatusChangeRequired(heroDTO);
            }
        };
        heroListAdapter = new HeroListItemAdapter(
                getActivity(),
                getActivity().getLayoutInflater(),
                R.layout.hero_list_item_empty_placeholder,
                R.layout.hero_list_item,
                R.layout.hero_list_header,
                R.layout.hero_list_header);
        heroListAdapter.setHeroStatusButtonClickedListener(heroStatusButtonClickedListener);
        if (heroListView != null)
        {
            heroListView.setAdapter(heroListAdapter);
            heroListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    handleHeroClicked(parent, view, position, id);
                }
            });
        }

        followCallback = new Callback<UserProfileDTO>()
        {
            @Override public void success(UserProfileDTO userProfileDTO, Response response)
            {
                userProfileCache.get().put(userProfileDTO.getBaseKey(), userProfileDTO);
                heroListCache.get().invalidate(userProfileDTO.getBaseKey());
                linkWith(userProfileDTO, true);
                fetchHeroes();
                if (progressDialog != null)
                {
                    progressDialog.hide();
                }
            }

            @Override public void failure(RetrofitError error)
            {
                THLog.e(TAG, "Failed to un/follow", error);
                if (progressDialog != null)
                {
                    progressDialog.hide();
                }
                THToast.show(R.string.manage_heroes_follow_failed);
            }
        };
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setTitle(R.string.manage_heroes_title);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onPause()
    {
        userProfileListener = null;
        if (userProfileFetchTask != null)
        {
            userProfileFetchTask.forgetListener(true);
        }
        userProfileFetchTask = null;

        heroListListener = null;
        if (heroListFetchTask != null)
        {
            heroListFetchTask.forgetListener(true);
        }
        heroListFetchTask = null;

        if (progressDialog != null)
        {
            progressDialog.hide();
        }
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        if (heroListAdapter != null)
        {
            heroListAdapter.setHeroStatusButtonClickedListener(null);
        }
        heroListAdapter = null;
        if (heroListView != null)
        {
            heroListView.setOnItemClickListener(null);
        }
        heroListView = null;
        followCreditCount = null;
        icnCoinStack = null;
        btnBuyMore = null;
        btnGoMostSkilled = null;
        followCallback = null;
        super.onDestroyView();
    }

    @Override protected void handleShowSkuDetailsMilestoneFailed(Throwable throwable)
    {
        // Nothing to do presumably
    }

    @Override protected void handleShowSkuDetailsMilestoneComplete()
    {
        super.handleShowSkuDetailsMilestoneComplete();
        fetchUserProfile();
        fetchHeroes();
    }

    private void handleBuyMoreClicked()
    {
        conditionalPopBuyFollowCredits();
    }

    @Override protected void handlePurchaseReportSuccess(BaseIABPurchase reportedPurchase, UserProfileDTO updatedUserPortfolio)
    {
        super.handlePurchaseReportSuccess(reportedPurchase, updatedUserPortfolio);
        fetchUserProfile();
    }

    private void handleHeroClicked(AdapterView<?> parent, View view, int position, long id)
    {
        THLog.d(TAG, "handleHeroClicked view " + view + ", id " + id);
        final HeroDTO clickedHeroDTO = (HeroDTO) heroListAdapter.getItem(position);
        pushTimelineFragment(clickedHeroDTO.getBaseKey());
    }

    private void handleHeroStatusChangeRequired(final HeroDTO clickedHeroDTO)
    {
        if (!clickedHeroDTO.active)
        {
            HeroAlertDialogUtil.popAlertFollowHero(getActivity(), new DialogInterface.OnClickListener()
            {
                @Override public void onClick(DialogInterface dialog, int which)
                {
                    followHero(clickedHeroDTO);
                }
            });
        }
        else
        {
            HeroAlertDialogUtil.popAlertUnfollowHero(getActivity(), new DialogInterface.OnClickListener()
            {
                @Override public void onClick(DialogInterface dialog, int which)
                {
                    unfollowHero(clickedHeroDTO);
                }
            });
        }
    }

    private void followHero(HeroDTO heroDTO)
    {
        if (userProfileDTO != null && userProfileDTO.ccBalance == 0)
        {
            waitForSkuDetailsMilestoneComplete(new Runnable()
            {
                @Override public void run()
                {
                    conditionalPopBuyFollowCredits();
                }
            });
        }
        else
        {
            progressDialog = ProgressDialog.show(
                    getActivity(),
                    getResources().getString(R.string.manage_heroes_follow_progress_title),
                    getResources().getString(R.string.manage_heroes_follow_progress_message),
                    true,
                    true
            );
            userService.get().follow(heroDTO.id, followCallback);
        }
    }

    private void unfollowHero(HeroDTO heroDTO)
    {
        progressDialog = ProgressDialog.show(
                getActivity(),
                getResources().getString(R.string.manage_heroes_unfollow_progress_title),
                getResources().getString(R.string.manage_heroes_unfollow_progress_message),
                true,
                true
                );

        userService.get().unfollow(heroDTO.id, followCallback);
    }

    private void pushTimelineFragment(UserBaseKey userBaseKey)
    {
        ((DashboardActivity) getActivity()).getDashboardNavigator().pushFragment(PushableTimelineFragment.class, userBaseKey.getArgs());
    }

    private void handleGoMostSkilled()
    {
        // TODO this feels HACKy
        ((DashboardActivity) getActivity()).getDashboardNavigator().popFragment();

        // TODO make it go to most skilled
        ((DashboardActivity) getActivity()).getDashboardNavigator().goToTab(DashboardTabType.COMMUNITY);
    }

    private void fetchUserProfile()
    {
        UserProfileDTO userProfileDTO = userProfileCache.get().get(userBaseKey);
        if (userProfileDTO != null)
        {
            display(userProfileDTO);
        }
        else
        {
            if (userProfileListener == null)
            {
                userProfileListener = new DTOCache.Listener<UserBaseKey, UserProfileDTO>()
                {
                    @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value)
                    {
                        display(value);
                    }

                    @Override public void onErrorThrown(UserBaseKey key, Throwable error)
                    {
                        THLog.e(TAG, "Could not fetch user profile", error);
                        THToast.show("There was an error fetching your profile information");
                    }
                };
            }
            if (userProfileFetchTask != null)
            {
                userProfileFetchTask.forgetListener(true);
            }
            userProfileFetchTask = userProfileCache.get().getOrFetch(userBaseKey, userProfileListener);
            userProfileFetchTask.execute();
        }
    }

    private void fetchHeroes()
    {
        HeroIdList heroIds = heroListCache.get().get(userBaseKey);
        HeroDTOList heroDTOs = heroCache.get().get(heroIds);
        if (heroIds != null && heroDTOs != null && heroIds.size() == heroDTOs.size()) // We need this longer test in case DTO have been flushed.
        {
            display(heroDTOs);
        }
        else
        {
            if (heroListListener == null)
            {
                heroListListener = new DTOCache.Listener<UserBaseKey, HeroIdList>()
                {
                    @Override public void onDTOReceived(UserBaseKey key, HeroIdList value)
                    {
                        display(heroCache.get().get(value));
                    }

                    @Override public void onErrorThrown(UserBaseKey key, Throwable error)
                    {
                        THLog.e(TAG, "Could not fetch heroes", error);
                        THToast.show("There was an error fetching your heroes");
                    }
                };
            }
            if (heroListFetchTask != null)
            {
                heroListFetchTask.forgetListener(true);
            }
            heroListFetchTask = heroListCache.get().getOrFetch(userBaseKey, heroListListener);
            heroListFetchTask.execute();
        }
    }

    public void display(UserProfileDTO userProfileDTO)
    {
        linkWith(userProfileDTO, true);
    }

    private void display(List<HeroDTO> heroDTOs)
    {
        linkWith(heroDTOs, true);
    }

    public void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        this.userProfileDTO = userProfileDTO;
        if (andDisplay)
        {
            displayFollowCount();
            displayCoinStack();
        }
    }

    public void linkWith(List<HeroDTO> heroDTOs, boolean andDisplay)
    {
        this.heroDTOs = heroDTOs;
        heroListAdapter.setItems(heroDTOs);
        if (andDisplay)
        {
            displayHeroList();
        }
    }

    public void display()
    {
        displayFollowCount();
        displayCoinStack();
        displayHeroList();
    }

    public void displayFollowCount()
    {
        if (followCreditCount != null)
        {
            if (userProfileDTO != null)
            {
                followCreditCount.setText(String.format("+%.0f", userProfileDTO.ccBalance));
            }
        }
    }

    public void displayCoinStack()
    {
        if (icnCoinStack != null)
        {
            if (userProfileDTO != null)
            {
                icnCoinStack.getDrawable().setLevel((int) userProfileDTO.ccBalance);
            }
        }
    }

    public void displayHeroList()
    {
        if (heroListAdapter != null)
        {
            heroListAdapter.notifyDataSetChanged();
        }
    }
}
