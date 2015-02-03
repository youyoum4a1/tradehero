package com.tradehero.th.fragments.location;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UpdateCountryCodeFormDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class LocationListFragment extends DashboardFragment
{
    private LocationAdapter mListAdapter;
    @Nullable private Subscription updateCountryCodeSubscription;
    private ProgressDialog progressDialog;
    protected UserProfileDTO currentUserProfile;

    @Inject Context context;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject Lazy<ProgressDialogUtil> progressDialogUtilLazy;
    @Inject ListedLocationDTOFactory listedLocationDTOFactory;
    @Inject UserProfileCacheRx userProfileCache;

    @InjectView(android.R.id.list) ListView listView;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mListAdapter = new LocationAdapter(
                context,
                R.layout.settings_location_list_item);
        mListAdapter.addAll(listedLocationDTOFactory.createListToShow());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings_location_list, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        listView.setAdapter(mListAdapter);
        listView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.location_fragment_title);
        setActionBarSubtitle(R.string.location_fragment_subtitle);
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchUserProfile();
    }

    @Override public void onStop()
    {
        unsubscribe(updateCountryCodeSubscription);
        updateCountryCodeSubscription = null;
        super.onStop();
    }

    @Override public void onDestroyOptionsMenu()
    {
        setActionBarSubtitle(null);
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
        listView.setOnScrollListener(null);
        listView.setEmptyView(null);
        ButterKnife.reset(this);
        progressDialog = null;
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        if (mListAdapter != null)
        {
            mListAdapter = null;
        }
        super.onDestroy();
    }

    protected void fetchUserProfile()
    {
        AppObservable.bindFragment(
                this,
                userProfileCache.get(currentUserId.toUserBaseKey())
                        .map(new PairGetSecond<>()))
                .subscribe(
                        this::linkWith,
                        e -> THToast.show(R.string.error_fetch_your_user_profile));
    }

    protected void linkWith(UserProfileDTO userProfileDTO)
    {
        this.currentUserProfile = userProfileDTO;
        if (userProfileDTO != null && userProfileDTO.countryCode != null)
        {
            try
            {
                Country currentCountry = Country.valueOf(userProfileDTO.countryCode);
                mListAdapter.setCurrentCountry(currentCountry);
                listView.smoothScrollToPosition(mListAdapter.getPosition(new ListedLocationDTO(currentCountry)));
            } catch (IllegalArgumentException e)
            {
                Timber.e(e, "Does not have country code for ", userProfileDTO.countryCode);
                mListAdapter.setCurrentCountry(null);
            }
        }
        else
        {
            mListAdapter.setCurrentCountry(null);
        }
        mListAdapter.notifyDataSetChanged();
    }

    @OnItemClick(android.R.id.list)
    public void onItemClick(
            @NonNull AdapterView<?> adapterView,
            @SuppressWarnings("UnusedParameters") View view,
            int position,
            @SuppressWarnings("UnusedParameters") long l)
    {
        getProgressDialog().show();
        updateCountryCode(((ListedLocationDTO) adapterView.getItemAtPosition(position)).country.name());
    }

    protected void updateCountryCode(@NonNull String countryCode)
    {
        String currentCountryCode = currentUserProfile != null ? currentUserProfile.countryCode : null;
        if (currentCountryCode != null &&
                countryCode.equals(currentCountryCode))
        {
            // Nothing to do
            backToSettings();
            return;
        }

        UpdateCountryCodeFormDTO updateCountryCodeFormDTO = new UpdateCountryCodeFormDTO(countryCode);
        unsubscribe(updateCountryCodeSubscription);
        updateCountryCodeSubscription = AppObservable.bindFragment(
                this,
                userServiceWrapperLazy.get().updateCountryCodeRx(
                        currentUserId.toUserBaseKey(), updateCountryCodeFormDTO))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        args -> backToSettings(),
                        e -> {
                            THToast.show(new THException(e));
                            getProgressDialog().hide();
                        });
    }

    private void backToSettings()
    {
        getProgressDialog().hide();
        navigator.get().popFragment();
    }

    private ProgressDialog getProgressDialog()
    {
        if (progressDialog != null)
        {
            return progressDialog;
        }
        progressDialog = progressDialogUtilLazy.get().show(getActivity(), R.string.loading_loading,
                R.string.alert_dialog_please_wait);
        progressDialog.hide();
        return progressDialog;
    }
}
