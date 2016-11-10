package com.androidth.general.models.realm;

import com.androidth.general.api.live1b.AccountBalanceResponseDTO;
import com.androidth.general.api.live1b.PositionsResponseDTO;
import com.androidth.general.api.users.UserLiveAccount;

import rx.Observable;

public interface DataService
{
    public Observable<PositionsResponseDTO> positionsResponseObservable();
}
