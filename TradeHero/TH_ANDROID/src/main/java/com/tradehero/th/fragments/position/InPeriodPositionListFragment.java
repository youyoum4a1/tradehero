package com.tradehero.th.fragments.position;

import com.tradehero.th.R;
import com.tradehero.th.adapters.position.PositionItemAdapter;

/** Created with IntelliJ IDEA. User: tho Date: 11/6/13 Time: 12:57 PM Copyright (c) TradeHero */
public class InPeriodPositionListFragment extends PositionListFragment
{
    @Override protected void createPositionItemAdapter()
    {
        positionItemAdapter = new PositionItemAdapter(
                getActivity(),
                getActivity().getLayoutInflater(),
                R.layout.position_item_header,
                R.layout.position_locked_item,
                R.layout.position_open_in_period,
                R.layout.position_closed_in_period,
                R.layout.position_quick_nothing);
    }

    @Override protected void fetchSimplePage()
    {
        // TODO :v
    }
}
