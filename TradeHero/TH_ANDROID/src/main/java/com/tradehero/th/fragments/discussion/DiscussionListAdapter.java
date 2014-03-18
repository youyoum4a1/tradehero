package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.discussion.DiscussionDTO;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/11/14 Time: 6:38 PM Copyright (c) TradeHero
 */
public class DiscussionListAdapter extends LoaderDTOAdapter<DiscussionDTO, DiscussionView, DiscussionListLoader>
{
    public DiscussionListAdapter(Context context, LayoutInflater inflater, int loaderId, int layoutResourceId)
    {
        super(context, inflater, loaderId, layoutResourceId);
    }

    @Override protected void fineTune(int position, DiscussionDTO dto, DiscussionView dtoView)
    {

    }


}
