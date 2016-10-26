package com.androidth.general.fragments.position.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import com.androidth.general.R;
import com.androidth.general.adapters.TypedRecyclerAdapter;
import com.androidth.general.base.THApp;

public class PositionNothingView extends RelativeLayout
{
    //<editor-fold desc="Constructors">
    public PositionNothingView(Context context)
    {
        super(context);
    }

    public PositionNothingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PositionNothingView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    public static class DTO
    {
        @StringRes @NonNull public final String description;
        public final boolean isCurrentUser;

        public DTO(@NonNull Resources resources, boolean isCurrentUser)
        {
            this.isCurrentUser = isCurrentUser;
            description = THApp.context().getString(isCurrentUser
                    ? R.string.position_nothing_description
                    : R.string.position_nothing_description_other_user);
        }
    }

    public static class ViewHolder extends TypedRecyclerAdapter.TypedViewHolder<Object>
    {
        @Bind(R.id.position_nothing_description) protected TextView description;

        public ViewHolder(PositionNothingView itemView)
        {
            super(itemView);
        }

        @Override public void onDisplay(Object o)
        {
            if (description != null && o instanceof DTO)
            {
                DTO dto = (DTO) o;
                description.setText(dto.description);
            }
        }
    }
}
