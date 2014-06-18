package com.tradehero.th.models.market;

import android.content.Context;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExchangeSpinnerDTOUtil
{
    //<editor-fold desc="Constructors">
    @Inject public ExchangeSpinnerDTOUtil()
    {
    }
    //</editor-fold>

    @Contract("_, null -> null; _, !null -> !null") @Nullable
    public ExchangeCompactSpinnerDTO[] getSpinnerDTOs(@NotNull Context context, @Nullable List<ExchangeCompactDTO> exchangeDTOs)
    {
        if (exchangeDTOs == null)
        {
            return null;
        }

        ExchangeCompactSpinnerDTO[] spinnerDTOs = new ExchangeCompactSpinnerDTO[exchangeDTOs.size() + 1];
        spinnerDTOs[0] = new ExchangeCompactSpinnerDTO(context); // That's the "All Exchanges" thing
        int index = 1;
        for (@NotNull ExchangeCompactDTO exchangeDTO: exchangeDTOs)
        {
            spinnerDTOs[index++] = new ExchangeCompactSpinnerDTO(context, exchangeDTO);
        }
        return spinnerDTOs;
    }

    @Contract("_, null -> null; _, !null -> !null") @Nullable
    public int[] getSpinnerIcons(@NotNull Context context, @Nullable List<ExchangeCompactDTO> exchangeCompactDTOs)
    {
        if (exchangeCompactDTOs == null)
        {
            return null;
        }

        int[] spinnerIcons = new int[exchangeCompactDTOs.size() + 1];
        spinnerIcons[0] = 0; // That's the "All Exchanges" thing
        int index = 1;
        Integer flagResId;
        for (@NotNull ExchangeCompactDTO exchangeDTO: exchangeCompactDTOs)
        {
            flagResId = exchangeDTO.getFlagResId();
            if (flagResId != null)
            {
                spinnerIcons[index] = flagResId;
            }
            index++;
        }
        return spinnerIcons;
    }

    public <T extends ExchangeCompactDTO> int indexOf(T[] exchangeCompactDTOs, T exchangeToFind)
    {
        return new ArrayList<T>(Arrays.asList(exchangeCompactDTOs)).indexOf(exchangeToFind);
    }
}
