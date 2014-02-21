package com.tradehero.th.billing.googleplay;

import android.app.Activity;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.googleplay.BaseIABActor;
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
public class THIABLogicHolder
    extends BaseIABActor<
        IABSKU,
        THIABProductDetail,
        THIABBillingInventoryFetcher,
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
    implements THIABActor
{
    public static final String TAG = THIABLogicHolder.class.getSimpleName();

    protected Map<Integer /*requestCode*/, THIABProductIdentifierFetcher> skuFetchers;
    protected Map<Integer /*requestCode*/, ProductIdentifierFetcher.OnProductIdentifierFetchedListener<IABSKU, IABException>> skuFetchedListeners;
    protected Map<Integer /*requestCode*/, WeakReference<ProductIdentifierFetcher.OnProductIdentifierFetchedListener<IABSKU, IABException>>> parentSkuFetchedListeners;

    protected Map<Integer /*requestCode*/, THIABPurchaseReporter> purchaseReporters;
    protected Map<Integer /*requestCode*/, PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, Exception>> purchaseReportedListeners;
    protected Map<Integer /*requestCode*/, WeakReference<BasePurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, Exception>>> parentPurchaseReportedHandlers;

    @Inject Lazy<UserProfileCache> userProfileCache;
    @Inject Lazy<PortfolioCompactListCache> portfolioCompactListCache;
    @Inject Lazy<PortfolioCache> portfolioCache;
    @Inject protected Lazy<IABSKUListCache> iabskuListCache;
    @Inject protected Lazy<THIABProductDetailCache> thskuDetailCache;

    public THIABLogicHolder(Activity activity)
    {
        super(activity);

        skuFetchers = new HashMap<>();
        skuFetchedListeners = new HashMap<>();
        parentSkuFetchedListeners = new HashMap<>();

        purchaseReporters = new HashMap<>();
        purchaseReportedListeners = new HashMap<>();
        parentPurchaseReportedHandlers = new HashMap<>();

        DaggerUtils.inject(this);
    }

    @Override public void onDestroy()
    {
        for (THIABProductIdentifierFetcher skuFetcher : skuFetchers.values())
        {
            if (skuFetcher != null)
            {
                skuFetcher.setProductIdentifierListener(null);
            }
        }
        skuFetchers.clear();
        skuFetchedListeners.clear();
        parentSkuFetchedListeners.clear();

        for (THIABPurchaseReporter purchaseReporter: purchaseReporters.values())
        {
            if (purchaseReporter != null)
            {
                purchaseReporter.setListener(null);
            }
        }
        purchaseReporters.clear();
        purchaseReportedListeners.clear();
        parentPurchaseReportedHandlers.clear();
        super.onDestroy();
    }

    @Override protected boolean isUnusedRequestCode(int randomNumber)
    {
        return super.isUnusedRequestCode(randomNumber) &&
                !skuFetchers.containsKey(randomNumber) &&
                !skuFetchedListeners.containsKey(randomNumber) &&
                !parentSkuFetchedListeners.containsKey(randomNumber) &&
                !purchaseReporters.containsKey(randomNumber) &&
                !purchaseReportedListeners.containsKey(randomNumber) &&
                !parentPurchaseReportedHandlers.containsKey(randomNumber);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);

        skuFetchers.remove(requestCode);
        skuFetchedListeners.remove(requestCode);
        parentSkuFetchedListeners.remove(requestCode);

        purchaseReporters.remove(requestCode);
        purchaseReportedListeners.remove(requestCode);
        parentPurchaseReportedHandlers.remove(requestCode);
    }

    protected void registerSkuFetchedListener(int requestCode, ProductIdentifierFetcher.OnProductIdentifierFetchedListener<IABSKU, IABException> skuFetchedListener)
    {
        parentSkuFetchedListeners.put(requestCode, new WeakReference<>(skuFetchedListener));
    }

    @Override public int registerSkuFetchedListener(ProductIdentifierFetcher.OnProductIdentifierFetchedListener<IABSKU, IABException> skuFetchedListener)
    {
        int requestCode = getUnusedRequestCode();
        registerSkuFetchedListener(requestCode, skuFetchedListener);
        return requestCode;
    }

    @Override public void launchSkuFetchSequence(int requestCode)
    {
        ProductIdentifierFetcher.OnProductIdentifierFetchedListener<IABSKU, IABException>
                skuFetchedListener = new ProductIdentifierFetcher.OnProductIdentifierFetchedListener<IABSKU, IABException>()
        {
            @Override public void onFetchedProductIdentifiers(int requestCode,
                    Map<String, List<IABSKU>> availableSkus)
            {
                notifySkuFetchedSuccess(requestCode, availableSkus);
            }

            @Override public void onFetchProductIdentifiersFailed(int requestCode,
                    IABException exception)
            {
                notifySkuFetchedFailed(requestCode, exception);
            }
        };
        skuFetchedListeners.put(requestCode, skuFetchedListener);
        THIABProductIdentifierFetcher skuFetcher = new THIABProductIdentifierFetcher();
        skuFetcher.setProductIdentifierListener(skuFetchedListener);
        skuFetchers.put(requestCode, skuFetcher);
        skuFetcher.fetchProductIdentifiers(requestCode);
    }

    @Override public ProductIdentifierFetcher.OnProductIdentifierFetchedListener<IABSKU, IABException> getSkuFetchedListener(int requestCode)
    {
        WeakReference<ProductIdentifierFetcher.OnProductIdentifierFetchedListener<IABSKU, IABException>> weakListener = parentSkuFetchedListeners.get(requestCode);
        if (weakListener == null)
        {
            return null;
        }
        return weakListener.get();
    }

    protected void notifySkuFetchedSuccess(int requestCode, Map<String, List<IABSKU>> availableSkus)
    {
        ProductIdentifierFetcher.OnProductIdentifierFetchedListener<IABSKU, IABException> fetchedListener = getSkuFetchedListener(requestCode);
        if (fetchedListener != null)
        {
            fetchedListener.onFetchedProductIdentifiers(requestCode, availableSkus);
        }
    }

    protected void notifySkuFetchedFailed(int requestCode, IABException exception)
    {
        ProductIdentifierFetcher.OnProductIdentifierFetchedListener<IABSKU, IABException> fetchedListener = getSkuFetchedListener(requestCode);
        if (fetchedListener != null)
        {
            fetchedListener.onFetchProductIdentifiersFailed(requestCode, exception);
        }
    }

    @Override public List<THIABProductDetail> getDetailsOfDomain(String domain)
    {
        return ArrayUtils.filter(thskuDetailCache.get().get(getAllSkus()), THIABProductDetail.getPredicateIsOfCertainDomain(domain));
    }

    public PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, Exception> getPurchaseReportHandler(int requestCode)
    {
        WeakReference<PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, Exception>> weakHandler = parentPurchaseReportedHandlers.get(requestCode);
        if (weakHandler != null)
        {
            return weakHandler.get();
        }
        return null;
    }

    protected void registerPurchaseReportedHandler(int requestCode, PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, Exception> purchaseReportedHandler)
    {
        parentPurchaseReportedHandlers.put(requestCode, new WeakReference<>(purchaseReportedHandler));
    }

    @Override public int registerPurchaseReportedHandler(PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, Exception> purchaseReportedHandler)
    {
        int requestCode = getUnusedRequestCode();
        registerPurchaseReportedHandler(requestCode, purchaseReportedHandler);
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

        PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, Exception> handler = getPurchaseReportHandler(requestCode);
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

    protected void handlePurchaseReportFailed(int requestCode, THIABPurchase reportedPurchase, Exception error)
    {
        THLog.e(TAG, "handlePurchaseReportFailed There was an exception during the report", error);
        PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, Exception> handler = getPurchaseReportHandler(requestCode);
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
        PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, Exception> reportedListener = new PurchaseReporter.OnPurchaseReportedListener<IABSKU, THIABOrderId, THIABPurchase, Exception>()
        {
            @Override public void onPurchaseReported(int requestCode, THIABPurchase reportedPurchase, UserProfileDTO updatedUserPortfolio)
            {
                handlePurchaseReported(requestCode, reportedPurchase, updatedUserPortfolio);
            }

            @Override public void onPurchaseReportFailed(int requestCode, THIABPurchase reportedPurchase, Exception error)
            {
                handlePurchaseReportFailed(requestCode, reportedPurchase, error);
            }
        };
        purchaseReportedListeners.put(requestCode, reportedListener);
        THIABPurchaseReporter purchaseReporter = new THIABPurchaseReporter();
        purchaseReporter.setListener(reportedListener);
        purchaseReporters.put(requestCode, purchaseReporter);
        purchaseReporter.reportPurchase(requestCode, purchase);
    }

    @Override public UserProfileDTO launchReportSequenceSync(THIABPurchase purchase) throws Exception
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

    @Override protected THIABBillingInventoryFetcher createInventoryFetcher()
    {
        return new THIABBillingInventoryFetcher(getActivity());
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
