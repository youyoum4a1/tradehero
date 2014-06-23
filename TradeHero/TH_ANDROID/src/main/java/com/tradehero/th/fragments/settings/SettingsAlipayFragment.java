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
import com.tradehero.common.persistence.DTOCacheNew;
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
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import com.tradehero.th.widget.ServerValidatedEmailText;
import javax.inject.Inject;

public class SettingsAlipayFragment extends DashboardFragment
{
    private View view;
    private ServerValidatedEmailText alipayAccountText;
    private ServerValidatedEmailText alipayAccountIDText;
    private ServerValidatedEmailText alipayAccountRealNameText;
    private ProgressDialog progressDialog;
    private Button submitButton;

    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    private MiddleCallback<UpdateAlipayAccountDTO> middleCallbackUpdateAlipayAccount;

    @Inject UserServiceWrapper userServiceWrapper;
    @Inject UserProfileCache userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject THLocalyticsSession localyticsSession;
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
        detachUserProfileCache();
        if (alipayAccountText != null)
        {
            alipayAccountText.setOnTouchListener(null);
            alipayAccountText = null;
        }
        if (alipayAccountIDText != null)
        {
            alipayAccountIDText.setOnTouchListener(null);
            alipayAccountIDText = null;
        }
        if (alipayAccountRealNameText != null)
        {
            alipayAccountRealNameText.setOnTouchListener(null);
            alipayAccountRealNameText = null;
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

    private void detachUserProfileCache()
    {
        userProfileCache.unregister(userProfileCacheListener);
        userProfileCacheListener = null;
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
                accountDTO.userIdentityNumber = alipayAccountIDText.getText().toString();
                accountDTO.userRealName = alipayAccountRealNameText.getText().toString();
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
        alipayAccountIDText = (ServerValidatedEmailText) view.findViewById(R.id.settings_alipay_id_text);
        alipayAccountIDText.setOnTouchListener(new FocusableOnTouchListener());
        alipayAccountRealNameText = (ServerValidatedEmailText) view.findViewById(R.id.settings_alipay_realname_text);
        alipayAccountRealNameText.setOnTouchListener(new FocusableOnTouchListener());
        detachUserProfileCache();
        userProfileCacheListener = createUserProfileCacheListener();
        userProfileCache.register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileCacheListener()
    {
        return new DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>()
        {
            @Override
            public void onDTOReceived(UserBaseKey key, UserProfileDTO value)
            {
                if (!isDetached())
                {
                    alipayAccountText.setText(value.alipayAccount);
                    alipayAccountIDText.setText(value.alipayIdentityNumber);
                    alipayAccountRealNameText.setText(value.alipayRealName);
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