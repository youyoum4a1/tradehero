package com.tradehero.chinabuild.saveload;

import android.content.Context;
import android.content.SharedPreferences;
import com.tradehero.chinabuild.data.CompetitionDataItem;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.utils.StringUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import org.apache.commons.codec.binary.Base64;

/**
 * Created by huhaiping on 14-11-27. save for search result in SearchUniteFragment.java
 */
public class SearchResultSave
{
    public static final int MAX_SIZE = 10;
    public static final String TH_SP_SEARCH_SAVE = "SEARCH_SAVE";
    public static final String KEY_SEARCH_SAVE_USERS = "SEARCH_SAVE_USERS";
    public static final String KEY_SEARCH_SAVE_COMPETITIONS = "KEY_SEARCH_SAVE_COMPETITIONS";
    public static final String KEY_SEARCH_SAVE_SECURITIES = "KEY_SEARCH_SAVE_SECURITIES";



    //FOR SEARCH SECURITY START
    public static void saveSearchSecurity(Context context, SecurityCompactDTO item)
    {
        ArrayList<SecurityCompactDTO> list = loadSearchSecurity(context);
        if (list == null)
        {
            list = new ArrayList<>();
            list.add(item);
        }
        else
        {
            removeAndAddObjectInList(list, item);
        }
        saveSearchSecurity(context, list);
    }

    public static void removeAndAddObjectInList(ArrayList<SecurityCompactDTO> list, SecurityCompactDTO item)
    {
        for (int i = 0; i < list.size(); i++)
        {
            SecurityCompactDTO securityCompactDTO = list.get(i);

            if (securityCompactDTO.id.intValue() == (item).id.intValue())
            {
                list.remove(list.get(i));
            }
        }
        list.add(0, item);

        while (list.size() > MAX_SIZE)
        {
            list.remove(list.size() - 1);
        }
        ;
    }

    public static void saveSearchSecurity(Context context, ArrayList<SecurityCompactDTO> list)
    {
        SharedPreferences sp = context.getSharedPreferences(TH_SP_SEARCH_SAVE, Context.MODE_PRIVATE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(3000);
        ObjectOutputStream oos;
        try
        {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(list);
            oos.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        String save = new String(Base64.encodeBase64(baos.toByteArray()));
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_SEARCH_SAVE_SECURITIES, save);
        editor.commit();
    }

    public static ArrayList<SecurityCompactDTO> loadSearchSecurity(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(TH_SP_SEARCH_SAVE, Context.MODE_PRIVATE);
        String load = sp.getString(KEY_SEARCH_SAVE_SECURITIES, "");
        if (StringUtils.isNullOrEmpty(load)) return null;
        byte[] base64Bytes = Base64.decodeBase64(load.getBytes());
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
        ArrayList<SecurityCompactDTO> list = null;
        try
        {
            ObjectInputStream ois = new ObjectInputStream(bais);
            list = (ArrayList<SecurityCompactDTO>) ois.readObject();
            ois.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return list;
    }
    //FOR SEARCH SECURITY END


    //FOR SEARCH USERS START
    public static void saveSearchCompetitons(Context context, CompetitionDataItem item)
    {
        ArrayList<CompetitionDataItem> list = loadSearchCompetitions(context);
        if (list == null)
        {
            list = new ArrayList<>();
            list.add(item);
        }
        else
        {
            removeAndAddObjectInList(list, item);
        }
        saveSearchCompetitions(context, list);
    }

    public static void removeAndAddObjectInList(ArrayList<CompetitionDataItem> list, CompetitionDataItem item)
    {
        for (int i = 0; i < list.size(); i++)
        {
            CompetitionDataItem competitionInterface = list.get(i);

            if (competitionInterface.userCompetitionDTO.id == (item).userCompetitionDTO.id)
            {
                list.remove(list.get(i));
            }
        }
        list.add(0, item);

        while (list.size() > MAX_SIZE)
        {
            list.remove(list.size() - 1);
        }
        ;
    }

    public static void saveSearchCompetitions(Context context, ArrayList<CompetitionDataItem> list)
    {
        SharedPreferences sp = context.getSharedPreferences(TH_SP_SEARCH_SAVE, Context.MODE_PRIVATE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(3000);
        ObjectOutputStream oos;
        try
        {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(list);
            oos.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        String save = new String(Base64.encodeBase64(baos.toByteArray()));
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_SEARCH_SAVE_COMPETITIONS, save);
        editor.commit();
    }

    public static ArrayList<CompetitionDataItem> loadSearchCompetitions(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(TH_SP_SEARCH_SAVE, Context.MODE_PRIVATE);
        String load = sp.getString(KEY_SEARCH_SAVE_COMPETITIONS, "");
        if (StringUtils.isNullOrEmpty(load)) return null;
        byte[] base64Bytes = Base64.decodeBase64(load.getBytes());
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
        ArrayList<CompetitionDataItem> list = null;
        try
        {
            ObjectInputStream ois = new ObjectInputStream(bais);
            list = (ArrayList<CompetitionDataItem>) ois.readObject();
            ois.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return list;
    }
    //FOR SEARCH COMPETITIONS END

    //FOR SEARCH USERS START
    public static void saveSearchUsers(Context context, UserSearchResultDTO user)
    {
        ArrayList<UserSearchResultDTO> list = loadSearchUsers(context);
        if (list == null)
        {
            list = new ArrayList<>();
            list.add(user);
        }
        else
        {
            removeAndAddObjectInList(list, user);
        }
        saveSearchUsers(context, list);
    }

    public static void removeAndAddObjectInList(ArrayList<UserSearchResultDTO> list, UserSearchResultDTO user)
    {
        for (int i = (list.size()-1); i >= 0; i--)
        {
            if (list.get(i).userId.intValue() == user.userId.intValue())
            {
                list.remove(i);
            }
        }
        list.add(0, user);

        while (list.size() > MAX_SIZE)
        {
            list.remove(list.size() - 1);
        }
        ;
    }

    public static void saveSearchUsers(Context context, ArrayList<UserSearchResultDTO> list)
    {
        SharedPreferences sp = context.getSharedPreferences(TH_SP_SEARCH_SAVE, Context.MODE_PRIVATE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(3000);
        ObjectOutputStream oos;
        try
        {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(list);
            oos.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        String save = new String(Base64.encodeBase64(baos.toByteArray()));
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY_SEARCH_SAVE_USERS, save);
        editor.commit();
    }

    public static ArrayList<UserSearchResultDTO> loadSearchUsers(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(TH_SP_SEARCH_SAVE, Context.MODE_PRIVATE);
        String load = sp.getString(KEY_SEARCH_SAVE_USERS, "");
        if (StringUtils.isNullOrEmpty(load)) return null;
        byte[] base64Bytes = Base64.decodeBase64(load.getBytes());
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
        ArrayList<UserSearchResultDTO> list = null;
        try
        {
            ObjectInputStream ois = new ObjectInputStream(bais);
            list = (ArrayList<UserSearchResultDTO>) ois.readObject();
            ois.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return list;
    }
    ////FOR SEARCH USERS END
}
