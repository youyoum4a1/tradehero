package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.compact.WarrantDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.competition.ProviderVideoListFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.persistence.competition.ProviderCacheRx;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import dagger.Lazy;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class WarrantInfoValueFragment extends AbstractSecurityInfoFragment<SecurityCompactDTO>
{
    public final static String BUNDLE_KEY_PROVIDER_ID_KEY = WarrantInfoValueFragment.class.getName() + ".providerId";

    @InjectView(R.id.warrant_help_video_link) protected Button mHelpVideoLink;
    @InjectView(R.id.vwarrant_type) protected TextView mWarrantType;
    @InjectView(R.id.vwarrant_code) protected TextView mWarrantCode;
    @InjectView(R.id.vwarrant_expiry) protected TextView mWarrantExpiry;
    @InjectView(R.id.vwarrant_strike_price) protected TextView mStrikePrice;
    @InjectView(R.id.vwarrant_underlying) protected TextView mUnderlying;
    @InjectView(R.id.vwarrant_issuer) protected TextView mIssuer;

    @Inject protected SecurityCompactCacheRx securityCompactCache;
    @Nullable Subscription securityCompactCacheSubscription;
    protected WarrantDTO warrantDTO;
    protected ProviderId providerId;
    protected ProviderDTO providerDTO;
    @Inject protected ProviderCacheRx providerCache;
    @Inject Lazy<DashboardNavigator> navigator;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_warrantinfo_value, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        ButterKnife.inject(this, view);

        if (mHelpVideoLink != null)
        {
            mHelpVideoLink.setOnClickListener(this::handleVideoLinkClicked);
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        Bundle args = getArguments();
        if (args != null)
        {
            Bundle providerIdBundle = getArguments().getBundle(BUNDLE_KEY_PROVIDER_ID_KEY);
            if (providerIdBundle != null)
            {
                linkWith(new ProviderId(providerIdBundle), true);
            }
        }
    }

    @Override public void onDestroyView()
    {
        detachSubscription(securityCompactCacheSubscription);
        securityCompactCacheSubscription = null;
        if (mHelpVideoLink != null)
        {
            mHelpVideoLink.setOnClickListener(null);
        }
        mHelpVideoLink = null;

        super.onDestroyView();
    }

    @Override protected SecurityCompactCacheRx getInfoCache()
    {
        return securityCompactCache;
    }

    public void linkWith(ProviderId providerId, boolean andDisplay)
    {
        this.providerId = providerId;
        if (this.providerId != null)
        {
            linkWith(providerCache.getValue(providerId), andDisplay);
        }
        else
        {
            linkWith((ProviderDTO) null, andDisplay);
        }
    }

    public void linkWith(ProviderDTO providerDTO, boolean andDisplay)
    {
        this.providerDTO = providerDTO;
        if (andDisplay)
        {
            displayLinkHelpVideoLink();
            displayLinkHelpVideoText();
        }
    }

    @Override public void linkWith(SecurityId securityId, final boolean andDisplay)
    {
        super.linkWith(securityId, andDisplay);
        if (this.securityId != null)
        {
            detachSubscription(securityCompactCacheSubscription);
            securityCompactCacheSubscription = securityCompactCache.get(securityId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Pair<SecurityId, SecurityCompactDTO>>()
                    {
                        @Override public void onCompleted()
                        {
                        }

                        @Override public void onError(Throwable e)
                        {
                            THToast.show(R.string.error_fetch_security_info);
                        }

                        @Override public void onNext(Pair<SecurityId, SecurityCompactDTO> pair)
                        {
                            linkWith(pair.second, andDisplay);
                        }
                    });
        }
    }

    @Override public void linkWith(SecurityCompactDTO value, boolean andDisplay)
    {
        warrantDTO = (WarrantDTO) value;
        super.linkWith(value, andDisplay);
    }

    //<editor-fold desc="Display Methods">
    public void display()
    {
        displayLinkHelpVideoLink();
        displayLinkHelpVideoText();
        displayWarrantType();
        displayWarrantCode();
        displayExpiry();
        displayStrikePrice();
        displayUnderlying();
        displayIssuer();
    }

    public void displayLinkHelpVideoLink()
    {
        if (!isDetached() && mHelpVideoLink != null)
        {
            mHelpVideoLink.setVisibility(hasHelpVideo() ? View.VISIBLE : View.GONE);
            if (providerDTO != null
                    && providerDTO.specificResources != null
                    && providerDTO.specificResources.helpVideoLinkBackgroundResId > 0)
            {
                mHelpVideoLink.setBackgroundResource(providerDTO.specificResources.helpVideoLinkBackgroundResId);
            }
        }
    }

    public void displayLinkHelpVideoText()
    {
        if (!isDetached() && mHelpVideoLink != null)
        {
            if (providerDTO != null)
            {
                mHelpVideoLink.setText(providerDTO.helpVideoText);
            }
            if (providerDTO != null
                    && providerDTO.specificResources != null
                    && providerDTO.specificResources.helpVideoLinkTextColourResId > 0)
            {
                mHelpVideoLink.setTextColor(getResources().getColor(
                        providerDTO.specificResources.helpVideoLinkTextColourResId));
           }
            else
            {
                mHelpVideoLink.setTextColor(getResources().getColor(R.color.black));
            }
        }
    }

    public boolean hasHelpVideo()
    {
        return providerDTO != null && providerDTO.hasHelpVideo;
    }

    public void displayWarrantType()
    {
        if (!isDetached() && mWarrantType != null)
        {
            if (warrantDTO == null || warrantDTO.warrantType == null)
            {
                mWarrantType.setText(R.string.na);
            }
            else
            {
                int warrantTypeStringResId;
                switch(warrantDTO.getWarrantType())
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
        }
    }

    public void displayWarrantCode()
    {
        if (!isDetached() && mWarrantCode != null)
        {
            if (value == null || value.symbol == null)
            {
                mWarrantCode.setText(R.string.na);
            }
            else
            {
                mWarrantCode.setText(value.symbol);
            }
        }
    }

    public void displayExpiry()
    {
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
    }

    public void displayStrikePrice()
    {
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
    }

    public void displayUnderlying()
    {
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
    }

    public void displayIssuer()
    {
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
    //</editor-fold>

    @SuppressWarnings("UnusedParameters")
    private void handleVideoLinkClicked(View view)
    {
        if (navigator != null)
        {
            Bundle args = new Bundle();
            ProviderVideoListFragment.putProviderId(args, providerId);
            navigator.get().pushFragment(ProviderVideoListFragment.class, args);
        }
    }
}
