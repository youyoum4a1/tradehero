package com.tradehero.th.fragments.billing.management;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/11/13 Time: 11:04 AM To change this template use File | Settings | File Templates. */
public class HeroManagerFragment extends BasePurchaseManagerFragment
{
    public static final String TAG = HeroManagerFragment.class.getSimpleName();

    private TextView followCount;
    private ImageView icnCoinStack;
    private ImageButton btnBuyMore;
    private Button btnGoMostSkilled;

    private UserProfileDTO userProfileDTO;

    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    private DTOCache.Listener<UserBaseKey, UserProfileDTO> userProfileListener;
    private DTOCache.GetOrFetchTask<UserProfileDTO> userProfileFetchTask;

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
        followCount = (TextView) view.findViewById(R.id.manage_heroes_follow_count);
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
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setTitle(R.string.manage_heroes_title);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchUserProfile();
    }

    @Override public void onPause()
    {
        userProfileListener = null;
        if (userProfileFetchTask != null)
        {
            userProfileFetchTask.forgetListener(true);
        }
        userProfileFetchTask = null;
        super.onPause();
    }

    private void handleBuyMoreClicked()
    {
        popBuyFollowCredits();
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
        UserProfileDTO userProfileDTO = userProfileCache.get().get(new UserBaseKey(getApplicablePortfolioId().userId));
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
                        THToast.show("There was an error fetching your profile information");
                    }
                };
            }
            if (userProfileFetchTask != null)
            {
                userProfileFetchTask.forgetListener(true);
            }
            userProfileFetchTask = userProfileCache.get().getOrFetch(new UserBaseKey(getApplicablePortfolioId().userId), userProfileListener);
            userProfileFetchTask.execute();
        }
    }

    public void display(UserProfileDTO userProfileDTO)
    {
        linkWith(userProfileDTO, true);
    }

    public void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        this.userProfileDTO = userProfileDTO;
        if (andDisplay)
        {
            displayFollowCount();
        }
    }

    public void display()
    {
        displayFollowCount();
    }

    public void displayFollowCount()
    {
        if (followCount != null)
        {
            if (userProfileDTO != null)
            {
                followCount.setText(String.format("+%d", userProfileDTO.heroIds == null ? 0 : userProfileDTO.heroIds.size()));
            }
        }
    }
}
