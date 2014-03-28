package com.tradehero.th.fragments.billing;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.localytics.android.LocalyticsSession;
import com.tradehero.common.billing.googleplay.exception.IABBillingUnavailableException;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.billing.googleplay.THIABAlertDialogUtil;
import com.tradehero.th.billing.googleplay.THIABUserInteractor;
import com.tradehero.th.fragments.alert.AlertManagerFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.tradehero.th.utils.LocalyticsConstants;
import javax.inject.Inject;
import timber.log.Timber;

public class StoreScreenFragment extends BasePurchaseManagerFragment
        implements WithTutorial
{
    public static final String TAG = StoreScreenFragment.class.getSimpleName();

    public static boolean alreadyNotifiedNeedCreateAccount = false;

    @Inject CurrentUserId currentUserId;
    @Inject THIABAlertDialogUtil THIABAlertDialogUtil;
    @Inject LocalyticsSession localyticsSession;

    private ListView listView;
    private StoreItemAdapter storeItemAdapter;

    private PortfolioCompactListRetrievedMilestone portfolioCompactListRetrievedMilestone;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        listView = (ListView) view.findViewById(R.id.store_option_list);
        storeItemAdapter = new StoreItemAdapter(getActivity(), getActivity().getLayoutInflater());
        if (listView != null)
        {
            listView.setAdapter(storeItemAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                {
                    handlePositionClicked(position);
                }
            });
        }
    }

    @Override protected void createUserInteractor()
    {
        userInteractor = new StoreScreenTHIABUserInteractor();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle(R.string.store_option_menu_title); // Add the changing cute icon
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();

        localyticsSession.tagEvent(LocalyticsConstants.TabBar_Store);

        storeItemAdapter.notifyDataSetChanged();
    }

    @Override public void onDestroyView()
    {
        if (listView != null)
        {
            listView.setOnItemClickListener(null);
        }
        listView = null;
        storeItemAdapter = null;
        super.onDestroyView();
    }

    @Override public boolean isTabBarVisible()
    {
        return true;
    }

    private void handlePositionClicked(int position)
    {
        Bundle bundle;
        switch (position)
        {
            case StoreItemAdapter.POSITION_BUY_VIRTUAL_DOLLARS:
                userInteractor.conditionalPopBuyVirtualDollars();
                beginAlipay(StoreItemAdapter.POSITION_BUY_VIRTUAL_DOLLARS);
                break;

            case StoreItemAdapter.POSITION_BUY_FOLLOW_CREDITS:
                userInteractor.conditionalPopBuyFollowCredits();
                break;

            case StoreItemAdapter.POSITION_BUY_STOCK_ALERTS:
                userInteractor.conditionalPopBuyStockAlerts();
                break;

            case StoreItemAdapter.POSITION_BUY_RESET_PORTFOLIO:
                userInteractor.conditionalPopBuyResetPortfolio();
                break;

            case StoreItemAdapter.POSITION_MANAGE_HEROES:
                pushHeroFragment();
                break;

            case StoreItemAdapter.POSITION_MANAGE_FOLLOWERS:
                pushFollowerFragment();
                break;
            case StoreItemAdapter.POSITION_MANAGE_STOCK_ALERTS:
                pushStockAlertFragment();
                break;
            default:
                THToast.show("Clicked at position " + position);
                break;
        }
    }

    private void beginAlipay(int type)
    {
        Timber.d("lyl beginAlipay");
        //String info = getNewOrderInfo(position);
        //String sign = Rsa.sign(info, Keys.PRIVATE);
    }

    private void pushStockAlertFragment()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(AlertManagerFragment.BUNDLE_KEY_USER_ID, currentUserId.get());
        pushFragment(AlertManagerFragment.class, bundle);
    }

    protected void pushHeroFragment()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(HeroManagerFragment.BUNDLE_KEY_FOLLOWER_ID, currentUserId.get());
        OwnedPortfolioId applicablePortfolio = userInteractor.getApplicablePortfolioId();
        if (applicablePortfolio != null)
        {
            bundle.putBundle(BasePurchaseManagerFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, applicablePortfolio.getArgs());
        }
        pushFragment(HeroManagerFragment.class, bundle);
    }

    protected void pushFollowerFragment()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(FollowerManagerFragment.BUNDLE_KEY_HERO_ID, currentUserId.get());
        OwnedPortfolioId applicablePortfolio = userInteractor.getApplicablePortfolioId();
        if (applicablePortfolio != null)
        {
            bundle.putBundle(BasePurchaseManagerFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, applicablePortfolio.getArgs());
        }
        pushFragment(FollowerManagerFragment.class, bundle);
    }

    private void pushFragment(Class<? extends Fragment> fragmentClass)
    {
        ((DashboardActivity) getActivity()).getDashboardNavigator().pushFragment(fragmentClass);
    }

    private void pushFragment(Class<? extends Fragment> fragmentClass, Bundle bundle)
    {
        ((DashboardActivity) getActivity()).getDashboardNavigator()
                .pushFragment(fragmentClass, bundle);
    }

    private void popPleaseWait()
    {
        THIABAlertDialogUtil.popWithNegativeButton(getActivity(),
                R.string.error_incomplete_info_title,
                R.string.error_incomplete_info_message,
                R.string.error_incomplete_info_cancel);
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_store_screen;
    }

    public class StoreScreenTHIABUserInteractor extends THIABUserInteractor
    {
        public StoreScreenTHIABUserInteractor()
        {
            super();
        }

        @Override protected void handleShowProductDetailsMilestoneFailed(Throwable throwable)
        {
            // TODO warn if there are things unset
            if (throwable instanceof IABBillingUnavailableException
                    && !alreadyNotifiedNeedCreateAccount)
            {
                alreadyNotifiedNeedCreateAccount = true;
                popBillingUnavailable();
            }
            // Nothing to do presumably
        }
    }
}
