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
public class ChartFragment  extends SherlockFragment implements DTOView<SecurityCompactDTO>
{
    private final static String TAG = ChartFragment.class.getSimpleName();

    private ImageView stockBgLogo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        stockBgLogo = (ImageView)view.findViewById(R.id.chart_imageView);
        return view;
    }

    @Override
    public void display(SecurityCompactDTO dto)
    {
        if (dto.yahooSymbol != null)
        {
            String imageURL = Utils.getChartURL(dto.yahooSymbol, ChartSize.large, Timespan.months3);
            Picasso.with(getActivity()).load(imageURL).into(stockBgLogo);
        }
    }
}



