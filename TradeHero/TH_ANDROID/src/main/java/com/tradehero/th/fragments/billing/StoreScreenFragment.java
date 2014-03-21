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
import com.tradehero.common.billing.ProductPurchase;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.googleplay.exception.IABBillingUnavailableException;
import com.tradehero.common.billing.request.UIBillingRequest;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.billing.googleplay.THIABAlertDialogUtil;
import com.tradehero.th.billing.request.THUIBillingRequest;
import com.tradehero.th.fragments.alert.AlertManagerFragment;
import com.tradehero.th.fragments.social.follower.FollowerManagerFragment;
import com.tradehero.th.fragments.social.hero.HeroManagerFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListRetrievedMilestone;
import com.tradehero.th.utils.LocalyticsConstants;
import javax.inject.Inject;
import javax.inject.Provider;
import timber.log.Timber;

public class StoreScreenFragment extends BasePurchaseManagerFragment
    implements WithTutorial
{
    public static final String TAG = StoreScreenFragment.class.getSimpleName();

    public static boolean alreadyNotifiedNeedCreateAccount = false;

    @Inject CurrentUserId currentUserId;
    @Inject Provider<THUIBillingRequest> uiBillingRequestProvider;
    @Inject THIABAlertDialogUtil THIABAlertDialogUtil;
    @Inject LocalyticsSession localyticsSession;

    private ListView listView;
    private StoreItemAdapter storeItemAdapter;

    private Integer showProductDetailRequestCode;

    private PortfolioCompactListRetrievedMilestone portfolioCompactListRetrievedMilestone;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
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
                @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                {
                    handlePositionClicked(position);
                }
            });
        }
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
        bundle.putInt(HeroManagerFragment.BUNDLE_KEY_FOLLOWER_ID, currentUserId.get());
        OwnedPortfolioId applicablePortfolio = getApplicablePortfolioId();
        if (applicablePortfolio != null)
        {
            bundle.putBundle(HeroManagerFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, applicablePortfolio.getArgs());
        }
        pushFragment(HeroManagerFragment.class, bundle);
    }

    protected void pushFollowerFragment()
    {
        Bundle bundle = new Bundle();
        bundle.putInt(FollowerManagerFragment.BUNDLE_KEY_FOLLOWED_ID, currentUserId.get());
        OwnedPortfolioId applicablePortfolio = getApplicablePortfolioId();
        if (applicablePortfolio != null)
        {
            bundle.putBundle(FollowerManagerFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, applicablePortfolio.getArgs());
        }
        pushFragment(FollowerManagerFragment.class, bundle);
    }

    private void pushFragment(Class<? extends Fragment> fragmentClass)
    {
        ((DashboardActivity) getActivity()).getDashboardNavigator().pushFragment(fragmentClass);
    }

    private void pushFragment(Class<? extends Fragment> fragmentClass, Bundle bundle)
    {
        ((DashboardActivity) getActivity()).getDashboardNavigator().pushFragment(fragmentClass, bundle);
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

    public void cancelOthersAndShowProductDetailList(ProductIdentifierDomain domain)
    {
        if (showProductDetailRequestCode != null)
        {
            userInteractor.forgetRequestCode(showProductDetailRequestCode);
        }
        showProductDetailRequestCode = showProductDetailListForPurchase(domain);
    }

    public int showProductDetailListForPurchase(ProductIdentifierDomain domain)
    {
        return userInteractor.run(getShowProductDetailRequest(domain));
    }

    public THUIBillingRequest getShowProductDetailRequest(ProductIdentifierDomain domain)
    {
        THUIBillingRequest request = uiBillingRequestProvider.get();
        request.applicablePortfolioId = getApplicablePortfolioId();
        request.startWithProgressDialog = true;
        request.popIfBillingNotAvailable = true;
        request.popIfProductIdentifierFetchFailed = true;
        request.popIfInventoryFetchFailed = true;
        request.domainToPresent = domain;
        request.popIfPurchaseFailed = true;
        request.onDefaultErrorListener = new UIBillingRequest.OnErrorListener()
        {
            @Override public void onError(int requestCode, BillingException billingException)
            {
                Timber.e(billingException, "Store had error");
            }
        };
        return request;
    }

    protected void handleShowProductDetailsMilestoneFailed(Throwable throwable)
    {
        // TODO warn if there are things unset
        if (throwable instanceof IABBillingUnavailableException && !alreadyNotifiedNeedCreateAccount)
        {
            alreadyNotifiedNeedCreateAccount = true;
            userInteractor.popBillingUnavailable((IABBillingUnavailableException) throwable);
        }
        // Nothing to do presumably
    }
}
