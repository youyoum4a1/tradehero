package com.tradehero.th.fragments.onboarding.pref;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeSectorCompactListDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.models.market.ExchangeSectorCompactKey;
import com.tradehero.th.persistence.market.ExchangeSectorCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class OnBoardPickExchangeSectorFragment extends BaseFragment
{
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject ExchangeSectorCompactListCache exchangeSectorCompactListCache;
    @NotNull OnBoardPickExchangeSectorViewHolder viewHolder;
    @Nullable DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    @Nullable DTOCacheNew.Listener<ExchangeSectorCompactKey, ExchangeSectorCompactListDTO> exchangeSectorListener;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        userProfileCacheListener = createUSerProfileListener();
        exchangeSectorListener = createExchangeSectorListener();
        viewHolder = new OnBoardPickExchangeSectorViewHolder(getActivity());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.onboard_select_exchange_sector, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        viewHolder.attachView(view);
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchUserProfile();
        fetchExchangeSectors();
    }

    @Override public void onStop()
    {
        detachUserProfileCache();
        detachExchangeSectorCompactListCache();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        viewHolder.detachView();
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        exchangeSectorListener = null;
        userProfileCacheListener = null;
        super.onDestroy();
    }

    protected void fetchUserProfile()
    {
        detachUserProfileCache();
        UserBaseKey key = currentUserId.toUserBaseKey();
        userProfileCache.register(key, userProfileCacheListener);
        userProfileCache.getOrFetchAsync(key);
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUSerProfileListener()
    {
        return new OnBoardPickExchangeSectorUserProfileListener();
    }

    protected class OnBoardPickExchangeSectorUserProfileListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            viewHolder.setUserProfile(value);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
        }
    }

    protected void detachUserProfileCache()
    {
        userProfileCache.unregister(userProfileCacheListener);
    }

    protected void fetchExchangeSectors()
    {
        detachExchangeSectorCompactListCache();
        ExchangeSectorCompactKey key = new ExchangeSectorCompactKey();
        exchangeSectorCompactListCache.register(key, exchangeSectorListener);
        exchangeSectorCompactListCache.getOrFetchAsync(key);
    }

    protected void detachExchangeSectorCompactListCache()
    {
        exchangeSectorCompactListCache.unregister(exchangeSectorListener);
    }

    protected DTOCacheNew.Listener<ExchangeSectorCompactKey, ExchangeSectorCompactListDTO>
        createExchangeSectorListener()
    {
        return new OnBoardPickExchangeSectorListener();
    }

    protected class OnBoardPickExchangeSectorListener
            implements DTOCacheNew.Listener<ExchangeSectorCompactKey, ExchangeSectorCompactListDTO>
    {
        @Override public void onDTOReceived(@NotNull ExchangeSectorCompactKey key, @NotNull ExchangeSectorCompactListDTO value)
        {
            Timber.d("lyl "+value.toString());
            viewHolder.setExchangeSector(value);
        }

        @Override public void onErrorThrown(@NotNull ExchangeSectorCompactKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.market_on_board_error_fetch_exchange_sector);
        }
    }

    @NotNull public OnBoardPrefDTO getOnBoardPrefs()
    {
        return viewHolder.getOnBoardPrefs();
    }

    public boolean canGetPrefs()
    {
        return viewHolder.canGetPrefs();
    }
}
