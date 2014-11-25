package com.tradehero.th.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.models.number.THSignedPercentage;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.StringUtils;
import dagger.Lazy;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class UserFriendsListAdapter extends BaseAdapter
{
    @Inject Lazy<Picasso> picasso;
    private Context context;
    private LayoutInflater inflater;
    private List<UserProfileCompactDTO> userProfileCompactDTOs = new ArrayList<>() ;

    private HashMap<String, ArrayList<UserProfileCompactDTO>> usersMap = new HashMap<>();
    private List<UserProfileCompactDTO> sortedUsers = new ArrayList<>() ;
    private String[] chars;

    private OnUserItemClickListener listener;

    public UserFriendsListAdapter(Context context)
    {
        DaggerUtils.inject(this);
        this.context = context;
        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        chars = context.getResources().getStringArray(R.array.character_divider);
    }

    public void setListData(List<UserProfileCompactDTO> list)
    {
        userProfileCompactDTOs = list;
        convertUserData(userProfileCompactDTOs);
    }

    public void addListData(List<UserProfileCompactDTO> list)
    {
        if (userProfileCompactDTOs == null) {
            userProfileCompactDTOs = new ArrayList<>();
        }
        userProfileCompactDTOs.addAll(list);
        convertUserData(userProfileCompactDTOs);
    }

    @Override public int getCount()
    {
        return sortedUsers == null ? 0 : sortedUsers.size();
    }

    @Override public Object getItem(int i)
    {
        return sortedUsers == null ? null : sortedUsers.get(i);
    }

    @Override public long getItemId(int i)
    {
        return i;
    }

    @Override public View getView(final int position, View convertView, ViewGroup viewGroup)
    {
        UserProfileCompactDTO item = (UserProfileCompactDTO) getItem(position);
        if (item != null)
        {
            ViewHolder holder;
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.user_friends_list_item, viewGroup, false);
                holder = new ViewHolder();
                holder.imgUserHead = (ImageView) convertView.findViewById(R.id.imgUserHead);
                holder.imgUserName = (TextView) convertView.findViewById(R.id.tvUserName);
                holder.tvUserExtraTitle = (TextView) convertView.findViewById(R.id.tvUserExtraTitle);
                holder.tvUserExtraValue = (TextView) convertView.findViewById(R.id.tvUserExtraValue);
                holder.dividerTV = (TextView)convertView.findViewById(R.id.textview_users_divider);
                holder.userRL = (RelativeLayout)convertView.findViewById(R.id.relativelayout_user_item);
                convertView.setTag(holder);
            }
            else
            {
                holder = (ViewHolder) convertView.getTag();
            }

            if(position == 0){
                holder.dividerTV.setVisibility(View.VISIBLE);
                holder.dividerTV.setText(item.displayNamePinYinFirstChar.toUpperCase());
            }else{
                int beforePosition = position - 1;
                UserProfileCompactDTO itemBefore = (UserProfileCompactDTO) getItem(beforePosition);
                if(item.displayNamePinYinFirstChar.equalsIgnoreCase(itemBefore.displayNamePinYinFirstChar)){
                    holder.dividerTV.setVisibility(View.GONE);
                    holder.dividerTV.setText("");
                }else{
                    holder.dividerTV.setVisibility(View.VISIBLE);
                    holder.dividerTV.setText(item.displayNamePinYinFirstChar.toUpperCase());
                }
            }
            picasso.get()
                    .load(item.picture)
                    .placeholder(R.drawable.superman_facebook)
                    .error(R.drawable.superman_facebook)
                    .into(holder.imgUserHead);

            holder.imgUserName.setText(item.getDisplayName());

            double roi = 0;
            if (item instanceof UserFollowerDTO)
            {
                roi = ((UserFollowerDTO) item).roiSinceInception;
            }
            else
            {
                if (item != null && item.roiSinceInception != null)
                {
                    roi = item.roiSinceInception;
                }
            }

            THSignedNumber thRoiSinceInception = THSignedPercentage.builder(roi * 100)
                    .build();
            holder.tvUserExtraValue.setText(thRoiSinceInception.toString());
            holder.tvUserExtraValue.setTextColor(
                    context.getResources().getColor(thRoiSinceInception.getColorResId()));
            holder.userRL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener!=null){
                        listener.onUserItemClick(position);
                    }
                }
            });
        }
        return convertView;
    }


    private void convertUserData(List<UserProfileCompactDTO> users){
        usersMap.clear();
        sortedUsers.clear();
        if(users==null || users.size()==0){
            return;
        }
        for(UserProfileCompactDTO user : users){
            String displayName = user.displayName;
            if(displayName==null || displayName.length()==0 ||displayName.trim().equals("")){
                user.displayNamePinYinFirstChar = "#";
                insertUserData(user, "#");
            }else {

                user.displayNamePinYinFirstChar = StringUtils.getCharacterPinYin(displayName.trim().charAt(0));
                if(user.displayNamePinYinFirstChar==null){
                    if(isInChar(displayName.trim().charAt(0))){
                        user.displayNamePinYinFirstChar = String.valueOf(displayName.trim().charAt(0));
                    }else {
                        user.displayNamePinYinFirstChar = "#";
                    }
                }else{
                    user.displayNamePinYinFirstChar = String.valueOf(user.displayNamePinYinFirstChar.charAt(0));
                }
                int charLen = chars.length;
                boolean isInChars = false;
                for(int num=0;num<charLen;num++){
                    if(user.displayNamePinYinFirstChar.equalsIgnoreCase(chars[num])){
                        insertUserData(user, chars[num]);
                        isInChars = true;
                        break;
                    }
                }
                if(!isInChars){
                    insertUserData(user, "#");
                }
            }
        }

        resortUserDisplayNames();
    }

    private void resortUserDisplayNames(){
        int charLen = chars.length;
        for(int num=0;num<charLen;num++){
            ArrayList<UserProfileCompactDTO> users_temp = usersMap.get(chars[num]);
            if(users_temp != null) {
                Collections.sort(users_temp);
                sortedUsers.addAll(users_temp);
            }
        }
        ArrayList<UserProfileCompactDTO> users_temp = usersMap.get("#");
        if(users_temp!=null) {
            Collections.sort(users_temp);
            sortedUsers.addAll(users_temp);
        }
    }

    private void insertUserData(UserProfileCompactDTO user, String key){
        ArrayList<UserProfileCompactDTO> users_temp = usersMap.get(key);
        if(users_temp==null){
            users_temp = new ArrayList<>();
            usersMap.put(key, users_temp);
        }
        usersMap.get(key).add(user);
    }

    private boolean isInChar(char ch){
        String str = String.valueOf(ch);
        int length = chars.length;
        for(int num=0;num<length;num++){
            if(str.equalsIgnoreCase(chars[num])){
                return true;
            }
        }
        return false;
    }

    public int getPosition(String divider){
        int size = sortedUsers.size();
        for(int num=0;num<size;num++){
            UserProfileCompactDTO user = sortedUsers.get(num);
            if(user.displayNamePinYinFirstChar.equalsIgnoreCase(divider)){
                return num;
            }
        }
        return -1;
    }

    class ViewHolder
    {
        public ImageView imgUserHead;
        public TextView imgUserName;
        public TextView tvUserExtraTitle;
        public TextView tvUserExtraValue;
        public TextView dividerTV;
        public RelativeLayout userRL;
    }

    public void setOnUserItemClickListener(OnUserItemClickListener listener){
        this.listener = listener;
    }


    public interface OnUserItemClickListener{
        public void onUserItemClick(int position);
    }
}
