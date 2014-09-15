package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.fragments.chinabuild.data.CompetitionDataItem;
import com.tradehero.th.fragments.chinabuild.data.CompetitionHeadItem;
import com.tradehero.th.fragments.chinabuild.data.CompetitionInterface;
import com.tradehero.th.fragments.chinabuild.data.PositionHeadItem;
import com.tradehero.th.fragments.chinabuild.data.PositionInterface;
import com.tradehero.th.fragments.chinabuild.data.SecurityPositionItem;
import com.tradehero.th.fragments.chinabuild.data.UserCompetitionDTO;
import com.tradehero.th.fragments.chinabuild.data.UserCompetitionDTOList;
import com.tradehero.th.fragments.chinabuild.data.WatchPositionItem;
import com.tradehero.th.fragments.chinabuild.fragment.competition.CompetitionUtils;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.utils.ColorUtils;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th2.R;
import dagger.Lazy;
import java.util.ArrayList;
import javax.inject.Inject;

//比赛列表Adapter 包括 所有比赛，我的比赛
public class CompetitionListAdapter extends BaseAdapter
{
    @Inject Lazy<Picasso> picasso;

    private int competitionPageType = 0;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<CompetitionInterface> listData;
    private ArrayList<CompetitionDataItem> OfficalCompetitionDtoList;//官方比赛
    private ArrayList<CompetitionDataItem> UserCompetitionDtoList;//用户自建比赛
    private ArrayList<CompetitionDataItem> MyCompetitionDtoList;//我参加的比赛
    private ArrayList<CompetitionDataItem> SearchCompetitionDtoList;//搜索出来的比赛

