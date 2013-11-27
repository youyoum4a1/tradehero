package com.tradehero.th.billing.googleplay;

import android.content.Context;
import android.content.Intent;
import com.tradehero.common.billing.googleplay.BaseIABPurchase;
import com.tradehero.common.billing.googleplay.GooglePlayPurchaseDTO;
import com.tradehero.th.utils.ExceptionUtils;
import com.tradehero.th.utils.StringUtils;
import com.tradehero.th.utils.VersionUtils;
import java.util.ArrayList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 10:55 AM To change this template use File | Settings | File Templates. */
public class GooglePlayUtils
{
    public static final String TAG = GooglePlayUtils.class.getSimpleName();

    public static Intent getSupportPurchaseReportEmailIntent(Context context, BaseIABPurchase purchase)
    {
        String deviceDetails = "\n\nThere appears to have been a problem reporting my purchase to TradeHero server\n\n-----\n" +
                StringUtils.join("\n", getPurchaseReportStrings(context, purchase)) +
                "\n-----\n";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"support@tradehero.mobi"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "TradeHero - GooglePlay Support");
        intent.putExtra(Intent.EXTRA_TEXT, deviceDetails);
        return intent;
    }

    public static List<String> getPurchaseReportStrings(Context context, BaseIABPurchase purchase)
    {
        List<String> reported = new ArrayList<>();

        if (purchase != null)
        {
            GooglePlayPurchaseDTO googlePlayPurchaseDTO = purchase.getGooglePlayPurchaseDTO();
            reported.add("data:");
            reported.add(googlePlayPurchaseDTO.google_play_data);
            reported.add("signature:");
            reported.add(googlePlayPurchaseDTO.google_play_signature);
            reported.add("-----");
        }
        reported.addAll(VersionUtils.getSupportEmailTraceParameters(context, true));

        return reported;
    }

    public static Intent getSupportPurchaseConsumeEmailIntent(Context context, Exception exception)
    {
        String deviceDetails = "\n\nThere appears to have been a problem consuming my purchase with GooglePlay\n\n-----\n" +
                StringUtils.join("\n", getExceptionStrings(context, exception)) +
                "\n-----\n";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"support@tradehero.mobi"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "TradeHero - GooglePlay Support");
        intent.putExtra(Intent.EXTRA_TEXT, deviceDetails);
        return intent;
    }

    public static Intent getSupportPurchaseRestoreEmailIntent(Context context, Exception exception)
    {
        String deviceDetails = "\n\nThere appears to have been a problem restoring my purchase with GooglePlay\n\n-----\n" +
                StringUtils.join("\n", getExceptionStrings(context, exception)) +
                "\n-----\n";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"support@tradehero.mobi"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "TradeHero - GooglePlay Support");
        intent.putExtra(Intent.EXTRA_TEXT, deviceDetails);
        return intent;
    }

    public static List<String> getExceptionStrings(Context context, Exception exception)
    {
        List<String> reported = new ArrayList<>();

        if (exception != null)
        {
            reported.addAll(ExceptionUtils.getElements(exception));
            reported.add("-----");
        }
        reported.addAll(VersionUtils.getSupportEmailTraceParameters(context, true));

        return reported;
    }
}
