package com.tradehero.th.models.share;

import android.app.ProgressDialog;
import android.view.Window;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.fragments.news.NewsDialogFactory;
import com.tradehero.th.fragments.news.NewsDialogLayout;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.persistence.translation.TranslationCache;
import com.tradehero.th.persistence.translation.TranslationKey;
import com.tradehero.th.persistence.translation.TranslationKeyFactory;
import com.tradehero.th.utils.AlertDialogUtil;
import javax.inject.Inject;
import javax.inject.Provider;

public class SocialShareTranslationHelper extends SocialShareHelper
{
    protected final TranslationKeyFactory translationKeyFactory;
    protected final TranslationCache translationCache;

    protected ProgressDialog translateProgressDialog;

    protected DTOCacheNew.Listener<TranslationKey, TranslationResult> translationListener;

    @Inject public SocialShareTranslationHelper(
            CurrentActivityHolder currentActivityHolder,
            NewsDialogFactory newsDialogFactory,
            AlertDialogUtil alertDialogUtil,
            Provider<SocialSharer> socialSharerProvider,
            TranslationKeyFactory translationKeyFactory,
            TranslationCache translationCache)
    {
        super(currentActivityHolder, newsDialogFactory, alertDialogUtil, socialSharerProvider);
        this.translationKeyFactory = translationKeyFactory;
        this.translationCache = translationCache;
    }

    @Override public void onDetach()
    {
        setMenuClickedListener(null);
        dismissTranslateProgress();
        detachTranslationCache();
        super.onDetach();
    }

    protected void dismissTranslateProgress()
    {
        ProgressDialog progressDialogCopy = translateProgressDialog;
        if (progressDialogCopy != null)
        {
            progressDialogCopy.dismiss();
        }
        translateProgressDialog = null;
    }

    protected void detachTranslationCache()
    {
        if (translationListener != null)
        {
            translationCache.unregister(translationListener);
        }
        translationListener = null;
    }

    //<editor-fold desc="Listener Handling">
    @Override public void setMenuClickedListener(SocialShareHelper.OnMenuClickedListener menuClickedListener)
    {
        if (menuClickedListener != null && !(menuClickedListener instanceof NewsDialogLayout.OnMenuClickedListener))
        {
            throw new IllegalArgumentException("Only accepts OnMenuClickedListener");
        }
        super.setMenuClickedListener(menuClickedListener);
    }

    protected void notifyTranslationClicked(AbstractDiscussionCompactDTO toTranslate)
    {
        OnMenuClickedListener listenerCopy = (OnMenuClickedListener) menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onTranslationClicked(toTranslate);
        }
    }

    protected void notifyTranslated(AbstractDiscussionCompactDTO toTranslate, TranslationResult translationResult)
    {
        OnMenuClickedListener listenerCopy = (OnMenuClickedListener) menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onTranslated(toTranslate, translationResult);
        }
    }

    protected void notifyTranslateFailed(AbstractDiscussionCompactDTO toTranslate, Throwable error)
    {
        OnMenuClickedListener listenerCopy = (OnMenuClickedListener) menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onTranslateFailed(toTranslate, error);
        }
    }
    //</editor-fold>

    public String getTargetLanguage()
    {
        return currentActivityHolder.getCurrentActivity().getResources().getConfiguration().locale.getLanguage();
    }

    public boolean canTranslate(AbstractDiscussionCompactDTO discussionToShare)
    {
        return translationKeyFactory.isValidLangCode(discussionToShare.langCode) &&
                !discussionToShare.langCode.equals(getTargetLanguage());
    }

    public void shareOrTranslate(AbstractDiscussionCompactDTO discussionToShare)
    {
        if (false && !canTranslate(discussionToShare))
        {
            share(discussionToShare);
        }
        else
        {
            cancelFormWaiting();
            dismissShareDialog();
            shareDialog = ((NewsDialogFactory) shareDialogFactory).createNewsDialog(
                    currentActivityHolder.getCurrentContext(), discussionToShare,
                    createShareMenuClickedListener());
        }
    }

    @Override protected NewsDialogLayout.OnMenuClickedListener createShareMenuClickedListener()
    {
        return new SocialShareTranslationHelperShareMenuClickedListener();
    }

    protected class SocialShareTranslationHelperShareMenuClickedListener
            extends SocialShareHelperShareMenuClickedListener
            implements NewsDialogLayout.OnMenuClickedListener
    {
        @Override public void onTranslationRequestedClicked(AbstractDiscussionCompactDTO toTranslate)
        {
            dismissShareDialog();
            notifyTranslationClicked(toTranslate);
            translate(toTranslate);
        }
    }

    public void translate(AbstractDiscussionCompactDTO toTranslate)
    {
        dismissTranslateProgress();
        translateProgressDialog = createTranslatingProgress();
        translateProgressDialog.show();

        detachTranslationCache();
        TranslationKey key = translationKeyFactory.createFrom(toTranslate, getTargetLanguage());
        translationListener = createTranslationCacheListener(toTranslate);
        translationCache.register(key, translationListener);
        translationCache.getOrFetchAsync(key);
    }

    public ProgressDialog createTranslatingProgress()
    {
        ProgressDialog progressDialog = new ProgressDialog(currentActivityHolder.getCurrentContext());
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setMessage(
                currentActivityHolder.getCurrentActivity().getString(R.string.translating));
        return progressDialog;
    }

    protected DTOCacheNew.Listener<TranslationKey, TranslationResult> createTranslationCacheListener(AbstractDiscussionCompactDTO toTranslate)
    {
        return new SocialShareTranslationHelperTranslationCacheListener(toTranslate);
    }

    protected class SocialShareTranslationHelperTranslationCacheListener implements DTOCacheNew.Listener<TranslationKey, TranslationResult>
    {
        private AbstractDiscussionCompactDTO toTranslate;

        public SocialShareTranslationHelperTranslationCacheListener(
                AbstractDiscussionCompactDTO toTranslate)
        {
            this.toTranslate = toTranslate;
        }

        @Override public void onDTOReceived(TranslationKey key, TranslationResult value)
        {
            dismissTranslateProgress();
            detachTranslationCache();
            notifyTranslated(toTranslate, value);
            THDialog.showTranslationResult(
                    currentActivityHolder.getCurrentContext(),
                    value.getContent());
        }

        @Override public void onErrorThrown(TranslationKey key, Throwable error)
        {
            dismissTranslateProgress();
            detachTranslationCache();
            notifyTranslateFailed(toTranslate, error);
        }
    }

    public interface OnMenuClickedListener extends SocialShareHelper.OnMenuClickedListener
    {
        void onTranslationClicked(AbstractDiscussionCompactDTO toTranslate);
        void onTranslated(AbstractDiscussionCompactDTO toTranslate, TranslationResult translationResult);
        void onTranslateFailed(AbstractDiscussionCompactDTO toTranslate, Throwable error);
    }
}