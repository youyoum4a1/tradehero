package com.tradehero.th.fragments.billing;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.tradehero.common.billing.googleplay.SKUPurchase;
import com.tradehero.common.billing.googleplay.exceptions.IABAlreadyOwnedException;
import com.tradehero.common.billing.googleplay.exceptions.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.billing.googleplay.exceptions.IABRemoteException;
import com.tradehero.common.billing.googleplay.exceptions.IABSendIntentException;
import com.tradehero.common.billing.googleplay.exceptions.IABUserCancelledException;
import com.tradehero.common.billing.googleplay.exceptions.IABVerificationFailedException;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.adapters.billing.StoreItemAdapter;
import com.tradehero.th.adapters.billing.THSKUDetailsAdapter;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.billing.googleplay.IABAlertSKUUtils;
import com.tradehero.th.billing.googleplay.IABAlertUtils;
import com.tradehero.th.billing.googleplay.THIABActor;
import com.tradehero.th.billing.googleplay.THIABActorUser;
import com.tradehero.th.billing.googleplay.THIABPurchaseHandler;
import com.tradehero.th.billing.googleplay.THSKUDetails;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.billing.management.FollowerManagerFragment;
import com.tradehero.th.fragments.billing.management.HeroManagerFragment;
import java.lang.ref.WeakReference;
import java.util.List;
import javax.inject.Inject;

public class StoreScreenFragment extends BasePurchaseManagerFragment
{
    public static final String TAG = StoreScreenFragment.class.getSimpleName();

    private ListView listView;
    private StoreItemAdapter storeItemAdapter;

    @Inject protected CurrentUserBaseKeyHolder currentUserBaseKeyHolder;

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
        if (!isBillingAvailable())
        {
            IABAlertUtils.popBillingUnavailable(getActivity());
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
                conditionalPopBuyVirtualDollars();
                break;

            case StoreItemAdapter.POSITION_BUY_FOLLOW_CREDITS:
                conditionalPopBuyFollowCredits();
                break;

            case StoreItemAdapter.POSITION_BUY_STOCK_ALERTS:
                conditionalPopBuyStockAlerts();
                break;

            case StoreItemAdapter.POSITION_BUY_RESET_PORTFOLIO:
                conditionalPopBuyResetPortfolio();
                break;

            case StoreItemAdapter.POSITION_MANAGE_HEROES:
                bundle = new Bundle();
                bundle.putInt(HeroManagerFragment.BUNDLE_KEY_USER_ID, currentUserBaseKeyHolder.getCurrentUserBaseKey().key);
                pushFragment(HeroManagerFragment.class, bundle);
                break;

            case StoreItemAdapter.POSITION_MANAGE_FOLLOWERS:
                bundle = new Bundle();
                bundle.putInt(HeroManagerFragment.BUNDLE_KEY_USER_ID, currentUserBaseKeyHolder.getCurrentUserBaseKey().key);
                pushFragment(FollowerManagerFragment.class, bundle);
                break;

            default:
                THToast.show("Clicked at position " + position);
                break;
        }
    }

    private void pushFragment(Class<? extends Fragment> fragmentClass)
    {
        ((DashboardActivity) getActivity()).getNavigator().pushFragment(fragmentClass);
    }

    private void pushFragment(Class<? extends Fragment> fragmentClass, Bundle bundle)
    {
        ((DashboardActivity) getActivity()).getNavigator().pushFragment(fragmentClass, bundle);
    }
}
