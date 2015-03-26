package com.tradehero.th.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.chinabuild.data.EmptyDiscussionCompactDTO;
import com.tradehero.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.MarkdownTextView;
import dagger.Lazy;
import org.ocpsoft.prettytime.PrettyTime;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by palmer on 14-11-11.
 */
public class TimeLineDetailDiscussSecItem extends BaseAdapter
{

    @Inject Lazy<Picasso> picasso;
    private List<AbstractDiscussionCompactDTO> listData = new ArrayList<AbstractDiscussionCompactDTO>();
    @Inject public Lazy<PrettyTime> prettyTime;
    public Context context;
    public LayoutInflater inflater;
    private TimeLineBaseAdapter.TimeLineOperater listener = null;

    public TimeLineDetailDiscussSecItem(Context context)
    {
        DaggerUtils.inject(this);
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount()
    {
        return listData == null ? 0 : listData.size();
    }

    @Override
    public AbstractDiscussionCompactDTO getItem(int i)
    {
        if (listData == null)
        {
            return null;
        }
        return listData.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup)
    {
        final AbstractDiscussionCompactDTO item = getItem(position);
        Holder holder = null;
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.time_line_discuss_second_item, null);
            holder = new Holder();
            holder.avatar = (ImageView) convertView.findViewById(R.id.imageview_discuss_second_avatar);
            holder.content = (MarkdownTextView) convertView.findViewById(R.id.textview_discuss_second_content);
            holder.moment = (TextView) convertView.findViewById(R.id.textview_discuss_second_time);
            holder.user = (TextView) convertView.findViewById(R.id.textview_discuss_second_user);
            holder.allContent = (LinearLayout) convertView.findViewById(R.id.linearlayout_discuss_second_allcontent);
            holder.rightAnswer = (TextView)convertView.findViewById(R.id.textview_discuss_reward_right_answer);
            convertView.setTag(holder);
        }
        else
        {
            holder = (Holder) convertView.getTag();
        }

        if (item instanceof EmptyDiscussionCompactDTO)
        {
            holder.allContent.setVisibility(View.GONE);
            holder.avatar.setVisibility(View.GONE);
            return convertView;
        }else{
            holder.allContent.setVisibility(View.VISIBLE);
            holder.avatar.setVisibility(View.VISIBLE);
        }

        holder.moment.setText(prettyTime.get().formatUnrounded(item.createdAtUtc));

        if (item instanceof DiscussionDTO)
        {
            DiscussionDTO discussionDTO = ((DiscussionDTO) item);
            if(discussionDTO.isAnswer){
                holder.rightAnswer.setVisibility(View.VISIBLE);
            }else{
                holder.rightAnswer.setVisibility(View.GONE);
            }

            holder.content.setText(discussionDTO.text);
            if (discussionDTO.user != null)
            {

                holder.user.setText(discussionDTO.user.getDisplayName());
                picasso.get()
                        .load(((DiscussionDTO) item).user.picture)
                        .placeholder(R.drawable.avatar_default)
                        .error(R.drawable.avatar_default)
                        .into(holder.avatar);
            }
            else
            {
                holder.user.setText("");
            }
        }

        holder.user.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (item instanceof DiscussionDTO)
                {
                    openUserProfile( ((DiscussionDTO) item).user.id);
                }
            }
        });
        holder.avatar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (item instanceof DiscussionDTO)
                {
                    openUserProfile( ((DiscussionDTO) item).user.id);
                }
            }
        });
        holder.allContent.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (listener != null)
                {
                    listener.OnTimeLineItemClicked(position);
                }
            }
        });
        holder.content.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (view instanceof MarkdownTextView)
                {
                    if (!((MarkdownTextView) view).isClicked)
                    {
                        listener.OnTimeLineItemClicked(position);
                    }
                    ((MarkdownTextView) view).isClicked = false;
                }
            }
        });
        return convertView;
    }

    public void setListData(List<AbstractDiscussionCompactDTO> listCompactDTO)
    {
        if (listCompactDTO != null && listCompactDTO.size() == 0)
        {
            listCompactDTO.add(new EmptyDiscussionCompactDTO());
        }
        listData.clear();
        listData.addAll(listCompactDTO);
        notifyDataSetChanged();
    }

    public void addListData(List<AbstractDiscussionCompactDTO> listCompactDTO){
        listData.addAll(listCompactDTO);
        notifyDataSetChanged();
    }

    public void setListener(TimeLineBaseAdapter.TimeLineOperater listener)
    {
        this.listener = listener;
    }

    public void removeDeletedItem(int discussionId){
        int size = listData.size();
        for(int num=0;num<size; num++){
           if(listData.get(num).id == discussionId){
               listData.remove(num);
               break;
           }
        }

        if (listData != null && listData.size() == 0)
        {
            listData.add(new EmptyDiscussionCompactDTO());
        }
        notifyDataSetChanged();
    }

    public void applyRightAnswer(int discussionId){
        if(listData == null || listData.size() ==0){
            return;
        }
        int size = listData.size();
        for(int num=0;num<size; num++){
            if(listData.get(num).id == discussionId){
                AbstractDiscussionCompactDTO abstractDiscussionCompactDTO = listData.get(num);
                if(abstractDiscussionCompactDTO instanceof DiscussionDTO){
                    DiscussionDTO discussionDTO = (DiscussionDTO)abstractDiscussionCompactDTO;
                    discussionDTO.isAnswer = true;
                }
                break;
            }
        }
        notifyDataSetChanged();
    }

    public class Holder
    {
        public ImageView avatar;
        public TextView user;
        public TextView moment;
        public MarkdownTextView content;
        public LinearLayout allContent;
        public TextView rightAnswer;
    }

    private DashboardNavigator getNavigator()
    {
        return ((DashboardNavigatorActivity) context).getDashboardNavigator();
    }

    public void gotoDashboard(String strFragment, Bundle bundle)
    {
        Bundle args = new Bundle();
        args.putString(DashboardFragment.BUNDLE_OPEN_CLASS_NAME, strFragment);
        args.putAll(bundle);
        ActivityHelper.launchDashboard((Activity) this.context, args);
    }

    private void openUserProfile(int userId)
    {
        if (userId >= 0)
        {
            Bundle bundle = new Bundle();
            bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, userId);
            if (getNavigator() != null)
            {
                getNavigator().pushFragment(UserMainPage.class, bundle);
            }
            else
            {
                gotoDashboard(UserMainPage.class.getName(), bundle);
            }
        }
    }
}
