package com.tradehero.th.fragments.location;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UpdateCountryCodeDTO;
import com.tradehero.th.api.users.UpdateCountryCodeFormDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class LocationListFragment extends DashboardFragment
{
    private LocationAdapter mListAdapter;
    private MiddleCallback<UpdateCountryCodeDTO> middleCallback;
    private ProgressDialog progressDialog;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    protected UserProfileDTO currentUserProfile;

    @Inject Context context;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserServiceWrapper> userServiceWrapperLazy;
    @Inject Lazy<ProgressDialogUtil> progressDialogUtilLazy;
    @Inject ListedLocationDTOFactory listedLocationDTOFactory;
    @Inject UserProfileCache userProfileCache;

    @InjectView(android.R.id.list) ListView listView;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        userProfileCacheListener = createUserProfileListener();
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
        detachUserProfileCache();
        detachMiddleCallback();
        super.onStop();
    }

    @Override public void onDestroyOptionsMenu()
    {
        setActionBarSubtitle(null);
        super.onDestroyOptionsMenu();
    }

    @Override public void onDestroyView()
    {
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
        userProfileCacheListener = null;
        super.onDestroy();
    }

    private void detachUserProfileCache()
    {
        userProfileCache.unregister(userProfileCacheListener);
    }

    private void detachMiddleCallback()
    {
        if (middleCallback != null)
        {
            middleCallback.setPrimaryCallback(null);
        }
        middleCallback = null;
    }

    protected void fetchUserProfile()
    {
        detachUserProfileCache();
        userProfileCache.register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileListener()
    {
        return new LocationListUserProfileListener();
    }

    protected class LocationListUserProfileListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
        }
    }

    protected void linkWith(UserProfileDTO userProfileDTO, boolean andDisplay)
    {
        this.currentUserProfile = userProfileDTO;
        if (userProfileDTO != null && userProfileDTO.countryCode != null)
        {
            try
            {
                Country currentCountry = Country.valueOf(userProfileDTO.countryCode);
                mListAdapter.setCurrentCountry(currentCountry);
                listView.smoothScrollToPosition(mListAdapter.getPosition(new ListedLocationDTO(currentCountry)));
            }
            catch (IllegalArgumentException e)
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
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
    {
        getProgressDialog().show();
        updateCountryCode(((ListedLocationDTO) adapterView.getItemAtPosition(position)).country.name());
    }

    protected void updateCountryCode(@NotNull String countryCode)
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
        detachMiddleCallback();
        middleCallback = userServiceWrapperLazy.get().updateCountryCode(
                currentUserId.toUserBaseKey(), updateCountryCodeFormDTO, new UpdateCountryCodeCallback());
    }

    private class UpdateCountryCodeCallback implements retrofit.Callback<UpdateCountryCodeDTO>
    {
        @Override public void success(UpdateCountryCodeDTO updateCountryCodeDTO, Response response2)
        {
            backToSettings();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            getProgressDialog().hide();
        }
    }

    private void backToSettings()
    {
        getProgressDialog().hide();
        getDashboardNavigator().popFragment();
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
