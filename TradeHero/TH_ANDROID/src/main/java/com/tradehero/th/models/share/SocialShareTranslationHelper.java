package com.tradehero.th.models.share;

import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTOFactory;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.fragments.news.NewsDialogFactory;
import com.tradehero.th.fragments.news.NewsDialogLayout;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.persistence.translation.TranslationCache;
import com.tradehero.th.persistence.translation.TranslationKey;
import com.tradehero.th.persistence.translation.TranslationKeyFactory;
import com.tradehero.th.persistence.translation.TranslationKeyList;
import com.tradehero.th.utils.AlertDialogUtil;
import javax.inject.Inject;
import javax.inject.Provider;

public class SocialShareTranslationHelper extends SocialShareHelper
{
    protected final TranslationKeyFactory translationKeyFactory;
    protected final AbstractDiscussionCompactDTOFactory abstractDiscussionCompactDTOFactory;
    protected final TranslationCache translationCache;

    private AbstractDiscussionCompactDTO toTranslate;
    private TranslationKeyList remainingKeys;
    private AbstractDiscussionCompactDTO translated;

    @Inject public SocialShareTranslationHelper(
            CurrentActivityHolder currentActivityHolder,
            NewsDialogFactory newsDialogFactory,
            AlertDialogUtil alertDialogUtil,
            Provider<SocialSharer> socialSharerProvider,
            TranslationKeyFactory translationKeyFactory,
            AbstractDiscussionCompactDTOFactory abstractDiscussionCompactDTOFactory,
            TranslationCache translationCache)
    {
        super(currentActivityHolder, newsDialogFactory, alertDialogUtil, socialSharerProvider);
        this.translationKeyFactory = translationKeyFactory;
        this.abstractDiscussionCompactDTOFactory = abstractDiscussionCompactDTOFactory;
        this.translationCache = translationCache;
    }

    @Override public void onDetach()
    {
        setMenuClickedListener(null);
        super.onDetach();
    }

    //<editor-fold desc="Listener Handling">
    @Override public void setMenuClickedListener(SocialShareHelper.OnMenuClickedListener menuClickedListener)
    {
        if (menuClickedListener != null && !(menuClickedListener instanceof OnMenuClickedListener))
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

    protected void notifyTranslatedOneAttribute(AbstractDiscussionCompactDTO toTranslate,
            TranslationResult translationResult)
    {
        OnMenuClickedListener listenerCopy = (OnMenuClickedListener) menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onTranslatedOneAttribute(toTranslate, translationResult);
        }
    }

    protected void notifyTranslatedAllAtributes(AbstractDiscussionCompactDTO toTranslate, AbstractDiscussionCompactDTO translated)
    {
        OnMenuClickedListener listenerCopy = (OnMenuClickedListener) menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onTranslatedAllAtributes(toTranslate, translated);
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

    public boolean canTranslate(AbstractDiscussionCompactDTO discussionToTranslate)
    {
        return discussionToTranslate != null &&
                translationKeyFactory.isValidLangCode(discussionToTranslate.langCode) &&
                !discussionToTranslate.langCode.equals(getTargetLanguage());
    }

    public void shareOrTranslate(AbstractDiscussionCompactDTO discussionToShare)
    {
        if (canTranslate(discussionToShare))
        {
            cancelFormWaiting();
            dismissShareDialog();
            shareDialog = ((NewsDialogFactory) shareDialogFactory).createNewsDialog(
                    currentActivityHolder.getCurrentContext(), discussionToShare,
                    createShareMenuClickedListener());
        }
        else
        {
            share(discussionToShare);
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
        if (toTranslate != null)
        {
            this.toTranslate = toTranslate;
            this.translated = abstractDiscussionCompactDTOFactory.clone(toTranslate);

            remainingKeys = translationKeyFactory.createFrom(toTranslate, getTargetLanguage());
            if (remainingKeys.size() == 0)
            {
                notifyAllDoneIfPossible();
            }
            else
            {
                for (TranslationKey key : new TranslationKeyList(remainingKeys))
                {
                    translationCache.register(key, createTranslationCacheListener());
                    translationCache.getOrFetchAsync(key);
                }
            }
        }
    }

    public void notifyAllDoneIfPossible()
    {
        if (remainingKeys == null || remainingKeys.size() == 0)
        {
            notifyTranslatedAllAtributes(toTranslate, translated);
        }
    }

    protected DTOCacheNew.Listener<TranslationKey, TranslationResult> createTranslationCacheListener()
    {
        return new SocialShareTranslationHelperTranslationCacheListener();
    }

    protected class SocialShareTranslationHelperTranslationCacheListener implements DTOCacheNew.Listener<TranslationKey, TranslationResult>
    {
        @Override public void onDTOReceived(TranslationKey key, TranslationResult value)
        {
            notifyTranslatedOneAttribute(toTranslate, value);
            abstractDiscussionCompactDTOFactory.populateTranslation(translated, key, value);
            remainingKeys.remove(key);
            notifyAllDoneIfPossible();
        }

        @Override public void onErrorThrown(TranslationKey key, Throwable error)
        {
            notifyTranslateFailed(toTranslate, error);
            remainingKeys.remove(key);
            notifyAllDoneIfPossible();
        }
    }

    public interface OnMenuClickedListener extends SocialShareHelper.OnMenuClickedListener
    {
        void onTranslationClicked(AbstractDiscussionCompactDTO toTranslate);
        void onTranslatedOneAttribute(AbstractDiscussionCompactDTO toTranslate, TranslationResult translationResult);
        void onTranslatedAllAtributes(AbstractDiscussionCompactDTO toTranslate, AbstractDiscussionCompactDTO translated);
        void onTranslateFailed(AbstractDiscussionCompactDTO toTranslate, Throwable error);
    }
}