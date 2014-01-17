package com.tradehero.th.fragments.billing;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.billing.googleplay.exceptions.IABBillingUnavailableException;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.billing.googleplay.IABAlertDialogUtil;
import com.tradehero.th.billing.googleplay.THIABActor;
import com.tradehero.th.fragments.billing.management.AlertManagerFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.tradehero.th.utils.AlertDialogUtil;
import javax.inject.Inject;

public class StoreScreenFragment extends BasePurchaseManagerFragment
{
    public static final String TAG = StoreScreenFragment.class.getSimpleName();

    public static boolean alreadyNotifiedNeedCreateAccount = false;

    @Inject CurrentUserBaseKeyHolder currentUserBaseKeyHolder;
    @Inject IABAlertDialogUtil iabAlertDialogUtil;

    private ListView listView;
    private StoreItemAdapter storeItemAdapter;

    private PortfolioCompactListRetrievedMilestone portfolioCompactListRetrievedMilestone;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        listView = (ListView) view.findViewById(R.id.store_option_list);
        storeItemAdapter = new StoreItemAdapter(getActivity(), getActivity().getLayoutInflater());
        if (listView != null)
        {
            listView.setAdapter(storeItemAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                {
                    handlePositionClicked(position);
                }
            });
        }
    }

    @Override protected void createUserInteractor()
    {
        userInteractor = new StoreScreenTHIABUserInteractor(getActivity(), getBillingActor(), getView().getHandler());
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
        storeItemAdapter.notifyDataSetChanged();
        userInteractor.conditionalPopBillingNotAvailable();
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

    private void pushStockAlertFragment()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(AlertManagerFragment.BUNDLE_KEY_USER_ID, currentUserBaseKeyHolder.getCurrentUserBaseKey().key);
        pushFragment(AlertManagerFragment.class, bundle);
    }

    protected void pushHeroFragment()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(HeroManagerFragment.BUNDLE_KEY_FOLLOWER_ID, currentUserBaseKeyHolder.getCurrentUserBaseKey().key);
        OwnedPortfolioId applicablePortfolio = userInteractor.getApplicablePortfolioId();
        if (applicablePortfolio != null)
        {
            bundle.putBundle(HeroManagerFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, applicablePortfolio.getArgs());
        }
        pushFragment(HeroManagerFragment.class, bundle);
    }

    protected void pushFollowerFragment()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(FollowerManagerFragment.BUNDLE_KEY_FOLLOWED_ID, currentUserBaseKeyHolder.getCurrentUserBaseKey().key);
        OwnedPortfolioId applicablePortfolio = userInteractor.getApplicablePortfolioId();
        if (applicablePortfolio != null)
        {
            bundle.putBundle(FollowerManagerFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, applicablePortfolio.getArgs());
        }
        pushFragment(FollowerManagerFragment.class, bundle);
    }

    private void pushFragment(Class<? extends Fragment> fragmentClass)
    {
        ((DashboardActivity) getActivity()).getNavigator().pushFragment(fragmentClass);
    }

    private void pushFragment(Class<? extends Fragment> fragmentClass, Bundle bundle)
    {
        ((DashboardActivity) getActivity()).getNavigator().pushFragment(fragmentClass, bundle);
    }

    private void popPleaseWait()
    {
        AlertDialogUtil.popWithNegativeButton(getActivity(),
                R.string.error_incomplete_info_title,
                R.string.error_incomplete_info_message,
                R.string.error_incomplete_info_cancel);
    }

    public class StoreScreenTHIABUserInteractor extends THIABUserInteractor
    {
        public StoreScreenTHIABUserInteractor(Activity activity, THIABActor billingActor, Handler handler)
        {
            super(activity, billingActor, handler);
        }

        @Override protected void handleShowSkuDetailsMilestoneFailed(Throwable throwable)
        {
            // TODO warn if there are things unset
            if (throwable instanceof IABBillingUnavailableException && !alreadyNotifiedNeedCreateAccount)
            {
                alreadyNotifiedNeedCreateAccount = true;
                iabAlertDialogUtil.popBillingUnavailable(getActivity());
            }
            // Nothing to do presumably
        }
    }
}
