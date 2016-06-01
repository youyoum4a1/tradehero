package com.ayondo.academy.fragments.location;

import android.app.Activity;
import android.app.ProgressDialog;
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
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.tradehero.common.rx.PairGetSecond;
import com.ayondo.academy.R;
import com.ayondo.academy.api.market.Country;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.users.UpdateCountryCodeDTO;
import com.ayondo.academy.api.users.UpdateCountryCodeFormDTO;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.fragments.base.BaseFragment;
import com.ayondo.academy.network.service.UserServiceWrapper;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;
import com.ayondo.academy.rx.ToastOnErrorAction1;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class LocationListFragment extends BaseFragment
{
    private LocationAdapter mListAdapter;
    @Nullable private Subscription updateCountryCodeSubscription;
    protected UserProfileDTO currentUserProfile;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject UserProfileCacheRx userProfileCache;

    @Bind(android.R.id.list) ListView listView;

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        mListAdapter = new LocationAdapter(
                activity,
                R.layout.settings_location_list_item);
        mListAdapter.addAll(ListedLocationDTOFactory.createListToShow());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_settings_location_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        listView.setAdapter(mListAdapter);
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
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDetach()
    {
        mListAdapter = null;
        super.onDetach();
    }

    protected void fetchUserProfile()
    {
        AppObservable.bindSupportFragment(
                this,
                userProfileCache.get(currentUserId.toUserBaseKey())
                        .map(new PairGetSecond<UserBaseKey, UserProfileDTO>()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<UserProfileDTO>()
                        {
                            @Override public void call(UserProfileDTO profile)
                            {
                                LocationListFragment.this.linkWith(profile);
                            }
                        },
                        new ToastOnErrorAction1(getString(R.string.error_fetch_your_user_profile)));
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
                listView.setSelection(mListAdapter.getPosition(new ListedLocationDTO(currentCountry)));
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

        final ProgressDialog progressDialog = getProgressDialogOld();

        UpdateCountryCodeFormDTO updateCountryCodeFormDTO = new UpdateCountryCodeFormDTO(countryCode);
        unsubscribe(updateCountryCodeSubscription);
        updateCountryCodeSubscription = AppObservable.bindSupportFragment(
                this,
                userServiceWrapperLazy.get().updateCountryCodeRx(
                        currentUserId.toUserBaseKey(), updateCountryCodeFormDTO))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<UpdateCountryCodeDTO>()
                {
                    @Override public void call(UpdateCountryCodeDTO updateCountryCodeDTO)
                    {
                        progressDialog.dismiss();
                    }
                })
                .subscribe(
                        new Action1<UpdateCountryCodeDTO>()
                        {
                            @Override public void call(UpdateCountryCodeDTO args)
                            {
                                LocationListFragment.this.backToSettings();
                            }
                        },
                        new ToastOnErrorAction1());
    }

    private void backToSettings()
    {
        navigator.get().popFragment();
    }

    private ProgressDialog getProgressDialogOld()
    {
        return ProgressDialog.show(
                getActivity(),
                getString(R.string.loading_loading),
                getString(R.string.alert_dialog_please_wait),
                true);
    }
}
