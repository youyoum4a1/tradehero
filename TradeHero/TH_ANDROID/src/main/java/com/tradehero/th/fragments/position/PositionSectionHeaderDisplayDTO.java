package com.tradehero.th.fragments.position;

import android.support.annotation.NonNull;
import com.tradehero.th.api.position.PositionStatus;

public class PositionSectionHeaderDisplayDTO
{
    @NonNull public final PositionStatus status;
    @NonNull public final String header;
    @NonNull public final String timeBase;
    @NonNull public final Type type;

    public PositionSectionHeaderDisplayDTO(
            @NonNull PositionStatus status,
            @NonNull String header,
            @NonNull Type type)
    {
        this.status = status;
        this.header = header;
        this.timeBase = "";
        this.type = type;
        //this(resources, status, header, null, null, type);
    }

    // TODO: Date is complained by proguard not convertable to Utf8.
    //public PositionSectionHeaderDisplayDTO(
    //        //@NonNull Resources resources,
    //        @NonNull PositionStatus status,
    //        @NonNull String header,
    //        @Nullable Date left,
    //        @Nullable Date right,
    //        @NonNull Type type)
    //{
    //    this.status = status;
    //    this.header = header;
    //    SimpleDateFormat sdf = new SimpleDateFormat(resources.getString(R.string.data_format_dd_mmm_yyyy), Locale.ENGLISH);
    //    if (left != null || right != null)
    //    {
    //        this.timeBase = resources.getString(
    //                R.string.position_list_header_time_base,
    //                left != null ? sdf.format(left) : "",
    //                right != null ? sdf.format(right) : "");
    //    }
    //    else
    //    {
    //        this.timeBase = "";
    //    }
    //    this.timeBase = "";
    //    this.type = type;
    //}

    public enum Type
    {
        PENDING, LONG, SHORT, CLOSED
    }
}
