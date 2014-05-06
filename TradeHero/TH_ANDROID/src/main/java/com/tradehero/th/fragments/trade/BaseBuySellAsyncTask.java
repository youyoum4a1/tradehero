package com.tradehero.th.fragments.trade;

import android.content.Context;
import android.os.AsyncTask;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.RetrofitError;
import timber.log.Timber;

/**
 * This class is very old already, we are now using MiddleCallback & DTOProcessor to handle retrofit call
 */
@Deprecated
abstract public class BaseBuySellAsyncTask extends AsyncTask<Void, Void, SecurityPositionDetailDTO>
{
    protected static final int CODE_OK = 0;
    protected static final int CODE_BUY_SELL_ORDER_NULL = 1;
    protected static final int CODE_RETROFIT_ERROR = 2;
    protected static final int CODE_RETURNED_NULL = 3;
    protected static final int CODE_UNKNOWN_ERROR = 4;

    protected int errorCode = CODE_OK;
    protected String errorMessage;
    protected final Context context;
    protected final boolean isBuy;
    protected final SecurityId securityId;
    protected final TransactionFormDTO buySellOrder;

    @Inject protected Lazy<SecurityServiceWrapper> securityServiceWrapper;
    @Inject protected Lazy<SecurityPositionDetailCache> securityPositionDetailCache;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected Lazy<UserProfileCache> userProfileCache;
    @Inject protected AlertDialogUtilBuySell alertDialogUtilBuySell;

    public BaseBuySellAsyncTask(final Context context, final boolean isBuy, final SecurityId securityId)
    {
        super();
        this.context = context;
        this.isBuy = isBuy;
        this.securityId = securityId;
        this.buySellOrder = getBuySellOrder();
        checkValid();
        DaggerUtils.inject(this);
    }

    protected void checkValid()
    {
        if (!isValid())
        {
            throw new IllegalStateException("This BaseBuySellAsyncTask is not valid");
        }
    }

    public static boolean isValid(BaseBuySellAsyncTask baseBuySellAsyncTask)
    {
        return baseBuySellAsyncTask.context != null && baseBuySellAsyncTask.securityId != null &&
                baseBuySellAsyncTask.securityId.exchange != null && baseBuySellAsyncTask.securityId.securitySymbol != null;
    }

    public boolean isValid()
    {
        return isValid(this);
    }

    abstract TransactionFormDTO getBuySellOrder();

    @Override protected SecurityPositionDetailDTO doInBackground(Void... voids)
    {
        SecurityPositionDetailDTO returned = null;
        if (buySellOrder == null)
        {
            errorCode = CODE_BUY_SELL_ORDER_NULL;
        }
        else
        {
            try
            {
                returned = effectiveBuySell();
                updateCaches(returned);
            }
            catch (RetrofitError e)
            {
                Timber.e("Failed to buy-sell", e);
                THException wrapped = new THException(e);
                if (wrapped.getCode() == THException.ExceptionCode.UnknownError)
                {
                    errorCode = CODE_UNKNOWN_ERROR;
                    errorMessage = wrapped.getMessage();
                }
                else
                {
                    errorCode = CODE_RETROFIT_ERROR;
                }
            }
        }
        return returned;
    }

    protected SecurityPositionDetailDTO effectiveBuySell() throws RetrofitError
    {
        if (isBuy)
        {
            return securityServiceWrapper.get().buy(securityId, buySellOrder);
        }
        return securityServiceWrapper.get().sell(securityId, buySellOrder);
    }

    protected void updateCaches(SecurityPositionDetailDTO returned)
    {
        if (returned != null)
        {
            securityPositionDetailCache.get().put(securityId, returned);

            if (returned.portfolio != null)
            {
                UserBaseKey userBaseKey = currentUserId.toUserBaseKey();
                UserProfileDTO userProfileDTO = userProfileCache.get().get(userBaseKey);
                if (userProfileDTO != null && (userProfileDTO.portfolio == null || userProfileDTO.portfolio.id == returned.portfolio.id))
                {
                    userProfileDTO.portfolio = returned.portfolio;
                }
            }
        }
        else
        {
            errorCode = CODE_RETURNED_NULL;
        }
    }

    protected void onPostHandleErrorCode()
    {
        switch (errorCode)
        {
            case CODE_BUY_SELL_ORDER_NULL:
                alertDialogUtilBuySell.informBuySellOrderWasNull(context);
                break;

            case CODE_RETROFIT_ERROR:
                alertDialogUtilBuySell.informBuySellOrderFailedRetrofit(context);
                break;

            case CODE_RETURNED_NULL:
                alertDialogUtilBuySell.informBuySellOrderReturnedNull(context);
                break;

            case CODE_UNKNOWN_ERROR:
                alertDialogUtilBuySell.informErrorWithMessage(context, errorMessage);
                break;
        }
    }

    @Override protected void onPostExecute(SecurityPositionDetailDTO securityPositionDetailDTO)
    {
        super.onPostExecute(securityPositionDetailDTO);
        if (isCancelled())
        {
            return;
        }

        onPostHandleErrorCode();
    }
}
