package com.tradehero.th.fragments.achievement;

import android.os.Bundle;
import android.text.InputType;
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
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.ExtendedDTO;
import com.tradehero.th.api.achievement.QuestBonusDTO;
import com.tradehero.th.api.achievement.QuestBonusDTOList;
import com.tradehero.th.api.achievement.key.QuestBonusListId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.key.MockQuestBonusId;
import com.tradehero.th.network.service.AchievementServiceWrapper;
import com.tradehero.th.persistence.achievement.QuestBonusListCache;
import com.tradehero.th.persistence.achievement.UserAchievementCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class QuestListTestingFragment extends DashboardFragment
{
    @InjectView(android.R.id.list) protected ListView listView;
    @InjectView(android.R.id.empty) protected ProgressBar emptyView;

    @Inject QuestBonusListCache questBonusListCache;
    @Inject AbstractAchievementDialogFragment.Creator creator;

    @Inject UserAchievementCache userAchievementCache;
    @Inject Lazy<ProgressDialogUtil> progressDialogUtilLazy;

    @Inject AchievementServiceWrapper achievementServiceWrapper;

    protected DTOCacheNew.Listener<QuestBonusListId, QuestBonusDTOList> questBonusListIdQuestBonusDTOListListener;
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

        questBonusListIdQuestBonusDTOListListener = createAchievementCategoryListCacheListener();

        initAdapter();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                QuestBonusDTO questBonusDTO = list.get(i - listView.getHeaderViewsCount());

                MockQuestBonusId mockQuestBonusId = new MockQuestBonusId(questBonusDTO.level, Integer.parseInt(mXPEarned.getText().toString()),
                        (Integer.parseInt(mXPEarned.getText().toString()) + Integer.parseInt(mXPFrom.getText().toString())));
                achievementServiceWrapper.getMockBonusDTO(mockQuestBonusId, new Callback<ExtendedDTO>()
                {
                    @Override public void success(ExtendedDTO dto, Response response)
                    {
                        progressDialogUtilLazy.get().dismiss(getActivity());
                    }

                    @Override public void failure(RetrofitError error)
                    {
                        progressDialogUtilLazy.get().dismiss(getActivity());
                    }
                });

                progressDialogUtilLazy.get().show(getActivity(), "Fetching Mock Quest", "Loading...");
            }
        });

        listView.addHeaderView(createHeaderView());
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
        attachAndFetchAchievementCategoryListener();
        super.onStart();
    }

    @Override public void onStop()
    {
        detachAchievementCategoryListener();
        super.onStop();
    }

    protected void attachAndFetchAchievementCategoryListener()
    {
        arrayAdapter.clear();
        questBonusListCache.register(questBonusListId, questBonusListIdQuestBonusDTOListListener);
        questBonusListCache.getOrFetchAsync(questBonusListId);
    }

    protected void detachAchievementCategoryListener()
    {
        questBonusListCache.unregister(questBonusListIdQuestBonusDTOListListener);
    }

    @Override public void onDestroy()
    {
        questBonusListIdQuestBonusDTOListListener = null;
        super.onDestroy();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    protected DTOCacheNew.Listener<QuestBonusListId, QuestBonusDTOList> createAchievementCategoryListCacheListener()
    {
        return new AchievementCategoryListCacheListener();
    }

    protected class AchievementCategoryListCacheListener implements DTOCacheNew.Listener<QuestBonusListId, QuestBonusDTOList>
    {
        @Override public void onDTOReceived(@NotNull QuestBonusListId key, @NotNull QuestBonusDTOList value)
        {
            list.clear();
            list.addAll(value);
            arrayAdapter.notifyDataSetChanged();
        }

        @Override public void onErrorThrown(@NotNull QuestBonusListId key, @NotNull Throwable error)
        {
            THToast.show(getString(R.string.error_fetch_achievements));
            Timber.e("Error fetching the list of competition info cell %s", key, error);
        }
    }
}
