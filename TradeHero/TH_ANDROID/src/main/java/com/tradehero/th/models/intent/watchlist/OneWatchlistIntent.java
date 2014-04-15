package com.tradehero.th.models.intent.watchlist;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.fragments.watchlist.WatchlistPositionFragment;
import com.tradehero.th.models.intent.portfolio.PortfolioIntent;
import java.util.List;

/**
 * Created by xavier on 1/10/14.
 */
abstract public class OneWatchlistIntent extends PortfolioIntent
{
    public static final String TAG = OneWatchlistIntent.class.getSimpleName();

    //<editor-fold desc="Constructors">
    protected OneWatchlistIntent(PortfolioId portfolioId)
    {
        super();
        setData(getWatchlistActionUri(portfolioId));
    }
    //</editor-fold>

    public Uri getWatchlistActionUri(PortfolioId portfolioId)
    {
        return Uri.parse(getWatchlistActionUriPath(portfolioId));
    }

    public String getWatchlistActionUriPath(PortfolioId portfolioId)
    {
        return getString(
                getIntentActionUriResId(),
                getString(R.string.intent_scheme),
                getString(R.string.intent_host_portfolio),
                getString(R.string.intent_action_watchlist),
                getString(getIntentActionResId()),
                portfolioId.key);
    }


    public int getIntentActionUriResId()
    {
        return R.string.intent_uri_action_one_watchlist;
    }

    abstract int getIntentActionResId();

    public PortfolioId getPortfolioId()
    {
        return getPortfolioId(getData());
    }

    public static PortfolioId getPortfolioId(Uri data)
    {
        return getPortfolioId(data.getPathSegments());
    }

    public static PortfolioId getPortfolioId(List<String> pathSegments)
    {
        return new PortfolioId(Integer.parseInt(pathSegments.get(getInteger(R.integer.intent_uri_action_watchlist_path_index_id))));
    }

    @Override public Class<? extends Fragment> getActionFragment()
    {
        return WatchlistPositionFragment.class;
    }

    @Override public void populate(Bundle bundle)
    {
        super.populate(bundle);
        // TODO have implementation
        //bundle.putInt(PositionWatchlistFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE, getPortfolioIdKey().key);
    }
}
