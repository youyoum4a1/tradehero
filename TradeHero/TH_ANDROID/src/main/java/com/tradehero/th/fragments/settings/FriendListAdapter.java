package com.tradehero.th.fragments.settings;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.adapters.ArrayDTOAdapter;
import com.tradehero.th.api.social.UserFriendsDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/21/14 Time: 7:28 PM Copyright (c) TradeHero
 */
public class FriendListAdapter extends ArrayDTOAdapter<UserFriendsDTO, UserFriendDTOView>
        implements StickyListHeadersAdapter, SectionIndexer
{
    private static final long NO_NAME_HEADER_ID = ' ';

    private String[] collectedNames;
    private Integer[] sectionIndices;
    private Character[] sections;
    private List<UserFriendsDTO> originalItems;

    public FriendListAdapter(Context context, LayoutInflater layoutInflater, int itemLayoutId)
    {
        super(context, layoutInflater, itemLayoutId);
    }

    @Override public void setItems(List<UserFriendsDTO> items)
    {
        THLog.d(TAG, "number of friends: " + (items != null ? items.size() : 0));
        filterOutInvitedFriends(items);

        originalItems = items != null ? Collections.unmodifiableList(items) : null;
        setItemsInternal(items);
    }

    private void setItemsInternal(List<UserFriendsDTO> items)
    {
        super.setItems(items);
        sortUserFriendListByName();
        initNamesFromDTOList();
        initSectionIndices();
        initDistinctFirstCharacterNames();
    }

    private void filterOutInvitedFriends(List<UserFriendsDTO> items)
    {
        for (UserFriendsDTO userFriendsDTO : items)
        {
            if (userFriendsDTO != null && userFriendsDTO.alreadyInvited)
            {
                items.remove(userFriendsDTO);
            }
        }
    }

    @Override protected void fineTune(int position, UserFriendsDTO dto, UserFriendDTOView dtoView)
    {
    }

    @Override public View getHeaderView(int position, View convertView, ViewGroup parent)
    {
        SectionViewHolder sectionViewHolder = null;
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.refer_friend_header_view, parent, false);
            TextView sectionTextView = (TextView) convertView.findViewById(R.id.refer_friend_list_header);
            sectionViewHolder = new SectionViewHolder();
            sectionViewHolder.labelText = sectionTextView;

            convertView.setTag(sectionViewHolder);
        }
        else
        {
            sectionViewHolder = (SectionViewHolder) convertView.getTag();
        }

        if (sectionViewHolder != null && sectionViewHolder.labelText != null)
        {
            sectionViewHolder.labelText.setText("" + sections[getSectionForPosition(position)]);
        }

        return convertView;
    }

    @Override public long getHeaderId(int position)
    {
        UserFriendsDTO item = (UserFriendsDTO) getItem(position);
        if (item != null && item.name != null && item.name.length() > 0)
        {
            return Character.toUpperCase(item.name.charAt(0));
        }
        return NO_NAME_HEADER_ID;
    }

    @Override public Object[] getSections()
    {
        return sections;
    }

    private void sortUserFriendListByName()
    {
        if (items != null)
        {
            Collections.sort(items, new Comparator<UserFriendsDTO>()
            {
                @Override public int compare(UserFriendsDTO lhs, UserFriendsDTO rhs)
                {
                    if (lhs == rhs) return 0;
                    else if (lhs == null) return -1;
                    else if (rhs == null) return 1;
                    else if (lhs.name == null || lhs.name.isEmpty()) return -1;
                    else if (rhs.name == null || lhs.name.isEmpty()) return 1;
                    else if (lhs.name.equals(rhs.name)) return 0;
                    else return lhs.name.compareTo(rhs.name);
                }
            });
        }
    }

    private Character[] initDistinctFirstCharacterNames()
    {
        sections = null;
        if (sectionIndices != null)
        {
            sections = new Character[sectionIndices.length];
            for (int i = 0; i<sectionIndices.length; ++i)
            {
                sections[i] = collectedNames[sectionIndices[i]].charAt(0);
            }
            return sections;
        }
        return sections;
    }

    /**
     * Position of where each section started
     */
    private void initSectionIndices()
    {
        sectionIndices = null;
        if (collectedNames != null && collectedNames.length > 0)
        {
            // collect distinct list of first appearance character
            List<Integer> firstCharacterAppearances = new ArrayList<>();
            char lastBeginningCharacter = collectedNames[0].charAt(0);
            firstCharacterAppearances.add(0);
            for (int characterIndex=1; characterIndex < collectedNames.length; ++characterIndex)
            {
                String name = collectedNames[characterIndex];
                THLog.d(TAG, "last beginning char: " + name +"/"+ collectedNames[0]);
                if (name != null && name.length() > 0 && name.charAt(0) != lastBeginningCharacter)
                {
                    lastBeginningCharacter = name.charAt(0);
                    firstCharacterAppearances.add(characterIndex);
                }
            }

            // populate sectionIndices array
            sectionIndices = new Integer[firstCharacterAppearances.size()];
            firstCharacterAppearances.toArray(sectionIndices);
        }
    }

    private void initNamesFromDTOList()
    {
        collectedNames = null;
        if (items != null)
        {
            ArrayList<String> nameList = new ArrayList<>();
            for (UserFriendsDTO userFriendsDTO: items)
            {
                if (userFriendsDTO.name != null)
                {
                    nameList.add(userFriendsDTO.name);
                }
            }
            collectedNames = new String[nameList.size()];
            nameList.toArray(collectedNames);
        }
    }

    @Override public int getPositionForSection(int section)
    {
        if (sections == null)
        {
            return 0;
        }

        if (section > sectionIndices.length)
        {
            return sectionIndices.length - 1;
        }
        if (section < 0)
        {
            return 0;
        }
        return sectionIndices[section];
    }

    @Override
    public int getSectionForPosition(int position)
    {
        if (sections == null)
        {
            return 0;
        }

        for (int i = 0; i < sectionIndices.length; i++)
        {
            if (position < sectionIndices[i])
            {
                return i - 1;
            }
        }
        return sectionIndices.length - 1;
    }

    public void filter(String searchText)
    {
        List<UserFriendsDTO> newItems = new ArrayList<>();
        if (originalItems != null && searchText != null)
        {
            for (UserFriendsDTO userFriendsDTO: originalItems)
            {
                if (userFriendsDTO != null && userFriendsDTO.name != null)
                {
                    if (userFriendsDTO.name.toUpperCase().contains(searchText.toUpperCase()))
                    {
                        newItems.add(userFriendsDTO);
                    }
                }
            }
        }
        setItemsInternal(newItems);
    }

    public void resetItems()
    {
        setItemsInternal(new ArrayList<>(originalItems));
    }

    private static class SectionViewHolder
    {
        TextView labelText = null;
    }
}
