package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABPurchaseOrder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import java.io.IOException;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 11/19/13 Time: 10:45 AM To change this template use File | Settings | File Templates. */
public class THIABPurchaseOrder implements IABPurchaseOrder<IABSKU>
{
    public static final String TAG = THIABPurchaseOrder.class.getSimpleName();

    private IABSKU sku;
    private int quantity;
    private OwnedPortfolioId developerPayload;

    //<editor-fold desc="Constructors">
    public THIABPurchaseOrder (IABSKU sku)
    {
        this(sku, 1);
    }

    public THIABPurchaseOrder (IABSKU sku, int quantity)
    {
        this(sku, quantity, null);
    }

    public THIABPurchaseOrder (IABSKU sku, OwnedPortfolioId developerPayload)
    {
        this(sku, 1, developerPayload);
        Timber.d("THIABPurchaseOrder with %s", developerPayload);
    }

    public THIABPurchaseOrder (IABSKU sku, int quantity, OwnedPortfolioId developerPayload)
    {
        this.sku = sku;
        this.quantity = quantity;
        this.developerPayload = developerPayload;
    }
    //</editor-fold>

    @Override public IABSKU getProductIdentifier()
    {
        return this.sku;
    }

    @Override public int getQuantity()
    {
        return this.quantity;
    }

    @Override public String getDeveloperPayload()
    {
        try
        {
            return THJsonAdapter.getInstance().toStringBody(this.developerPayload);
        }
        catch (IOException e)
        {
            Timber.e("Failed to stringify developerPayload", e);
        }
        return "";
    }

    @Override public void setDeveloperPayload(String developerPayload)
    {
        if (developerPayload != null)
        {
            this.developerPayload = (OwnedPortfolioId) THJsonAdapter.getInstance().fromBody(developerPayload, OwnedPortfolioId.class);
        }
        else
        {
            this.developerPayload = null;
        }
    }
}
