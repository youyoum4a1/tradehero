package com.tradehero.th.models.intent.position;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.PortfolioId;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.models.intent.portfolio.PortfolioIntent;
import java.util.List;

abstract public class OnePortfolioIntent extends PortfolioIntent
{
    //<editor-fold desc="Constructors">
    protected OnePortfolioIntent(PortfolioId portfolioId)
    {
        super();
        setData(getPortfolioActionUri(portfolioId));
    }
    //</editor-fold>

    public Uri getPortfolioActionUri(PortfolioId portfolioId)
    {
        return Uri.parse(getPortfolioActionUriPath(portfolioId));
    }

    public String getPortfolioActionUriPath(PortfolioId portfolioId)
    {
        return getString(
                getIntentActionUriResId(),
                getString(R.string.intent_scheme),
                getString(R.string.intent_host_portfolio),
                getString(getIntentActionResId()),
                portfolioId.key);
    }

    public int getIntentActionUriResId()
    {
        return R.string.intent_uri_action_one_portfolio;
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
        return new PortfolioId(Integer.parseInt(pathSegments.get(getInteger(R.integer.intent_uri_action_portfolio_path_index_id))));
    }

    @Override public Class<? extends Fragment> getActionFragment()
    {
        return PositionListFragment.class;
    }

    @Override public void populate(Bundle bundle)
    {
        super.populate(bundle);
        // TODO need to be able to pass an OwnedPortfolioId
        //PositionListFragment.putGetPositionsDTOKey();
        //bundle.putInt(PositionListFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE, getPortfolioId().key);
    }
}
