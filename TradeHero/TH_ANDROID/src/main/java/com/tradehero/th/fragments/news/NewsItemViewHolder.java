package com.tradehero.th.fragments.news;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewHolder;
import com.tradehero.th.fragments.security.SimpleSecurityItemViewAdapter;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import java.util.ArrayList;
import java.util.Map;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewsItemViewHolder<DiscussionType extends NewsItemDTO> extends
        NewsItemCompactViewHolder<DiscussionType>
{
    @InjectView(R.id.news_detail_wrapper) @Optional protected BetterViewAnimator mNewsContentWrapper;
    @InjectView(R.id.news_detail_loading) @Optional protected View loadingTextContent;
    @InjectView(R.id.discussion_content) protected TextView textContent;
    @InjectView(R.id.news_detail_title_placeholder) @Optional ImageView mNewsDetailTitlePlaceholder;
    @InjectView(R.id.news_detail_reference) @Optional GridView mNewsDetailReference;
    @InjectView(R.id.news_detail_reference_container) @Optional LinearLayout mNewsDetailReferenceContainer;

    @Inject SecurityServiceWrapper securityServiceWrapper;

    protected SimpleSecurityItemViewAdapter simpleSecurityItemViewAdapter;
    protected MiddleCallback<Map<Integer, SecurityCompactDTO>> multipleSecurityMiddleCallback;

    //<editor-fold desc="Constructors">
    public NewsItemViewHolder()
    {
        super();
    }
    //</editor-fold>

    @Override public void onFinishInflate(@NotNull View view)
    {
        super.onFinishInflate(view);
        if (mNewsDetailReference != null)
        {
            simpleSecurityItemViewAdapter = new SimpleSecurityItemViewAdapter(
                    context, LayoutInflater.from(context), R.layout.trending_security_item);
            mNewsDetailReference.setAdapter(simpleSecurityItemViewAdapter);
            mNewsDetailReference.setOnItemClickListener(createSecurityItemClickListener());
        }
    }

    @Override public void onDetachedFromWindow()
    {
        detachMultipleSecurityMiddleCallback();
        super.onDetachedFromWindow();
    }

    protected void detachMultipleSecurityMiddleCallback()
    {
        MiddleCallback<Map<Integer, SecurityCompactDTO>> middleCallback =
                multipleSecurityMiddleCallback;
        if (middleCallback != null)
        {
            middleCallback.setPrimaryCallback(null);
        }
        multipleSecurityMiddleCallback = null;
    }

    @Override public void linkWith(DiscussionType discussionDTO, boolean andDisplay)
    {
        super.linkWith(discussionDTO, andDisplay);
        fetchMultipleSecurities();
        if (andDisplay)
        {
        }
    }

    protected void fetchMultipleSecurities()
    {
        if (mNewsDetailReference != null && discussionDTO.securityIds != null && !discussionDTO.securityIds.isEmpty())
        {
            detachMultipleSecurityMiddleCallback();
            multipleSecurityMiddleCallback = securityServiceWrapper.getMultipleSecurities(
                    discussionDTO.securityIds,
                    createMultiSecurityCallback());
        }
    }

    @Override public boolean isAutoTranslate()
    {
        return true;
    }

    //<editor-fold desc="Display Methods">
    @Override public void display()
    {
        super.display();
        displayContentWrapper();
        displayMoreButton();
    }

    protected void displayMoreButton()
    {
        if (discussionActionButtonsView != null)
        {
            discussionActionButtonsView.setShowMore(false);
        }
    }

    @Override protected String getTimeToDisplay()
    {
        if (discussionDTO != null && discussionDTO.createdAtUtc != null)
        {
            return prettyTime.format(discussionDTO.createdAtUtc);
        }
        return null;
    }

    @Override public void displayTranslatableTexts()
    {
        super.displayTranslatableTexts();
        displayText();
    }

    protected void displayText()
    {
        if (textContent != null)
        {
            textContent.setText(getText());
        }
    }

    protected String getText()
    {
        switch (currentTranslationStatus)
        {
            case ORIGINAL:
                if (discussionDTO != null)
                {
                    return discussionDTO.text;
                }
                return null;

            case TRANSLATING:
            case TRANSLATED:
                if (translatedDiscussionDTO != null)
                {
                    return translatedDiscussionDTO.text;
                }
                return null;

        }
        throw new IllegalStateException("Unhandled state " + currentTranslationStatus);
    }

    protected void displayContentWrapper()
    {
        if (mNewsContentWrapper != null)
        {
            mNewsContentWrapper.setDisplayedChildByLayoutId(getContentViewIdToShow());
        }
    }

    protected int getContentViewIdToShow()
    {
        if (loadingTextContent != null && (discussionDTO == null || discussionDTO.text == null))
        {
            return loadingTextContent.getId();
        }
        else if (textContent != null)
        {
            return textContent.getId();
        }
        return 0;
    }

    public void setTitleBackground(int resId)
    {
        if (mNewsDetailTitlePlaceholder != null)
        {
            mNewsDetailTitlePlaceholder.setBackgroundResource(resId);
        }
    }
    //</editor-fold>

    @OnClick(R.id.news_start_new_discussion) @Optional
    protected void handleStartNewDiscussionClicked(View view)
    {
        notifyCommentButtonClicked();
    }

    @Optional @OnClick(R.id.news_view_on_web)
    protected void handleOpenOnWebClicked(View view)
    {
        notifyOpenOnWebClicked();
    }

    protected void notifyOpenOnWebClicked()
    {
        AbstractDiscussionCompactItemViewHolder.OnMenuClickedListener menuClickedListenerCopy = menuClickedListener;
        if (menuClickedListenerCopy instanceof OnMenuClickedListener)
        {
            ((OnMenuClickedListener) menuClickedListenerCopy).onOpenOnWebClicked();
        }
    }

    protected void notifySecurityClicked(SecurityId securityId)
    {
        AbstractDiscussionCompactItemViewHolder.OnMenuClickedListener menuClickedListenerCopy = menuClickedListener;
        if (menuClickedListenerCopy instanceof OnMenuClickedListener)
        {
            ((OnMenuClickedListener) menuClickedListenerCopy).onSecurityClicked(securityId);
        }
    }

    protected Callback<Map<Integer, SecurityCompactDTO>> createMultiSecurityCallback()
    {
        return new NewsItemViewHolderMultiSecurityCallback();
    }

    protected class NewsItemViewHolderMultiSecurityCallback implements Callback<Map<Integer, SecurityCompactDTO>>
    {
        @Override public void success(Map<Integer, SecurityCompactDTO> securityCompactDTOList,
                Response response)
        {
            if (mNewsDetailReferenceContainer != null)
            {
                ViewGroup.LayoutParams lp = mNewsDetailReferenceContainer.getLayoutParams();
                //TODO it changes with solution
                lp.width = (int) context.getResources().getDimension(R.dimen.stock_item_width) * securityCompactDTOList.size();
                mNewsDetailReferenceContainer.setLayoutParams(lp);
            }
            mNewsDetailReference.setNumColumns(securityCompactDTOList.size());
            simpleSecurityItemViewAdapter.setItems(new ArrayList<>(securityCompactDTOList.values()));
            simpleSecurityItemViewAdapter.notifyDataSetChanged();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
        }
    }

    protected AdapterView.OnItemClickListener createSecurityItemClickListener()
    {
        return new NewsItemViewHolderSecurityItemClickListener();
    }

    protected class NewsItemViewHolderSecurityItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            notifySecurityClicked(((SecurityCompactDTO) parent.getItemAtPosition(position)).getSecurityId());
        }
    }

    public static interface OnMenuClickedListener extends NewsItemCompactViewHolder.OnMenuClickedListener
    {
        void onOpenOnWebClicked();
        void onSecurityClicked(SecurityId securityId);
    }
}
