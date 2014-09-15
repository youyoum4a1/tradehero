package com.tradehero.th.fragments.chinabuild.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import butterknife.ButterKnife;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th2.R;
import com.tradehero.th.adapters.SecurityListAdapter;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.security.key.TrendingAllSecurityListType;
import com.tradehero.th.fragments.base.DashboardFragment;
import timber.log.Timber;

public class TradeOfHotHoldFragment extends TradeOfTypeBaseFragment
{

    public int getTradeType()
    {
        return TrendingAllSecurityListType.ALL_SECURITY_LIST_TYPE_HOLD;
    }

}
