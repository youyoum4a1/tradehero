package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.actionbarsherlock.app.SherlockFragment;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.utils.yahoo.*;
import javax.inject.Inject;

/**
 * Created by julien on 9/10/13
 */
public class ChartFragment extends SherlockFragment implements DTOView<SecurityCompactDTO>
{
    private final static String TAG = ChartFragment.class.getSimpleName();
    public final static String BUNDLE_KEY_YAHOO_SYMBOL = ChartFragment.class.getName() + ".yahooSymbol";

    private ImageView stockBgLogo;
    private String yahooSymbol;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        stockBgLogo = (ImageView)view.findViewById(R.id.chart_imageView);

        if (savedInstanceState != null)
        {
            yahooSymbol = savedInstanceState.getString(BUNDLE_KEY_YAHOO_SYMBOL, null);
        }

        return view;
    }

    @Override public void onResume()
    {
        super.onResume();
        loadImage();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        if (yahooSymbol != null)
        {
            outState.putString(BUNDLE_KEY_YAHOO_SYMBOL, yahooSymbol);
        }
    }

    @Override
    public void display(SecurityCompactDTO dto)
    {
        if (dto.yahooSymbol != null)
        {
            yahooSymbol = dto.yahooSymbol;
            loadImage();
        }
    }

    private void loadImage()
    {
        if (yahooSymbol != null && stockBgLogo != null)
        {
            String imageURL = Utils.getChartURL(yahooSymbol, ChartSize.large, Timespan.months3);
            Picasso.with(getActivity()).load(imageURL).into(stockBgLogo);
        }
    }
}



