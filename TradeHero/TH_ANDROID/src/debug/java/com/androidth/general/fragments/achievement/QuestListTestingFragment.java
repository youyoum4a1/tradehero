package com.androidth.general.fragments.achievement;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.androidth.general.common.utils.THToast;
import com.tradehero.th.R;
import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.achievement.QuestBonusDTO;
import com.androidth.general.api.achievement.QuestBonusDTOList;
import com.androidth.general.api.achievement.key.MockQuestBonusId;
import com.androidth.general.api.achievement.key.QuestBonusListId;
import com.androidth.general.fragments.base.BaseFragment;
import com.androidth.general.network.service.AchievementMockServiceWrapper;
import com.androidth.general.persistence.achievement.QuestBonusListCacheRx;
import com.androidth.general.rx.EmptyAction1;
import com.androidth.general.rx.view.DismissDialogAction0;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class QuestListTestingFragment extends BaseFragment
{
    @Bind(R.id.generic_swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.generic_ptr_list) protected ListView listView;
    @Bind(android.R.id.progress) protected ProgressBar emptyView;

    @Inject QuestBonusListCacheRx questBonusListCache;
    @Inject AchievementMockServiceWrapper achievementMockServiceWrapper;

    private List<QuestBonusDTO> list = new ArrayList<>();
    private ArrayAdapter<QuestBonusDTO> arrayAdapter;

    private QuestBonusListId questBonusListId = new QuestBonusListId();
    private EditText mXPFrom;
    private EditText mXPEarned;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_generic_list, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        swipeRefreshLayout.setEnabled(false);
        listView.addHeaderView(createHeaderView());
        initAdapter();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> parent, View view1, int position, long id)
            {
                QuestListTestingFragment.this.onItemClick(parent, view1, position, id);
            }
        });
    }

    @SuppressWarnings("UnusedParameters")
    public void onItemClick(@SuppressWarnings("UnusedParameters") AdapterView<?> adapterView, View view, int i, long l)
    {
        QuestBonusDTO questBonusDTO = list.get(i - listView.getHeaderViewsCount());

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "Fetching Mock Quest", "Loading...", true);

        MockQuestBonusId mockQuestBonusId = new MockQuestBonusId(questBonusDTO.level, Integer.parseInt(mXPEarned.getText().toString()),
                (Integer.parseInt(mXPEarned.getText().toString()) + Integer.parseInt(mXPFrom.getText().toString())));
        achievementMockServiceWrapper.getMockBonusDTORx(mockQuestBonusId)
                .finallyDo(new DismissDialogAction0(progressDialog))
                .subscribe(
                        new EmptyAction1<BaseResponseDTO>(),
                        new EmptyAction1<Throwable>());
    }

    private View createHeaderView()
    {
        LinearLayout linearLayout = new LinearLayout(getActivity());
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(lp);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        mXPFrom = getEditText();
        mXPEarned = getEditText();

        mXPFrom.setHint("Start XP");
        mXPEarned.setHint("XP Earned");

        linearLayout.addView(mXPFrom);
        linearLayout.addView(mXPEarned);

        return linearLayout;
    }

    private EditText getEditText()
    {
        EditText editText = new EditText(getActivity());
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        return editText;
    }

    private void initAdapter()
    {
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(arrayAdapter);
    }

    @Override public void onStart()
    {
        attachAndFetchAchievementCategory();
        super.onStart();
    }

    protected void attachAndFetchAchievementCategory()
    {
        arrayAdapter.clear();
        AppObservable.bindSupportFragment(this,
                questBonusListCache.get(questBonusListId))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createAchievementCategoryListCacheObserver());
    }

    @Override public void onDestroyView()
    {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    protected Observer<Pair<QuestBonusListId, QuestBonusDTOList>> createAchievementCategoryListCacheObserver()
    {
        return new AchievementCategoryListCacheObserver();
    }

    protected class AchievementCategoryListCacheObserver implements Observer<Pair<QuestBonusListId, QuestBonusDTOList>>
    {
        @Override public void onNext(Pair<QuestBonusListId, QuestBonusDTOList> pair)
        {
            list.clear();
            list.addAll(pair.second);
            arrayAdapter.notifyDataSetChanged();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(getString(R.string.error_fetch_achievements));
            Timber.e(e, "Error fetching the list of competition info cell");
        }
    }
}
