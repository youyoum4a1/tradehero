package com.tradehero.th.utils.metrics.tapstream;

import com.tradehero.th.R;

/**
 * Created by alex on 14-5-9.
 */
public enum TapStreamType
{
    GooglePlay(0, R.string.tap_stream_type_install, R.string.tap_stream_type_open),
    Baidu(1, R.string.tap_stream_type_baidu_install, R.string.tap_stream_type_baidu_open),
    Tencent(2, R.string.tap_stream_type_tencent_install, R.string.tap_stream_type_tencent_open);

    private final int type;
    private final int installResId;
    private final int openResId;

    TapStreamType(int type, int installResId, int openResId)
    {
        this.type = type;
        this.installResId = installResId;
        this.openResId = openResId;
    }

    public int getInstallResId()
    {
        return installResId;
    }

    public int getOpenResId()
    {
        return openResId;
    }

    public int getType()
    {
        return type;
    }

    public static TapStreamType fromType(int type)
    {
        for (TapStreamType tapStreamType : values())
        {
            if (tapStreamType.type == type)
            {
                return tapStreamType;
            }
        }
        return null;
    }
}