package com.tradehero.th.fragments.news;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Optional;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewHolder;
import com.tradehero.th.utils.StringUtils;
import java.net.MalformedURLException;
import java.net.URL;
import javax.inject.Inject;

public class NewsItemCompactViewHolder<DiscussionType extends NewsItemCompactDTO>
        extends AbstractDiscussionCompactItemViewHolder<DiscussionType>
{
    @InjectView(R.id.news_title_title) @Optional protected TextView newsTitle;
    @InjectView(R.id.news_icon) ImageView newsIcon;
    @Inject Picasso picasso;

    private static final int SEEKING_ALPHA_ID = 9;
    private static final int MOTLEY_FOOL_ID = 6;

    //<editor-fold desc="Constructors">
    public NewsItemCompactViewHolder(@NonNull Context context)
    {
        super(context);
    }
    //</editor-fold>

    @Override public void onDetachedFromWindow()
    {
        picasso.cancelRequest(newsIcon);
        super.onDetachedFromWindow();
    }

    //<editor-fold desc="Display Methods">
    @Override public void display()
    {
        super.display();
        displayTitle();
        String url = discussionDTO.thumbnail;

        int placeHolderResId = R.drawable.card_item_top_bg;
        if (TextUtils.isEmpty(url)) {
            if (discussionDTO.source.id == SEEKING_ALPHA_ID) {
                placeHolderResId = R.drawable.seeking_alpha;
            } else if (discussionDTO.source.id == MOTLEY_FOOL_ID) {
                placeHolderResId = R.drawable.motley_fool;
            } else
            {
                url = discussionDTO.source.imageUrl;
                placeHolderResId = R.drawable.card_item_top_bg;
            }
        }
        picasso.load(url)
                .placeholder(placeHolderResId)
                .into(newsIcon);
    }

    @Override public void displayTranslatableTexts()
    {
        super.displayTranslatableTexts();
        displayTitle();
    }

    protected void displayTitle()
    {
        if (newsTitle != null)
        {
            newsTitle.setText(getTitleText());
        }
    }

    @Nullable public String getTitleText()
    {
        switch (currentTranslationStatus)
        {
            case ORIGINAL:
            case TRANSLATING:
            case FAILED:
                if (discussionDTO != null)
                {
                    return discussionDTO.title;
                }
                return null;

            case TRANSLATED:
                if (translatedDiscussionDTO != null)
                {
                    return translatedDiscussionDTO.title;
                }
                return null;
        }
        throw new IllegalStateException("Unhandled state " + currentTranslationStatus);
    }
    //</editor-fold>
}
