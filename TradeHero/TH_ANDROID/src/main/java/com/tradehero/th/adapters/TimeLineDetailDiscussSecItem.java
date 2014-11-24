package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.fragments.chinabuild.data.EmptyDiscussionCompactDTO;
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
        final AbstractDiscussionCompactDTO item = (AbstractDiscussionCompactDTO) getItem(position);
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
            holder.content.setText(((DiscussionDTO) item).text);
            if (((DiscussionDTO) item).user != null)
            {
                holder.user.setText(((DiscussionDTO) item).user.getDisplayName());
                picasso.get()
                        .load(((DiscussionDTO) item).user.picture)
                        .placeholder(R.drawable.superman_facebook)
                        .error(R.drawable.superman_facebook)
                        .into(holder.avatar);
            }
            else
            {
                holder.user.setText("");
            }
        }
        holder.avatar.setOnClickListener(new View.OnClickListener()
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

    public class Holder
    {
        public ImageView avatar;
        public TextView user;
        public TextView moment;
        public MarkdownTextView content;
        public LinearLayout allContent;
    }
}
