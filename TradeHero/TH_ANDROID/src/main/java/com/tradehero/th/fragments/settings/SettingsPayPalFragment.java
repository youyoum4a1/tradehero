package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailFormDTO;
import com.tradehero.th.base.Application;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.widget.ServerValidatedEmailText;
import dagger.Lazy;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: nia
 * Date: 22/10/13
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class SettingsPayPalFragment extends DashboardFragment
{
    public static final String TAG = SettingsPayPalFragment.class.getSimpleName();

    private View view;
    private ServerValidatedEmailText paypalEmailText;
    private ProgressDialog progressDialog;
    private Button submitButton;

    @Inject UserService userService;
    @Inject protected Lazy<UserProfileCache> userProfileCache;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_settings_paypal, container, false);

        setupSubmitButton();
        setupPaypalEmailText();
        return view;
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        getSherlockActivity().getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        getSherlockActivity().getSupportActionBar().setTitle(getResources().getString(R.string.settings_paypal_header));
        super.onCreateOptionsMenu(menu, inflater);
    }
    //</editor-fold>

    private void setupSubmitButton()
    {
        submitButton = (Button)view.findViewById(R.id.settings_paypal_update_button);
        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                progressDialog = ProgressDialog.show(
                        getSherlockActivity(),
                        Application.getResourceString(R.string.please_wait),
                        Application.getResourceString(R.string.connecting_tradehero_only),
                        true);
                UpdatePayPalEmailFormDTO emailDTO = new UpdatePayPalEmailFormDTO();
                emailDTO.newPayPalEmailAddress = paypalEmailText.getText().toString();
                userService.updatePayPalEmail(THUser.getCurrentUserBase().id, emailDTO, new THCallback<UpdatePayPalEmailDTO>()
                {
                    @Override
                    protected void success(UpdatePayPalEmailDTO updatePayPalEmailDTO, THResponse thResponse)
                    {
                        THToast.show(getString(R.string.settings_paypal_successful_update));
                        progressDialog.hide();
                        Navigator navigator = ((NavigatorActivity) getActivity()).getNavigator();
                        navigator.popFragment();
                    }

                    @Override
                    protected void failure(THException ex)
                    {
                        THToast.show(ex.getMessage());
                        progressDialog.hide();
                    }
                });
            }
        });
    }

    private void setupPaypalEmailText()
    {
        paypalEmailText = (ServerValidatedEmailText) view.findViewById(R.id.settings_paypal_email_text);
        // HACK: force this email to focus instead of the TabHost stealing focus..
        paypalEmailText.setOnTouchListener(new FocusableOnTouchListener());

        UserBaseDTO dto = THUser.getCurrentUserBase();
        UserBaseKey baseKey = new UserBaseKey(dto.id);
        userProfileCache.get()
                .getOrFetch(baseKey, false, new DTOCache.Listener<UserBaseKey, UserProfileDTO>()
                {
                    @Override
                    public void onDTOReceived(UserBaseKey key, UserProfileDTO value)
                    {
                        paypalEmailText.setText(value.paypalEmailAddress);
                    }

                    @Override public void onErrorThrown(UserBaseKey key, Throwable error)
                    {
                        THToast.show(getString(R.string.error_fetch_your_user_profile));
                        THLog.e(TAG, "Error fetching the user profile " + key, error);
                    }
                }).execute();
    }

    @Override
    public void onDestroyView()
    {
        if (paypalEmailText != null)
        {
            paypalEmailText.setOnTouchListener(null);
            paypalEmailText = null;
        }
        if (submitButton != null)
        {
            submitButton.setOnClickListener(null);
            submitButton = null;
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
