package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.squareup.picasso.Picasso;
import com.tradehero.chinabuild.data.CompetitionDataItem;
import com.tradehero.chinabuild.data.CompetitionHeadItem;
import com.tradehero.chinabuild.data.CompetitionInterface;
import com.tradehero.chinabuild.data.UserCompetitionDTO;
import com.tradehero.chinabuild.fragment.competition.CompetitionUtils;
import com.tradehero.th.R;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;

import javax.inject.Inject;
import java.util.ArrayList;

//比赛列表Adapter 包括 所有比赛，我的比赛
public class CompetitionListAdapter extends BaseAdapter
{
    @Inject Lazy<Picasso> picasso;

    private int competitionPageType = 0;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<CompetitionInterface> listData;
    private ArrayList<CompetitionDataItem> OfficalCompetitionDtoList= new ArrayList();//官方比赛
    private ArrayList<CompetitionDataItem> UserCompetitionDtoList = new ArrayList();//用户自建比赛
    private ArrayList<CompetitionDataItem> MyCompetitionDtoList= new ArrayList();//我参加的比赛
    private ArrayList<CompetitionDataItem> SearchCompetitionDtoList= new ArrayList();//搜索出来的比赛

    public CompetitionListAdapter(Context context, int pageType) {
        DaggerUtils.inject(this);
        this.context = context;
        competitionPageType = pageType;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOfficalCompetitionDtoList(ArrayList<UserCompetitionDTO> list) {
        ArrayList<CompetitionDataItem> listItem = new ArrayList<CompetitionDataItem>();
        if (list != null) {
            int sizeList = list.size();
            for (int i = 0; i < sizeList; i++) {
                listItem.add(new CompetitionDataItem(list.get(i)));
            }
        }
        OfficalCompetitionDtoList = listItem;
        doRefreshData();
    }

    public void setUserCompetitionDataList(ArrayList<CompetitionDataItem> listItem) {
        if(listItem!=null) {
            SearchCompetitionDtoList = listItem;
            doRefreshData();
        }
    }

    public void setUserCompetitionDtoList(ArrayList<UserCompetitionDTO> list) {
        ArrayList<CompetitionDataItem> listItem = new ArrayList<CompetitionDataItem>();
        if (list != null) {
            int sizeList = list.size();
            for (int i = 0; i < sizeList; i++) {
                if (!list.get(i).isOfficial)  {
                    listItem.add(new CompetitionDataItem(list.get(i)));
                }
            }
        }
        UserCompetitionDtoList = listItem;
        doRefreshData();
    }

    public void addUserCompetitionDtoList(ArrayList<UserCompetitionDTO> list) {
        if (list != null) {
            int sizeList = list.size();
            for (int i = 0; i < sizeList; i++) {
                if(isNotExist(list.get(i), UserCompetitionDtoList)){
                    UserCompetitionDtoList.add(new CompetitionDataItem(list.get(i)));
                }
            }
        }
        doRefreshData();
    }

    public void setMyCompetitionDtoList(ArrayList<UserCompetitionDTO> list) {
        ArrayList<CompetitionDataItem> listItem = new ArrayList<CompetitionDataItem>();
        if (list != null)  {
            MyCompetitionDtoList.clear();
            int sizeList = list.size();
            for (int i = 0; i < sizeList; i++) {
                listItem.add(new CompetitionDataItem(list.get(i)));
            }
        }
        MyCompetitionDtoList = listItem;
        doRefreshData();
    }

    public void addMyCompetitionDtoList(ArrayList<UserCompetitionDTO> list) {
        if (list != null) {
            int sizeList = list.size();
            for (int i = 0; i < sizeList; i++) {
                if(isNotExist(list.get(i), MyCompetitionDtoList)){
                    MyCompetitionDtoList.add(new CompetitionDataItem(list.get(i)));
                }
            }
        }
        doRefreshData();
    }

    private boolean isNotExist(UserCompetitionDTO userCompetitionDTO, ArrayList<CompetitionDataItem> competitionDataItems){
        if(competitionDataItems ==null || competitionDataItems.size()==0){
            return true;
        }
        for(CompetitionDataItem old: competitionDataItems){
            if(userCompetitionDTO.id == old.userCompetitionDTO.id){
                return false;
            }
        }
        return true;
    }

    public void setSearchCompetitionDtoList(ArrayList<UserCompetitionDTO> list) {
        ArrayList<CompetitionDataItem> listItem = new ArrayList<CompetitionDataItem>();
        if (list != null) {
            int sizeList = list.size();
            for (int i = 0; i < sizeList; i++) {
                listItem.add(new CompetitionDataItem(list.get(i)));
            }
        }
        SearchCompetitionDtoList = listItem;
        doRefreshData();
    }

    public int getOfficialCompetitions() {
        if (OfficalCompetitionDtoList == null) {
            return 0;
        }
        return OfficalCompetitionDtoList.size();
    }

    public int getUserCompetitions() {
        if (UserCompetitionDtoList == null) {
            return 0;
        }
        return UserCompetitionDtoList.size();
    }

    private void doRefreshData() {
        listData = new ArrayList<>();
        if (competitionPageType == CompetitionUtils.COMPETITION_PAGE_MINE) {
            listData.addAll(MyCompetitionDtoList);
        } else if (competitionPageType == CompetitionUtils.COMPETITION_PAGE_ALL) {
            if (getOfficalCompetitionCount() > 0) {
                listData.add(new CompetitionHeadItem(getHeadStrOfOfficalCompetition()));
                listData.addAll(OfficalCompetitionDtoList);
            }
            if (getUserCompetitionCount() > 0) {
                listData.add(new CompetitionHeadItem(getHeadStrOfUserCompetition()));
                listData.addAll(UserCompetitionDtoList);
            }
        } else if (competitionPageType == CompetitionUtils.COMPETITION_PAGE_SEARCH) {
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

                THSignedNumber roi = THSignedPercentage.builder(((CompetitionDataItem) data).userCompetitionDTO.roi * 100)
                        .withSign()
                        .signTypeArrow()
                        .build();
                holder.tvCompetitionROIValue.setText(roi.toString());
                holder.tvCompetitionROIValue.setTextColor(context.getResources().getColor(roi.getColorResId()));
                holder.tvCompetitionJumpValue.setText(((CompetitionDataItem) data).userCompetitionDTO.getRankRise());
            }
        }
        return convertView;
    }

    static class ViewHolder {
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
