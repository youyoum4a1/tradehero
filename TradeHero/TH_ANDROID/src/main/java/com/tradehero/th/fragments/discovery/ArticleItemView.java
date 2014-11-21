package com.tradehero.th.fragments.discovery;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.article.ArticleInfoDTO;

/**
 * Created by Tho Nguyen on 11/21/2014.
 */
public class ArticleItemView extends LinearLayout
        implements DTOView<ArticleInfoDTO>
{
    @InjectView(R.id.article_title) TextView mArticleTitle;
    @InjectView(R.id.article_description) TextView mArticleDescription;

    private ArticleInfoDTO articleInfoDTO;

    public ArticleItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
    }

    @Override public void display(ArticleInfoDTO articleInfoDTO)
    {
        this.articleInfoDTO = articleInfoDTO;

        displayArticleTitle();
        displayArticlePreview();
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
