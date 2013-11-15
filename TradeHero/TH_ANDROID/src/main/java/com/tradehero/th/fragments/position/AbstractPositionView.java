package com.tradehero.th.fragments.position;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.tradehero.common.widget.ColorIndicator;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.position.OwnedLeaderboardPositionId;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.persistence.leaderboard.position.LeaderboardPositionCache;
import com.tradehero.th.persistence.position.PositionCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import dagger.Lazy;

import javax.inject.Inject;
import java.lang.ref.WeakReference;

/**
 * Created by julien on 30/10/13
 */
public abstract class AbstractPositionView extends LinearLayout
{
    protected OwnedPositionId ownedPositionId;
    protected PositionPartialTopView topView;
    protected ColorIndicator colorIndicator;

    protected ImageButton btnBuy;
    protected ImageButton btnSell;
    protected ImageButton btnAddAlert;
    protected ImageButton btnStockInfo;

    @Inject Lazy<PositionCache> positionCache;
    @Inject Lazy<LeaderboardPositionCache> leaderboardPositionCache;

    protected PositionDTO positionDTO;

    protected WeakReference<PositionListener> listener = new WeakReference<>(null);

    //<editor-fold desc="Constructors">
    public AbstractPositionView(Context context)
    {
        super(context);
    }

    public AbstractPositionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AbstractPositionView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        initViews();
    }

    protected void initViews()
    {
        topView = (PositionPartialTopView) findViewById(R.id.position_partial_top);
        colorIndicator = (ColorIndicator) findViewById(R.id.color_indicator);
        initShortcutButtons();
    }

    public void onDestroyView()
    {
        if (btnBuy != null)
        {
            btnBuy.setOnClickListener(null);
        }
        btnBuy = null;
        if (btnSell != null)
        {
            btnSell.setOnClickListener(null);
        }
        btnSell = null;
        if (btnAddAlert != null)
        {
            btnAddAlert.setOnClickListener(null);
        }
        btnAddAlert = null;
        if (btnStockInfo != null)
        {
            btnStockInfo.setOnClickListener(null);
        }
        btnStockInfo = null;
    }

    public void linkWith(OwnedPositionId ownedPositionId, boolean andDisplay)
    {
        this.ownedPositionId = ownedPositionId;
        this.topView.linkWith(ownedPositionId, andDisplay);
        this.linkWith(positionCache.get().get(ownedPositionId), andDisplay);
    }

    public void linkWith(OwnedLeaderboardPositionId ownedLeaderboardPositionId, boolean andDisplay)
    {
        this.topView.linkWith(ownedLeaderboardPositionId, andDisplay);
        this.linkWith(leaderboardPositionCache.get().get(ownedLeaderboardPositionId), andDisplay);
    }

    protected void linkWith(PositionDTO positionDTO, boolean andDisplay)
    {
        this.positionDTO = positionDTO;
        if (andDisplay)
        {
            display();
        }
    }

    protected void display()
    {
        displayColorIndicator();
    }

    protected void displayColorIndicator()
    {
        if (colorIndicator != null && positionDTO != null)
        {
            Double roi = positionDTO.getROISinceInception();
            colorIndicator.linkWith(roi);
        }
    }

    public void initShortcutButtons()
    {
        btnBuy = (ImageButton) findViewById(R.id.btn_buy_now);
        if (btnBuy != null)
        {
            btnBuy.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    PositionListener l = listener.get();
                    if (l != null)
                    {
                        l.onBuyClicked(ownedPositionId);
                    }
                }
            });
        }

        btnSell = (ImageButton) findViewById(R.id.btn_sell_now);
        if (btnSell != null)
        {
            btnSell.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    PositionListener l = listener.get();
                    if (l != null)
                    {
                        l.onSellClicked(ownedPositionId);
                    }
                }
            });
        }

        btnAddAlert = (ImageButton) findViewById(R.id.btn_add_alert);
        if (btnAddAlert != null)
        {
            btnAddAlert.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    PositionListener l = listener.get();
                    if (l != null)
                    {
                        l.onAddAlertClicked(ownedPositionId);
                    }
                }
            });
        }

        btnStockInfo = (ImageButton) findViewById(R.id.btn_stock_info);
        if (btnStockInfo != null)
        {
            btnStockInfo.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    PositionListener l = listener.get();
                    if (l != null)
                    {
                        l.onStockInfoClicked(ownedPositionId);
                    }
                }
            });
        }

        ImageButton historyButton = topView.getTradeHistoryButton();
        if (historyButton != null)
        {
            historyButton.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    PositionListener l = listener.get();
                    if (l != null)
                    {
                        l.onTradeHistoryClicked(ownedPositionId);
                    }
                }
            });
        }
    }

    public PositionListener getListener()
    {
        return listener.get();
    }

    public void setListener(PositionListener listener)
    {
        this.listener = new WeakReference<>(listener);
    }
}
