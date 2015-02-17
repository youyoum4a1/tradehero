package com.tradehero.th.fragments.news;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Optional;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIntegerId;
import com.tradehero.th.api.security.SecurityIntegerIdList;
import com.tradehero.th.fragments.discussion.DiscussionActionButtonsView;
import com.tradehero.th.fragments.security.SimpleSecurityItemViewAdapter;
import com.tradehero.th.persistence.security.SecurityMultiFetchAssistant;
import com.tradehero.th.rx.EmptyAction1;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class NewsItemViewHolder<DiscussionType extends NewsItemCompactDTO> extends
        NewsItemCompactViewHolder<DiscussionType>
{
    @InjectView(R.id.news_detail_wrapper) @Optional protected BetterViewAnimator mNewsContentWrapper;
    @InjectView(R.id.news_detail_loading) @Optional protected View loadingTextContent;
    @InjectView(R.id.discussion_content) protected TextView textContent;
    @InjectView(R.id.news_detail_title_placeholder) @Optional ImageView mNewsDetailTitlePlaceholder;
    @InjectView(R.id.news_detail_reference) @Optional GridView mNewsDetailReference;
    @InjectView(R.id.news_detail_reference_container) @Optional LinearLayout mNewsDetailReferenceContainer;

    protected SimpleSecurityItemViewAdapter simpleSecurityItemViewAdapter;
    @Inject protected SecurityMultiFetchAssistant multiFetchAssistant;
    protected Subscription multiFetchSubscription;

    //<editor-fold desc="Constructors">
    public NewsItemViewHolder(@NonNull Context context)
    {
        super(context);
    }
    //</editor-fold>

    @Override public void onFinishInflate(@NonNull View view)
    {
        super.onFinishInflate(view);
        if (mNewsDetailReference != null)
        {
            simpleSecurityItemViewAdapter = new SimpleSecurityItemViewAdapter(
                    context, R.layout.trending_security_item);
            mNewsDetailReference.setAdapter(simpleSecurityItemViewAdapter);
        }
    }

    @Override public void onDetachedFromWindow()
    {
        detachMultiFetchAssistant();
        super.onDetachedFromWindow();
    }

    protected void detachMultiFetchAssistant()
    {
        Subscription copy = multiFetchSubscription;
        if (copy != null)
        {
            copy.unsubscribe();
        }
        multiFetchSubscription = null;
    }

    @Override public void linkWith(DiscussionType discussionDTO)
    {
        super.linkWith(discussionDTO);
        fetchMultipleSecurities();
    }

    protected void fetchMultipleSecurities()
    {
        if (mNewsDetailReference != null &&
                discussionDTO instanceof NewsItemDTO)
        {
            List<Integer> securityIds = ((NewsItemDTO) discussionDTO).securityIds;
            if (securityIds != null && !securityIds.isEmpty())
            {
                detachMultiFetchAssistant();
                multiFetchSubscription = multiFetchAssistant.get(new SecurityIntegerIdList(securityIds, 0))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<Map<SecurityIntegerId, SecurityCompactDTO>>()
                                {
                                    @Override public void call(Map<SecurityIntegerId, SecurityCompactDTO> map)
                                    {
                                        NewsItemViewHolder.this.onReceivedSecurities(map);
                                    }
                                },
                                new EmptyAction1<Throwable>());
            }
        }
    }

    public void onReceivedSecurities(Map<SecurityIntegerId, SecurityCompactDTO> map)
    {
        if (mNewsDetailReferenceContainer != null)
        {
            ViewGroup.LayoutParams lp = mNewsDetailReferenceContainer.getLayoutParams();
            //TODO it changes with solution
            lp.width = (int) context.getResources().getDimension(R.dimen.stock_item_width) * map.size();
            mNewsDetailReferenceContainer.setLayoutParams(lp);
        }
        mNewsDetailReference.setNumColumns(map.size());
        simpleSecurityItemViewAdapter.setItems(new ArrayList<>(map.values()));
        simpleSecurityItemViewAdapter.notifyDataSetChanged();
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
            case TRANSLATING:
            case FAILED:
                if (discussionDTO instanceof NewsItemDTO)
                {
                    return ((NewsItemDTO) discussionDTO).text;
                }
                return null;

            case TRANSLATED:
                if (translatedDiscussionDTO instanceof NewsItemDTO)
                {
                    return ((NewsItemDTO) translatedDiscussionDTO).text;
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
        if (loadingTextContent != null && (!(discussionDTO instanceof NewsItemDTO) || ((NewsItemDTO) discussionDTO).text == null))
        {
            return loadingTextContent.getId();
        }
        else if (textContent != null)
        {
            return textContent.getId();
        }
        return 0;
    }

    @Override public void setBackgroundResource(int resId)
    {
        if (mNewsDetailTitlePlaceholder != null)
        {
            mNewsDetailTitlePlaceholder.setBackgroundResource(resId);
        }
    }
    //</editor-fold>

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.news_start_new_discussion) @Optional
    protected void handleStartNewDiscussionClicked(View view)
    {
        userActionBehavior.onNext(new DiscussionActionButtonsView.CommentUserAction());
    }

    @SuppressWarnings("UnusedDeclaration")
    @Optional @OnClick(R.id.news_view_on_web)
    protected void handleOpenOnWebClicked(View view)
    {
        userActionBehavior.onNext(new OpenWebUserAction());
    }

    @Optional @OnItemClick(R.id.news_detail_reference)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        SecurityId securityId = ((SecurityCompactDTO) parent.getItemAtPosition(position)).getSecurityId();
        userActionBehavior.onNext(new SecurityUserAction(securityId));
    }

    public static class OpenWebUserAction implements DiscussionActionButtonsView.UserAction
    {
    }

    public static class SecurityUserAction implements DiscussionActionButtonsView.UserAction
    {
        @NonNull public final SecurityId securityId;

        public SecurityUserAction(@NonNull SecurityId securityId)
        {
            this.securityId = securityId;
        }
    }
}
