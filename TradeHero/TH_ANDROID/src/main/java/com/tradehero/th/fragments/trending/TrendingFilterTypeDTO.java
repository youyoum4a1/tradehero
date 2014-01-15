package com.tradehero.th.fragments.trending;

import com.tradehero.th.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xavier on 1/15/14.
 */
public class TrendingFilterTypeDTO
{
    public static final String TAG = TrendingFilterTypeDTO.class.getSimpleName();

    public final boolean hasPreviousButton;
    public final boolean hasNextButton;
    public final int titleResId;
    public final int titleIconResId;
    public final int descriptionResId;

    public TrendingFilterTypeDTO(boolean hasPreviousButton, boolean hasNextButton, int titleResId, int titleIconResId, int descriptionResId)
    {
        this.hasPreviousButton = hasPreviousButton;
        this.hasNextButton = hasNextButton;
        this.titleResId = titleResId;
        this.titleIconResId = titleIconResId;
        this.descriptionResId = descriptionResId;
    }

    public static List<TrendingFilterTypeDTO> getAll()
    {
        List<TrendingFilterTypeDTO> all = new ArrayList<>();
        all.add(getBasic());
        all.add(getVolume());
        all.add(getPrice());
        all.add(getGeneric());
        return all;
    }

    public static TrendingFilterTypeDTO getBasic()
    {
        return new TrendingFilterTypeDTO(false, true,
                R.string.trending_filter_basic_title, 0,
                R.string.trending_filter_basic_description);
    }

    public static TrendingFilterTypeDTO getVolume()
    {
        return new TrendingFilterTypeDTO(true, true,
                R.string.trending_filter_volume_title, R.drawable.ic_trending_volume,
                R.string.trending_filter_volume_description);
    }

    public static TrendingFilterTypeDTO getPrice()
    {
        return new TrendingFilterTypeDTO(true, true,
                R.string.trending_filter_price_title, R.drawable.ic_trending_price,
                R.string.trending_filter_price_description);
    }

    public static TrendingFilterTypeDTO getGeneric()
    {
        return new TrendingFilterTypeDTO(true, false,
                R.string.trending_filter_all_title, 0,
                R.string.trending_filter_all_description);
    }
}
