package com.tradehero.th.fragments.security;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.persistence.LiveDTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.WarrantDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.competition.ProviderVideoListFragment;
import com.tradehero.th.models.provider.ProviderSpecificResourcesDTO;
import com.tradehero.th.models.provider.ProviderSpecificResourcesFactory;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.NumberDisplayUtils;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.inject.Inject;

public class WarrantInfoValueFragment extends AbstractSecurityInfoFragment<SecurityCompactDTO>
{
    public final static String BUNDLE_KEY_PROVIDER_ID_KEY = WarrantInfoValueFragment.class.getName() + ".providerId";

    @InjectView(R.id.warrant_help_video_link) protected View mHelpVideoLink;
    @InjectView(R.id.warrant_help_video_text) protected TextView mHelpVideoText;
    @InjectView(R.id.vwarrant_type) protected TextView mWarrantType;
    @InjectView(R.id.vwarrant_code) protected TextView mWarrantCode;
    @InjectView(R.id.vwarrant_expiry) protected TextView mWarrantExpiry;
    @InjectView(R.id.vwarrant_strike_price) protected TextView mStrikePrice;
    @InjectView(R.id.vwarrant_underlying) protected TextView mUnderlying;
    @InjectView(R.id.vwarrant_issuer) protected TextView mIssuer;

    @Inject protected SecurityCompactCache securityCompactCache;
    protected WarrantDTO warrantDTO;
    protected ProviderId providerId;
    protected ProviderDTO providerDTO;
    protected ProviderSpecificResourcesDTO providerSpecificResourcesDTO;
    @Inject protected ProviderCache providerCache;
    @Inject protected ProviderSpecificResourcesFactory providerSpecificResourcesFactory;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
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
            mHelpVideoLink.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handleVideoLinkClicked();
                }
            });
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
        if (mHelpVideoLink != null)
        {
            mHelpVideoLink.setOnClickListener(null);
        }
        mHelpVideoLink = null;

        super.onDestroyView();
    }

    @Override protected LiveDTOCache<SecurityId, SecurityCompactDTO> getInfoCache()
    {
        return securityCompactCache;
    }

    public void linkWith(ProviderId providerId, boolean andDisplay)
    {
        this.providerId = providerId;
        if (this.providerId != null)
        {
            linkWith(providerCache.get(providerId), andDisplay);
        }
        else
        {
            linkWith((ProviderDTO) null, andDisplay);
        }
        if (andDisplay)
        {
        }
    }

    public void linkWith(ProviderDTO providerDTO, boolean andDisplay)
    {
        this.providerDTO = providerDTO;
        this.providerSpecificResourcesDTO = providerSpecificResourcesFactory.createResourcesDTO(providerDTO);
        if (andDisplay)
        {
            displayLinkHelpVideoLink();
            displayLinkHelpVideoText();
        }
    }

    @Override public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        super.linkWith(securityId, andDisplay);
        if (this.securityId != null)
        {
            linkWith(securityCompactCache.get(this.securityId), andDisplay);
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
            if (providerSpecificResourcesDTO != null && providerSpecificResourcesDTO.helpVideoLinkBackgroundResId > 0)
            {
                mHelpVideoLink.setBackgroundResource(providerSpecificResourcesDTO.helpVideoLinkBackgroundResId);
            }
        }
    }

    public void displayLinkHelpVideoText()
    {
        if (!isDetached() && mHelpVideoText != null)
        {
            if (providerDTO != null)
            {
                mHelpVideoText.setText(providerDTO.helpVideoText);
            }
            if (providerSpecificResourcesDTO != null && providerSpecificResourcesDTO.helpVideoLinkTextColourResId > 0)
            {
                mHelpVideoText.setTextColor(getResources().getColor(
                        providerSpecificResourcesDTO.helpVideoLinkTextColourResId));
           }
            else
            {
                mHelpVideoText.setTextColor(getResources().getColor(R.color.black));
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
                mStrikePrice.setText(getString(
                        R.string.warrant_info_strike_price_value_display,
                        warrantDTO.strikePriceCcy,
                        NumberDisplayUtils.formatWithRelevantDigits(warrantDTO.strikePrice, 4)));
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

    private void handleVideoLinkClicked()
    {
        Activity activity = getActivity();
        if (activity instanceof DashboardNavigatorActivity)
        {
            Bundle args = new Bundle();
            ProviderVideoListFragment.putProviderId(args, providerId);
            ((DashboardNavigatorActivity) activity).getDashboardNavigator().pushFragment(ProviderVideoListFragment.class, args);
        }
    }
}
