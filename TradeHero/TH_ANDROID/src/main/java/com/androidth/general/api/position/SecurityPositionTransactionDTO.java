package com.androidth.general.api.position;

import android.support.annotation.NonNull;
import com.androidth.general.api.portfolio.PortfolioDTO;

public class SecurityPositionTransactionDTO extends SecurityPositionDTO
{
    @NonNull public PortfolioDTO portfolio;
    public int tradeId;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration") // Necessary for deserialisation
    SecurityPositionTransactionDTO()
    {
        super();
    }
    //</editor-fold>
}
