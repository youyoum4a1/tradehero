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
import com.special.ResideMenu.ResideMenu;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.request.UIBillingRequest;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.alert.AlertManagerFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.utils.THRouter;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;

@Routable({
        "store", "store/:action"
})
public class StoreScreenFragment extends BasePurchaseManagerFragment
        implements WithTutorial
{
    public static boolean alreadyNotifiedNeedCreateAccount = false;
    protected Integer showBillingAvailableRequestCode;

    @Inject CurrentUserId currentUserId;
    @Inject THLocalyticsSession localyticsSession;
    @Inject Lazy<ResideMenu> resideMenuLazy;
    @Inject THRouter thRouter;

    @RouteProperty("action") Integer routeClickedPosition;

    private ListView listView;
    private StoreItemAdapter storeItemAdapter;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.inject(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store, container, false);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        listView = (ListView) view.findViewById(R.id.store_option_list);
        storeItemAdapter = new StoreItemAdapter(getActivity());
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

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(R.string.store_option_menu_title); // Add the changing cute icon
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();

        localyticsSession.tagEvent(LocalyticsConstants.TabBar_Store);

        storeItemAdapter.notifyDataSetChanged();
        //TODO hacked by alipay alex
        //cancelOthersAndShowBillingAvailable();

        if (routeClickedPosition != null)
        {
            handlePositionClicked(routeClickedPosition);
        }
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

    public void cancelOthersAndShowBillingAvailable()
    {
        if (alreadyNotifiedNeedCreateAccount)
        {
            return;
        }

        if (showBillingAvailableRequestCode != null)
        {
            userInteractor.forgetRequestCode(showBillingAvailableRequestCode);
        }
        showBillingAvailableRequestCode = showBillingAvailable();
        alreadyNotifiedNeedCreateAccount = true;
    }

    public int showBillingAvailable()
    {
        return userInteractor.run(getShowBillingAvailableRequest());
    }

    public THUIBillingRequest getShowBillingAvailableRequest()
    {
        THUIBillingRequest request = uiBillingRequestProvider.get();
        request.applicablePortfolioId = getApplicablePortfolioId();
        request.startWithProgressDialog = false;
        request.popIfBillingNotAvailable = !alreadyNotifiedNeedCreateAccount;
        request.billingAvailable = true;
        request.onDefaultErrorListener = new UIBillingRequest.OnErrorListener()
        {
            @Override public void onError(int requestCode, BillingException billingException)
            {
                Timber.e(billingException, "Store had error");
            }
        };
        return request;
    }

    private void handlePositionClicked(int position)
    {
        switch (position)
        {
            case StoreItemAdapter.POSITION_BUY_VIRTUAL_DOLLARS:
                cancelOthersAndShowProductDetailList(ProductIdentifierDomain.DOMAIN_VIRTUAL_DOLLAR);
                break;

            case StoreItemAdapter.POSITION_BUY_FOLLOW_CREDITS:
                cancelOthersAndShowProductDetailList(ProductIdentifierDomain.DOMAIN_FOLLOW_CREDITS);
                break;

            case StoreItemAdapter.POSITION_BUY_STOCK_ALERTS:
                cancelOthersAndShowProductDetailList(ProductIdentifierDomain.DOMAIN_STOCK_ALERTS);
                break;

            case StoreItemAdapter.POSITION_BUY_RESET_PORTFOLIO:
                cancelOthersAndShowProductDetailList(ProductIdentifierDomain.DOMAIN_RESET_PORTFOLIO);
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
        bundle.putInt(AlertManagerFragment.BUNDLE_KEY_USER_ID, currentUserId.get());
        pushFragment(AlertManagerFragment.class, bundle);
    }

    protected void pushHeroFragment()
    {
        Bundle bundle = new Bundle();
        HeroManagerFragment.putFollowerId(bundle, currentUserId.toUserBaseKey());
        OwnedPortfolioId applicablePortfolio = getApplicablePortfolioId();
        if (applicablePortfolio != null)
        {
            HeroManagerFragment.putApplicablePortfolioId(bundle, applicablePortfolio);
        }
        pushFragment(HeroManagerFragment.class, bundle);
    }

    protected void pushFollowerFragment()
    {
        Bundle bundle = new Bundle();
        FollowerManagerFragment.putHeroId(bundle, currentUserId.toUserBaseKey());
        OwnedPortfolioId applicablePortfolio = getApplicablePortfolioId();
        if (applicablePortfolio != null)
        {
            //FollowerManagerFragment.putApplicablePortfolioId(bundle, applicablePortfolio);
        }
        pushFragment(FollowerManagerFragment.class, bundle);
    }

    private void pushFragment(Class<? extends Fragment> fragmentClass, Bundle bundle)
    {
        ((DashboardActivity) getActivity()).getDashboardNavigator()
                .pushFragment(fragmentClass, bundle);
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_store_screen;
    }
}
