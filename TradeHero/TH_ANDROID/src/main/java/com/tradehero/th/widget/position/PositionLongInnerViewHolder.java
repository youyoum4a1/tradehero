package com.tradehero.th.widget.position;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.position.OwnedPositionId;
import com.tradehero.th.api.position.PositionDTO;
import com.tradehero.th.utils.NumberDisplayUtils;
import com.tradehero.th.utils.SecurityUtils;

/** Created with IntelliJ IDEA. User: xavier Date: 10/19/13 Time: 11:43 AM To change this template use File | Settings | File Templates. */
public class PositionLongInnerViewHolder<OnClickedListenerType extends PositionLongInnerViewHolder.OnPositionLongInnerClickedListener>
        extends PositionQuickInnerViewHolder<OnClickedListenerType>
{
    public static final String TAG = PositionLongInnerViewHolder.class.getSimpleName();

    private TableRow unrealisedPLRow;
    private TextView unrealisedPLHeader;
    private TextView unrealisedPLValue;
    private TableRow realisedPLRow;
    private TextView realisedPLHeader;
    private TextView realisedPLValue;
    private TableRow totalInvestedRow;
    private TextView totalInvestedHeader;
    private TextView totalInvestedValue;
    private TableRow marketValueRow;
    private TextView marketValueHeader;
    private TextView marketValueValue;
    private TableRow quantityRow;
    private TextView quantityHeader;
    private TextView quantityValue;
    private TableRow averagePriceRow;
    private TextView averagePriceHeader;
    private TextView averagePriceValue;
    private ImageButton btnBuy;
    private ImageButton btnSell;
    private ImageButton btnAddAlert;
    private ImageButton btnStockInfo;

    //<editor-fold desc="Constructors">
    public PositionLongInnerViewHolder()
    {
        super();
    }
    //</editor-fold>

    @Override public void initViews(View view)
    {
        super.initViews(view);

        if (view != null)
        {
            unrealisedPLRow = (TableRow) view.findViewById(R.id.unrealised_pl_value_row);
            unrealisedPLHeader = (TextView) view.findViewById(R.id.unrealised_pl_header);
            unrealisedPLValue = (TextView) view.findViewById(R.id.unrealised_pl_value);

            realisedPLRow = (TableRow) view.findViewById(R.id.realised_pl_value_row);
            realisedPLHeader = (TextView) view.findViewById(R.id.realised_pl_header);
            realisedPLValue = (TextView) view.findViewById(R.id.realised_pl_value);

            totalInvestedRow = (TableRow) view.findViewById(R.id.total_invested_value_row);
            totalInvestedHeader = (TextView) view.findViewById(R.id.total_invested_header);
            totalInvestedValue = (TextView) view.findViewById(R.id.total_invested_value);

            marketValueRow = (TableRow) view.findViewById(R.id.market_value_value_row);
            marketValueHeader = (TextView) view.findViewById(R.id.market_value_header);
            marketValueValue = (TextView) view.findViewById(R.id.market_value_value);

            quantityRow = (TableRow) view.findViewById(R.id.quantity_row);
            quantityHeader = (TextView) view.findViewById(R.id.quantity_header);
            quantityValue = (TextView) view.findViewById(R.id.quantity_value);

            averagePriceRow = (TableRow) view.findViewById(R.id.average_price_value_row);
            averagePriceHeader = (TextView) view.findViewById(R.id.average_price_header);
            averagePriceValue = (TextView) view.findViewById(R.id.average_price_value);

            btnBuy = (ImageButton) view.findViewById(R.id.btn_buy_now);
            if (btnBuy != null)
            {
                btnBuy.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override public boolean onTouch(View view, MotionEvent motionEvent)
                    {
                        return onViewTouched(view, motionEvent);
                    }
                });
            }

            btnSell = (ImageButton) view.findViewById(R.id.btn_sell_now);
            if (btnSell != null)
            {
                btnSell.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override public boolean onTouch(View view, MotionEvent motionEvent)
                    {
                        return onViewTouched(view, motionEvent);
                    }
                });
            }

            btnAddAlert = (ImageButton) view.findViewById(R.id.btn_add_alert);
            if (btnAddAlert != null)
            {
                btnAddAlert.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override public boolean onTouch(View view, MotionEvent motionEvent)
                    {
                        return onViewTouched(view, motionEvent);
                    }
                });
            }

            btnStockInfo = (ImageButton) view.findViewById(R.id.btn_stock_info);
            if (btnStockInfo != null)
            {
                btnStockInfo.setOnTouchListener(new View.OnTouchListener()
                {
                    @Override public boolean onTouch(View view, MotionEvent motionEvent)
                    {
                        return onViewTouched(view, motionEvent);
                    }
                });
            }
        }
    }

    //<editor-fold desc="DTO Methods">
    @Override public void linkWith(PositionDTO positionDTO, boolean andDisplay)
    {
        super.linkWith(positionDTO, andDisplay);

        if (andDisplay)
        {
            displayUnrealisedPLRow();
            displayUnrealisedPLValue();
            displayRealisedPLValue();
            displayTotalInvested();
            displayMarketValueValue();
            displayQuantityValue();
            displayAveragePriceValue();
            displayButtonSell();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Display Methods">
    @Override public void display()
    {
        super.display();
        displayUnrealisedPLRow();
        displayUnrealisedPLValue();
        displayRealisedPLValue();
        displayTotalInvested();
        displayMarketValueValue();
        displayQuantityValue();
        displayAveragePriceValue();
        displayButtonSell();
    }

    public void displayUnrealisedPLRow()
    {
        if (unrealisedPLRow != null)
        {
            if (positionDTO != null)
            {
                unrealisedPLRow.setVisibility(positionDTO.isOpen() ? View.VISIBLE : View.GONE);
            }
        }
    }

    public void displayUnrealisedPLValue()
    {
        if (unrealisedPLValue != null)
        {
            if (positionDTO != null && positionDTO.unrealizedPLRefCcy != null)
            {
                unrealisedPLValue.setText(NumberDisplayUtils.formatWithRelevantDigits(
                        positionDTO.unrealizedPLRefCcy,
                        4,
                        SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY));
            }
            else
            {
                unrealisedPLValue.setText(R.string.na);
            }
        }
    }

    public void displayRealisedPLValue()
    {
        if (realisedPLValue != null)
        {
            if (positionDTO != null && positionDTO.realizedPLRefCcy != null)
            {
                realisedPLValue.setText(NumberDisplayUtils.formatWithRelevantDigits(
                        positionDTO.realizedPLRefCcy,
                        4,
                        SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY));
            }
            else
            {
                realisedPLValue.setText(R.string.na);
            }
        }
    }

    public void displayTotalInvested()
    {
        if (totalInvestedValue != null)
        {
            if (positionDTO != null && positionDTO.sumInvestedAmountRefCcy != null)
            {
                totalInvestedValue.setText(NumberDisplayUtils.formatWithRelevantDigits(
                        positionDTO.sumInvestedAmountRefCcy,
                        4,
                        SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY));
            }
            else
            {
                totalInvestedValue.setText(R.string.na);
            }
        }
    }

    public void displayMarketValueValue()
    {
        if (marketValueValue != null)
        {
            if (positionDTO != null)
            {
                marketValueValue.setText(NumberDisplayUtils.formatWithRelevantDigits(
                        positionDTO.marketValueRefCcy,
                        4,
                        SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY));
            }
            else
            {
                marketValueValue.setText(R.string.na);
            }
        }
    }

    public void displayQuantityValue()
    {
        if (quantityValue != null)
        {
            if (positionDTO != null && positionDTO.shares != null)
            {
                quantityValue.setText(String.format("%,d", positionDTO.shares));
            }
            else
            {
                quantityValue.setText(R.string.na);
            }
        }
    }

    public void displayAveragePriceValue()
    {
        if (averagePriceValue != null)
        {
            if (positionDTO != null && positionDTO.averagePriceRefCcy != null)
            {
                averagePriceValue.setText(NumberDisplayUtils.formatWithRelevantDigits(
                        positionDTO.averagePriceRefCcy,
                        4,
                        SecurityUtils.DEFAULT_VIRTUAL_CASH_CURRENCY_DISPLAY));
            }
            else
            {
                averagePriceValue.setText(R.string.na);
            }
        }
    }

    public void displayButtonSell()
    {
        if (btnSell != null)
        {
            if (positionDTO != null)
            {
                btnSell.setVisibility(positionDTO.isClosed() == null || positionDTO.isClosed() ? View.INVISIBLE : View.VISIBLE);
            }
        }

    }
    //</editor-fold>

    //<editor-fold desc="Notify Methods">
    @Override protected void notifyViewClicked(View clickedView)
    {
        super.notifyViewClicked(clickedView);

        if (clickedView == btnBuy)
        {
            notifyBuyClicked();
        }
        else if (clickedView == btnSell)
        {
            notifySellClicked();
        }
        else if (clickedView == btnAddAlert)
        {
            notifyAddAlertClicked();
        }
        else if (clickedView == btnStockInfo)
        {
            notifyStockInfoClicked();
        }
    }

    protected void notifyBuyClicked()
    {
        OnClickedListenerType listener = positionClickedListener.get();
        if (listener != null)
        {
            listener.onBuyClicked(ownedPositionId);
        }
    }

    protected void notifySellClicked()
    {
        OnClickedListenerType listener = positionClickedListener.get();
        if (listener != null)
        {
            listener.onSellClicked(ownedPositionId);
        }
    }

    protected void notifyAddAlertClicked()
    {
        OnClickedListenerType listener = positionClickedListener.get();
        if (listener != null)
        {
            listener.onAddAlertClicked(ownedPositionId);
        }
    }

    protected void notifyStockInfoClicked()
    {
        OnClickedListenerType listener = positionClickedListener.get();
        if (listener != null)
        {
            listener.onStockInfoClicked(ownedPositionId);
        }
    }
    //</editor-fold>

    public static interface OnPositionLongInnerClickedListener extends OnPositionQuickInnerClickedListener
    {
        void onBuyClicked(OwnedPositionId clickedOwnedPositionId);
        void onSellClicked(OwnedPositionId clickedOwnedPositionId);
        void onAddAlertClicked(OwnedPositionId clickedOwnedPositionId);
        void onStockInfoClicked(OwnedPositionId clickedOwnedPositionId);
    }
}
