package com.tradehero.th.api.position;

import com.tradehero.th.api.portfolio.PortfolioDTO;
import org.jetbrains.annotations.NotNull;

public class SecurityPositionTransactionDTO extends SecurityPositionDTO
{
    @NotNull public PortfolioDTO portfolio;
    public int tradeId;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration") // Necessary for deserialisation
    SecurityPositionTransactionDTO()
    {
        super();
    }
    //</editor-fold>
}
