package com.tradehero.th.fragments.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.View;
import android.widget.ToggleButton;
import butterknife.ButterKnife;
import butterknife.Bind;
import android.support.annotation.Nullable;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.SocialLinkToggleButton;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserProfileDTOUtil;
import com.tradehero.th.fragments.social.ShareDelegateFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.share.SocialShareHelper;
import com.tradehero.th.models.share.preference.SocialSharePreferenceHelper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.ToastOnErrorAction1;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.rx.view.ViewArrayObservable;
import com.tradehero.th.utils.SocialAlertDialogRxUtil;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class BaseShareableDialogFragment extends BaseDialogFragment
{
    protected ShareDelegateFragment shareDelegateFragment;

    @Nullable protected UserProfileDTO userProfileDTO;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        shareDelegateFragment = new ShareDelegateFragment(this.getParentFragment());
        shareDelegateFragment.onCreate(savedInstanceState);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        shareDelegateFragment.onViewCreated(view, savedInstanceState);
    }

    @Override public void onDestroyView()
    {
        super.onDestroyView();
        shareDelegateFragment.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        shareDelegateFragment.onDestroy();
    }

    @NonNull protected List<SocialNetworkEnum> getEnabledSharePreferences()
    {
        return shareDelegateFragment.getEnabledSharePreferences();
    }
}
