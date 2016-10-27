package com.androidth.general.api.live1b;
import com.androidth.general.common.persistence.DTO;

import android.os.Bundle;

import io.realm.RealmObject;

public class AccountBalanceResponseDTO extends RealmObject implements DTO
{
    private static final String BUNDLE_KEY_ACCOUNT_ID = AccountBalanceResponseDTO.class.getName()+ ".account_id";
    private static final String BUNDLE_KEY_CASH_BALANCE = AccountBalanceResponseDTO.class.getName() + ".cash_balance";
    private static final String BUNDLE_KEY_MARGIN_AVAILABLE = AccountBalanceResponseDTO.class.getName()+ ".margin_available";
    private static final String BUNDLE_KEY_CURRENCY = AccountBalanceResponseDTO.class.getName() + ".currency";


//    public String RequestId;
    public String RequestCompletedAtUtcTicks;
    public Double ErrorCode;
//    public String Description;


    public String AccountId;
    public Double CashBalance;
    public Double MarginAvailable;
    public String Currency;

    public AccountBalanceResponseDTO()
    {
        super();
    }

    public AccountBalanceResponseDTO(Bundle bundle)
    {
        this.AccountId = bundle.getString(BUNDLE_KEY_ACCOUNT_ID);
        this.CashBalance = bundle.getDouble(BUNDLE_KEY_CASH_BALANCE);
        this.MarginAvailable = bundle.getDouble(BUNDLE_KEY_MARGIN_AVAILABLE);
        this.Currency = bundle.getString(BUNDLE_KEY_CURRENCY);
    }
}
