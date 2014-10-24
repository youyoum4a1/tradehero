package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailFormDTO;
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
import com.tradehero.th.widget.ServerValidatedEmailText;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public class SettingsPayPalFragment extends DashboardFragment
{
    private View view;
    private ServerValidatedEmailText paypalEmailText;
    private ProgressDialog progressDialog;
    private Button submitButton;

    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    private MiddleCallback<UpdatePayPalEmailDTO> middleCallbackUpdatePayPalEmail;

    @Inject UserServiceWrapper userServiceWrapper;
    @Inject UserProfileCache userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject ProgressDialogUtil progressDialogUtil;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_settings_paypal, container, false);

        setupSubmitButton();
        setupPaypalEmailText();
        return view;
    }

    @Override public void onResume()
    {
        super.onResume();
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(getResources().getString(R.string.settings_paypal_header));
        super.onCreateOptionsMenu(menu, inflater);
    }
    //</editor-fold>

    @Override public void onDestroyView()
    {
        detachMiddleCallbackUpdatePayPalEmail();
        detachUserProfileCache();
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

    private void detachMiddleCallbackUpdatePayPalEmail()
    {
        if (middleCallbackUpdatePayPalEmail != null)
        {
            middleCallbackUpdatePayPalEmail.setPrimaryCallback(null);
        }
        middleCallbackUpdatePayPalEmail = null;
    }

    private void detachUserProfileCache()
    {
        if (userProfileCacheListener != null)
        {
            userProfileCache.unregister(userProfileCacheListener);
        }
        userProfileCacheListener = null;
    }

    private void setupSubmitButton()
    {
        submitButton = (Button) view.findViewById(R.id.settings_paypal_update_button);
        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View view)
            {
                progressDialog = progressDialogUtil.show(
                        getActivity(),
                        R.string.alert_dialog_please_wait,
                        R.string.authentication_connecting_tradehero_only);
                UpdatePayPalEmailFormDTO emailDTO = new UpdatePayPalEmailFormDTO();
                emailDTO.newPayPalEmailAddress = paypalEmailText.getText().toString();
                detachMiddleCallbackUpdatePayPalEmail();
                middleCallbackUpdatePayPalEmail = userServiceWrapper.updatePayPalEmail(currentUserId.toUserBaseKey(), emailDTO, createUpdatePayPalCallback());
            }
        });
    }

    private THCallback<UpdatePayPalEmailDTO> createUpdatePayPalCallback()
    {
        return new THCallback<UpdatePayPalEmailDTO>()
        {
            @Override
            protected void success(UpdatePayPalEmailDTO updatePayPalEmailDTO, THResponse thResponse)
            {
                if (!isDetached())
                {
                    THToast.show(getString(R.string.settings_paypal_successful_update));
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

    private void setupPaypalEmailText()
    {
        paypalEmailText = (ServerValidatedEmailText) view.findViewById(R.id.settings_paypal_email_text);
        // HACK: force this email to focus instead of the TabHost stealing focus..
        paypalEmailText.setOnTouchListener(new FocusableOnTouchListener());
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
            public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
            {
                if (!isDetached())
                {
                    paypalEmailText.setText(value.paypalEmailAddress);
                }
            }

            @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
            {
                if (!isDetached())
                {
                    THToast.show(getString(R.string.error_fetch_your_user_profile));
                    Timber.e("Error fetching the user profile %s", key, error);
                }
            }
        };
    }
}