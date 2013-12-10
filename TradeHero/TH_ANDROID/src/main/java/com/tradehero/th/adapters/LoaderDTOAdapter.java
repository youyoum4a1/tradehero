package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.loaders.PaginationListLoader;

/**
 * Created with IntelliJ IDEA. User: tho Date: 12/10/13 Time: 4:40 PM Copyright (c) TradeHero
 */
public abstract class LoaderDTOAdapter<DTOType extends DTO, DTOViewType extends DTOView<DTOType>> extends DTOAdapter<DTOType, DTOViewType>
{
    private PaginationListLoader<DTOType> loader;

    public LoaderDTOAdapter(Context context, LayoutInflater inflater, int layoutResourceId)
    {
        super(context, inflater, layoutResourceId);
    }

    @Override public int getCount()
    {
        return loader.getItems().size();
    }

    @Override public Object getItem(int position)
    {
        return getCount() > position ? loader.getItems().get(position) : null;
    }

    public void setLoader(PaginationListLoader<DTOType> loader)
    {
        this.loader = loader;
    }

    public PaginationListLoader<DTOType> getLoader()
    {
        return loader;
    }
}
