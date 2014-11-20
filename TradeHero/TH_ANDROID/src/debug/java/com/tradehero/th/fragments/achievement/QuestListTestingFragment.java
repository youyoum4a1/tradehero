package com.tradehero.th.fragments.achievement;

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
import butterknife.InjectView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.achievement.QuestBonusDTO;
import com.tradehero.th.api.achievement.QuestBonusDTOList;
import com.tradehero.th.api.achievement.key.MockQuestBonusId;
import com.tradehero.th.api.achievement.key.QuestBonusListId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.network.service.AchievementMockServiceWrapper;
import com.tradehero.th.persistence.achievement.QuestBonusListCacheRx;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observer;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

public class QuestListTestingFragment extends DashboardFragment
{
    @InjectView(R.id.generic_swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.generic_ptr_list) protected ListView listView;
    @InjectView(android.R.id.progress) protected ProgressBar emptyView;

    @Inject QuestBonusListCacheRx questBonusListCache;
    @Inject Lazy<ProgressDialogUtil> progressDialogUtilLazy;
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
        ButterKnife.inject(this, view);
        swipeRefreshLayout.setEnabled(false);
        initAdapter();
        listView.setOnItemClickListener(this::onItemClick);
        listView.addHeaderView(createHeaderView());
    }

    @SuppressWarnings("UnusedParameters")
    public void onItemClick(@SuppressWarnings("UnusedParameters") AdapterView<?> adapterView, View view, int i, long l)
    {
        QuestBonusDTO questBonusDTO = list.get(i - listView.getHeaderViewsCount());

        MockQuestBonusId mockQuestBonusId = new MockQuestBonusId(questBonusDTO.level, Integer.parseInt(mXPEarned.getText().toString()),
                (Integer.parseInt(mXPEarned.getText().toString()) + Integer.parseInt(mXPFrom.getText().toString())));
        achievementMockServiceWrapper.getMockBonusDTORx(mockQuestBonusId)
        .subscribe(new Observer<BaseResponseDTO>()
        {
            @Override public void onNext(BaseResponseDTO baseResponseDTO)
            {
                progressDialogUtilLazy.get().dismiss(getActivity());
            }

            @Override public void onCompleted()
            {
            }

            @Override public void onError(Throwable e)
            {
                progressDialogUtilLazy.get().dismiss(getActivity());
            }
        });

        progressDialogUtilLazy.get().show(getActivity(), "Fetching Mock Quest", "Loading...");
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
        AndroidObservable.bindFragment(this,
                questBonusListCache.get(questBonusListId))
                .subscribe(createAchievementCategoryListCacheObserver());
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
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
