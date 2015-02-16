package com.tradehero.th.fragments.discovery;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.article.ArticleInfoDTO;
import com.tradehero.th.inject.HierarchyInjector;
import javax.inject.Inject;

/**
 * Created by Tho Nguyen on 11/21/2014.
 */
public class ArticleItemView extends LinearLayout
        implements DTOView<ArticleInfoDTO>
{
    @InjectView(R.id.article_title) TextView mArticleTitle;
    @InjectView(R.id.article_description) TextView mArticleDescription;
    @InjectView(R.id.article_image) ImageView imageView;
    @Inject Picasso picasso;

    private ArticleInfoDTO articleInfoDTO;

    public ArticleItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        HierarchyInjector.inject(this);
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        picasso.cancelRequest(imageView);
    }

    @Override public void display(ArticleInfoDTO articleInfoDTO)
    {
        this.articleInfoDTO = articleInfoDTO;

        displayArticleImage();
        displayArticleTitle();
        displayArticlePreview();
    }

    private void displayArticleImage()
    {
        picasso.load(articleInfoDTO.image)
                // TODO better placeholder images showing that image is still being loaded
                .placeholder(R.drawable.card_item_top_bg)
                .into(imageView);
    }

    private void displayArticlePreview()
    {
        if (mArticleDescription != null)
        {
            mArticleDescription.setText(articleInfoDTO.previewText);
        }
    }

    private void displayArticleTitle()
    {
        if (mArticleTitle != null)
        {
            mArticleTitle.setText(articleInfoDTO.headline);
        }
    }
}
