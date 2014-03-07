package com.tradehero.th.fragments.news;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.*;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;


/**
 * Created by tradehero on 14-3-7.
 */
public class NewsDialogLayout extends LinearLayout implements View.OnClickListener,AdapterView.OnItemClickListener,THDialog.DialogCallback {
    private View titleView;
    private View backView;
    private View cancelView;
    private ViewSwitcher viewSwitcher;
    private ListView listViewFirst;
    private ListView listViewSecond;

    private THDialog.DialogInterface dialogCallback;
    public NewsDialogLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        findView();
        fillData();
        registerListener();
    }

    private void findView() {
       this.titleView  = (TextView)findViewById(R.id.news_action_share_title);
       this.backView = findViewById(R.id.news_action_back);
       this.cancelView = findViewById(R.id.news_action_share_cancel);
       this.viewSwitcher = (ViewSwitcher)findViewById(R.id.news_action_list_switcher);
       this.listViewFirst = (android.widget.ListView)this.viewSwitcher.findViewById(R.id.news_action_list_sharing_translation);
       this.listViewSecond = (android.widget.ListView)this.viewSwitcher.findViewById(R.id.news_action_list_sharing_items);



    }

    private void registerListener() {
        backView.setOnClickListener(this);
        cancelView.setOnClickListener(this);
        listViewFirst.setOnItemClickListener(this);
        listViewSecond.setOnItemClickListener(this);
    }

    private void fillData() {
        String[] dataForFirst = {"Sharing","Translation"};
        String[] dataForSecond = {"Facebook","Twitter","LinkedIn"};
        MyListAdapter adapterForFirst = new MyListAdapter(getContext(),R.layout.common_dialog_item_layout,R.id.popup_text,dataForFirst);
        MyListAdapter adapterForSecond = new MyListAdapter(getContext(),R.layout.common_dialog_item_layout,R.id.popup_text,dataForSecond);
        listViewFirst.setAdapter(adapterForFirst);
        listViewSecond.setAdapter(adapterForSecond);

    }
    private void showFirstChild() {
        this.viewSwitcher.setOutAnimation(getContext(),R.anim.slide_right_out);
        this.viewSwitcher.setInAnimation(getContext(),R.anim.slide_left_in);

        this.backView.setVisibility(View.INVISIBLE);
        this.viewSwitcher.setDisplayedChild(0);
    }

    private void showSecondChild() {
        this.viewSwitcher.setOutAnimation(getContext(),R.anim.slide_left_out);
        this.viewSwitcher.setInAnimation(getContext(),R.anim.slide_right_in);

        this.backView.setVisibility(View.VISIBLE);
        this.viewSwitcher.setDisplayedChild(1);
    }

    private void handleShareAction(int position) {

    }

    private void handleTranslation() {

    }

    private void dismissDialog() {
        if(dialogCallback != null) {
            dialogCallback.onDialogDismiss();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.news_action_back:
                showFirstChild();
                break;
            case R.id.news_action_share_cancel:
                dismissDialog();
                break;

        }
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent == listViewFirst) {
            if (position == 0) {
                showSecondChild();
            }else if (position == 1){
                handleTranslation();
                dismissDialog();
            }
        }else {
            handleShareAction(position);
            dismissDialog();
        }
    }

    @Override
    public void setOnDismissCallback(THDialog.DialogInterface listener) {
        this.dialogCallback = listener;
    }

    @Override
    public void setShowDividers(int showDividers) {
        super.setShowDividers(showDividers);
    }

    private class MyListAdapter extends ArrayAdapter<String> {


        public MyListAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
            super(context, resource, textViewResourceId, objects);
        }


    }
}
