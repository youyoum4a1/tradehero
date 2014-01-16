package com.tradehero.th.models.market;

import android.content.Context;
import android.graphics.drawable.Drawable;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.market.Exchange;
import com.tradehero.th.api.market.ExchangeDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 1/16/14.
 */
@Singleton public class ExchangeSpinnerDTOUtil
{
    public static final String TAG = ExchangeSpinnerDTOUtil.class.getSimpleName();

    @Inject public ExchangeSpinnerDTOUtil()
    {
    }

    public ExchangeSpinnerDTO[] getSpinnerDTOs(Context context, List<ExchangeDTO> exchangeDTOs)
    {
        if (exchangeDTOs == null)
        {
            return null;
        }

        ExchangeSpinnerDTO[] spinnerDTOs = new ExchangeSpinnerDTO[exchangeDTOs.size() + 1];
        spinnerDTOs[0] = new ExchangeSpinnerDTO(context); // That's the "All Exchanges" thing
        int index = 1;
        for (ExchangeDTO exchangeDTO: exchangeDTOs)
        {
            spinnerDTOs[index++] = new ExchangeSpinnerDTO(context, exchangeDTO);
        }
        return spinnerDTOs;
    }

    public Drawable[] getSpinnerIcons(Context context, List<ExchangeDTO> exchangeDTOs)
    {
        if (exchangeDTOs == null)
        {
            return null;
        }

        Drawable[] spinnerIcons = new Drawable[exchangeDTOs.size() + 1];
        spinnerIcons[0] = null; // That's the "All Exchanges" thing
        int index = 1;
        for (ExchangeDTO exchangeDTO: exchangeDTOs)
        {
            try
            {
                spinnerIcons[index] = context.getResources().getDrawable(Exchange.valueOf(exchangeDTO.name).logoId);
            }
            catch (IllegalArgumentException ex)
            {
                THLog.d(TAG, "Exchange logo does not exist: " + ex.getMessage());
            }
            finally
            {
                index++;
            }
        }
        return spinnerIcons;
    }

    public <T extends ExchangeDTO> int indexOf(T[] exchangeDTOs, T exchangeToFind)
    {
        return new ArrayList<T>(Arrays.asList(exchangeDTOs)).indexOf(exchangeToFind);
    }
}
