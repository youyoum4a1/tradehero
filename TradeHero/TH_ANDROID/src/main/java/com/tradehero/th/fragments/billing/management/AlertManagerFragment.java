package com.tradehero.th.fragments.billing.management;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;

/** Created with IntelliJ IDEA. User: xavier Date: 11/13/13 Time: 12:04 PM To change this template use File | Settings | File Templates. */
public class AlertManagerFragment extends BasePurchaseManagerFragment
{
    public static final String TAG = AlertManagerFragment.class.getSimpleName();

    public static final String BUNDLE_KEY_USER_ID = AlertManagerFragment.class.getName() + ".userId";

    private TextView planCount;
    private ImageView planCountHint;
    private ImageButton btnPlanUpgrade;
    private AlertListView alertListView;

    //private AlertListItemAdapter alertListItemAdapter;

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_store_manage_alerts, container, false);
        initViews(view);
        return view;
    }

    protected void initViews(View view)
    {
        planCount = (TextView) view.findViewById(R.id.manage_alerts_count);
        planCountHint = (ImageView) view.findViewById(R.id.icn_alert_plan_hint);
        btnPlanUpgrade = (ImageButton) view.findViewById(R.id.btn_upgrade_plan);
        alertListView = (AlertListView) view.findViewById(R.id.alerts_list);

        //if (alertListItemAdapter == null)
        //{
        //    alertListItemAdapter = new AlertListItemAdapter(getActivity(),
        //            getActivity().getLayoutInflater(),
        //            R.layout.alert_list_header,
        //            R.layout.alert_list_item,
        //            R.layout.alert_list_item
        //
        //    );
        //}

        if (alertListView != null)
        {
            alertListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    handleAlertItemClicked(view, position, id);
                }
            });
            //alertListView.setAdapter(alertListItemAdapter);
        }
    }

    private void handleAlertItemClicked(View view, int position, long id)
    {

    }
}
