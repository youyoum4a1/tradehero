package com.ayondo.academy.api.users;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.ayondo.academy.activities.ActivityBuildTypeUtil;
import com.ayondo.academy.utils.Constants;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

@Singleton public class CurrentUserId extends IntPreference
{
    private static final String PREF_CURRENT_USER_ID_KEY = "PREF_CURRENT_USER_ID_KEY";
    @NonNull private final AccountManager accountManager;
    @NonNull private final BehaviorSubject<Integer> keyObservable;

    //<editor-fold desc="Constructors">
    @Inject public CurrentUserId(@ForUser SharedPreferences preference, @NonNull Context context)
    {
        super(preference, PREF_CURRENT_USER_ID_KEY, 0);
        this.accountManager = AccountManager.get(context);
        keyObservable = BehaviorSubject.create(get());
    }
    //</editor-fold>

    @NonNull public UserBaseKey toUserBaseKey()
    {
        return new UserBaseKey(get());
    }

    @NonNull @Override public Integer get()
    {
        Integer id = super.get();
        if (id == 0)
        {
            Account[] accounts = accountManager.getAccountsByType(Constants.Auth.PARAM_ACCOUNT_TYPE);
            if (accounts != null)
            {
                for (Account account : accounts)
                {
                    accountManager.removeAccount(account, null, null);
                }
            }
        }

        return id;
    }

    @Override public void set(@NonNull Integer value)
    {
        super.set(value);
        keyObservable.onNext(value);
    }

    @NonNull public Observable<Integer> getKeyObservable()
    {
        return keyObservable
                .filter(
                        new Func1<Integer, Boolean>()
                        {
                            @Override public Boolean call(Integer userId)
                            {
                                return userId > 0;
                            }
                        })
                .distinctUntilChanged()
                .doOnNext(
                        new Action1<Integer>()
                        {
                            @Override public void call(Integer userId)
                            {
                                ActivityBuildTypeUtil.setUpCrashReports(new UserBaseKey(userId));
                            }
                        });
    }
}
