package com.ayondo.academy.fragments.security;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.Bind;
import com.ayondo.academy.R;
import com.ayondo.academy.api.security.SecurityCompactDTO;
import com.ayondo.academy.api.security.compact.WarrantDTO;
import com.ayondo.academy.models.number.THSignedMoney;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class WarrantSecurityItemView extends SecurityItemView
{
    @Bind(R.id.combined_strike_price_type) TextView combinedStrikePriceType;
    @Bind(R.id.warrant_type) TextView warrantType;
    @Bind(R.id.expiry_date) TextView expiryDate;

    //<editor-fold desc="Constructors">
    public WarrantSecurityItemView(Context context)
    {
        super(context);
    }

    public WarrantSecurityItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public WarrantSecurityItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        if (combinedStrikePriceType != null)
        {
            combinedStrikePriceType.setSelected(true);
        }
    }

    @Override public void display(SecurityCompactDTO securityCompactDTO)
    {
        super.display(securityCompactDTO);

        displayCombinedStrikePriceType();
        displayWarrantType();
        displayExpiryDate();
    }

    @Override public void display()
    {
        super.display();
        displayCombinedStrikePriceType();
        displayWarrantType();
        displayExpiryDate();
    }

    @Override public void displayExchangeSymbol()
    {
        if (exchangeSymbol != null)
        {
            if (securityCompactDTO != null)
            {
                exchangeSymbol.setText(securityCompactDTO.symbol);
            }
            else
            {
                exchangeSymbol.setText(R.string.na);
            }
            exchangeSymbol.setTextColor(getResources().getColor(R.color.text_primary));
        }
    }

    public void displayCombinedStrikePriceType()
    {
        if (combinedStrikePriceType != null)
        {
            if (securityCompactDTO instanceof WarrantDTO)
            {
                WarrantDTO warrantDTO = (WarrantDTO) securityCompactDTO;
                if (warrantDTO.strikePrice != null)
                {
                    THSignedMoney.builder(warrantDTO.strikePrice)
                            .currency(warrantDTO.strikePriceCcy)
                            .build()
                            .into(combinedStrikePriceType);
                }
                else
                {
                    combinedStrikePriceType.setText(R.string.na);
                }
            }
            else
            {
                combinedStrikePriceType.setText(R.string.na);
            }
        }
    }

    public void displayWarrantType()
    {
        if (warrantType != null)
        {
            if (securityCompactDTO instanceof WarrantDTO)
            {
                warrantType.setText(((WarrantDTO) securityCompactDTO).getWarrantType().stringResId);
            }
            else
            {
                warrantType.setText(R.string.na);
            }
        }
    }

    public void displayExpiryDate()
    {
        if (expiryDate != null)
        {
            if (securityCompactDTO instanceof WarrantDTO && ((WarrantDTO) securityCompactDTO).expiryDate != null)
            {
                SimpleDateFormat df = new SimpleDateFormat("d MMM yy", Locale.US);
                expiryDate.setText(df.format(((WarrantDTO) securityCompactDTO).expiryDate));
            }
            else
            {
                expiryDate.setText(R.string.na);
            }
        }
    }
}
