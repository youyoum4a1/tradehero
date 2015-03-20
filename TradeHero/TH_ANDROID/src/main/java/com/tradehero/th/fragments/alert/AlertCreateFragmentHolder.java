package com.tradehero.th.fragments.alert;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.th.api.alert.AlertCompactDTO;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertFormDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.models.alert.SecurityAlertCountingHelper;
import com.tradehero.th.network.service.AlertServiceWrapper;
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import dagger.Lazy;
import rx.Observable;
import rx.functions.Func1;

public class AlertCreateFragmentHolder extends BaseAlertEditFragmentHolder
{
    @NonNull protected final SecurityCompactCacheRx securityCompactCache;
    @NonNull protected final Lazy<AlertServiceWrapper> alertServiceWrapper;
    @NonNull protected final SecurityId securityId;

    //<editor-fold desc="Constructors">
    public AlertCreateFragmentHolder(
            @NonNull Activity activity,
            @NonNull Resources resources,
            @NonNull CurrentUserId currentUserId,
            @NonNull SecurityAlertCountingHelper securityAlertCountingHelper,
            @NonNull QuoteServiceWrapper quoteServiceWrapper,
            @NonNull SecurityCompactCacheRx securityCompactCache,
            @NonNull Lazy<AlertServiceWrapper> alertServiceWrapper,
            @NonNull SecurityId securityId)
    {
        super(activity, resources, currentUserId, securityAlertCountingHelper, quoteServiceWrapper);
        this.securityCompactCache = securityCompactCache;
        this.alertServiceWrapper = alertServiceWrapper;
        this.securityId = securityId;
    }
    //</editor-fold>

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
