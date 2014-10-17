package com.tradehero.th.fragments.chinabuild.fragment.competition;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.*;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.CompetitionCollegesAdapter;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;

import javax.inject.Inject;

/*
    比赛选择高校
 */
public class CompetitionCollegeFragment extends DashboardFragment{

    @InjectView(R.id.listview_competition_colleges) ListView lvColleges;
    @InjectView(R.id.edittext_competition_search_college) EditText etSearchCollege;
    @InjectView(R.id.textview_competition_search_college_noresult) TextView tvNoResult;


    private String[] colleges = new String[]{};
    private CompetitionCollegesAdapter collegesAdapter;

    //Dialog
    private Dialog confirmCollegeDialog;
    private String chooseCollegeStrA;
    private String chooseCollegeStrB;
    private TextView tvConfirm;
    private TextView tvCancel;
    private TextView tvContent;

    //Update college to server
    private String selectedCollege = "";
    private MiddleCallback<UserProfileDTO> middleCallbackUpdateUserProfile;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCache userProfileCache;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;

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
        initConfirmDialog();
        return view;
    }

    private void initConfirmDialog() {
        confirmCollegeDialog = new Dialog(getActivity());
        confirmCollegeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        confirmCollegeDialog.setContentView(R.layout.competition_college_dialog_layout);
        tvConfirm = (TextView) confirmCollegeDialog.findViewById(R.id.textview_ok);
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog();
                if(TextUtils.isEmpty(selectedCollege)){
                    return;
                }
                UserFormDTO userFormDTO = createForm(selectedCollege);
                detachMiddleCallbackUpdateUserProfile();
                middleCallbackUpdateUserProfile = userServiceWrapper.get().uploadCollege(
                        currentUserId.toUserBaseKey(),
                        userFormDTO,
                        createUpdateUserProfileCallback());

                progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.updating);
            }
        });
        tvCancel = (TextView) confirmCollegeDialog.findViewById(R.id.textview_cancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissDialog();
            }
        });
        tvContent = (TextView) confirmCollegeDialog.findViewById(R.id.textview_content);
        chooseCollegeStrA = getActivity().getResources().getString(R.string.dialog_competition_choose_college_a);
        chooseCollegeStrB = getActivity().getResources().getString(R.string.dialog_competition_choose_college_b);
    }

    private void showDialog(String collegeName){
        if(confirmCollegeDialog==null){
            return;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(chooseCollegeStrA+collegeName+chooseCollegeStrB);
        ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
        ForegroundColorSpan blackSpan = new ForegroundColorSpan(Color.BLACK);
        int lengthA = chooseCollegeStrA.length();
        int lengthB = lengthA + collegeName.length();
        int lengthC = lengthB + chooseCollegeStrB.length();
        builder.setSpan(blackSpan, 0, lengthA, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(redSpan,lengthA, lengthB, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(blackSpan,lengthB, lengthC, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvContent.setText(builder);
        if(confirmCollegeDialog.isShowing()){
            return;
        }
        confirmCollegeDialog.show();
    }

    private void dismissDialog(){
        if(confirmCollegeDialog==null){
            return;
        }
        confirmCollegeDialog.dismiss();
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
                etSearchCollege.addTextChangedListener(new TextWatcher() {
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
                        } else {
                            tvNoResult.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                lvColleges.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        selectedCollege = collegesAdapter.getItem(position).toString();
                        if(TextUtils.isEmpty(selectedCollege)){
                            return;
                        }
                        showDialog(selectedCollege);
                    }
                });
            }
        });

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

    private boolean hasMatchedCollege(CharSequence charSequence) {
        String input = charSequence.toString();
        for (String college : colleges) {
            if (college.contains(input)) {
                return true;
            }
        }
        return false;
    }


    public UserFormDTO createForm(String college)
    {
        UserFormDTO created = new UserFormDTO();
        created.school = college;
        return created;
    }

    private THCallback<UserProfileDTO> createUpdateUserProfileCallback()
    {
        return new THCallback<UserProfileDTO>()
        {
            @Override protected void success(UserProfileDTO userProfileDTO, THResponse thResponse)
            {
                progressDialogUtil.dismiss(getActivity());
                userProfileCache.put(currentUserId.toUserBaseKey(), userProfileDTO);
                THToast.show(R.string.settings_update_profile_successful);
                popCurrentFragment();
            }

            @Override protected void failure(THException ex)
            {
                progressDialogUtil.dismiss(getActivity());
                THToast.show(ex.getMessage());
                popCurrentFragment();
            }
        };
    }

    private void detachMiddleCallbackUpdateUserProfile()
    {
        if (middleCallbackUpdateUserProfile != null)
        {
            middleCallbackUpdateUserProfile.setPrimaryCallback(null);
        }
        middleCallbackUpdateUserProfile = null;
    }

}
