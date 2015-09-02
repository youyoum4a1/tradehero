package com.tradehero.th.fragments.discovery.newsfeed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedRecyclerAdapter;

public class NewsfeedPaginatedAdapter extends PagedRecyclerAdapter<NewsfeedDisplayDTO>
{
    private static final int VIEW_TYPE_NEWS = 0;
    private static final int VIEW_TYPE_DISCUSSION = 1;
    private static final int VIEW_TYPE_STOCK_TWIT = 2;

    public NewsfeedPaginatedAdapter()
    {
        super(NewsfeedDisplayDTO.class, new NewsfeedDisplayDTOComparator());
    }

    @Override public int getItemViewType(int position)
    {
        NewsfeedDisplayDTO displayDTO = getItem(position);
        if (displayDTO instanceof NewsfeedDisplayNewsDTO)
        {
            return VIEW_TYPE_NEWS;
        }
        else if (displayDTO instanceof NewsfeedDisplayDiscussionDTO)
        {
            return VIEW_TYPE_DISCUSSION;
        }
        else if (displayDTO instanceof NewsfeedDisplayStockTwitDTO)
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
                return new NewsfeedNewsViewHolder(inflater.inflate(R.layout.newsfeed_item_news, parent, false));
            case VIEW_TYPE_DISCUSSION:
                return new NewsfeedDiscussionViewHolder(inflater.inflate(R.layout.newsfeed_item_discussion, parent, false));
            case VIEW_TYPE_STOCK_TWIT:
                return new NewsfeedStockTwitViewHolder(inflater.inflate(R.layout.newsfeed_item_stocktwit, parent, false));
            default:
                return null;
        }
    }

    public static class NewsfeedDisplayDTOComparator extends TypedRecyclerComparator<NewsfeedDisplayDTO>
    {
        @Override public int compare(NewsfeedDisplayDTO o1, NewsfeedDisplayDTO o2)
        {
            return o1.createdAtUTC.compareTo(o2.createdAtUTC);
        }

        @Override public boolean areContentsTheSame(NewsfeedDisplayDTO oldItem, NewsfeedDisplayDTO newItem)
        {
            if (oldItem.picture != null && newItem.picture == null) return false;
            if (oldItem.picture == null && newItem.picture != null) return false;
            if (oldItem.picture != null && !oldItem.picture.equals(newItem.picture)) return false;
            if (!oldItem.name.equals(newItem.name)) return false;
            if (!oldItem.body.equals(newItem.body)) return false;
            if (!oldItem.createdAtUTC.equals(newItem.createdAtUTC)) return false;

            if (oldItem instanceof NewsfeedDisplayNewsDTO && newItem instanceof NewsfeedDisplayNewsDTO)
            {
                NewsfeedDisplayNewsDTO oldNews = (NewsfeedDisplayNewsDTO) oldItem;
                NewsfeedDisplayNewsDTO newNews = (NewsfeedDisplayNewsDTO) newItem;

                if (oldNews.heroImage != null && newNews.heroImage == null) return false;
                if (oldNews.heroImage == null && newNews.heroImage != null) return false;
                if (oldNews.heroImage != null && !oldNews.heroImage.equals(newNews.heroImage)) return false;
                if (!oldNews.title.equals(newNews.title)) return false;
                return true;
            }
            else if (oldItem instanceof NewsfeedDisplayStockTwitDTO && newItem instanceof NewsfeedDisplayStockTwitDTO)
            {
                NewsfeedDisplayStockTwitDTO oldTwit = (NewsfeedDisplayStockTwitDTO) oldItem;
                NewsfeedDisplayStockTwitDTO newTwit = (NewsfeedDisplayStockTwitDTO) newItem;

                if (oldTwit.heroImage != null && newTwit.heroImage == null) return false;
                if (oldTwit.heroImage == null && newTwit.heroImage != null) return false;
                if (oldTwit.heroImage != null && !oldTwit.heroImage.equals(newTwit.heroImage)) return false;
                return true;
            }
            else if (oldItem instanceof NewsfeedDisplayDiscussionDTO && newItem instanceof NewsfeedDisplayDiscussionDTO)
            {
                NewsfeedDisplayDiscussionDTO oldDiscussion = (NewsfeedDisplayDiscussionDTO) oldItem;
                NewsfeedDisplayDiscussionDTO newDiscussion = (NewsfeedDisplayDiscussionDTO) newItem;

                if (oldDiscussion.logo != null && newDiscussion.logo == null) return false;
                if (oldDiscussion.logo == null && newDiscussion.logo != null) return false;
                if (oldDiscussion.logo != null && !oldDiscussion.logo.equals(newDiscussion.logo)) return false;
                if (!oldDiscussion.content.equals(newDiscussion.content)) return false;
                return true;
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
        @Bind(R.id.newsfeed_item_avatar) ImageView avatar;
        @Bind(R.id.newsfeed_item_name) TextView name;
        @Bind(R.id.newsfeed_item_time) TextView time;
        @Bind(R.id.newsfeed_item_body) TextView body;

        public NewsfeedViewHolder(View itemView)
        {
            super(itemView);
        }

        @Override public void display(NewsfeedDisplayDTO newsfeedDisplayDTO)
        {

        }
    }

    public static class NewsfeedNewsViewHolder extends NewsfeedViewHolder
    {
        @Bind(R.id.newsfeed_item_hero_image) ImageView heroImg;
        @Bind(R.id.newsfeed_item_title) TextView title;

        public NewsfeedNewsViewHolder(View itemView)
        {
            super(itemView);
        }

        @Override public void display(NewsfeedDisplayDTO newsfeedDisplayDTO)
        {

        }
    }

    public static class NewsfeedDiscussionViewHolder extends NewsfeedViewHolder
    {
        @Bind(R.id.newsfeed_item_discussion_content) TextView content;
        @Bind(R.id.newsfeed_item_discussion_logo) ImageView logo;

        public NewsfeedDiscussionViewHolder(View itemView)
        {
            super(itemView);
        }

        @Override public void display(NewsfeedDisplayDTO newsfeedDisplayDTO)
        {

        }
    }

    public static class NewsfeedStockTwitViewHolder extends NewsfeedViewHolder
    {
        @Bind(R.id.newsfeed_item_hero_image) ImageView heroImg;

        public NewsfeedStockTwitViewHolder(View itemView)
        {
            super(itemView);
        }

        @Override public void display(NewsfeedDisplayDTO newsfeedDisplayDTO)
        {

        }
    }
}