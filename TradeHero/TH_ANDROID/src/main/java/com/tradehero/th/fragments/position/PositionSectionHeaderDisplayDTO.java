package com.tradehero.th.fragments.position;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;
import com.tradehero.th.api.position.PositionStatus;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PositionSectionHeaderDisplayDTO
{
    @NonNull public final PositionStatus status;
    @NonNull public final String header;
    @NonNull public final String timeBase;
    @NonNull public final Type type;

    public PositionSectionHeaderDisplayDTO(
            @NonNull Resources resources,
            @NonNull PositionStatus status,
            @NonNull String header,
            @NonNull Type type)
    {
        this(resources, status, header, null, null, type);
    }

    public PositionSectionHeaderDisplayDTO(
            @NonNull Resources resources,
            @NonNull PositionStatus status,
            @NonNull String header,
            @Nullable Date left,
            @Nullable Date right,
            @NonNull Type type)
    {
        this.status = status;
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

    public enum Type
    {
        PENDING, LONG, SHORT, CLOSED
    }
}
