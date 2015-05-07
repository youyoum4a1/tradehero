package com.tradehero.th.models.push;

import android.content.Context;
import android.telephony.TelephonyManager;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.activities.GuideActivity;
import com.tradehero.th.api.misc.DeviceType;
import com.tradehero.th.base.Application;
import com.tradehero.th.persistence.prefs.DiviceID;
import com.tradehero.th.persistence.prefs.SavedPushDeviceIdentifier;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class DeviceTokenHelper
{
    StringPreference savedPushDeviceIdentifier;
    @NotNull Context context;
    static StringPreference mDeviceIDStringPreference;
    static CurrentActivityHolder currentActivityHolder;

    //<editor-fold desc="Constructors">
    @Inject public DeviceTokenHelper(
            @NotNull @SavedPushDeviceIdentifier StringPreference savedPushDeviceIdentifier,
            @NotNull Context context,
            @DiviceID StringPreference deviceIDStringPreference,
            CurrentActivityHolder currentActivityHolder)
    {
        this.savedPushDeviceIdentifier = savedPushDeviceIdentifier;
        this.context = context;
        this.mDeviceIDStringPreference = deviceIDStringPreference;
        this.currentActivityHolder = currentActivityHolder;
    }
    //</editor-fold>

    /**
     * If locale is Chinese, return the token from baidu,otherwise from urbanairship
     */
    public String getDeviceToken()
    {
        String token = savedPushDeviceIdentifier.get();
        return token;
    }

    public DeviceType getDeviceType()
    {
        return Constants.TAP_STREAM_TYPE.marketSegment.deviceType;
    }

    public static String getIMEI()
    {
        String imei = mDeviceIDStringPreference.get();
        if (imei.isEmpty())
        {
            TelephonyManager tm = (TelephonyManager) Application.context().getSystemService(Context.TELEPHONY_SERVICE);
            String strIMEI = tm.getDeviceId();
            if (StringUtils.isNullOrEmpty(strIMEI) || strIMEI.contains("000000000000000"))
            {
                strIMEI = String.valueOf((int) Math.floor((Math.random() + 1) * GuideActivity.TIMES));
                strIMEI = strIMEI + String.valueOf((int) Math.floor((Math.random() + 1) * GuideActivity.TIMES2));
                mDeviceIDStringPreference.set(strIMEI);
            }
            else
            {
                mDeviceIDStringPreference.set(strIMEI);
            }
            return strIMEI;
        }
        return imei;
    }
}
