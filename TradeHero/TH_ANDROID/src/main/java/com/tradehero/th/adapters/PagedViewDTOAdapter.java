package com.tradehero.th.adapters;

import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.api.DTOView;

public interface PagedViewDTOAdapter<
        DTOType,
        ViewType extends View & DTOView<DTOType>>
        extends PagedDTOAdapter<DTOType>
{
    ViewType getView(int position, View convertView, ViewGroup viewGroup);
}
