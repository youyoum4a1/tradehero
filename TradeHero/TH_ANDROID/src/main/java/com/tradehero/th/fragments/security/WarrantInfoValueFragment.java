package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.WarrantDTO;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.NumberDisplayUtils;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.inject.Inject;

public class WarrantInfoValueFragment extends AbstractSecurityInfoFragment<SecurityCompactDTO>
{
    private final static String TAG = WarrantInfoValueFragment.class.getSimpleName();

    private TextView mWarrantType;
    private TextView mWarrantCode;
    private TextView mWarrantExpiry;
    private TextView mStrikePrice;
    private TextView mUnderlying;
    private TextView mIssuer;

    @Inject protected SecurityCompactCache securityCompactCache;
    protected WarrantDTO warrantDTO;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = null;
        view = inflater.inflate(R.layout.fragment_warrantinfo_value, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View v)
    {
        mWarrantType = (TextView) v.findViewById(R.id.vwarrant_type);
        mWarrantCode = (TextView) v.findViewById(R.id.vwarrant_code);
        mWarrantExpiry = (TextView) v.findViewById(R.id.vwarrant_expiry);
        mStrikePrice = (TextView) v.findViewById(R.id.vwarrant_strike_price);
        mUnderlying = (TextView) v.findViewById(R.id.vwarrant_underlying);
        mIssuer = (TextView) v.findViewById(R.id.vwarrant_issuer);
    }

    @Override public void onPause()
    {
        if (securityId != null)
        {
            securityCompactCache.unRegisterListener(this);
        }
        super.onPause();
    }

    @Override public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        super.linkWith(securityId, andDisplay);
        if (this.securityId != null)
        {
            securityCompactCache.registerListener(this);
            linkWith((WarrantDTO) securityCompactCache.get(this.securityId), andDisplay);
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
        displayWarrantType();
        displayWarrantCode();
        displayExpiry();
        displayStrikePrice();
        displayUnderlying();
        displayIssuer();
    }

    public void displayWarrantType()
    {
        if (mWarrantType != null)
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
        if (mWarrantCode != null)
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
        if (mWarrantExpiry != null)
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
        if (mStrikePrice != null)
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
        if (mUnderlying != null)
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
        if (mIssuer != null)
        {
            if (warrantDTO == null || warrantDTO.issuerName == null)
            {
                mIssuer.setText(R.string.na);
            }
            else
            {
                mIssuer.setText(warrantDTO.issuerName);
            }
        }
    }
    //</editor-fold>
}
