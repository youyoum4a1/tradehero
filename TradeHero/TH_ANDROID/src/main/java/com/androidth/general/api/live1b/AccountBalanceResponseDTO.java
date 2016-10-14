package com.androidth.general.api.live1b;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.BaseResponseDTO;

import android.os.Bundle;
public class AccountBalanceResponseDTO extends BaseMessageResponseDTO implements DTO
{
    private static final String BUNDLE_KEY_ACCOUNT_ID = AccountBalanceResponseDTO.class.getName()+ ".account_id";
    private static final String BUNDLE_KEY_CASH_BALANCE = AccountBalanceResponseDTO.class.getName() + ".cash_balance";
    private static final String BUNDLE_KEY_MARGIN_AVAILABLE = AccountBalanceResponseDTO.class.getName()+ ".margin_available";
    private static final String BUNDLE_KEY_CURRENCY = AccountBalanceResponseDTO.class.getName() + ".currency";



    public String accountId;
    public float cashBalance;
    public float marginAvailable;
    public String currency;

    public AccountBalanceResponseDTO()
    {
        super();
    }

    public AccountBalanceResponseDTO(Bundle bundle)
    {
        this.accountId = bundle.getString(BUNDLE_KEY_ACCOUNT_ID);
        this.cashBalance = bundle.getFloat(BUNDLE_KEY_CASH_BALANCE);
        this.marginAvailable = bundle.getFloat(BUNDLE_KEY_MARGIN_AVAILABLE);
        this.currency = bundle.getString(BUNDLE_KEY_CURRENCY);
    }
}
