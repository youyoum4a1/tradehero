package com.androidth.general.billing.googleplay;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.billing.googleplay.IABOrderId;
import com.androidth.general.common.utils.THJsonAdapter;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.billing.THOrderId;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;

public class THIABOrderId
        extends IABOrderId
        implements THOrderId
{
    //<editor-fold desc="Constructors">
    public THIABOrderId(@NonNull String orderId)
    {
        super(orderId);
    }
    //</editor-fold>

    public void setDeveloperPayload(@NonNull OwnedPortfolioId ownedPortfolioId) throws IOException
    {
        developerPayload = THJsonAdapter.getInstance().toStringBody(ownedPortfolioId);
    }

    @Nullable public OwnedPortfolioId getApplicableOwnedPortfolioId()
    {
        if (developerPayload != null)
        {
            try{
                return (OwnedPortfolioId) THJsonAdapter.getInstance()
                        .fromBody(ResponseBody.create(MediaType.parse("text/plain"), developerPayload.toString()));
            }catch (IOException e){
                e.printStackTrace();
                return null;
            }

//            return (OwnedPortfolioId) THJsonAdapter.getInstance().fromBody(developerPayload, OwnedPortfolioId.class);
        }
        return null;
    }
}
