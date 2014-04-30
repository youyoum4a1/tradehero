package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.localytics.android.LocalyticsSession;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.payment.UpdateAlipayAccountDTO;
import com.tradehero.th.api.users.payment.UpdateAlipayAccountFormDTO;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.base.NavigatorActivity;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import com.tradehero.th.widget.ServerValidatedEmailText;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 22/10/13
 * Time: 12:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class SettingsAlipayFragment extends DashboardFragment
{
    private View view;
    private ServerValidatedEmailText alipayAccountText;
    private ProgressDialog progressDialog;
    private Button submitButton;

    private DTOCache.GetOrFetchTask<UserBaseKey, UserProfileDTO> userProfileFetchTask;
    private MiddleCallback<UpdateAlipayAccountDTO> middleCallbackUpdateAlipayAccount;

    @Inject UserServiceWrapper userServiceWrapper;
    @Inject UserProfileCache userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject LocalyticsSession localyticsSession;
    @Inject ProgressDialogUtil progressDialogUtil;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_settings_alipay, container, false);

        setupSubmitButton();
        setupAlipayAccountText();
        return view;
    }

    @Override public void onResume()
    {
        super.onResume();

        localyticsSession.tagEvent(LocalyticsConstants.Settings_Alipay);
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        getSherlockActivity().getSupportActionBar().setDisplayOptions(
                ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_HOME_AS_UP);
        getSherlockActivity().getSupportActionBar().setTitle(getResources().getString(R.string.settings_alipay_header));
        super.onCreateOptionsMenu(menu, inflater);
    }
    //</editor-fold>

    @Override public void onDestroyView()
    {
        detachMiddleCallbackUpdateAlipayAccount();
        detachUserProfileFetchTask();
        if (alipayAccountText != null)
        {
            alipayAccountText.setOnTouchListener(null);
            alipayAccountText = null;
        }
        if (submitButton != null)
        {
            submitButton.setOnClickListener(null);
            submitButton = null;
        }
        super.onDestroyView();
    }

    private void detachMiddleCallbackUpdateAlipayAccount()
    {
        if (middleCallbackUpdateAlipayAccount != null)
        {
            middleCallbackUpdateAlipayAccount.setPrimaryCallback(null);
        }
        middleCallbackUpdateAlipayAccount = null;
    }

    private void detachUserProfileFetchTask()
    {
        if (userProfileFetchTask != null)
        {
            userProfileFetchTask.setListener(null);
        }
        userProfileFetchTask = null;
    }

    private void setupSubmitButton()
    {
        submitButton = (Button) view.findViewById(R.id.settings_alipay_update_button);
        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view)
            {
                progressDialog = progressDialogUtil.show(
                        getActivity(),
                        R.string.alert_dialog_please_wait,
                        R.string.authentication_connecting_tradehero_only);
                UpdateAlipayAccountFormDTO accountDTO = new UpdateAlipayAccountFormDTO();
                accountDTO.newAlipayAccount = alipayAccountText.getText().toString();
                detachMiddleCallbackUpdateAlipayAccount();
                middleCallbackUpdateAlipayAccount = userServiceWrapper.updateAlipayAccount(
                        currentUserId.toUserBaseKey(), accountDTO, createUpdatePayPalCallback());
            }
        });
    }

    private THCallback<UpdateAlipayAccountDTO> createUpdatePayPalCallback()
    {
        return new THCallback<UpdateAlipayAccountDTO>()
        {
            @Override
            protected void success(UpdateAlipayAccountDTO updateAlipayEmailDTO, THResponse thResponse)
            {
                if (!isDetached())
                {
                    THToast.show(getString(R.string.settings_alipay_successful_update));
                    progressDialog.hide();
                    Navigator navigator = ((NavigatorActivity) getActivity()).getNavigator();
                    navigator.popFragment();
                }
            }

            @Override
            protected void failure(THException ex)
            {
                if (!isDetached())
                {
                    THToast.show(ex.getMessage());
                    progressDialog.hide();
                }
            }
        };
    }

    private void setupAlipayAccountText()
    {
        alipayAccountText = (ServerValidatedEmailText) view.findViewById(R.id.settings_alipay_email_text);
        // HACK: force this email to focus instead of the TabHost stealing focus..
        alipayAccountText.setOnTouchListener(new FocusableOnTouchListener());
        detachUserProfileFetchTask();
        userProfileFetchTask = userProfileCache.getOrFetch(currentUserId.toUserBaseKey(), createUserProfileCacheListener());
        userProfileFetchTask.execute();
    }

    private DTOCache.Listener<UserBaseKey, UserProfileDTO> createUserProfileCacheListener()
    {
        return new DTOCache.Listener<UserBaseKey, UserProfileDTO>()
        {
            @Override
            public void onDTOReceived(UserBaseKey key, UserProfileDTO value, boolean fromCache)
            {
                if (!isDetached())
                {
                    alipayAccountText.setText(value.alipayAccount);
                }
            }

            @Override public void onErrorThrown(UserBaseKey key, Throwable error)
            {
                if (!isDetached())
                {
                    THToast.show(getString(R.string.error_fetch_your_user_profile));
                }
            }
        };
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return false;
    }
    //</editor-fold>
}