package com.tradehero.th.fragments.discovery.newsfeed;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.CallSuper;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import com.squareup.picasso.Picasso;
import com.tradehero.common.text.ClickableTagProcessor;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedRecyclerAdapter;
import com.tradehero.th.api.discussion.newsfeed.NewsfeedDTO;
import com.tradehero.th.api.discussion.newsfeed.NewsfeedDTOList;
import com.tradehero.th.api.discussion.newsfeed.NewsfeedPagedDTOKey;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.resource.ResourceUtil;
import com.tradehero.th.widget.MarkdownTextView;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;
import rx.Observable;
import rx.subjects.PublishSubject;

public class NewsfeedPaginatedAdapter extends PagedRecyclerAdapter<NewsfeedDisplayDTO>
{
    private static final int VIEW_TYPE_NEWS = 0;
    private static final int VIEW_TYPE_DISCUSSION = 1;
    private static final int VIEW_TYPE_STOCK_TWIT = 2;

    @Inject Picasso picasso;

    private PublishSubject<ClickableTagProcessor.UserAction> userActionSubject;

    public NewsfeedPaginatedAdapter(Context context)
    {
        super(NewsfeedDisplayDTO.class, new NewsfeedDisplayDTOComparator());
        HierarchyInjector.inject(context, this);
        userActionSubject = PublishSubject.create();
    }

    @Override public int getItemViewType(int position)
    {
        NewsfeedDisplayDTO displayDTO = getItem(position);
        if (displayDTO instanceof NewsfeedNewsDisplayDTO)
        {
            return VIEW_TYPE_NEWS;
        }
        else if (displayDTO instanceof NewsfeedDiscussionDisplayDTO)
        {
            return VIEW_TYPE_DISCUSSION;
        }
        else if (displayDTO instanceof NewsfeedStockTwitDisplayDTO)
        {
            return VIEW_TYPE_STOCK_TWIT;
        }
        throw new IllegalStateException("Unhandled Type " + displayDTO.getClass());
    }

