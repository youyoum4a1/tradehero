package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.billing.googleplay.BaseIABLogicHolder;
import com.tradehero.common.billing.googleplay.BaseIABSKUList;
import com.tradehero.common.billing.googleplay.IABPurchaseFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListType;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.utils.ArrayUtils;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.BasePurchaseReporter;
import com.tradehero.th.billing.PurchaseReporter;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListCache;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 12:32 PM To change this template use File | Settings | File Templates. */
public class THIABLogicHolderFull
    extends BaseIABLogicHolder<
            IABSKU,
            THIABProductIdentifierFetcherHolder,
            ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
                    IABSKU,
                    IABException>,
            THIABProductDetail,
            THIABInventoryFetcherHolder,
            BillingInventoryFetcher.OnInventoryFetchedListener<
                    IABSKU,
                    THIABProductDetail,
                    IABException>,
            THIABPurchaseOrder,
            THIABOrderId,
            THIABPurchase,
            THIABPurchaseFetcher,
            IABPurchaseFetcher.OnPurchaseFetchedListener<
                    IABSKU,
                    THIABOrderId,
                    THIABPurchase>,
            THIABPurchaser,
            BillingPurchaser.OnPurchaseFinishedListener<
                    IABSKU,
                    THIABPurchaseOrder,
                    THIABOrderId,
                    THIABPurchase,
                    IABException>,
            THIABPurchaseConsumer,
            THIABPurchaseConsumer.OnIABConsumptionFinishedListener<
                    IABSKU,
                    THIABOrderId,
                    THIABPurchase,
                    IABException>>
    implements THIABLogicHolder
{
    public static final String TAG = THIABLogicHolderFull.class.getSimpleName();

    protected Map<Integer /*requestCode*/, THIABPurchaseReporter> purchaseReporters;
    protected Map<Integer /*requestCode*/, PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, IABException>> purchaseReportedListeners;
    protected Map<Integer /*requestCode*/, WeakReference<BasePurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, IABException>>> parentPurchaseReportedHandlers;

    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    @Inject Lazy<PortfolioCache> portfolioCache;
    @Inject protected Lazy<IABSKUListCache> iabskuListCache;
    @Inject protected Lazy<THIABProductDetailCache> thskuDetailCache;

    public THIABLogicHolderFull(Activity activity)
    {
        super(activity);

        purchaseReporters = new HashMap<>();
        purchaseReportedListeners = new HashMap<>();
        parentPurchaseReportedHandlers = new HashMap<>();

        DaggerUtils.inject(this);
    }

    @Override public void onDestroy()
    {
        for (THIABPurchaseReporter purchaseReporter: purchaseReporters.values())
        {
            if (purchaseReporter != null)
            {
                purchaseReporter.setPurchaseReporterListener(null);
            }
        }
        purchaseReporters.clear();
        purchaseReportedListeners.clear();
        parentPurchaseReportedHandlers.clear();
        super.onDestroy();
    }

    @Override public THIABInventoryFetcherHolder getInventoryFetcherHolder()
    {
        return inventoryFetcherHolder;
    }

    @Override public boolean isUnusedRequestCode(int randomNumber)
    {
        return super.isUnusedRequestCode(randomNumber) &&
                !purchaseReporters.containsKey(randomNumber) &&
                !purchaseReportedListeners.containsKey(randomNumber) &&
                !parentPurchaseReportedHandlers.containsKey(randomNumber);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);

        purchaseReporters.remove(requestCode);
        purchaseReportedListeners.remove(requestCode);
        parentPurchaseReportedHandlers.remove(requestCode);
    }

    @Override public List<THIABProductDetail> getDetailsOfDomain(String domain)
    {
        return ArrayUtils.filter(thskuDetailCache.get().get(getAllSkus()), THIABProductDetail.getPredicateIsOfCertainDomain(domain));
    }

    public PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> getPurchaseReportListener(
            int requestCode)
    {
        WeakReference<PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, IABException>> weakHandler = parentPurchaseReportedHandlers.get(requestCode);
        if (weakHandler != null)
        {
            return weakHandler.get();
        }
        return null;
    }

    protected void registerPurchaseReportedHandler(int requestCode, PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> purchaseReportedHandler)
    {
        parentPurchaseReportedHandlers.put(requestCode, new WeakReference<>(purchaseReportedHandler));
    }

    @Override public int registerPurchaseReportedListener(
            PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> purchaseReportedListener)
    {
        int requestCode = getUnusedRequestCode();
        registerPurchaseReportedHandler(requestCode, purchaseReportedListener);
        return requestCode;
    }

    protected void handlePurchaseReported(int requestCode, THIABPurchase reportedPurchase, UserProfileDTO updatedUserPortfolio)
    {
        THLog.d(TAG, "handlePurchaseReported Purchase info " + reportedPurchase);

        if (updatedUserPortfolio != null)
        {
            userProfileCache.get().put(updatedUserPortfolio.getBaseKey(), updatedUserPortfolio);
        }

        OwnedPortfolioId applicablePortfolioId = reportedPurchase.getApplicableOwnedPortfolioId();
        if (applicablePortfolioId != null)
        {
            portfolioCompactListCache.get().invalidate(applicablePortfolioId.getUserBaseKey());
            portfolioCache.get().invalidate(applicablePortfolioId);
        }

        PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> handler = getPurchaseReportListener(
                requestCode);
        if (handler != null)
        {
            THLog.d(TAG, "handlePurchaseReported passing on the purchase for requestCode " + requestCode);
            handler.onPurchaseReported(requestCode, reportedPurchase, updatedUserPortfolio);
        }
        else
        {
            THLog.d(TAG, "handlePurchaseReported No PurchaseReportedHandler for requestCode " + requestCode);
        }
    }

    protected void handlePurchaseReportFailed(int requestCode, THIABPurchase reportedPurchase, IABException error)
    {
        THLog.e(TAG, "handlePurchaseReportFailed There was an exception during the report", error);
        PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> handler = getPurchaseReportListener(
                requestCode);
        if (handler != null)
        {
            THLog.d(TAG, "handlePurchaseReportFailed passing on the exception for requestCode " + requestCode);
            handler.onPurchaseReportFailed(requestCode, reportedPurchase, error);
        }
        else
        {
            THLog.d(TAG, "handlePurchaseReportFailed No THIABPurchaseHandler for requestCode " + requestCode);
        }
    }

    @Override public void launchReportSequence(int requestCode, THIABPurchase purchase)
    {
        PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, IABException> reportedListener = new PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, IABException>()
        {
            @Override public void onPurchaseReported(int requestCode, THIABPurchase reportedPurchase, UserProfileDTO updatedUserPortfolio)
            {
                handlePurchaseReported(requestCode, reportedPurchase, updatedUserPortfolio);
            }

            @Override public void onPurchaseReportFailed(int requestCode, THIABPurchase reportedPurchase, IABException error)
            {
                handlePurchaseReportFailed(requestCode, reportedPurchase, error);
            }
        };
        purchaseReportedListeners.put(requestCode, reportedListener);
        THIABPurchaseReporter purchaseReporter = new THIABPurchaseReporter();
        purchaseReporter.setPurchaseReporterListener(reportedListener);
        purchaseReporters.put(requestCode, purchaseReporter);
        purchaseReporter.reportPurchase(requestCode, purchase);
    }

    @Override public UserProfileDTO launchReportSequenceSync(THIABPurchase purchase) throws BillingException
    {
        return new THIABPurchaseReporter().reportPurchaseSync(purchase);
    }

    @Override protected BaseIABSKUList<IABSKU> getAllSkus()
    {
        BaseIABSKUList<IABSKU> mixed = iabskuListCache.get().get(IABSKUListType.getInApp());
        BaseIABSKUList<IABSKU> subs = iabskuListCache.get().get(IABSKUListType.getSubs());
        if (subs != null)
        {
            mixed.addAll(subs);
        }
        return mixed;
    }

    @Override protected THIABProductIdentifierFetcherHolder createProductIdentifierFetcherHolder()
    {
        return new THBaseIABProductIdentifierFetcherHolder();
    }

    @Override protected THIABInventoryFetcherHolder createInventoryFetcherHolder()
    {
        return new THBaseIABInventoryFetcherHolder(getActivity());
    }

    @Override protected THIABPurchaseFetcher createPurchaseFetcher()
    {
        return new THIABPurchaseFetcher(getActivity());
    }

    @Override protected THIABPurchaser createPurchaser()
    {
        return new THIABPurchaser(getActivity());
    }

    @Override protected THIABPurchaseConsumer createPurchaseConsumer()
    {
        return new THIABPurchaseConsumer(getActivity());
    }
}
