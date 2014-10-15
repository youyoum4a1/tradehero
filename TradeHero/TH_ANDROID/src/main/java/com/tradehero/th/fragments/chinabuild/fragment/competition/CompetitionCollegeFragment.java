package com.tradehero.th.fragments.chinabuild.fragment.competition;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.adapters.CompetitionCollegesAdapter;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.fragment.test.FragmentTest02;
import timber.log.Timber;

/*
    比赛选择高校
 */
public class CompetitionCollegeFragment extends DashboardFragment implements TextWatcher, AdapterView.OnItemClickListener {

    @InjectView(R.id.listview_competition_colleges)ListView lvColleges;
    @InjectView(R.id.edittext_competition_search_college)EditText etSearchCollege;
    @InjectView(R.id.textview_competition_search_college_noresult) TextView tvNoResult;

    private String[] colleges = new String[]{};
    private CompetitionCollegesAdapter collegesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain("高校选择");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.competition_college_fragment, container, false);
        ButterKnife.inject(this, view);
        initViews();
        return view;
    }

    private void initViews() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (getActivity() == null) {
                    return;
                }
                colleges = getActivity().getResources().getStringArray(R.array.competition_colleges);
                collegesAdapter = new CompetitionCollegesAdapter(getActivity(), colleges);
                lvColleges.setAdapter(collegesAdapter);
            }
        });
        etSearchCollege.addTextChangedListener(this);
        lvColleges.setOnItemClickListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        if (collegesAdapter != null) {
            collegesAdapter.getFilter().filter(charSequence);
        }
        if (!hasMatchedCollege(charSequence)) {
            tvNoResult.setVisibility(View.VISIBLE);
        }else{
            tvNoResult.setVisibility(View.GONE);
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private boolean hasMatchedCollege(CharSequence charSequence){
        String input = charSequence.toString();
        for(String college:colleges){
            if(college.contains(input)){
                return true;
            }
        }
        return false;
    }

}
