package com.tradehero.th.api.users;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.utils.Constants;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.subjects.BehaviorSubject;

@Singleton public class CurrentUserId extends IntPreference
{
    private static final String PREF_CURRENT_USER_ID_KEY = "PREF_CURRENT_USER_ID_KEY";
    @NonNull private final AccountManager accountManager;
    @NonNull private final BehaviorSubject<Integer> keyObservable;

    //<editor-fold desc="Constructors">
    @Inject public CurrentUserId(@ForUser SharedPreferences preference, @NonNull AccountManager accountManager)
    {
        super(preference, PREF_CURRENT_USER_ID_KEY, 0);
        this.accountManager = accountManager;
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
                for (Account account: accounts)
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
        return keyObservable.asObservable();
    }
}
