package com.tradehero.th.fragments.position.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.widget.ColorIndicator;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.fragments.position.PositionListener;
import com.tradehero.th.fragments.position.partial.AbstractPartialBottomView;
import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import timber.log.Timber;

public class PositionView extends LinearLayout
{
    @InjectView(R.id.position_partial_top) protected PositionPartialTopView topView;
    @InjectView(R.id.expanding_layout) protected AbstractPartialBottomView/*<PositionDTO, ExpandableListItem<PositionDTO>>*/ bottomView;

    @InjectView(R.id.color_indicator) protected ColorIndicator colorIndicator;
    @InjectView(R.id.btn_buy_now) protected View btnBuy;
    @InjectView(R.id.btn_sell_now) protected View btnSell;
    @InjectView(R.id.btn_add_alert) protected View btnAddAlert;
    @InjectView(R.id.btn_stock_info) protected View btnStockInfo;
    @InjectView(R.id.btn_trade_history) protected View historyButton;

    protected boolean hasHistoryButton = true;
    protected ExpandableListItem<PositionDTO> expandableListItem;
    protected PositionDTO positionDTO;

    protected PositionListener<PositionDTO> listener = null;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public PositionView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public PositionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public PositionView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        this.listener = null;
        super.onDetachedFromWindow();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.btn_buy_now)
    protected void handleBuyClicked(View v)
    {
        notifyBuyClicked();
    }

    protected void notifyBuyClicked()
    {
        PositionListener<PositionDTO> listenerCopy = listener;
        if (listenerCopy != null)
        {
            listenerCopy.onBuyClicked(getPositionDTO());
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.btn_sell_now)
    protected void handleSellClicked(View v)
    {
        notifySellClicked();
    }

    protected void notifySellClicked()
    {
        PositionListener<PositionDTO> listenerCopy = listener;
        if (listenerCopy != null)
        {
            listenerCopy.onSellClicked(getPositionDTO());
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.btn_add_alert)
    protected void handleAddAlertClicked(View v)
    {
        notifyAddAlertClicked();
    }

    protected void notifyAddAlertClicked()
    {
        PositionListener<PositionDTO> listenerCopy = listener;
        if (listenerCopy != null)
        {
            listenerCopy.onAddAlertClicked(getPositionDTO());
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.btn_stock_info)
    protected void handleStockInfoClicked(View v)
    {
        notifyStockInfoClicked();
    }

    protected void notifyStockInfoClicked()
    {
        PositionListener<PositionDTO> listenerCopy = listener;
        if (listenerCopy != null)
        {
            listenerCopy.onStockInfoClicked(getPositionDTO());
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.btn_trade_history)
    protected void handleTradeHistoryClicked(View v)
    {
        notifyTradeHistoryClicked();
    }

    protected void notifyTradeHistoryClicked()
    {
        PositionListener<PositionDTO> listenerCopy = listener;
        if (listenerCopy != null)
        {
            listenerCopy.onTradeHistoryClicked(getPositionDTO());
        }
    }

    public void linkWithHasHistoryButton(boolean hasHistoryButton, boolean andDisplay)
    {
        this.hasHistoryButton = hasHistoryButton;
        if (andDisplay)
        {
            displayHistoryButton();
        }
    }

    public void linkWith(ExpandableListItem<PositionDTO> expandableListItem, boolean andDisplay)
    {
        this.expandableListItem = expandableListItem;
        linkWith(expandableListItem == null ? null : expandableListItem.getModel(), andDisplay);
        if (bottomView != null)
        {
            this.bottomView.linkWith(expandableListItem, andDisplay);
        }
        if (andDisplay)
        {
        }
    }

    public void linkWith(PositionDTO positionDTO, boolean andDisplay)
    {
        this.positionDTO = positionDTO;

        if (this.topView != null)
        {
            this.topView.linkWith(positionDTO, andDisplay);
        }
        if (this.bottomView != null)
        {
            this.bottomView.linkWith(positionDTO, andDisplay);
        }

        if (andDisplay)
        {
            displayModelPart();
        }
    }

    public PositionDTO getPositionDTO()
    {
        Timber.d("getPositionDTO %s", positionDTO);
        Timber.d("getPositionDTO %s", positionDTO.getPositionDTOKey());
        return positionDTO;
    }

    public void display()
    {
        displayTopView();
        displayBottomView();
        displayModelPart();
        displayHistoryButton();
    }

    public void displayModelPart()
    {
        displayColorIndicator();
        displayButtonSell();
    }

    public void displayTopView()
    {
        if (topView != null)
        {
            topView.display();
        }
    }

    public void displayBottomView()
    {
        if (bottomView != null)
        {
            bottomView.display();
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

    public PositionListener<PositionDTO> getListener()
    {
        return listener;
    }

    public void setListener(PositionListener<PositionDTO> listener)
    {
        this.listener = listener;
    }
}