    public CompetitionListAdapter(Context context, int pageType)
    {
        DaggerUtils.inject(this);
        this.context = context;
        competitionPageType = pageType;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOfficalCompetitionDtoList(ArrayList<UserCompetitionDTO> list)
    {
        ArrayList<CompetitionDataItem> listItem = new ArrayList<CompetitionDataItem>();
        if (list != null)
        {
            int sizeList = list.size();
            for (int i = 0; i < sizeList; i++)
            {
                listItem.add(new CompetitionDataItem(list.get(i)));
            }
        }
        OfficalCompetitionDtoList = listItem;
        doRefreshData();
    }

    public void setUserCompetitionDtoList(ArrayList<UserCompetitionDTO> list)
    {
        ArrayList<CompetitionDataItem> listItem = new ArrayList<CompetitionDataItem>();
        if (list != null)
        {
            int sizeList = list.size();
            for (int i = 0; i < sizeList; i++)
            {
                listItem.add(new CompetitionDataItem(list.get(i)));
            }
        }
        UserCompetitionDtoList = listItem;
        doRefreshData();
    }

    public void addUserCompetitionDtoList(ArrayList<UserCompetitionDTO> list)
    {
        ArrayList<CompetitionDataItem> listItem = new ArrayList<CompetitionDataItem>();
        if (list != null)
        {
            int sizeList = list.size();
            for (int i = 0; i < sizeList; i++)
            {
                listItem.add(new CompetitionDataItem(list.get(i)));
            }
        }
        UserCompetitionDtoList.addAll(listItem);
        doRefreshData();
    }



    public void setMyCompetitionDtoList(ArrayList<UserCompetitionDTO> list)
    {
        ArrayList<CompetitionDataItem> listItem = new ArrayList<CompetitionDataItem>();
        if (list != null)
        {
            int sizeList = list.size();
            for (int i = 0; i < sizeList; i++)
            {
                listItem.add(new CompetitionDataItem(list.get(i)));
            }
        }
        MyCompetitionDtoList = listItem;
        doRefreshData();
    }

    public void addMyCompetitionDtoList(ArrayList<UserCompetitionDTO> list)
    {
        ArrayList<CompetitionDataItem> listItem = new ArrayList<CompetitionDataItem>();
        if (list != null)
        {
            int sizeList = list.size();
            for (int i = 0; i < sizeList; i++)
            {
                listItem.add(new CompetitionDataItem(list.get(i)));
            }
        }
        MyCompetitionDtoList.addAll(listItem);
        doRefreshData();
    }

    public void setSearchCompetitionDtoList(ArrayList<UserCompetitionDTO> list)
    {
        ArrayList<CompetitionDataItem> listItem = new ArrayList<CompetitionDataItem>();
        if (list != null)
        {
            int sizeList = list.size();
            for (int i = 0; i < sizeList; i++)
            {
                listItem.add(new CompetitionDataItem(list.get(i)));
            }
        }
        SearchCompetitionDtoList = listItem;
        doRefreshData();
    }

    private void doRefreshData()
    {
        listData = new ArrayList<CompetitionInterface>();
        if (competitionPageType == CompetitionUtils.COMPETITION_PAGE_MINE)
        {
            listData.addAll(MyCompetitionDtoList);
        }
        else if (competitionPageType == CompetitionUtils.COMPETITION_PAGE_ALL)
        {
            if (getOfficalCompetitionCount() > 0)
            {
                listData.add(new CompetitionHeadItem(getHeadStrOfOfficalCompetition()));
                listData.addAll(OfficalCompetitionDtoList);
            }
            if (getUserCompetitionCount() > 0)
            {
                listData.add(new CompetitionHeadItem(getHeadStrOfUserCompetition()));
                listData.addAll(UserCompetitionDtoList);
            }
        }
        else if (competitionPageType == CompetitionUtils.COMPETITION_PAGE_SEARCH)
        {
            listData.addAll(SearchCompetitionDtoList);
        }
        notifyDataSetChanged();
    }

    public String getHeadStrOfOfficalCompetition()
    {
        return "官方比赛";
    }

    public String getHeadStrOfUserCompetition()
    {
        return "热点比赛";
    }

    public int getOfficalCompetitionCount()
    {
        return OfficalCompetitionDtoList == null ? 0 : OfficalCompetitionDtoList.size();
    }

    public int getUserCompetitionCount()
    {
        return UserCompetitionDtoList == null ? 0 : UserCompetitionDtoList.size();
    }

    @Override public int getCount()
    {
        return listData == null ? 0 : listData.size();
    }

    @Override public CompetitionInterface getItem(int i)
    {
        return listData.get(i);
    }

    @Override public long getItemId(int i)
    {
        return i;
    }

    @Override public boolean areAllItemsEnabled()
    {
        return false;
    }

    @Override public boolean isEnabled(int position)
    {
        return !(getItem(position) instanceof CompetitionHeadItem);
    }

    @Override public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        CompetitionInterface data = getItem(position);
        if (data != null)
        {
            ViewHolder holder = null;
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.ugc_competition_list_item, viewGroup, false);
                holder = new ViewHolder();
                holder.llCompetitionHead = (LinearLayout) convertView.findViewById(R.id.llCompetitionHead);
                holder.tvCompetitionHead = (TextView) convertView.findViewById(R.id.tvCompetitionHead);
                holder.rlCompetitionData = (RelativeLayout) convertView.findViewById(R.id.rlCompetitionData);
                holder.rlCompetitionDataExtroMine = (RelativeLayout) convertView.findViewById(R.id.rlCompetitionDataExtroMine);
                holder.rlCompetitionDataExtroOffical = (RelativeLayout) convertView.findViewById(R.id.rlCompetitionDataExtroOffical);
                holder.imgCompetitionHead = (ImageView) convertView.findViewById(R.id.imgCompetitionHead);
                holder.tvCompetitionName = (TextView) convertView.findViewById(R.id.tvCompetitionName);
                holder.tvCompetitionJoinCount = (TextView) convertView.findViewById(R.id.tvCompetitionJoinCount);
                holder.tvCompetitionPeriod = (TextView) convertView.findViewById(R.id.tvCompetitionPeriod);
                holder.tvCompetitionIntro = (TextView) convertView.findViewById(R.id.tvCompetitionIntro);
                holder.tvCompetitionROIValue = (TextView) convertView.findViewById(R.id.tvCompetitionROIValue);
                holder.tvCompetitionJumpValue = (TextView) convertView.findViewById(R.id.tvCompetitionJumpValue);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            if (data instanceof CompetitionHeadItem)
            {
                holder.llCompetitionHead.setVisibility(View.VISIBLE);
                holder.rlCompetitionData.setVisibility(View.GONE);
                holder.tvCompetitionHead.setText(((CompetitionHeadItem) data).strHead);
            }
            else if (data instanceof CompetitionDataItem)
            {
                holder.llCompetitionHead.setVisibility(View.GONE);
                holder.rlCompetitionData.setVisibility(View.VISIBLE);
                holder.tvCompetitionName.setText(((CompetitionDataItem) data).userCompetitionDTO.name);
                holder.tvCompetitionJoinCount.setText(((CompetitionDataItem) data).userCompetitionDTO.getUserCounter());
                holder.tvCompetitionPeriod.setText(((CompetitionDataItem) data).userCompetitionDTO.getDisplayDatePeriod());
                picasso.get()
                        .load(((CompetitionDataItem) data).userCompetitionDTO.iconUrl)
                        .placeholder(R.drawable.superman_facebook)
                        .error(R.drawable.superman_facebook)
                        .into(holder.imgCompetitionHead);

                if (competitionPageType == CompetitionUtils.COMPETITION_PAGE_ALL
                        || competitionPageType == CompetitionUtils.COMPETITION_PAGE_SEARCH)
                {
                    holder.rlCompetitionDataExtroMine.setVisibility(View.GONE);
                    holder.rlCompetitionDataExtroOffical.setVisibility(View.VISIBLE);

                    holder.tvCompetitionIntro.setText(((CompetitionDataItem) data).userCompetitionDTO.description);
                }
                else if (competitionPageType == CompetitionUtils.COMPETITION_PAGE_MINE)
                {
                    holder.rlCompetitionDataExtroMine.setVisibility(View.VISIBLE);
                    holder.rlCompetitionDataExtroOffical.setVisibility(View.GONE);
                }
            }
        }
        return convertView;
    }

    static class ViewHolder
    {
        public LinearLayout llCompetitionHead = null;
        public TextView tvCompetitionHead = null;
        public RelativeLayout rlCompetitionData = null;
        public RelativeLayout rlCompetitionDataExtroMine = null;
        public RelativeLayout rlCompetitionDataExtroOffical = null;
        public ImageView imgCompetitionHead = null;
        public TextView tvCompetitionName = null;
        public TextView tvCompetitionJoinCount = null;
        public TextView tvCompetitionPeriod = null;
        public TextView tvCompetitionIntro = null;
        public TextView tvCompetitionROIValue = null;
        public TextView tvCompetitionJumpValue = null;
    }
}
