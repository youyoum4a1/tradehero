package com.tradehero.th.models.user.payment;

import com.tradehero.th.api.users.payment.UpdateAlipayAccountDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import retrofit.Callback;

/**
 * Created by xavier on 3/7/14.
 */
public class MiddleCallbackUpdateAlipayAccount extends MiddleCallback<UpdateAlipayAccountDTO>
{
    public static final String TAG = MiddleCallbackUpdateAlipayAccount.class.getSimpleName();

    public MiddleCallbackUpdateAlipayAccount(Callback<UpdateAlipayAccountDTO> primaryCallback)
    {
        super(primaryCallback);
    }
}
