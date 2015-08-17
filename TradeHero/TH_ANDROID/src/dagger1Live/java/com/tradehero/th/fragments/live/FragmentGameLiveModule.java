package com.tradehero.th.fragments.live;

import android.content.SharedPreferences;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.th.fragments.base.BaseLiveFragmentUtil;
import com.tradehero.th.fragments.base.TrendingLiveFragmentUtil;
import com.tradehero.th.fragments.live.ayondo.FragmentAyondoModule;
import com.tradehero.th.models.sms.ForSMSId;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        includes = {
                FragmentAyondoModule.class,
        },
        injects = {
                LiveCallToActionFragment.class,
                LiveSignUpMainFragment.class,
                TrendingLiveFragmentUtil.class,
                BaseLiveFragmentUtil.class,
                DatePickerDialogFragment.class,
                VerifyPhoneDialogFragment.class
        },
        library = true,
        complete = false
)
public class FragmentGameLiveModule
{
    public static final String TRADEHERO_LIVE_SMS_ID = "TRADEHERO_LIVE_SMS_ID";

    @Provides @Singleton @ForSMSId
    public StringPreference provideSMSId(@ForUser SharedPreferences sharedPreferences)
    {
        return new StringPreference(sharedPreferences, TRADEHERO_LIVE_SMS_ID, "");
    }
}
