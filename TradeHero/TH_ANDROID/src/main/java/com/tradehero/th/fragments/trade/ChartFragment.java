package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.actionbarsherlock.app.SherlockFragment;
import com.squareup.picasso.Picasso;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.yahoo.*;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created by julien on 9/10/13
 */
public class ChartFragment extends SherlockFragment
    implements DTOCache.Listener<SecurityId, SecurityCompactDTO>
{
    private final static String TAG = ChartFragment.class.getSimpleName();

    private ImageView stockBgLogo;

    private SecurityId securityId;
    private SecurityCompactDTO securityCompactDTO;
    @Inject protected Lazy<SecurityCompactCache> securityCompactCache;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        stockBgLogo = (ImageView)view.findViewById(R.id.chart_imageView);
        return view;
    }

    @Override public void onResume()
    {
        super.onResume();
        Bundle args = getArguments();
        if (args != null)
        {
            linkWith(new SecurityId(args), true);
        }
        else
        {
            display();
        }
    }

    @Override public void onPause()
    {
        if (securityId != null)
        {
            securityCompactCache.get().unRegisterListener(this);
        }
        super.onPause();
    }

    public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId = securityId;
        if (this.securityId != null)
        {
            securityCompactCache.get().registerListener(this);
            linkWith(securityCompactCache.get().get(this.securityId), andDisplay);
        }
    }

    @Override public void onDTOReceived(SecurityId key, SecurityCompactDTO value)
    {
        if (key.equals(securityId))
        {
            linkWith(value, true);
        }
    }

    public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        this.securityCompactDTO = securityCompactDTO;
        if (andDisplay)
        {
            display();
        }
    }

    public void display()
    {
        displayBgLogo();
    }

    public void displayBgLogo()
    {
        if (stockBgLogo != null)
        {
            if (securityCompactDTO != null && securityCompactDTO.yahooSymbol != null)
            {
                String imageURL = Utils.getChartURL(securityCompactDTO.yahooSymbol, ChartSize.large, Timespan.months3);
                Picasso.with(getActivity()).load(imageURL).into(stockBgLogo);
            }
        }
    }
}



