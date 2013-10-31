package com.tradehero.th.widget.position;

import com.tradehero.th.api.position.OwnedPositionId;

/**
 * Created by julien on 30/10/13
 */
public interface PositionListener
{
    void onTradeHistoryClicked(OwnedPositionId clickedOwnedPositionId);
    void onBuyClicked(OwnedPositionId clickedOwnedPositionId);
    void onSellClicked(OwnedPositionId clickedOwnedPositionId);
    void onAddAlertClicked(OwnedPositionId clickedOwnedPositionId);
    void onStockInfoClicked(OwnedPositionId clickedOwnedPositionId);
}
