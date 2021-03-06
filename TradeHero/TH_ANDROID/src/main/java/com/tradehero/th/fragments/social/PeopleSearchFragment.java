package com.tradehero.th.fragments.social;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.Menu;
import android.view.View;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.users.SearchUserListType;
import com.tradehero.th.api.users.UserListType;
import com.tradehero.th.api.users.UserSearchResultDTO;
import com.tradehero.th.api.users.UserSearchResultDTOList;
import com.tradehero.th.fragments.BaseSearchRxFragment;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.timeline.MeTimelineFragment;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.fragments.trending.PeopleItemViewAdapter;
import com.tradehero.th.fragments.trending.SearchPeopleItemView;
import com.tradehero.th.persistence.user.UserBaseKeyListCacheRx;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.utils.route.THRouter;
import javax.inject.Inject;
import rx.Observer;
import timber.log.Timber;

public class PeopleSearchFragment extends BaseSearchRxFragment<
        UserListType,
        UserSearchResultDTO,
        UserSearchResultDTOList,
        UserSearchResultDTOList,
        SearchPeopleItemView>
        implements HasSelectedItem
{
    @Inject UserBaseKeyListCacheRx userBaseKeyListCache;
    @Inject Analytics analytics;
    @Inject THRouter thRouter;

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

        if (getArguments() != null && getArguments().containsKey(
                DashboardNavigator.BUNDLE_KEY_RETURN_FRAGMENT))
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
        thRouter.save(args, userSearchResultDTO.getUserBaseKey());
        if (currentUserId.toUserBaseKey().equals(userSearchResultDTO.getUserBaseKey()))
        {
            navigator.get().pushFragment(MeTimelineFragment.class, args);
        }
        else
        {
            navigator.get().pushFragment(PushableTimelineFragment.class, args);
        }
    }

    @Override @NonNull protected Observer<Pair<UserListType, UserSearchResultDTOList>> createListCacheObserver(@NonNull UserListType key)
    {
        return new UserBaseKeyListCacheObserver(key);
    }

    protected class UserBaseKeyListCacheObserver extends ListCacheObserver
    {
        protected UserBaseKeyListCacheObserver(@NonNull UserListType key)
        {
            super(key);
        }

        @Override public void onNext(Pair<UserListType, UserSearchResultDTOList> pair)
        {
            super.onNext(pair);
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.SearchResult_User));
        }

        @Override public void onError(Throwable error)
        {
            super.onError(error);
            THToast.show(getString(R.string.error_fetch_people_list_info));
            Timber.e("Error fetching the list of securities " + key, error);
        }
    }
}