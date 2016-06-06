package com.androidth.general.fragments.social;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import com.androidth.general.common.fragment.HasSelectedItem;
import com.androidth.general.common.persistence.DTOCacheRx;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.R;
import com.androidth.general.api.users.SearchUserListType;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserListType;
import com.androidth.general.api.users.UserSearchResultDTO;
import com.androidth.general.api.users.UserSearchResultDTOList;
import com.androidth.general.fragments.BaseSearchRxFragment;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.timeline.MeTimelineFragment;
import com.androidth.general.fragments.timeline.PushableTimelineFragment;
import com.androidth.general.fragments.trending.PeopleItemViewAdapter;
import com.androidth.general.persistence.user.UserBaseKeyListCacheRx;

import javax.inject.Inject;

import timber.log.Timber;

public class PeopleSearchFragment extends BaseSearchRxFragment<
        UserListType,
        UserSearchResultDTO,
        UserSearchResultDTOList,
        UserSearchResultDTOList>
        implements HasSelectedItem
{
    @Inject UserBaseKeyListCacheRx userBaseKeyListCache;
    //TODO Change Analytics
    //@Inject Analytics analytics;

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        searchEmptyTextView.setText(R.string.trending_search_no_people_found);
    }

    //<editor-fold desc="ActionBar">
    @Override public void onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);

        if (mSearchTextField != null)
        {
            mSearchTextField.setHint(R.string.trending_search_empty_result_for_people);
        }
    }
    //</editor-fold>

    @Override @Nullable public UserSearchResultDTO getSelectedItem()
    {
        return selectedItem;
    }

    @Override @NonNull protected PeopleItemViewAdapter createItemViewAdapter()
    {
        return new PeopleItemViewAdapter(
                getActivity(),
                R.layout.search_people_item);
    }

    @Override @NonNull protected DTOCacheRx<UserListType, UserSearchResultDTOList> getCache()
    {
        return userBaseKeyListCache;
    }

    @NonNull @Override public SearchUserListType makePagedDtoKey(int page)
    {
        return new SearchUserListType(mSearchText, page, perPage);
    }

    protected void handleDtoClicked(UserSearchResultDTO clicked)
    {
        super.handleDtoClicked(clicked);

        if (getArguments() != null &&
                DashboardNavigator.getReturnFragment(getArguments()) != null)
        {
            navigator.get().popFragment();
            return;
        }

        if (clicked == null)
        {
            Timber.e(new NullPointerException("clicked was null"), null);
        }
        else
        {
            pushTimelineFragmentIn(clicked);
        }
    }

    protected void pushTimelineFragmentIn(UserSearchResultDTO userSearchResultDTO)
    {
        Bundle args = new Bundle();
        UserBaseKey userToSee =  userSearchResultDTO.getUserBaseKey();
        if (currentUserId.toUserBaseKey().equals(userToSee))
        {
            navigator.get().pushFragment(MeTimelineFragment.class, args);
        }
        else
        {
            PushableTimelineFragment.putUserBaseKey(args, userToSee);
            navigator.get().pushFragment(PushableTimelineFragment.class, args);
        }
    }

    @Override protected void onNext(@NonNull UserListType key, @NonNull UserSearchResultDTOList value)
    {
        super.onNext(key, value);
        //TODO Change Analytics
        //analytics.addEvent(new SimpleEvent(AnalyticsConstants.SearchResult_User));
    }

    @Override protected void onError(@NonNull UserListType key, @NonNull Throwable error)
    {
        super.onError(key, error);
        THToast.show(getString(R.string.error_fetch_people_list_info));
        Timber.e("Error fetching the list of securities " + key, error);
    }

    public EditText getSearchTextField()
    {
        return mSearchTextField;
    }
}