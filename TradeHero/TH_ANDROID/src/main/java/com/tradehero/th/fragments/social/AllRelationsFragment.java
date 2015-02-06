package com.tradehero.th.fragments.social;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.AllowableRecipientDTO;
import com.tradehero.th.api.users.PaginatedAllowableRecipientDTO;
import com.tradehero.th.api.users.SearchAllowableRecipientListType;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.social.message.NewPrivateMessageFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.user.AllowableRecipientPaginatedCacheRx;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCacheRx;
import com.tradehero.th.utils.AdapterViewUtils;
import com.tradehero.th.utils.AlertDialogUtil;
import java.util.List;
import javax.inject.Inject;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.functions.Actions;
import timber.log.Timber;

public class AllRelationsFragment extends BasePurchaseManagerFragment
        implements AdapterView.OnItemClickListener, HasSelectedItem
{
    List<AllowableRecipientDTO> mRelationsList;
    @Inject AllowableRecipientPaginatedCacheRx allowableRecipientPaginatedCache;
    @Inject UserMessagingRelationshipCacheRx userMessagingRelationshipCache;

    @InjectView(R.id.sending_to_header) View sendingToHeader;

    private AllowableRecipientDTO selectedItem;

    private RelationsListItemAdapter mRelationsListItemAdapter;
    @InjectView(R.id.relations_list) ListView mRelationsListView;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_all_relations, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        mRelationsListItemAdapter = new RelationsListItemAdapter(
                getActivity(),
                R.layout.relations_list_item);
        mRelationsListView.setAdapter(mRelationsListItemAdapter);
        mRelationsListView.setOnItemClickListener(this);
        mRelationsListView.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());

        sendingToHeader.setVisibility(isForReturn() ? View.GONE : View.VISIBLE);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(isForReturn() ? R.string.message_pick_relation : R.string.message_center_new_message_title);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onStart()
    {
        super.onStart();
        downloadRelations();
        onStopSubscriptions.add(mRelationsListItemAdapter.getFollowRequestObservable()
                .subscribe(
                        request -> handlePremiumFollowRequested(request.heroId),
                        Actions.empty()
                ));
    }

    @Override public void onPause()
    {
        AlertDialogUtil.dismissProgressDialog();
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        mRelationsListView.setAdapter(null);
        mRelationsListView.setOnItemClickListener(null);
        mRelationsListView.setOnScrollListener(null);
        mRelationsListView = null;
        mRelationsListItemAdapter.clear();
        mRelationsListItemAdapter = null;
        mRelationsList = null;
        super.onDestroyView();
    }

    @Nullable @Override public AllowableRecipientDTO getSelectedItem()
    {
        return selectedItem;
    }

    private boolean isForReturn()
    {
        Bundle args = getArguments();
        return args != null && args.containsKey(DashboardNavigator.BUNDLE_KEY_RETURN_FRAGMENT);
    }

    public void downloadRelations()
    {
        AlertDialogUtil
                .showProgressDialog(getActivity(), getString(R.string.downloading_relations));
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                allowableRecipientPaginatedCache.get(new SearchAllowableRecipientListType(null, null, null)))
                .subscribe(
                        this::onNextRecipients,
                        this::onErrorRecipients));
    }

    protected void onNextRecipients(Pair<SearchAllowableRecipientListType, PaginatedAllowableRecipientDTO> pair)
    {
        mRelationsList = pair.second.getData();
        AlertDialogUtil.dismissProgressDialog();
        mRelationsListItemAdapter.addAll(mRelationsList);
        mRelationsListItemAdapter.notifyDataSetChanged();
    }

    protected void onErrorRecipients(Throwable e)
    {
        THToast.show(new THException(e));
        AlertDialogUtil.dismissProgressDialog();
    }

    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        selectedItem = mRelationsList.get(position);
        if (isForReturn())
        {
            navigator.get().popFragment();
            return;
        }
        pushPrivateMessageFragment(position);
    }

    protected void pushPrivateMessageFragment(int position)
    {
        Bundle args = new Bundle();
        NewPrivateMessageFragment.putCorrespondentUserBaseKey(args,
                mRelationsList.get(position).user.getBaseKey());
        navigator.get().pushFragment(NewPrivateMessageFragment.class, args);
    }

    protected void handlePremiumFollowRequested(UserBaseKey userBaseKey)
    {
        //noinspection unchecked,RedundantCast
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                userInteractorRx.purchaseAndPremiumFollowAndClear(userBaseKey))
                .subscribe(
                        result -> forceUpdateLook(userBaseKey),
                        error -> THToast.show(new THException((Throwable) error))
                ));
    }

    protected void forceUpdateLook(@NonNull final UserBaseKey userFollowed)
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                userMessagingRelationshipCache.get(userFollowed))
                .subscribe(new Observer<Pair<UserBaseKey, UserMessagingRelationshipDTO>>()
                {
                    private boolean isEmpty = true;

                    @Override public void onCompleted()
                    {
                        if (isEmpty)
                        {
                            allowableRecipientPaginatedCache.invalidateAll();
                            downloadRelations();
                            Timber.e("Strangely, there was no longer the relation in cache");
                        }
                    }

                    @Override public void onError(Throwable e)
                    {
                    }

                    @Override public void onNext(Pair<UserBaseKey, UserMessagingRelationshipDTO> pair)
                    {
                        isEmpty = false;
                        mRelationsListItemAdapter.updateItem(userFollowed, pair.second);
                        AdapterViewUtils
                                .updateSingleRowWhere(mRelationsListView, AllowableRecipientDTO.class,
                                        allowableRecipientDTO -> allowableRecipientDTO != null
                                                && allowableRecipientDTO.user.getBaseKey().equals(userFollowed));
                    }
                }));
    }
}