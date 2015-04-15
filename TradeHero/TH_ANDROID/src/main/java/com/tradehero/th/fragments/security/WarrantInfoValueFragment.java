package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.WarrantType;
import com.tradehero.th.api.security.compact.WarrantDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.competition.ProviderVideoListFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.persistence.competition.ProviderCacheRx;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.rx.ToastAction;
import com.tradehero.th.rx.ToastOnErrorAction;
import dagger.Lazy;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.inject.Inject;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class WarrantInfoValueFragment extends AbstractSecurityInfoFragment
{
    private final static String BUNDLE_KEY_PROVIDER_ID_KEY = WarrantInfoValueFragment.class.getName() + ".providerId";

    @InjectView(R.id.warrant_help_video_link) protected Button mHelpVideoLink;
    @InjectView(R.id.vwarrant_type) protected TextView mWarrantType;
    @InjectView(R.id.vwarrant_code) protected TextView mWarrantCode;
    @InjectView(R.id.vwarrant_expiry) protected TextView mWarrantExpiry;
    @InjectView(R.id.vwarrant_strike_price) protected TextView mStrikePrice;
    @InjectView(R.id.vwarrant_underlying) protected TextView mUnderlying;
    @InjectView(R.id.vwarrant_issuer) protected TextView mIssuer;

    @Inject protected SecurityCompactCacheRx securityCompactCache;
    @Inject protected ProviderCacheRx providerCache;
    @Inject Lazy<DashboardNavigator> navigator;
    protected WarrantDTO warrantDTO;
    @Nullable protected ProviderId providerId;
    protected ProviderDTO providerDTO;

    //<editor-fold desc="Argument Passing">
    public static void putProviderId(@NonNull Bundle args, @NonNull ProviderId providerId)
    {
        args.putBundle(BUNDLE_KEY_PROVIDER_ID_KEY, providerId.getArgs());
    }

    @Nullable private static ProviderId getProviderId(@NonNull Bundle args)
    {
        Bundle providerIdBundle = args.getBundle(BUNDLE_KEY_PROVIDER_ID_KEY);
        if (providerIdBundle != null)
        {
            return new ProviderId(providerIdBundle);
        }
        return null;
    }
    //</editor-fold>

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(this);
        this.providerId = getProviderId(getArguments());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_warrantinfo_value, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        fetchProvider();
        fetchSecurity();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    protected void fetchProvider()
    {
        if (this.providerId != null)
        {
            onDestroyViewSubscriptions.add(AppObservable.bindFragment(
                    this,
                    providerCache.getOne(this.providerId))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Action1<Pair<ProviderId, ProviderDTO>>()
                            {
                                @Override public void call(Pair<ProviderId, ProviderDTO> pair)
                                {
                                    linkWith(pair.second);
                                }
                            },
                            new ToastOnErrorAction()));
        }
        else
        {
            linkWith((ProviderDTO) null);
        }
    }

    public void linkWith(ProviderDTO providerDTO)
    {
        this.providerDTO = providerDTO;
        displayVideo();
    }

    protected void fetchSecurity()
    {
        if (securityId != null)
        {
            onDestroyViewSubscriptions.add(AppObservable.bindFragment(
                    this,
                    securityCompactCache.getOne(securityId))
                    .map(new PairGetSecond<SecurityId, SecurityCompactDTO>())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Action1<SecurityCompactDTO>()
                            {
                                @Override public void call(SecurityCompactDTO compactDTO)
                                {
                                    linkWith(compactDTO);
                                }
                            },
                            new ToastAction<Throwable>(getString(R.string.error_fetch_security_info))));
        }
    }

    public void linkWith(SecurityCompactDTO value)
    {
        securityCompactDTO = value;
        warrantDTO = (WarrantDTO) value;

        displayVideo();

        if (!isDetached() && mWarrantType != null)
        {
            if (warrantDTO == null || warrantDTO.warrantType == null)
            {
                mWarrantType.setText(R.string.na);
            }
            else
            {
                int warrantTypeStringResId;
                WarrantType warrantType = warrantDTO.getWarrantType();
                if (warrantType != null)
                {
                    switch (warrantType)
                    {
                        case CALL:
                            warrantTypeStringResId = R.string.warrant_type_call;
                            break;
                        case PUT:
                            warrantTypeStringResId = R.string.warrant_type_put;
                            break;
                        default:
                            throw new IllegalArgumentException("Unhandled warrant type " + warrantDTO.getWarrantType());
                    }
                    mWarrantType.setText(warrantTypeStringResId);
                }
                else
                {
                    mWarrantType.setText(R.string.na);
                }
            }
        }

        if (!isDetached() && mWarrantCode != null)
        {
            if (securityCompactDTO == null || securityCompactDTO.symbol == null)
            {
                mWarrantCode.setText(R.string.na);
            }
            else
            {
                mWarrantCode.setText(securityCompactDTO.symbol);
            }
        }

        if (!isDetached() && mWarrantExpiry != null)
        {
            if (warrantDTO == null || warrantDTO.expiryDate == null)
            {
                mWarrantExpiry.setText(R.string.na);
            }
            else
            {
                SimpleDateFormat df = new SimpleDateFormat("d MMM yy", Locale.US);
                mWarrantExpiry.setText(df.format(warrantDTO.expiryDate));
            }
        }

        if (!isDetached() && mStrikePrice != null)
        {
            if (warrantDTO == null || warrantDTO.strikePrice == null || warrantDTO.strikePriceCcy == null)
            {
                mStrikePrice.setText(R.string.na);
            }
            else
            {
                mStrikePrice.setText(
                        THSignedMoney.builder(warrantDTO.strikePrice)
                                .currency(warrantDTO.strikePriceCcy)
                                .withOutSign()
                                .build().toString()
                );
            }
        }

        if (!isDetached() && mUnderlying != null)
        {
            if (warrantDTO == null || warrantDTO.underlyingName == null)
            {
                mUnderlying.setText(R.string.na);
            }
            else
            {
                mUnderlying.setText(warrantDTO.underlyingName);
            }
        }

        if (!isDetached() && mIssuer != null)
        {
            if (warrantDTO == null || warrantDTO.issuerName == null)
            {
                mIssuer.setText(R.string.na);
            }
            else
            {
                mIssuer.setText(warrantDTO.issuerName.toUpperCase()); // HACK upperCase
            }
        }

    }

    protected void displayVideo()
    {
        if (!isDetached() && mHelpVideoLink != null)
        {
            mHelpVideoLink.setVisibility(hasHelpVideo() ? View.VISIBLE : View.GONE);
        }

        if (!isDetached() && mHelpVideoLink != null)
        {
            if (providerDTO != null)
            {
                mHelpVideoLink.setText(providerDTO.helpVideoText);
            }
            mHelpVideoLink.setTextColor(getResources().getColor(R.color.black));
        }
    }

    public boolean hasHelpVideo()
    {
        return providerDTO != null && providerDTO.hasHelpVideo;
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.warrant_help_video_link)
    protected void handleVideoLinkClicked(View view)
    {
        if (providerId != null)
        {
            Bundle args = new Bundle();
            ProviderVideoListFragment.putProviderId(args, providerId);
            navigator.get().pushFragment(ProviderVideoListFragment.class, args);
        }
    }
}
