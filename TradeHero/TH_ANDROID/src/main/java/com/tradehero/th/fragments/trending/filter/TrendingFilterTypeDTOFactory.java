package com.tradehero.th.fragments.trending.filter;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

@Singleton public class TrendingFilterTypeDTOFactory
{
    private final Resources resources;

    //<editor-fold desc="Constructors">
    @Inject public TrendingFilterTypeDTOFactory(@NotNull Context context)
    {
        super();
        this.resources = context.getResources();
    }
    //</editor-fold>

    @NotNull public TrendingFilterTypeDTO create(Bundle bundle)
    {
        String classType = bundle.getString(TrendingFilterTypeDTO.BUNDLE_KEY_CLASS_TYPE);
        TrendingFilterTypeDTO trendingFilterTypeDTO;
        try
        {
            Class endClass = Class.forName(classType);
            Constructor constructor = endClass.getConstructor(Bundle.class);
            trendingFilterTypeDTO = (TrendingFilterTypeDTO) constructor.newInstance(bundle);
        }
        catch (ClassNotFoundException |
                NoSuchMethodException |
                InstantiationException |
                IllegalAccessException |
                InvocationTargetException |
                ClassCastException e)
        {
            Timber.e(new IllegalArgumentException("Failed to create TrendingFilterTypeDTO for classType " + classType, e), "");
            trendingFilterTypeDTO = new TrendingFilterTypeBasicDTO(resources);
        }

        return trendingFilterTypeDTO;
    }
}
