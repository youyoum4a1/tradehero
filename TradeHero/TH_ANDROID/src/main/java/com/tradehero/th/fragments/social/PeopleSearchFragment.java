package com.tradehero.th.fragments.social;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.SearchUserListType;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.api.users.UserSearchResultDTOList;
import com.tradehero.th.fragments.BaseSearchFragment;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trending.PeopleItemViewAdapter;
import com.tradehero.th.fragments.trending.SearchPeopleItemView;
import com.tradehero.th.persistence.user.UserBaseKeyListCache;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class PeopleSearchFragment extends BaseSearchFragment<
        UserListType,
        UserSearchResultDTO,
        UserSearchResultDTOList,
        UserSearchResultDTOList,
        SearchPeopleItemView>
        implements HasSelectedItem
{
    @Inject Lazy<UserBaseKeyListCache> userBaseKeyListCache;
    @Inject DashboardNavigator navigator;

    protected void initViews(View view)
    {
        super.initViews(view);
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

    @Override protected PeopleItemViewAdapter createItemViewAdapter()
    {
        return new PeopleItemViewAdapter(
                getActivity(),
                R.layout.search_people_item);
    }

    @Override protected void unregisterCache(DTOCacheNew.Listener<UserListType, UserSearchResultDTOList> listener)
    {
        userBaseKeyListCache.get().unregister(listener);
    }

    @Override protected void registerCache(UserListType key, DTOCacheNew.Listener<UserListType, UserSearchResultDTOList> listener)
    {
        userBaseKeyListCache.get().register(key, listener);
    }

    @Override protected void requestCache(UserListType key)
    {
        userBaseKeyListCache.get().getOrFetchAsync(key);
    }

    @NotNull @Override public SearchUserListType makePagedDtoKey(int page)
    {
        return new SearchUserListType(mSearchText, page, perPage);
    }

    protected void handleDtoClicked(UserSearchResultDTO clicked)
    {
        super.handleDtoClicked(clicked);

        if (getArguments() != null && getArguments().containsKey(
                DashboardNavigator.BUNDLE_KEY_RETURN_FRAGMENT))
        {
            navigator.popFragment();
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
        thRouter.save(args, userSearchResultDTO.getUserBaseKey());
        if (currentUserId.toUserBaseKey().equals(userSearchResultDTO.getUserBaseKey()))
        {
            navigator.pushFragment(MeTimelineFragment.class, args);
        }
        else
        {
            navigator.pushFragment(PushableTimelineFragment.class, args);
        }
    }

    private DTOCacheNew.Listener<UserListType, UserSearchResultDTOList> createUserBaseKeyListCacheListener()
    {
        return new UserBaseKeyListCacheListener();
    }

    protected class UserBaseKeyListCacheListener extends ListCacheListener
    {
        @Override
        public void onDTOReceived(@NotNull UserListType key, @NotNull UserSearchResultDTOList value)
        {
            super.onDTOReceived(key, value);
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.SearchResult_User));
        }

        @Override public void onErrorThrown(@NotNull UserListType key, @NotNull Throwable error)
        {
            super.onErrorThrown(key, error);
            THToast.show(getString(R.string.error_fetch_people_list_info));
            Timber.e("Error fetching the list of securities " + key, error);
        }
    }
}