package com.tradehero.th.fragments.position;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.utils.AlertDialogRxUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PositionSectionHeaderItemView extends RelativeLayout
    implements DTOView<PositionSectionHeaderItemView.DTO>
{
    public static final int INFO_TYPE_LONG = 0;
    public static final int INFO_TYPE_SHORT = 1;
    public static final int INFO_TYPE_CLOSED = 2;

    @InjectView(R.id.header_text) protected TextView headerText;
    @InjectView(R.id.header_time_base) protected TextView timeBaseText;

    @Nullable protected DTO viewDTO;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public PositionSectionHeaderItemView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public PositionSectionHeaderItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public PositionSectionHeaderItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override public void display(@NonNull DTO dto)
    {
        this.viewDTO = dto;
        if (headerText != null)
        {
            headerText.setText(dto.header);
        }

        if (timeBaseText != null)
        {
            timeBaseText.setText(dto.timeBase);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.header_get_info)
    protected void handleInfoClicked(@SuppressWarnings("UnusedParameters") View view)
    {
        if (viewDTO != null)
        {
            int resInt = -1;
            if (viewDTO.type == PositionSectionHeaderItemView.INFO_TYPE_LONG)
            {
                resInt = R.string.position_long_info;
            }
            else if (viewDTO.type == PositionSectionHeaderItemView.INFO_TYPE_SHORT)
            {
                resInt = R.string.position_short_info;
            }
            else if (viewDTO.type == PositionSectionHeaderItemView.INFO_TYPE_CLOSED)
            {
                resInt = R.string.position_close_info;
            }

            if (resInt != -1)
            {
                AlertDialogRxUtil.buildDefault(getContext())
                        .setTitle(R.string.position_title_info)
                        .setMessage(resInt)
                        .setPositiveButton(R.string.ok)
                        .build()
                        .subscribe(
                                new EmptyAction1<OnDialogClickEvent>(),
                                new EmptyAction1<Throwable>());
            }
        }
    }

    public static class DTO
    {
        @NonNull public final String header;
        @NonNull public final String timeBase;
        public final int type;

        public DTO(
                @NonNull Resources resources,
                @NonNull String header,
                @Nullable Date left,
                @Nullable Date right,
                int type)
        {
            this.header = header;
            SimpleDateFormat sdf = new SimpleDateFormat(resources.getString(R.string.data_format_dd_mmm_yyyy), Locale.ENGLISH);
            if (left != null || right != null)
            {
                this.timeBase = resources.getString(
                        R.string.position_list_header_time_base,
                        left != null ? sdf.format(left) : "",
                        right != null ? sdf.format(right) : "");
            }
            else
            {
                this.timeBase = "";
            }
            this.type = type;
        }
    }
}
