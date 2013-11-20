package com.tradehero.th.fragments.trade;

import android.content.Context;
import android.os.AsyncTask;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.position.SecurityPositionDetailDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.TransactionFormDTO;
import com.tradehero.th.api.users.CurrentUserBaseKeyHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.SecurityService;
import com.tradehero.th.persistence.position.SecurityPositionDetailCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.RetrofitError;

/** Created with IntelliJ IDEA. User: xavier Date: 11/20/13 Time: 3:23 PM To change this template use File | Settings | File Templates. */
abstract public class BaseBuySellAsyncTask extends AsyncTask<Void, Void, SecurityPositionDetailDTO>
{
    public static final String TAG = BaseBuySellAsyncTask.class.getSimpleName();

    protected static final int CODE_OK = 0;
    protected static final int CODE_BUY_SELL_ORDER_NULL = 1;
    protected static final int CODE_RETROFIT_ERROR = 2;
    protected static final int CODE_RETURNED_NULL = 3;

    protected int errorCode = CODE_OK;
    protected final Context context;
    protected final boolean isBuy;
    protected final SecurityId securityId;
    protected final TransactionFormDTO buySellOrder;
    @Inject protected Lazy<SecurityService> securityService;
    @Inject protected Lazy<SecurityPositionDetailCache> securityPositionDetailCache;
    @Inject protected Lazy<CurrentUserBaseKeyHolder> currentUserBaseKeyHolder;
    @Inject protected Lazy<UserProfileCache> userProfileCache;

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
                THLog.e(TAG, "Failed to buy-sell", e);
                errorCode = CODE_RETROFIT_ERROR;
            }
        }
        return returned;
    }

    protected SecurityPositionDetailDTO effectiveBuySell() throws RetrofitError
    {
        if (isBuy)
        {
            return securityService.get().buy(securityId.exchange, securityId.securitySymbol, buySellOrder);
        }
        return securityService.get().sell(securityId.exchange, securityId.securitySymbol, buySellOrder);
    }

    protected void updateCaches(SecurityPositionDetailDTO returned)
    {
        if (returned != null)
        {
            securityPositionDetailCache.get().put(securityId, returned);

            if (returned.portfolio != null)
            {
                UserBaseKey userBaseKey = currentUserBaseKeyHolder.get().getCurrentUserBaseKey();
                UserProfileDTO userProfileDTO = userProfileCache.get().get(userBaseKey);
                if (userProfileDTO != null && (userProfileDTO.portfolio == null || userProfileDTO.portfolio.id == returned.portfolio.id))
                {
                    userProfileDTO.portfolio = returned.portfolio;
                    userProfileCache.get().put(userBaseKey, userProfileDTO);
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
                AlertDialogUtilBuySell.informBuySellOrderWasNull(context);
                break;

            case CODE_RETROFIT_ERROR:
                AlertDialogUtilBuySell.informBuySellOrderFailedRetrofit(context);
                break;

            case CODE_RETURNED_NULL:
                AlertDialogUtilBuySell.informBuySellOrderReturnedNull(context);
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
