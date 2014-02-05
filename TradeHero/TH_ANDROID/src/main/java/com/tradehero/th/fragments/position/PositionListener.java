package com.tradehero.th.fragments.position;

import com.tradehero.th.api.position.PositionDTO;

/**
 * Created by julien on 30/10/13
 */
public interface PositionListener<PositionDTOType extends PositionDTO>
{
    void onTradeHistoryClicked(PositionDTOType clickedOwnedPositionId);
    void onBuyClicked(PositionDTOType clickedOwnedPositionId);
    void onSellClicked(PositionDTOType clickedOwnedPositionId);
    void onAddAlertClicked(PositionDTOType clickedOwnedPositionId);
    void onStockInfoClicked(PositionDTOType clickedOwnedPositionId);
}
