package com.tradehero.th.models.user.payment;

import com.tradehero.th.api.users.payment.UpdatePayPalEmailDTO;
import com.tradehero.th.network.retrofit.MiddleCallback;
import retrofit.Callback;

/**
 * Created by xavier on 3/7/14.
 */
public class MiddleCallbackUpdatePayPalEmail extends MiddleCallback<UpdatePayPalEmailDTO>
{
    public static final String TAG = MiddleCallbackUpdatePayPalEmail.class.getSimpleName();

    public MiddleCallbackUpdatePayPalEmail(Callback<UpdatePayPalEmailDTO> primaryCallback)
    {
        super(primaryCallback);
    }
}
