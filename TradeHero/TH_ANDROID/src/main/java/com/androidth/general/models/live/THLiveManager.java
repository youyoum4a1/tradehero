package com.androidth.general.models.live;

import com.androidth.general.api.kyc.BrokerApplicationDTO;
import com.androidth.general.api.live1b.AccountBalanceResponseDTO;
import com.androidth.general.api.live1b.PositionsResponseDTO;
import com.androidth.general.api.users.UserLiveAccount;
import com.androidth.general.common.persistence.RealmManager;

/**
 * Created by jeffgan on 14/11/16.
 */

public class THLiveManager {
    private static THLiveManager mInstance = null;

    private BrokerApplicationDTO brokerApplicationDTO;

    private UserLiveAccount userLiveAccount;
    private AccountBalanceResponseDTO accountBalanceResponseDTO;
    private PositionsResponseDTO positionsResponseDTO;

    private THLiveManager(){

    }

    public static THLiveManager getInstance(){
        if(mInstance==null){
            mInstance = new THLiveManager();
        }
        return mInstance;
    }

    public UserLiveAccount getUserLiveAccount() {
        UserLiveAccount userLiveAccount = (UserLiveAccount) RealmManager.getOne(UserLiveAccount.class);
        return userLiveAccount;
    }

    public void setUserLiveAccount(UserLiveAccount userLiveAccount) {
        if(userLiveAccount!=null){
            RealmManager.replaceOldValueWith(userLiveAccount);
        }
    }

    public AccountBalanceResponseDTO getAccountBalanceResponseDTO() {
        AccountBalanceResponseDTO accountBalanceResponseDTO = (AccountBalanceResponseDTO) RealmManager.getOne(AccountBalanceResponseDTO.class);
        return accountBalanceResponseDTO;
    }

    public void setAccountBalanceResponseDTO(AccountBalanceResponseDTO accountBalanceResponseDTO) {
        if(accountBalanceResponseDTO!=null){
            RealmManager.replaceOldValueWith(accountBalanceResponseDTO);
        }
    }

    public PositionsResponseDTO getPositionsResponseDTO() {
        PositionsResponseDTO positionsResponseDTO = (PositionsResponseDTO) RealmManager.getOne(PositionsResponseDTO.class);
        return positionsResponseDTO;
    }

    public void setPositionsResponseDTO(PositionsResponseDTO positionsResponseDTO) {
        if(positionsResponseDTO!=null){
            RealmManager.replaceOldValueWith(positionsResponseDTO);
        }
    }

    public BrokerApplicationDTO getBrokerApplicationDTO() {
        BrokerApplicationDTO brokerApplicationDTO = (BrokerApplicationDTO) RealmManager.getOne(BrokerApplicationDTO.class);
        return brokerApplicationDTO;
    }

    public void setBrokerApplicationDTO(BrokerApplicationDTO brokerApplicationDTO) {
        if(brokerApplicationDTO!=null){
            RealmManager.replaceOldValueWith(brokerApplicationDTO);
        }
    }
}
