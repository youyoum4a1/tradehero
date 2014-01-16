package com.tradehero.th.fragments.trending.filter;

import android.os.Bundle;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 1/16/14.
 */
@Singleton public class TrendingFilterTypeDTOFactory
{
    public static final String TAG = TrendingFilterTypeDTOFactory.class.getSimpleName();

    @Inject public TrendingFilterTypeDTOFactory()
    {
        super();
    }

    public TrendingFilterTypeDTO create(Bundle bundle)
    {
        String classType = bundle.getString(TrendingFilterTypeDTO.BUNDLE_KEY_CLASS_TYPE);
        TrendingFilterTypeDTO trendingFilterTypeDTO = null;
        try
        {
            Class endClass = Class.forName(classType);
            Constructor constructor = endClass.getConstructor(Bundle.class);
            trendingFilterTypeDTO = (TrendingFilterTypeDTO) constructor.newInstance(bundle);
        }
        catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | ClassCastException e)
        {
            e.printStackTrace();
        }

        return trendingFilterTypeDTO;
    }
}
