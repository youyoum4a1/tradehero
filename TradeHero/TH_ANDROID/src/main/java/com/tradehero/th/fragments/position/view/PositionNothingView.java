package com.tradehero.th.fragments.position.view;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;

public class PositionNothingView extends RelativeLayout
    implements DTOView<PositionNothingView.DTO>
{
    @InjectView(R.id.position_nothing_description) protected TextView description;

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

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override public void display(DTO dto)
    {
        if (description != null)
        {
            description.setText(dto.description);
        }
    }

    public static class DTO
    {
        @StringRes @NonNull public final String description;

        public DTO(@NonNull Resources resources, boolean isCurrentUser)
        {
            description = resources.getString(isCurrentUser
                    ? R.string.position_nothing_description
                    : R.string.position_nothing_description_other_user);
        }
    }
}
