package com.tradehero.th.fragments.social.friend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import com.tradehero.th.api.social.UserFriendsDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tradehero on 14-5-26.
 */
public class SocialFriendsAdapter extends ArrayAdapter<UserFriendsDTO> {

    private Context mContext;
    private final Object mLock = new Object();
    private Filter mFilter;
    private int mLayoutResourceId;
    private SocialFriendItemView.OnElementClickListener elementClickedListener;
    private List<UserFriendsDTO> mOriginalValues;
    private List<UserFriendsDTO> mObjects;

    //<editor-fold desc="Constructors">
    public SocialFriendsAdapter(Context context, List<UserFriendsDTO> objects, int layoutResourceId) {
        super(context,0,objects);
        this.mContext = context;
        this.mObjects = objects;
        this.mLayoutResourceId = layoutResourceId;
    }
    //</editor-fold>

    @Override
    public SocialFriendItemView getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(getViewResId(position), parent, false);
        }
        SocialFriendItemView dtoView = (SocialFriendItemView) convertView;
        dtoView.display(getItem(position));
        dtoView.setOnElementClickedListener(elementClickedListener);
        return dtoView;

    }

    protected int getViewResId(int position) {
        return mLayoutResourceId;
    }

    public void setOnElementClickedListener(
            SocialFriendItemView.OnElementClickListener elementClickedListener) {
        this.elementClickedListener = elementClickedListener;
    }

    protected void handleFollowEvent(UserFriendsDTO userFriendsDTO) {
    }

    protected void handleInviteEvent(UserFriendsDTO userFriendsDTO) {
    }

    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new MyFilter();
        }
        return mFilter;
    }

    protected SocialFriendItemView.OnElementClickListener createUserClickedListener() {
        return new SocialElementClickListener();
    }

    protected class SocialElementClickListener implements SocialFriendItemView.OnElementClickListener {

        @Override
        public void onFollowButtonClick(UserFriendsDTO userFriendsDTO) {
            handleFollowEvent(userFriendsDTO);
        }

        @Override
        public void onInviteButtonClick(UserFriendsDTO userFriendsDTO) {
            handleInviteEvent(userFriendsDTO);
        }

    }

    /**
     * Copy from ArrayAdapter
     */
    private class MyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (mLock) {
                    mOriginalValues = new ArrayList<UserFriendsDTO>(mObjects);//
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (mLock) {
                    ArrayList<UserFriendsDTO> list = new ArrayList<UserFriendsDTO>(mOriginalValues);
                    results.values = list;
                    results.count = list.size();
                    return results;
                }
            } else {
                String prefixString = prefix.toString().toLowerCase();
                final int count = mOriginalValues.size();
                final ArrayList<UserFriendsDTO> newValues = new ArrayList<UserFriendsDTO>(count);
                for (int i = 0; i < count; i++) {
                    final UserFriendsDTO value = mOriginalValues.get(i);
                    final String valueText = value.name.toLowerCase();
                    if (valueText.startsWith(prefixString)) {
                        newValues.add(value);
                    } else {
                        final String[] words = valueText.split(" ");
                        final int wordCount = words.length;

                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mObjects = (List<UserFriendsDTO>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
}
