package com.tradehero.th.widget.trade;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.squareup.picasso.Picasso;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.trade.OwnedTradeId;
import com.tradehero.th.api.trade.TradeDTO;
import com.tradehero.th.persistence.trade.TradeCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;

import javax.inject.Inject;

/**
 * Created by julien on 23/10/13
 */
public class TradeListItemView extends LinearLayout implements DTOView<ExpandableListItem<OwnedTradeId>>
{
    public static final String TAG = TradeListItemView.class.getName();


    private OwnedTradeId ownedTradeId;
    private TradeDTO trade;

    @Inject Lazy<TradeCache> tradeCache;
    @Inject Lazy<Picasso> picasso;

    public TradeListItemView(Context context)
    {
        super(context);
    }

    public TradeListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public TradeListItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }


    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        initViews();
        DaggerUtils.inject(this);
    }

    private void initViews()
    {
    }

    @Override public void display(ExpandableListItem<OwnedTradeId> expandableItem)
    {
        linkWith(expandableItem.getModel(), true);
    }

    public void linkWith(OwnedTradeId ownedTradeId, boolean andDisplay)
    {
        this.ownedTradeId = ownedTradeId;
        if (this.ownedTradeId != null)
        {
            linkWith(tradeCache.get().get(ownedTradeId.getTradeId()), false);
        }

        if (andDisplay)
        {
            display();
        }
    }

    public void linkWith(TradeDTO trade, boolean andDisplay)
    {
        this.trade = trade;
        if (andDisplay)
        {
            display();
        }

    }

    public void display()
    {

    }


}
