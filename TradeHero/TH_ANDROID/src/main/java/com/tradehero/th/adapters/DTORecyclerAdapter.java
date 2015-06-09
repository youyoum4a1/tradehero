package com.tradehero.th.adapters;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.DTOView;
import java.util.List;

public abstract class DTORecyclerAdapter<DTOType extends DTO> extends TypedRecyclerAdapter<DTOType>
{
    public DTORecyclerAdapter(Class<DTOType> klass, List<DTOType> mSortedList,
            OnItemClickedListener<DTOType> onItemClickedListener)
    {
        super(klass, mSortedList, onItemClickedListener);
    }

    public DTORecyclerAdapter(Class<DTOType> klass, @Nullable TypedRecyclerComparator<DTOType> comparator, List<DTOType> list,
            OnItemClickedListener<DTOType> onItemClickedListener)
    {
        super(klass, comparator, list, onItemClickedListener);
    }

    @Override public abstract TypedViewHolder<DTOType> instantiateViewHolder(ViewGroup parent, int viewType);

    public abstract class DTOViewHolder<ViewType extends View & DTOView<DTOType>> extends TypedViewHolder<DTOType>
    {
        public DTOViewHolder(ViewType itemView)
        {
            super(itemView);
        }

        @Override public abstract void display(DTOType t);
    }
}
