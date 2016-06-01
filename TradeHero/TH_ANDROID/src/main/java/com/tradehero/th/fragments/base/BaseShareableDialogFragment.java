package com.ayondo.academy.fragments.base;

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
import com.ayondo.academy.R;
import com.ayondo.academy.api.social.SocialNetworkEnum;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.api.users.UserProfileDTOUtil;
import com.ayondo.academy.fragments.social.ShareDelegateFragment;
import com.ayondo.academy.misc.exception.THException;
import com.ayondo.academy.models.share.SocialShareHelper;
import com.ayondo.academy.models.share.preference.SocialSharePreferenceHelper;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;
import com.ayondo.academy.rx.EmptyAction1;
import com.ayondo.academy.rx.ToastOnErrorAction1;
import com.ayondo.academy.rx.dialog.OnDialogClickEvent;
import com.ayondo.academy.rx.view.ViewArrayObservable;
import com.ayondo.academy.utils.SocialAlertDialogRxUtil;
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

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        shareDelegateFragment = new ShareDelegateFragment(this);
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