    @Override public TypedViewHolder<NewsfeedDisplayDTO> onCreateViewHolder(ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType)
        {
            case VIEW_TYPE_NEWS:
                return new NewsfeedNewsViewHolder(inflater.inflate(R.layout.newsfeed_item_news, parent, false), picasso);
            case VIEW_TYPE_DISCUSSION:
                return new NewsfeedDiscussionViewHolder(inflater.inflate(R.layout.newsfeed_item_discussion, parent, false), picasso);
            case VIEW_TYPE_STOCK_TWIT:
                return new NewsfeedStockTwitViewHolder(inflater.inflate(R.layout.newsfeed_item_stocktwit, parent, false), picasso, userActionSubject);
            default:
                return null;
        }
    }

    public Observable<ClickableTagProcessor.UserAction> getUserActionObservable()
    {
        return userActionSubject.asObservable();
    }

    public static Pair<NewsfeedPagedDTOKey, NewsfeedDisplayDTO.DTOList<NewsfeedDisplayDTO>> createList(
            Pair<NewsfeedPagedDTOKey, NewsfeedDTOList> newsfeedPagedDTOKeyNewsfeedDTOListPair)
    {
        NewsfeedDisplayDTO.DTOList<NewsfeedDisplayDTO> list = new NewsfeedDisplayDTO.DTOList<>(newsfeedPagedDTOKeyNewsfeedDTOListPair.second.size());
        PrettyTime prettyTime = new PrettyTime();
        for (NewsfeedDTO dto : newsfeedPagedDTOKeyNewsfeedDTOListPair.second)
        {
            list.add(NewsfeedDisplayDTO.from(dto, prettyTime));
        }
        return Pair.create(newsfeedPagedDTOKeyNewsfeedDTOListPair.first, list);
    }

    public static class NewsfeedDisplayDTOComparator extends TypedRecyclerComparator<NewsfeedDisplayDTO>
    {
        @Override public int compare(NewsfeedDisplayDTO o1, NewsfeedDisplayDTO o2)
        {
            return o2.createdAtUTC.compareTo(o1.createdAtUTC);
        }

        @Override public boolean areContentsTheSame(NewsfeedDisplayDTO oldItem, NewsfeedDisplayDTO newItem)
        {
            if (oldItem.picture != null && newItem.picture == null) return false;
            if (oldItem.picture == null && newItem.picture != null) return false;
            if (oldItem.picture != null && !oldItem.picture.equals(newItem.picture)) return false;
            if (!oldItem.name.equals(newItem.name)) return false;
            if (!oldItem.getBody().equals(newItem.getBody())) return false;
            if (!oldItem.createdAtUTC.equals(newItem.createdAtUTC)) return false;

            if (oldItem instanceof NewsfeedNewsDisplayDTO && newItem instanceof NewsfeedNewsDisplayDTO)
            {
                NewsfeedNewsDisplayDTO oldNews = (NewsfeedNewsDisplayDTO) oldItem;
                NewsfeedNewsDisplayDTO newNews = (NewsfeedNewsDisplayDTO) newItem;

                if (oldNews.heroImage != null && newNews.heroImage == null) return false;
                if (oldNews.heroImage == null && newNews.heroImage != null) return false;
                if (oldNews.heroImage != null && !oldNews.heroImage.equals(newNews.heroImage)) return false;
                return oldNews.title.equals(newNews.title);
            }
            else if (oldItem instanceof NewsfeedStockTwitDisplayDTO && newItem instanceof NewsfeedStockTwitDisplayDTO)
            {
                NewsfeedStockTwitDisplayDTO oldTwit = (NewsfeedStockTwitDisplayDTO) oldItem;
                NewsfeedStockTwitDisplayDTO newTwit = (NewsfeedStockTwitDisplayDTO) newItem;

                if (oldTwit.heroImage != null && newTwit.heroImage == null) return false;
                if (oldTwit.heroImage == null && newTwit.heroImage != null) return false;
                return !(oldTwit.heroImage != null && !oldTwit.heroImage.equals(newTwit.heroImage));
            }
            else if (oldItem instanceof NewsfeedDiscussionDisplayDTO && newItem instanceof NewsfeedDiscussionDisplayDTO)
            {
                NewsfeedDiscussionDisplayDTO oldDiscussion = (NewsfeedDiscussionDisplayDTO) oldItem;
                NewsfeedDiscussionDisplayDTO newDiscussion = (NewsfeedDiscussionDisplayDTO) newItem;

                if (oldDiscussion.logo != null && newDiscussion.logo == null) return false;
                if (oldDiscussion.logo == null && newDiscussion.logo != null) return false;
                if (oldDiscussion.logo != null && !oldDiscussion.logo.equals(newDiscussion.logo)) return false;
                return oldDiscussion.content.equals(newDiscussion.content);
            }
            return super.areContentsTheSame(oldItem, newItem);
        }

        @Override public boolean areItemsTheSame(NewsfeedDisplayDTO item1, NewsfeedDisplayDTO item2)
        {
            return item1.id == item2.id && item1.getClass().equals(item2.getClass());
        }
    }

    public static class NewsfeedViewHolder extends TypedViewHolder<NewsfeedDisplayDTO>
    {
        protected final Picasso picasso;
        @Bind(R.id.newsfeed_item_avatar) ImageView avatar;
        @Bind(R.id.newsfeed_item_name) TextView name;
        @Bind(R.id.newsfeed_item_time) TextView time;
        @Bind(R.id.newsfeed_item_body) TextView body;

        public NewsfeedViewHolder(View itemView, Picasso picasso)
        {
            super(itemView);
            this.picasso = picasso;
        }

        @CallSuper
        @Override public void onDisplay(NewsfeedDisplayDTO newsfeedDisplayDTO)
        {
            // TODO: temporary hard coded avatar url until server return avatar url
            //picasso.load(newsfeedDisplayDTO.picture)
            //        .placeholder(R.drawable.superman_facebook)
            //        .error(R.drawable.superman_facebook)
            //        .into(avatar);

            int newsAvatar = R.drawable.superman_facebook;

            switch (newsfeedDisplayDTO.name)
            {
                case "Motley Fool":
                    newsAvatar = R.drawable.ic_icn_news_fool;
                    break;
                case "Seeking Alpha":
                    newsAvatar = R.drawable.ic_icn_news_seeking_alpha;
                    break;
                case "Yahoo Finance":
                    newsAvatar = R.drawable.ic_icn_news_yahoo;
                    break;
                case "Google Finance":
                    newsAvatar = R.drawable.ic_icn_news_google;
                    break;
            }

            picasso.load(newsAvatar)
                    .into(avatar);

            name.setText(newsfeedDisplayDTO.name);
            time.setText(newsfeedDisplayDTO.time);
            body.setText(newsfeedDisplayDTO.getBody());
        }
    }

    public static class NewsfeedNewsViewHolder extends NewsfeedViewHolder
    {
        @Bind(R.id.newsfeed_item_hero_image) ImageView heroImg;
        @Bind(R.id.newsfeed_item_title) TextView title;

        public NewsfeedNewsViewHolder(View itemView, Picasso picasso)
        {
            super(itemView, picasso);
        }

        @Override public void onDisplay(NewsfeedDisplayDTO newsfeedDisplayDTO)
        {
            super.onDisplay(newsfeedDisplayDTO);
            NewsfeedNewsDisplayDTO newsDTO = (NewsfeedNewsDisplayDTO) newsfeedDisplayDTO;
            if (newsDTO.heroImage != null)
            {
                heroImg.setVisibility(View.VISIBLE);
                picasso.load(newsDTO.heroImage)
                        .placeholder(R.drawable.img_placeholder_news_21)
                        .error(R.drawable.img_placeholder_news_21)
                        .into(heroImg);
            }
            else
            {
                heroImg.setVisibility(View.GONE);
            }
            title.setText(newsDTO.title);
        }
    }

    public static class NewsfeedDiscussionViewHolder extends NewsfeedViewHolder
    {
        @Bind(R.id.newsfeed_item_discussion_content) TextView content;
        @Bind(R.id.newsfeed_item_discussion_logo) ImageView logo;

        public NewsfeedDiscussionViewHolder(View itemView, Picasso picasso)
        {
            super(itemView, picasso);
        }

        @Override public void onDisplay(NewsfeedDisplayDTO newsfeedDisplayDTO)
        {
            super.onDisplay(newsfeedDisplayDTO);
            //TODO in the future
        }
    }

    public static class NewsfeedStockTwitViewHolder extends NewsfeedViewHolder
    {
        @Bind(R.id.newsfeed_item_hero_image) ImageView heroImg;

        public NewsfeedStockTwitViewHolder(View itemView, Picasso picasso, PublishSubject<ClickableTagProcessor.UserAction> subject)
        {
            super(itemView, picasso);
            MarkdownTextView textView = (MarkdownTextView) body;
            textView.getUserActionObservable().subscribe(subject);
        }

        @Override public void onDisplay(NewsfeedDisplayDTO newsfeedDisplayDTO)
        {
            super.onDisplay(newsfeedDisplayDTO);
            NewsfeedStockTwitDisplayDTO stockTwitDTO = (NewsfeedStockTwitDisplayDTO) newsfeedDisplayDTO;
            if (stockTwitDTO.heroImage != null)
            {
                heroImg.setVisibility(View.VISIBLE);
                picasso.load(stockTwitDTO.heroImage)
                        .placeholder(R.drawable.img_placeholder_news_2)
                        .error(R.drawable.img_placeholder_news_2)
                        .into(heroImg);
            }
            else
            {
                heroImg.setVisibility(View.GONE);
            }
        }
    }
}