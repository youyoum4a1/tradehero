package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.payment.UpdateAlipayAccountDTO;
import com.tradehero.th.api.users.payment.UpdateAlipayAccountFormDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.widget.ServerValidatedEmailText;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.EmptyObserver;

public class SettingsAlipayFragment extends DashboardFragment
{
    private View view;
    private ServerValidatedEmailText alipayAccountText;
    private ServerValidatedEmailText alipayAccountIDText;
    private ServerValidatedEmailText alipayAccountRealNameText;
    private ProgressDialog progressDialog;
    private Button submitButton;

    @Nullable private Subscription updateAlipayAccountSubscription;

    @Inject UserServiceWrapper userServiceWrapper;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject Analytics analytics;
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

        analytics.addEvent(new SimpleEvent(AnalyticsConstants.Settings_Alipay));
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(getResources().getString(R.string.settings_alipay_header));
        super.onCreateOptionsMenu(menu, inflater);
    }
    //</editor-fold>

    @Override public void onDestroyView()
    {
        unsubscribe(updateAlipayAccountSubscription);
        updateAlipayAccountSubscription = null;
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
                unsubscribe(updateAlipayAccountSubscription);
                updateAlipayAccountSubscription = userServiceWrapper.updateAlipayAccountRx(
                        currentUserId.toUserBaseKey(), accountDTO)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(createUpdatePayPalObserver());
            }
        });
    }

    private Observer<UpdateAlipayAccountDTO> createUpdatePayPalObserver()
    {
        return new EmptyObserver<UpdateAlipayAccountDTO>()
        {
            @Override public void onNext(UpdateAlipayAccountDTO args)
            {
                if (!isDetached())
                {
                    THToast.show(getString(R.string.settings_alipay_successful_update));
                    progressDialog.hide();
                    navigator.get().popFragment();
                }
            }

            @Override public void onError(Throwable e)
            {
                if (!isDetached())
                {
                    THToast.show(new THException(e));
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
        AndroidObservable.bindFragment(this,
                userProfileCache.get(currentUserId.toUserBaseKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createUserProfileCacheObserver());
    }

    private Observer<Pair<UserBaseKey, UserProfileDTO>> createUserProfileCacheObserver()
    {
        return new Observer<Pair<UserBaseKey,UserProfileDTO>>()
        {
            @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
            {
                alipayAccountText.setText(pair.second.alipayAccount);
                alipayAccountIDText.setText(pair.second.alipayIdentityNumber);
                alipayAccountRealNameText.setText(pair.second.alipayRealName);
            }

            @Override public void onCompleted()
            {
            }

            @Override public void onError(Throwable e)
            {
                THToast.show(getString(R.string.error_fetch_your_user_profile));
            }
        };
    }
}