package com.tradehero.th.fragments.trending.filter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.market.Exchange;
import com.tradehero.th.api.market.ExchangeDTO;
import com.tradehero.th.api.market.ExchangeStringId;
import com.tradehero.th.api.security.TrendingBasicSecurityListType;
import com.tradehero.th.api.security.TrendingSecurityListType;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 1/15/14.
 */
@Singleton public class TrendingFilterTypeDTOUtil
{
    public static final String TAG = TrendingFilterTypeDTOUtil.class.getSimpleName();

    private List<CharSequence> dropDownTexts;
    private List<Drawable> dropDownIcons;

    @Inject public TrendingFilterTypeDTOUtil()
    {
        super();
    }

    public List<TrendingFilterTypeDTO> getAll()
    {
        List<TrendingFilterTypeDTO> all = new ArrayList<>();
        all.add(new TrendingFilterTypeBasicDTO());
        all.add(new TrendingFilterTypeVolumeDTO());
        all.add(new TrendingFilterTypePriceDTO());
        all.add(new TrendingFilterTypeGenericDTO());
        return all;
    }

    public void createDropDownTextsAndIcons(Context context, List<ExchangeDTO> exchangeDTOs)
    {
        if (exchangeDTOs == null)
        {
            dropDownTexts = null;
            dropDownIcons = null;
        }
        else
        {
            dropDownTexts = new ArrayList<>();
            dropDownIcons = new ArrayList<>();
            for (ExchangeDTO exchangeDTO: exchangeDTOs)
            {
                dropDownTexts.add(context.getString(R.string.trending_filter_exchange_drop_down, exchangeDTO.name, exchangeDTO.desc));
                try
                {
                    dropDownIcons.add(context.getResources().getDrawable(Exchange.valueOf(exchangeDTO.name).logoId));
                }
                catch (IllegalArgumentException ex)
                {
                    THLog.d(TAG, "Exchange logo does not exist: " + ex.getMessage());
                }
            }
        }
    }

    public CharSequence[] getDropDownTextsArray(Context context)
    {
        if (dropDownTexts == null)
        {
            return null;
        }
        CharSequence[] texts = new CharSequence[dropDownTexts.size() + 1];
        int index = 0;
        texts[index++] = context.getString(R.string.trending_filter_exchange_all);
        for (CharSequence charSequence: dropDownTexts)
        {
            texts[index++] = charSequence;
        }
        return texts;
    }

    public Drawable[] getDropDownIconsArray()
    {
        if (dropDownIcons == null)
        {
            return null;
        }
        Drawable[] drawables = new Drawable[dropDownIcons.size() + 1];
        int index = 0;
        drawables[index++] = null;
        for (Drawable drawable: dropDownIcons)
        {
            drawables[index++] = drawable;
        }
        return drawables;
    }

    public TrendingSecurityListType getSecurityListType(TrendingFilterTypeDTO trendingFilterTypeDTO, String usableExchangeName, Integer page, Integer perPage)
    {
        if (trendingFilterTypeDTO == null)
        {
            return null;
        }
        return trendingFilterTypeDTO.getSecurityListType(usableExchangeName, page, perPage);
    }
}
