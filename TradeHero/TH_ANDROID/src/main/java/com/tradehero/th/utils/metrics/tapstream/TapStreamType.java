package com.tradehero.th.utils.metrics.tapstream;

import com.tradehero.th.R;

/**
 * Created by alex on 14-5-9.
 */
public enum TapStreamType
{
    GooglePlay(0, R.string.tap_stream_type_google_install, R.string.tap_stream_type_google_open),
    Baidu(1, R.string.tap_stream_type_baidu_install, R.string.tap_stream_type_baidu_open),
    Tencent(2, R.string.tap_stream_type_tencent_install, R.string.tap_stream_type_tencent_open),
    XiaoMi(3, R.string.tap_stream_type_xiaomi_install, R.string.tap_stream_type_xiaomi_open),
    NineOne(4, R.string.tap_stream_type_91_install, R.string.tap_stream_type_91_open),
    AZouSC(5, R.string.tap_stream_type_azhuosc_install, R.string.tap_stream_type_azhuosc_open),
    AZhiSC(6, R.string.tap_stream_type_azhisc_install, R.string.tap_stream_type_azhisc_open),
    Lenovo(7, R.string.tap_stream_type_lenovo_install, R.string.tap_stream_type_lenovo_open),
    WanDouJia(8, R.string.tap_stream_type_wandoujia_install, R.string.tap_stream_type_wandoujia_open),
    QiHu(9, R.string.tap_stream_type_360_install, R.string.tap_stream_type_360_open),
    Oppo(10, R.string.tap_stream_type_oppo_install, R.string.tap_stream_type_oppo_open),
    JiFeng(11, R.string.tap_stream_type_jifeng_install, R.string.tap_stream_type_jifeng_open),
    YYHui(12, R.string.tap_stream_type_yyhui_install, R.string.tap_stream_type_yyhui_open),
    YDMM(13, R.string.tap_stream_type_ydmm_install, R.string.tap_stream_type_ydmm_open),
    MPTang(14, R.string.tap_stream_type_mptang_install, R.string.tap_stream_type_mptang_open),
    ;

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