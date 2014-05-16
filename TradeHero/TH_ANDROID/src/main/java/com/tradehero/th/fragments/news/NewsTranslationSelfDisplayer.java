package com.tradehero.th.fragments.news;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.Window;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.persistence.translation.TranslationCache;
import com.tradehero.th.persistence.translation.TranslationKey;
import timber.log.Timber;

public class NewsTranslationSelfDisplayer implements DTOCache.Listener<TranslationKey, TranslationResult>
{
    private Context context;
    private final TranslationCache translationCache;
    private ProgressDialog dialog;

    public NewsTranslationSelfDisplayer(
            Context context,
            TranslationCache translationCache,
            ProgressDialog dialog)
    {
        this.context = context;
        this.translationCache = translationCache;
        this.dialog = dialog;
    }

    public DTOCache.GetOrFetchTask<TranslationKey, TranslationResult> launchTranslation(TranslationKey key)
    {
        if (dialog == null)
        {
            dialog = new ProgressDialog(context);
        }
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setMessage(context.getString(R.string.translating));
        dialog.show();

        DTOCache.GetOrFetchTask<TranslationKey, TranslationResult> task = translationCache.getOrFetch(
                key, this);
        task.execute();
        return task;
    }

    @Override public void onDTOReceived(TranslationKey key, TranslationResult value,
            boolean fromCache)
    {
        if (dialog != null && dialog.isShowing())
        {
            dialog.dismiss();
        }

        //TODO
        if (value != null && value.getContent() != null)
        {
            THToast.show("Success");
            THDialog.showTranslationResult(context, value.getContent());
        }
        else
        {
            THToast.show("error");
        }
    }

    @Override public void onErrorThrown(TranslationKey key, Throwable error)
    {
        Timber.e(error, "");
        if (dialog != null)
        {
            dialog.dismiss();
        }
    }
}
