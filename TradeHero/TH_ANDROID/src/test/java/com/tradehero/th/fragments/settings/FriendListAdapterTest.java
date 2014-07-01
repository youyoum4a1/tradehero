package com.tradehero.th.fragments.settings;

import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.R;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.api.social.UserFriendsContactEntryDTO;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.social.UserFriendsFacebookDTO;
import com.tradehero.th.api.social.UserFriendsLinkedinDTO;
import com.tradehero.th.api.social.UserFriendsTwitterDTO;
import java.util.ArrayList;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(RobolectricMavenTestRunner.class)
public class FriendListAdapterTest
{

    @Inject Context context;

    private LayoutInflater layoutInflater;
    private FriendListAdapter adapter;
    private ArrayList<UserFriendsDTO> list;

    @Before
    public void setUp() throws Exception
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivity.class);

        layoutInflater = activity.getLayoutInflater();
        adapter = new FriendListAdapter(context, layoutInflater, R.layout.refer_friend_list_item_view);
        list = new ArrayList<>();

        UserFriendsFacebookDTO mockUserFriendsFacebookDTO1 = new UserFriendsFacebookDTO();
        mockUserFriendsFacebookDTO1.fbId = "1231241";
        mockUserFriendsFacebookDTO1.name = "lorem ipsum 1";
        mockUserFriendsFacebookDTO1.thUserId = 23;
        mockUserFriendsFacebookDTO1.alreadyInvited = false;

        UserFriendsFacebookDTO mockUserFriendsFacebookDTO2 = new UserFriendsFacebookDTO();
        mockUserFriendsFacebookDTO2.fbId = "5123123";
        mockUserFriendsFacebookDTO2.name = "lorem ipsum 2";
        mockUserFriendsFacebookDTO2.thUserId = 0;
        mockUserFriendsFacebookDTO2.alreadyInvited = false;

        UserFriendsFacebookDTO mockUserFriendsFacebookDTO3 = new UserFriendsFacebookDTO();
        mockUserFriendsFacebookDTO3.fbId = "5123123";
        mockUserFriendsFacebookDTO3.name = "lorem ipsum 3";
        mockUserFriendsFacebookDTO3.thUserId = 0;
        mockUserFriendsFacebookDTO3.alreadyInvited = true;

        UserFriendsTwitterDTO mockUserFriendsTwitterDTO4 = new UserFriendsTwitterDTO();
        mockUserFriendsTwitterDTO4.twId = "423123";
        mockUserFriendsTwitterDTO4.name = "lorem ipsum 4";
        mockUserFriendsTwitterDTO4.thUserId = 2;
        mockUserFriendsTwitterDTO4.alreadyInvited = true;

        list.add(mockUserFriendsFacebookDTO1);
        list.add(mockUserFriendsFacebookDTO2);
        list.add(mockUserFriendsFacebookDTO3);
        list.add(mockUserFriendsTwitterDTO4);

        adapter.setItems(list);
        adapter.notifyDataSetChanged();
    }

    @After
    public void tearDown() throws Exception
    {
        adapter.resetItems();
        layoutInflater = null;
        adapter = null;
        list = null;
    }

    @Test public void testFilterInvitedFriendsWillReturnNonInvitedFriends()
    {
        assertThat(adapter.getCount()).isEqualTo(2);

        for (UserFriendsDTO userFriendsDTO : list)
        {
            assertThat(userFriendsDTO.alreadyInvited).isEqualTo(false);
        }
    }

    @Test public void testUserNameIsOrdered()
    {
        String n = "";
        for (UserFriendsDTO userFriendsDTO : list)
        {
            assertThat(userFriendsDTO.name.compareTo(n)).isGreaterThan(0);
            n = userFriendsDTO.name;
        }
    }

    @Test public void testSectionCountReturnsCorrectCount()
    {
        assertThat(adapter.getSections().length).isEqualTo(1);
    }

    @Test public void testFilterReturnsCorrectCount()
    {
        adapter.filter("1");
        assertThat(adapter.getCount()).isEqualTo(1);
    }

    @Test public void testToggleWillSelectAllCorrespondingFriends()
    {
        int shouldBeInvitedFBDTOCount = 0;
        for (UserFriendsDTO userFriendsDTO : list)
        {
            if (userFriendsDTO instanceof UserFriendsFacebookDTO && !userFriendsDTO.alreadyInvited)
            {
                shouldBeInvitedFBDTOCount++;
            }
        }
        adapter.toggleFacebookSelection(true);
        assertThat(adapter.getSelectedFacebookFriends().size()).isEqualTo(shouldBeInvitedFBDTOCount);

        int shouldBeInvitedLiDTOCount = 0;
        for (UserFriendsDTO userFriendsDTO : list)
        {
            if (userFriendsDTO instanceof UserFriendsLinkedinDTO && !userFriendsDTO.alreadyInvited)
            {
                shouldBeInvitedLiDTOCount++;
            }
        }
        adapter.toggleLinkedInSelection(true);
        assertThat(adapter.getSelectedLinkedInFriends().size()).isEqualTo(shouldBeInvitedLiDTOCount);

        int shouldBeInvitedContactCount = 0;
        for (UserFriendsDTO userFriendsDTO : list)
        {
            if (userFriendsDTO instanceof UserFriendsContactEntryDTO && !userFriendsDTO.alreadyInvited)
            {
                shouldBeInvitedContactCount++;
            }
        }
        adapter.toggleContactSelection(true);
        assertThat(adapter.getSelectedContacts().size()).isEqualTo(shouldBeInvitedContactCount);

        assertThat(adapter.getSelectedCount()).isEqualTo(shouldBeInvitedFBDTOCount + shouldBeInvitedLiDTOCount + shouldBeInvitedContactCount);

        adapter.toggleFacebookSelection(false);
        assertThat(adapter.getSelectedFacebookFriends().size()).isEqualTo(0);

        adapter.toggleLinkedInSelection(false);
        assertThat(adapter.getSelectedLinkedInFriends().size()).isEqualTo(0);

        adapter.toggleContactSelection(false);
        assertThat(adapter.getSelectedContacts().size()).isEqualTo(0);

        assertThat(adapter.getSelectedCount()).isEqualTo(0);
    }
}
