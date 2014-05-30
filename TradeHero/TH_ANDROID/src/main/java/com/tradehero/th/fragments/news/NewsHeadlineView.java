package com.tradehero.th.fragments.news;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.SocialShareResultDTO;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.fragments.discussion.AbstractDiscussionItemView;
import com.tradehero.th.fragments.discussion.NewsDiscussionFragment;
import com.tradehero.th.models.share.SocialShareTranslationHelper;
import com.tradehero.th.persistence.news.NewsItemCompactCacheNew;
import javax.inject.Inject;

public class NewsHeadlineView extends AbstractDiscussionItemView<NewsItemDTOKey>
        implements THDialog.OnDialogItemClickListener
{
    @Inject NewsItemCompactCacheNew newsItemCompactCache;
    @Inject SocialShareTranslationHelper socialShareHelper;

    //<editor-fold desc="Constructors">
    public NewsHeadlineView(Context context)
    {
        super(context);
    }

    public NewsHeadlineView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public NewsHeadlineView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        socialShareHelper.setMenuClickedListener(createSocialShareMenuClickedListener());
        ((NewsItemViewHolder) viewHolder).setMenuClickedListener(createViewHolderMenuClickedListener());
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        socialShareHelper.setMenuClickedListener(createSocialShareMenuClickedListener());
        ((NewsItemViewHolder) viewHolder).setMenuClickedListener(
                createViewHolderMenuClickedListener());
    }

    @Override protected void onDetachedFromWindow()
    {
        socialShareHelper.onDetach();
        super.onDetachedFromWindow();
        ((NewsItemViewHolder) viewHolder).setMenuClickedListener(null);
    }

    @Override protected NewsItemViewHolder createViewHolder()
    {
        return new NewsItemViewHolder();
    }

    //<editor-fold desc="Related to share dialog">
    @Override
    public void onClick(int whichButton)
    {
        switch (whichButton)
        {
            case 0:
                break;
            case 1:
                break;
        }
    }
    //</editor-fold>

    @Override public void display(NewsItemDTOKey discussionKey)
    {
        super.display(discussionKey);
        linkWith(newsItemCompactCache.get(discussionKey), true);
    }

    @Override protected SecurityId getSecurityId()
    {
        throw new IllegalStateException("It has no securityId");
    }

    protected void pushDiscussionFragment()
    {
        if (discussionKey != null)
        {
            Bundle args = new Bundle();
            args.putBundle(NewsDiscussionFragment.DISCUSSION_KEY_BUNDLE_KEY,
                    discussionKey.getArgs());
            getNavigator().pushFragment(NewsDiscussionFragment.class, args);
        }
    }
    @Override
    protected DTOCache.Listener<DiscussionKey, AbstractDiscussionDTO> createDiscussionFetchListener()
    {
        // We are ok with the NewsItemDTO being saved in cache, but we do not want
        // to get it here...
        return null;
    }

    protected NewsItemViewHolder.OnMenuClickedListener createViewHolderMenuClickedListener()
    {
        return new NewsHeadLineViewHolderClickedListener();
    }

    protected class NewsHeadLineViewHolderClickedListener implements NewsItemViewHolder.OnMenuClickedListener
    {
        @Override public void onCommentButtonClicked()
        {
            pushDiscussionFragment();
        }

        @Override public void onTranslationRequested()
        {
            socialShareHelper.translate(abstractDiscussionCompactDTO);
        }

        @Override public void onMoreButtonClicked()
        {
            socialShareHelper.shareOrTranslate(abstractDiscussionCompactDTO);
        }
    }

    protected SocialShareTranslationHelper.OnMenuClickedListener createSocialShareMenuClickedListener()
    {
        return new NewsHeadlineViewShareTranslationMenuClickListener();
    }

    protected class NewsHeadlineViewShareTranslationMenuClickListener implements SocialShareTranslationHelper.OnMenuClickedListener
    {
        @Override public void onCancelClicked()
        {
        }

        @Override public void onShareRequestedClicked(SocialShareFormDTO socialShareFormDTO)
        {
        }

        @Override public void onConnectRequired(SocialShareFormDTO shareFormDTO)
        {
        }

        @Override public void onShared(SocialShareFormDTO shareFormDTO,
                SocialShareResultDTO socialShareResultDTO)
        {
        }

        @Override public void onShareFailed(SocialShareFormDTO shareFormDTO, Throwable throwable)
        {
        }

        @Override public void onTranslationClicked(AbstractDiscussionCompactDTO toTranslate)
        {
        }

        @Override public void onTranslatedOneAttribute(AbstractDiscussionCompactDTO toTranslate,
                TranslationResult translationResult)
        {
            ((NewsItemViewHolder) viewHolder).latestTranslationResult = translationResult;
            ((NewsItemViewHolder) viewHolder).displayTranslateNotice();
        }

        @Override public void onTranslatedAllAtributes(AbstractDiscussionCompactDTO toTranslate,
                AbstractDiscussionCompactDTO translated)
        {
            ((NewsItemViewHolder) viewHolder).translatedAbstractDiscussionCompactDTO = translated;
            if (((NewsItemViewHolder) viewHolder).currentTranslationStatus.equals(NewsItemViewHolder.TranslationStatus.TRANSLATING))
            {
                ((NewsItemViewHolder) viewHolder).currentTranslationStatus = NewsItemViewHolder.TranslationStatus.TRANSLATED;
                ((NewsItemViewHolder) viewHolder).displayTranslatableTexts();
            }
        }

        @Override public void onTranslateFailed(AbstractDiscussionCompactDTO toTranslate,
                Throwable error)
        {
        }
    }
}
