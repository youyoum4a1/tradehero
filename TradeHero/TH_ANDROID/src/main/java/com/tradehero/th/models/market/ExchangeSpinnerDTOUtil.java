package com.tradehero.th.models.market;

import android.content.Context;
import com.tradehero.th.api.market.Exchange;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton public class ExchangeSpinnerDTOUtil
{
    @Inject public ExchangeSpinnerDTOUtil()
    {
    }

    public ExchangeCompactSpinnerDTO[] getSpinnerDTOs(Context context, List<ExchangeCompactDTO> exchangeDTOs)
    {
        if (exchangeDTOs == null)
        {
            return null;
        }

        ExchangeCompactSpinnerDTO[] spinnerDTOs = new ExchangeCompactSpinnerDTO[exchangeDTOs.size() + 1];
        spinnerDTOs[0] = new ExchangeCompactSpinnerDTO(context); // That's the "All Exchanges" thing
        int index = 1;
        for (ExchangeCompactDTO exchangeDTO: exchangeDTOs)
        {
            spinnerDTOs[index++] = new ExchangeCompactSpinnerDTO(context, exchangeDTO);
        }
        return spinnerDTOs;
    }

    public int[] getSpinnerIcons(Context context, List<ExchangeCompactDTO> exchangeCompactDTOs)
    {
        if (exchangeCompactDTOs == null)
        {
            return null;
        }

        int[] spinnerIcons = new int[exchangeCompactDTOs.size() + 1];
        spinnerIcons[0] = 0; // That's the "All Exchanges" thing
        int index = 1;
        for (ExchangeCompactDTO exchangeDTO: exchangeCompactDTOs)
        {
            try
            {
                spinnerIcons[index] = Exchange.valueOf(exchangeDTO.name).logoId;
            }
            catch (IllegalArgumentException ex)
            {
                Timber.d("Exchange logo does not exist: %s", ex.getMessage());
            }
            finally
            {
                index++;
            }
        }
        return spinnerIcons;
    }

    public <T extends ExchangeCompactDTO> int indexOf(T[] exchangeCompactDTOs, T exchangeToFind)
    {
        return new ArrayList<T>(Arrays.asList(exchangeCompactDTOs)).indexOf(exchangeToFind);
    }
}
