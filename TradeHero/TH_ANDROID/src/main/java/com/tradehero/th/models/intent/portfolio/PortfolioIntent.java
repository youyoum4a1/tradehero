package com.tradehero.th.models.intent.portfolio;

import android.net.Uri;
import com.tradehero.th.R;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import com.tradehero.th.models.intent.THIntent;

/**
 * Created by xavier on 1/10/14.
 */
public class PortfolioIntent extends THIntent
{
    public static final String TAG = PortfolioIntent.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public PortfolioIntent()
    {
        super();
    }

    public PortfolioIntent(Uri uri)
    {
        super(uri);
    }
    //</editor-fold>

    @Override public String getUriPath()
    {
        return getHostUriPath(R.string.intent_host_portfolio);
    }

    @Override public DashboardTabType getDashboardType()
    {
        return DashboardTabType.PORTFOLIO;
    }
}
