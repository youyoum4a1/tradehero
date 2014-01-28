package com.tradehero.th.fragments.security;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.WarrantDTO;
import com.tradehero.th.models.security.WarrantDTOFormatter;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.inject.Inject;

/**
 * Created by xavier on 1/21/14.
 */
public class WarrantSecurityItemView extends SecurityItemView<SecurityCompactDTO>
{
    public static final String TAG = WarrantSecurityItemView.class.getSimpleName();

    private TextView combinedStrikePriceType;
    private TextView strikePrice;
    private TextView strikePriceCcy;
    private TextView warrantType;
    private TextView expiryDate;

    @Inject protected WarrantDTOFormatter warrantDTOFormatter;

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

    @Override protected void fetchViews()
    {
        super.fetchViews();

        combinedStrikePriceType = (TextView) findViewById(R.id.combined_strike_price_type);
        if (combinedStrikePriceType != null)
        {
            combinedStrikePriceType.setSelected(true);
        }
        strikePrice = (TextView) findViewById(R.id.strike_price);
        strikePriceCcy = (TextView) findViewById(R.id.strike_currency_display);
        warrantType = (TextView) findViewById(R.id.warrant_type);
        expiryDate = (TextView) findViewById(R.id.expiry_date);
    }

    @Override public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        super.linkWith(securityCompactDTO, andDisplay);

        if (andDisplay)
        {
            displayCombinedStrikePriceType();
            displayStrikePrice();
            displayStrikePriceCcy();
            displayWarrantType();
            displayExpiryDate();
        }
    }

    @Override public void display()
    {
        super.display();

        displayCombinedStrikePriceType();
        displayStrikePrice();
        displayStrikePriceCcy();
        displayWarrantType();
        displayExpiryDate();
    }

    public void displayCombinedStrikePriceType()
    {
        if (combinedStrikePriceType != null)
        {
            if (securityCompactDTO instanceof WarrantDTO)
            {
                combinedStrikePriceType.setText(warrantDTOFormatter.getCombinedStrikePriceType(getContext(), (WarrantDTO) securityCompactDTO));
            }
            else
            {
                combinedStrikePriceType.setText(R.string.na);
            }
        }
    }

    public void displayStrikePrice()
    {
        if (strikePrice != null)
        {
            if (securityCompactDTO instanceof WarrantDTO)
            {
                strikePrice.setText(String.format("%.2f", ((WarrantDTO) securityCompactDTO).strikePrice));
            }
            else
            {
                strikePrice.setText(R.string.na);
            }
        }
    }

    public void displayStrikePriceCcy()
    {
        if (strikePriceCcy != null)
        {
            if (securityCompactDTO instanceof WarrantDTO)
            {
                strikePriceCcy.setText(((WarrantDTO) securityCompactDTO).strikePriceCcy);
            }
            else
            {
                strikePriceCcy.setText(R.string.na);
            }
        }
    }

    public void displayWarrantType()
    {
        if (warrantType != null)
        {
            if (securityCompactDTO instanceof WarrantDTO)
            {
                warrantType.setText(String.format("(%s)", ((WarrantDTO) securityCompactDTO).warrantType));
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
