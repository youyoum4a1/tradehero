package com.tradehero.th.loaders;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Contacts;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.social.UserFriendsDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/24/14 Time: 11:16 AM Copyright (c) TradeHero
 */
public class FriendListLoader extends ListLoader<UserFriendsDTO>
{
    private static final String TAG = FriendListLoader.class.getName();

    private static final String[] CONTACT_PROJECTIONS =
            {
                    Contacts._ID,
                    Contacts.DISPLAY_NAME,
                    Contacts.PHOTO_URI,
            };
    private static final int CONTACT_ID_COLUMN = 0;
    private static final int CONTACT_DISPLAY_NAME_COLUMN = 1;
    private static final int CONTACT_PHOTO_URI_COLUMN = 2;

    private static final String[] EMAIL_PROJECTIONS =
            {
                    Email._ID,
                    Email.CONTACT_ID,
                    Email.ADDRESS
            };
    private static final int EMAIL_CONTACT_ID_COLUMN = 1;
    private static final int EMAIL_ADDRESS_COLUMN = 2;


    private static final String EMAIL_SELECTION = "";
    private static final String CONTACT_SELECTION = "";
    private static final String CONTACT_ID_SORT_ORDER = Contacts._ID + " ASC";
    private static final String EMAIL_CONTACT_ID_SORT_ORDER = Email.CONTACT_ID + " ASC";

    @Inject protected Lazy<UserService> userService;
    @Inject protected CurrentUserId currentUserId;

    private List<UserFriendsDTO> contactEntries;
    private List<UserFriendsDTO> userFriendsDTOs;

    public FriendListLoader(Context context)
    {
        super(context);
        DaggerUtils.inject(this);
    }

    @Override public List<UserFriendsDTO> loadInBackground()
    {
        Thread emailRetrieverThread = new Thread(new Runnable()
        {
            @Override public void run()
            {
                readContacts();
            }
        });

        Thread friendListRetrieverThread = new Thread(new Runnable()
        {
            @Override public void run()
            {
                retrieveFriendList();
            }
        });

        emailRetrieverThread.start();
        friendListRetrieverThread.start();

        List<UserFriendsDTO> friendsDTOs = new ArrayList<>();
        try
        {
            emailRetrieverThread.join();
            friendListRetrieverThread.join();
        }
        catch (Exception e)
        {
            Timber.e("Unable to get friend list", e);
            return friendsDTOs;
        }

        if (contactEntries != null && !contactEntries.isEmpty())
        {
            friendsDTOs.addAll(contactEntries);
        }
        if (userFriendsDTOs != null && !userFriendsDTOs.isEmpty())
        {
            friendsDTOs.addAll(userFriendsDTOs);
        }

        return friendsDTOs;
    }

    private void readContacts()
    {
        Cursor emailQueryCursor = getContext().getContentResolver().query(
                Email.CONTENT_URI,
                EMAIL_PROJECTIONS,
                EMAIL_SELECTION,
                null,
                EMAIL_CONTACT_ID_SORT_ORDER);

        Cursor contactQueryCursor = getContext().getContentResolver().query(
                Contacts.CONTENT_URI,
                CONTACT_PROJECTIONS,
                CONTACT_SELECTION,
                null,
                CONTACT_ID_SORT_ORDER);

        contactEntries = new ArrayList<>();
        if (emailQueryCursor != null && !emailQueryCursor.isClosed())
        {
            // 2 while-loops but complexity is O(max(number_of_emails, number_of_contact))
            while (emailQueryCursor.moveToNext())
            {
                ContactEntry contactEntry = new ContactEntry();
                String currentEmail = emailQueryCursor.getString(EMAIL_ADDRESS_COLUMN);
                if (currentEmail != null)
                {
                    contactEntry.setEmail(currentEmail);
                    int contactIdFromEmailTable = emailQueryCursor.getInt(EMAIL_CONTACT_ID_COLUMN);

                    while (contactQueryCursor.moveToNext())
                    {
                        int contactIdFromContactTable = contactQueryCursor.getInt(CONTACT_ID_COLUMN);
                        THLog.d(TAG, String.format("contactId from Contact/Email table: %d/%d", contactIdFromContactTable, contactIdFromEmailTable));
                        if (contactIdFromContactTable > contactIdFromEmailTable)
                        {
                            break;
                        }
                        if (contactIdFromContactTable == contactIdFromEmailTable)
                        {
                            contactEntry.setName(contactQueryCursor.getString(CONTACT_DISPLAY_NAME_COLUMN));
                            // contactEntry.setPhotoUri(contactQueryCursor.get(CONTACT_PHOTO_URI_COLUMN));
                            break;
                        }
                    }
                    if (contactEntry.getName() == null)
                    {
                        contactEntry.setName(currentEmail);
                    }
                }
                contactEntries.add(UserFriendsDTO.parse(contactEntry));
            }

            if (contactQueryCursor != null && !contactQueryCursor.isClosed())
            {
                contactQueryCursor.close();
            }
            emailQueryCursor.close();
        }
    }

    private void retrieveFriendList()
    {
        userFriendsDTOs = userService.get().getFriends(currentUserId.get());
    }
}
