package com.androidth.general.fragments.market;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.androidth.general.api.security.SecurityTypeDTO;

public class SecurityTypeSpinner extends Spinner
{
    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public SecurityTypeSpinner(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    public void setSelection(@NonNull SecurityTypeDTO securityTypeDTO)
    {
        SpinnerAdapter adapter = getAdapter();
        int index = 0;
        while (index < adapter.getCount())
        {
            SecurityTypeDTO item = (SecurityTypeDTO) adapter.getItem(index);
            if (item.name.equals(securityTypeDTO.getName()))
            {
                setSelection(index);
            }
            index++;
        }
    }

//    public void setSelectionById(ExchangeIntegerId id)
//    {
//        SpinnerAdapter adapter = getAdapter();
//        int index = 0;
//        while (index < adapter.getCount())
//        {
//            SecurityTypeDTO item = (SecurityTypeDTO) adapter.getItem(index);
//            if (item.getId().equals(id))
//            {
//                setSelection(index);
//            }
//            index++;
//        }
//    }
}
