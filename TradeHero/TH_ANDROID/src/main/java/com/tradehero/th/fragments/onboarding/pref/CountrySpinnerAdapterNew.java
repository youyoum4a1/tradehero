package com.tradehero.th.fragments.onboarding.pref;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.adapters.ViewDTOSetAdapter;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.market.CountryNameComparator;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CountrySpinnerAdapterNew
        extends ViewDTOSetAdapter<Country, CountrySpinnerItemView>
{
    @LayoutRes private int resId;
    @LayoutRes private int dropDownResId;

    //<editor-fold desc="Constructors">
    public CountrySpinnerAdapterNew(@NotNull Context context, @LayoutRes int layoutResourceId)
    {
        super(context);
        this.resId = layoutResourceId;
    }
    //</editor-fold>

    @NotNull @Override protected Set<Country> createSet(
            @Nullable Collection<Country> objects)
    {
        Set<Country> created = new TreeSet<>(new CountryNameComparator(context));
        if (objects != null)
        {
            created.addAll(objects);
        }
        return created;
    }

    @Override @LayoutRes protected int getViewResId(int position)
    {
        return resId;
    }

    public void setDropDownViewResource(@LayoutRes int resource)
    {
        this.dropDownResId = resource;
    }

    @Override public CountrySpinnerItemView getDropDownView(int position, @Nullable View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = View.inflate(context, dropDownResId, null);
        }
        CountrySpinnerItemView dtoView = (CountrySpinnerItemView) convertView;
        dtoView.display(getItem(position));
        return dtoView;
    }
}
