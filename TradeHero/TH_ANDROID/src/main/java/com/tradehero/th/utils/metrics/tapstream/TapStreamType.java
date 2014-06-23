package com.tradehero.th.utils.metrics.tapstream;

import com.tradehero.th.R;
import com.tradehero.th.models.push.PushConstants;
import com.tradehero.th.utils.metrics.MarketSegment;
import org.jetbrains.annotations.NotNull;

public enum TapStreamType
{
    GooglePlay(0, MarketSegment.ROW, PushConstants.PushProvider.URBAN_AIRSHIP, R.string.tap_stream_type_google_install, R.string.tap_stream_type_google_open),
    Baidu(1, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_baidu_install, R.string.tap_stream_type_baidu_open),
    Tencent(2, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_tencent_install, R.string.tap_stream_type_tencent_open),
    XiaoMi(3, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_xiaomi_install, R.string.tap_stream_type_xiaomi_open),
    NineOne(4, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_91_install, R.string.tap_stream_type_91_open),
    AZouSC(5, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_azhuosc_install, R.string.tap_stream_type_azhuosc_open),
    AZhiSC(6, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_azhisc_install, R.string.tap_stream_type_azhisc_open),
    Lenovo(7, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_lenovo_install, R.string.tap_stream_type_lenovo_open),
    WanDouJia(8, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_wandoujia_install, R.string.tap_stream_type_wandoujia_open),
    QiHu(9, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_360_install, R.string.tap_stream_type_360_open),
    Oppo(10, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_oppo_install, R.string.tap_stream_type_oppo_open),
    JiFeng(11, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_jifeng_install, R.string.tap_stream_type_jifeng_open),
    YYHui(12, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_yyhui_install, R.string.tap_stream_type_yyhui_open),
    YDMM(13, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_ydmm_install, R.string.tap_stream_type_ydmm_open),
    MPTang(14, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_mptang_install, R.string.tap_stream_type_mptang_open),
    DianXin(15, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_dianxin_install, R.string.tap_stream_type_dianxin_open),
    BaiduGuard(16, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_baiduguard_install, R.string.tap_stream_type_baiduguard_open),
    WiFiWanNeng(17, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_wifiwanneng_install, R.string.tap_stream_type_wifiwanneng_open),
    CaiPiao500(18, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_caipiao500_install, R.string.tap_stream_type_caipiao500_open),
    MengTian1(19, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_mengtian1_install, R.string.tap_stream_type_mengtian1_open),
    MengTian2(20, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_mengtian2_install, R.string.tap_stream_type_mengtian2_open),
    SouGou(21, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_sougou_install, R.string.tap_stream_type_sougou_open),
    HuaWei(22, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_huawei_install, R.string.tap_stream_type_huawei_open),
    MeiZu(23, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_meizu_install, R.string.tap_stream_type_meizu_open),
    DianKe1(24, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_dianke1_install, R.string.tap_stream_type_dianke1_open),
    DianKe2(25, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_dianke2_install, R.string.tap_stream_type_dianke2_open),
    DianKe3(26, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_dianke3_install, R.string.tap_stream_type_dianke3_open),
    DianKe4(27, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_dianke4_install, R.string.tap_stream_type_dianke4_open),
    DianKe5(28, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_dianke5_install, R.string.tap_stream_type_dianke5_open),
    NineOneM(29, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_91m_install, R.string.tap_stream_type_91m_open),
    DianPing(30, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_dianping_install, R.string.tap_stream_type_dianping_open),
    AiQiYi(31, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_aiqiyi_install, R.string.tap_stream_type_aiqiyi_open),
    DingKai(32, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_dingkai_install, R.string.tap_stream_type_dingkai_open),
    Adsage(33, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_adsage_install, R.string.tap_stream_type_adsage_open),
    Wandoujiaad(34, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_wandoujiaad_install, R.string.tap_stream_type_wandoujiaad_open),
    Channel1(35, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_channel1_install, R.string.tap_stream_type_channel1_open),
    Channel2(36, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_channel2_install, R.string.tap_stream_type_channel2_open),
    Channel3(37, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_channel3_install, R.string.tap_stream_type_channel3_open),
    Channel4(38, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_channel4_install, R.string.tap_stream_type_channel4_open),
    Channel5(39, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_channel5_install, R.string.tap_stream_type_channel5_open),
    Channel6(40, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_channel6_install, R.string.tap_stream_type_channel6_open),
    Channel7(41, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_channel7_install, R.string.tap_stream_type_channel7_open),
    Channel8(42, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_channel8_install, R.string.tap_stream_type_channel8_open),
    Channel9(43, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_channel9_install, R.string.tap_stream_type_channel9_open),
    Channel10(44, MarketSegment.CHINA, PushConstants.PushProvider.BAIDU, R.string.tap_stream_type_channel10_install, R.string.tap_stream_type_channel10_open),
    ;

    public final int type;
    @NotNull public final MarketSegment marketSegment;
    @NotNull public final PushConstants.PushProvider pushProvider;
    public final int installResId;
    public final int openResId;

    //<editor-fold desc="Constructors">
    TapStreamType(
            int type,
            @NotNull MarketSegment marketSegment,
            @NotNull PushConstants.PushProvider pushProvider,
            int installResId,
            int openResId)
    {
        this.type = type;
        this.marketSegment = marketSegment;
        this.pushProvider = pushProvider;
        this.installResId = installResId;
        this.openResId = openResId;
    }
    //</editor-fold>

    public static TapStreamType fromType(int type)
    {
        int previous = -1;
        for (TapStreamType tapStreamType : values())
        {
            if (tapStreamType.type <= previous)
            {
                throw new IllegalArgumentException("Only increasing values allowed");
            }
            if (tapStreamType.type == type)
            {
                return tapStreamType;
            }
            previous = tapStreamType.type;
        }
        return null;
    }
}