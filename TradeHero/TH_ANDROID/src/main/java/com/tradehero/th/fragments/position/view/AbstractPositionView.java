package com.tradehero.th.fragments.position.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.widget.ColorIndicator;
import com.tradehero.thm.R;
import com.tradehero.th.adapters.ExpandableListItem;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.fragments.position.PositionListener;
import com.tradehero.th.fragments.position.partial.AbstractPartialBottomView;
import com.tradehero.th.fragments.position.partial.PositionPartialTopView;
import timber.log.Timber;

public abstract class AbstractPositionView<
            PositionDTOType extends PositionDTO,
            ExpandableListItemType extends ExpandableListItem<PositionDTOType>>
        extends LinearLayout
{
    @InjectView(R.id.position_partial_top) protected PositionPartialTopView topView;
    @InjectView(R.id.expanding_layout) protected AbstractPartialBottomView/*<PositionDTOType, ExpandableListItemType>*/ bottomView;

    @InjectView(R.id.color_indicator) protected ColorIndicator colorIndicator;
    @InjectView(R.id.btn_buy_now) protected View btnBuy;
    @InjectView(R.id.btn_sell_now) protected View btnSell;
    @InjectView(R.id.btn_add_alert) protected View btnAddAlert;
    @InjectView(R.id.btn_stock_info) protected View btnStockInfo;
    @InjectView(R.id.btn_trade_history) protected View historyButton;

    protected boolean hasHistoryButton = true;
    protected ExpandableListItemType expandableListItem;
    protected PositionDTOType positionDTO;

    protected PositionListener<PositionDTOType> listener = null;

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

    @OnClick(R.id.btn_buy_now)
    protected void handleBuyClicked(View v)
    {
        notifyBuyClicked();
    }

    protected void notifyBuyClicked()
    {
        PositionListener<PositionDTOType> listenerCopy = listener;
        if (listenerCopy != null)
        {
            listenerCopy.onBuyClicked(getPositionDTO());
        }
    }

    @OnClick(R.id.btn_sell_now)
    protected void handleSellClicked(View v)
    {
        notifySellClicked();
    }

    protected void notifySellClicked()
    {
        PositionListener<PositionDTOType> listenerCopy = listener;
        if (listenerCopy != null)
        {
            listenerCopy.onSellClicked(getPositionDTO());
        }
    }

    @OnClick(R.id.btn_add_alert)
    protected void handleAddAlertClicked(View v)
    {
        notifyAddAlertClicked();
    }

    protected void notifyAddAlertClicked()
    {
        PositionListener<PositionDTOType> listenerCopy = listener;
        if (listenerCopy != null)
        {
            listenerCopy.onAddAlertClicked(getPositionDTO());
        }
    }

    @OnClick(R.id.btn_stock_info)
    protected void handleStockInfoClicked(View v)
    {
        notifyStockInfoClicked();
    }

    protected void notifyStockInfoClicked()
    {
        PositionListener<PositionDTOType> listenerCopy = listener;
        if (listenerCopy != null)
        {
            listenerCopy.onStockInfoClicked(getPositionDTO());
        }
    }

    @OnClick(R.id.btn_trade_history)
    protected void handleTradeHistoryClicked(View v)
    {
        notifyTradeHistoryClicked();
    }

    protected void notifyTradeHistoryClicked()
    {
        PositionListener<PositionDTOType> listenerCopy = listener;
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

    public void linkWith(ExpandableListItemType expandableListItem, boolean andDisplay)
    {
        this.expandableListItem = expandableListItem;
        linkWith(expandableListItem == null ? null : expandableListItem.getModel(), andDisplay);
        if (bottomView != null)
        {
            this.bottomView.linkWith(expandableListItem, andDisplay);
        }
        if (andDisplay)
        {
            displayExpandingPart();
        }
    }

    public void linkWith(PositionDTOType positionDTO, boolean andDisplay)
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

    public PositionDTOType getPositionDTO()
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
        displayExpandingPart();
        displayHistoryButton();
    }

    public void displayModelPart()
    {
        displayColorIndicator();
        displayButtonSell();
    }

    public void displayExpandingPart()
    {
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

    public PositionListener<PositionDTOType> getListener()
    {
        return listener;
    }

    public void setListener(PositionListener<PositionDTOType> listener)
    {
        this.listener = listener;
    }
}
