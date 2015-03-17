package com.tradehero.th.fragments.alert;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.service.AlertServiceWrapper;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;

public class AlertCreateFragment extends BaseAlertEditFragment
{
    private static final String BUNDLE_KEY_SECURITY_ID_BUNDLE = BaseAlertEditFragment.class.getName() + ".securityId";

    @Inject protected SecurityCompactCacheRx securityCompactCache;
    @Inject protected Lazy<AlertServiceWrapper> alertServiceWrapper;

    SecurityId securityId;

    public static void putSecurityId(@NonNull Bundle args, @NonNull SecurityId securityId)
    {
        args.putBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE, securityId.getArgs());
    }

    @NonNull public static SecurityId getSecurityId(@NonNull Bundle args)
    {
        return new SecurityId(args.getBundle(BUNDLE_KEY_SECURITY_ID_BUNDLE));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        securityId = getSecurityId(getArguments());
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.stock_alert_add_alert);
    }

    @NonNull @Override protected Observable<AlertDTO> getAlertObservable()
    {
        return securityCompactCache.getOne(securityId)
                .map(new Func1<Pair<SecurityId, SecurityCompactDTO>, AlertDTO>()
                {
                    @Override public AlertDTO call(Pair<SecurityId, SecurityCompactDTO> pair)
                    {
                        return createDummyInitialAlertDTO(pair.second);
                    }
                });
    }

    protected AlertDTO createDummyInitialAlertDTO(@NonNull SecurityCompactDTO securityCompactDTO)
    {
        AlertDTO dummy = new AlertDTO();
        dummy.active = true;
        dummy.priceMovement = 0.1d;
        dummy.upOrDown = true;
        dummy.security = securityCompactDTO;
        return dummy;
    }

    @NonNull protected Observable<AlertCompactDTO> saveAlertProperRx(AlertFormDTO alertFormDTO)
    {
        return alertServiceWrapper.get().createAlertRx(
                currentUserId.toUserBaseKey(),
                alertFormDTO);
    }
}
