package com.tradehero.th.fragments.chinabuild.fragment.userCenter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

/*
   用户的账户财产总览
 */
public class UserAccountPage extends DashboardFragment
{
    public static final String BUNDLE_SHOW_USER_ID = "bundle_show_user_id";
    public UserBaseKey showUserBaseKey;

    @Inject Lazy<UserProfileCache> userProfileCache;
    private DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;

    @InjectView(R.id.tvWatchListItemROI) TextView tvItemROI;
    @InjectView(R.id.tvWatchListItemAllAmount) TextView tvItemAllAmount;
    @InjectView(R.id.tvWatchListItemDynamicAmount) TextView tvItemDynamicAmount;
    @InjectView(R.id.tvWatchListItemCash) TextView tvItemCash;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initArgument();
        userProfileCacheListener = createUserProfileFetchListener();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain("资产信息");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.user_account_page_fragment, container, false);
        ButterKnife.inject(this, view);
        fetchUserProfile();
        return view;
    }

    public void initArgument()
    {
        Bundle bundle = getArguments();
        int userId = bundle.getInt(BUNDLE_SHOW_USER_ID, 0);
        if (userId != 0)
        {
            showUserBaseKey = new UserBaseKey(userId);
        }
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        detachUserProfileCache();
        ButterKnife.reset(this);

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        userProfileCacheListener = null;
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
    }

    private void detachUserProfileCache()
    {
        userProfileCache.get().unregister(userProfileCacheListener);
    }

    protected void fetchUserProfile()
    {
        detachUserProfileCache();
        userProfileCache.get().register(showUserBaseKey, userProfileCacheListener);
        userProfileCache.get().getOrFetchAsync(showUserBaseKey);
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileFetchListener()
    {
        return new UserProfileFetchListener();
    }

    protected class UserProfileFetchListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override
        public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            displayProfolioDTO(value);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {

        }
    }

    private void displayProfolioDTO(UserProfileDTO value)
    {
        PortfolioDTO cached = value.portfolio;
        if (cached == null) return;

        if (cached.roiSinceInception != null)
        {
            THSignedNumber roi = THSignedPercentage.builder(cached.roiSinceInception * 100)
                    .withSign()
                    .signTypeArrow()
                    .build();
            tvItemROI.setText(roi.toString());
            tvItemROI.setTextColor(getResources().getColor(roi.getColorResId()));
        }

        String valueString = String.format("%s %,.0f", cached.getNiceCurrency(), cached.totalValue);
        tvItemAllAmount.setText(valueString);

        Double pl = cached.plSinceInception;
        if (pl == null)
        {
            pl = 0.0;
        }
        THSignedNumber thPlSinceInception = THSignedMoney.builder(pl)
                .withSign()
                .signTypePlusMinusAlways()
                .currency(cached.getNiceCurrency())
                .build();
        tvItemDynamicAmount.setText(thPlSinceInception.toString());
        tvItemDynamicAmount.setTextColor(thPlSinceInception.getColor());

        String vsCash = String.format("%s %,.0f", cached.getNiceCurrency(), cached.cashBalance);
        tvItemCash.setText(vsCash);
    }
}
