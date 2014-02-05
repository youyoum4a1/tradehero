package com.tradehero.th.fragments.position;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.widget.ColorIndicator;
import com.tradehero.th.R;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import com.tradehero.th.utils.DaggerUtils;

/**
 * Created by julien on 30/10/13
 */
public abstract class AbstractPositionView<PositionDTOType extends PositionDTO> extends LinearLayout
{
    public static final String TAG = AbstractPositionView.class.getSimpleName();

    protected PositionPartialTopView topView;
    protected ColorIndicator colorIndicator;

    protected View btnBuy;
    protected View btnSell;
    protected View btnAddAlert;
    protected View btnStockInfo;
    protected View historyButton;

    protected boolean hasHistoryButton = true;
    protected PositionDTOType positionDTO;

    protected PositionListener listener = null;

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
        btnBuy = findViewById(R.id.btn_buy_now);
        btnSell = findViewById(R.id.btn_sell_now);
        btnAddAlert = findViewById(R.id.btn_add_alert);
        btnStockInfo = findViewById(R.id.btn_stock_info);
        if (topView != null)
        {
            historyButton = topView.getTradeHistoryButton();
        }
    }

    @Override protected void onAttachedToWindow()
    {
        if (btnBuy != null)
        {
            btnBuy.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    PositionListener listenerCopy = listener;
                    if (listenerCopy != null)
                    {
                        listenerCopy.onBuyClicked(getOwnedPositionId());
                    }
                }
            });
        }

        if (btnSell != null)
        {
            btnSell.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    PositionListener listenerCopy = listener;
                    if (listenerCopy != null)
                    {
                        listenerCopy.onSellClicked(getOwnedPositionId());
                    }
                }
            });
        }

        if (btnAddAlert != null)
        {
            btnAddAlert.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    PositionListener listenerCopy = listener;
                    if (listenerCopy != null)
                    {
                        listenerCopy.onAddAlertClicked(getOwnedPositionId());
                    }
                }
            });
        }

        if (btnStockInfo != null)
        {
            btnStockInfo.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    PositionListener listenerCopy = listener;
                    if (listenerCopy != null)
                    {
                        listenerCopy.onStockInfoClicked(getOwnedPositionId());
                    }
                }
            });
        }

        if (historyButton != null)
        {
            historyButton.setOnClickListener(new OnClickListener()
            {
                @Override public void onClick(View v)
                {
                    PositionListener listenerCopy = listener;
                    if (listenerCopy != null)
                    {
                        listenerCopy.onTradeHistoryClicked(getOwnedPositionId());
                    }
                }
            });
        }
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow()
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

        if (historyButton != null)
        {
            historyButton.setOnClickListener(null);
        }
        historyButton = null;

        this.listener = null;

        super.onDetachedFromWindow();
    }

    public void linkWithHasHistoryButton(boolean hasHistoryButton, boolean andDisplay)
    {
        this.hasHistoryButton = hasHistoryButton;
        if (andDisplay)
        {
            displayHistoryButton();
        }
    }

    public void linkWith(PositionDTOType positionDTO, boolean andDisplay)
    {
        this.positionDTO = positionDTO;

        if (topView != null)
        {
            topView.linkWith(positionDTO, andDisplay);
        }

        if (andDisplay)
        {
            displayColorIndicator();
            displayButtonSell();
        }
    }

    public OwnedPositionId getOwnedPositionId()
    {
        if (positionDTO == null)
        {
            return null;
        }
        return positionDTO.getOwnedPositionId();
    }

    protected void display()
    {
        displayTopView();
        displayColorIndicator();
        displayButtonSell();
        displayHistoryButton();
    }

    public void displayTopView()
    {
        if (topView != null)
        {
            topView.display();
        }
    }

    protected void displayColorIndicator()
    {
        if (colorIndicator != null && positionDTO != null)
        {
            Double roi = positionDTO.getROISinceInception();
            colorIndicator.linkWith(roi);
        }
    }

    protected void displayButtonSell()
    {
        if (btnSell != null)
        {
            btnSell.setVisibility(this.positionDTO == null || this.positionDTO.isClosed() ? GONE : VISIBLE);
        }
    }

    protected void displayHistoryButton()
    {
        if (historyButton != null)
        {
            historyButton.setVisibility(hasHistoryButton ? VISIBLE : GONE);
        }
    }

    public PositionListener getListener()
    {
        return listener;
    }

    public void setListener(PositionListener listener)
    {
        THLog.d(TAG, "Setting listener on " + this);
        this.listener = listener;
    }
}
