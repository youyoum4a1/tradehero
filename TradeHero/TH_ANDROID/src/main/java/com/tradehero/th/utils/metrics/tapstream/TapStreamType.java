package com.tradehero.th.utils.metrics.tapstream;

import com.tradehero.th.R;

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
    DianXin(15, R.string.tap_stream_type_dianxin_install, R.string.tap_stream_type_dianxin_open),
    BaiduGuard(16, R.string.tap_stream_type_baiduguard_install, R.string.tap_stream_type_baiduguard_open),
    WiFiWanNeng(17, R.string.tap_stream_type_wifiwanneng_install, R.string.tap_stream_type_wifiwanneng_open),
    CaiPiao500(18, R.string.tap_stream_type_caipiao500_install, R.string.tap_stream_type_caipiao500_open),
    MengTian1(19, R.string.tap_stream_type_mengtian1_install, R.string.tap_stream_type_mengtian1_open),
    MengTian2(20, R.string.tap_stream_type_mengtian2_install, R.string.tap_stream_type_mengtian2_open),
    SouGou(21, R.string.tap_stream_type_sougou_install, R.string.tap_stream_type_sougou_open),
    HuaWei(22, R.string.tap_stream_type_huawei_install, R.string.tap_stream_type_huawei_open),
    MeiZu(23, R.string.tap_stream_type_meizu_install, R.string.tap_stream_type_meizu_open),
    DianKe1(24, R.string.tap_stream_type_dianke1_install, R.string.tap_stream_type_dianke1_open),
    DianKe2(25, R.string.tap_stream_type_dianke2_install, R.string.tap_stream_type_dianke2_open),
    DianKe3(26, R.string.tap_stream_type_dianke3_install, R.string.tap_stream_type_dianke3_open),
    DianKe4(27, R.string.tap_stream_type_dianke4_install, R.string.tap_stream_type_dianke4_open),
    DianKe5(28, R.string.tap_stream_type_dianke5_install, R.string.tap_stream_type_dianke5_open),
    NineOneM(29, R.string.tap_stream_type_91m_install, R.string.tap_stream_type_91m_open),
    DianPing(30, R.string.tap_stream_type_dianping_install, R.string.tap_stream_type_dianping_open),
    AiQiYi(31, R.string.tap_stream_type_aiqiyi_install, R.string.tap_stream_type_aiqiyi_open),
    DingKai(32, R.string.tap_stream_type_dingkai_install, R.string.tap_stream_type_dingkai_open),
    Adsage(33, R.string.tap_stream_type_adsage_install, R.string.tap_stream_type_adsage_open),
    Wandoujiaad(34, R.string.tap_stream_type_wandoujiaad_install, R.string.tap_stream_type_wandoujiaad_open),
    Channel1(35, R.string.tap_stream_type_channel1_install, R.string.tap_stream_type_channel1_open),
    Channel2(36, R.string.tap_stream_type_channel2_install, R.string.tap_stream_type_channel2_open),
    Channel3(37, R.string.tap_stream_type_channel3_install, R.string.tap_stream_type_channel3_open),
    Channel4(38, R.string.tap_stream_type_channel4_install, R.string.tap_stream_type_channel4_open),
    Channel5(39, R.string.tap_stream_type_channel5_install, R.string.tap_stream_type_channel5_open),
    Channel6(40, R.string.tap_stream_type_channel6_install, R.string.tap_stream_type_channel6_open),
    Channel7(41, R.string.tap_stream_type_channel7_install, R.string.tap_stream_type_channel7_open),
    Channel8(42, R.string.tap_stream_type_channel8_install, R.string.tap_stream_type_channel8_open),
    Channel9(43, R.string.tap_stream_type_channel9_install, R.string.tap_stream_type_channel9_open),
    Channel10(44, R.string.tap_stream_type_channel10_install, R.string.tap_stream_type_channel10_open),
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