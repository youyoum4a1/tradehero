package com.tradehero.th.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

/**
 * Created with IntelliJ IDEA.
 * User: nia
 * Date: 22/10/13
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class SettingsPayPalFragment extends DashboardFragment
{

    private View view;
    private EditText paypalEmailText;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_settings_paypal, container, false);

        paypalEmailText = (EditText) view.findViewById(R.id.settings_paypal_email_text);
        // HACK: force this email to focus instead of the TabHost stealing focus..
        paypalEmailText.setOnTouchListener(new FocusableOnTouchListener());
        return view;
    }

    @Override
    public void onDestroyView() {
        if (paypalEmailText != null)
        {
            paypalEmailText.setOnTouchListener(null);
            paypalEmailText = null;
        }
        super.onDestroyView();
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>
}
