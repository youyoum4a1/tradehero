package com.tradehero.th.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import com.tradehero.th.api.DTOView;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class PagedArrayDTOAdapterNew<
        DTOType,
        ViewType extends View & DTOView<DTOType>>
        extends ArrayDTOAdapterNew<DTOType, ViewType>
{
    protected Integer lastPageLoaded;

    //<editor-fold desc="Constructors">
    public PagedArrayDTOAdapterNew(@NotNull Context context, @LayoutRes int layoutResourceId)
    {
        super(context, layoutResourceId);
    }
    //</editor-fold>

    public Integer getLastPageLoaded()
    {
        return lastPageLoaded;
    }

    @Override public void clear()
    {
        super.clear();
        this.lastPageLoaded = null;
    }

    public void addPage(int page, @NotNull List<DTOType> dtos)
    {
        this.lastPageLoaded = page;
        addAll(dtos);
    }
}
