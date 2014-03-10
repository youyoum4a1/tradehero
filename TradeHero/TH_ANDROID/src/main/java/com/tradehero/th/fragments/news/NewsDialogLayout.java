package com.tradehero.th.fragments.news;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.widget.*;
import com.squareup.picasso.Downloader;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.translation.TranslationResult;
import com.tradehero.th.network.service.DiscussionService;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.network.service.TranslationService;
import com.tradehero.th.network.service.TranslationServiceWrapper;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import retrofit.Callback;
import retrofit.client.Response;
import timber.log.Timber;

import javax.inject.Inject;


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

    private NewsItemDTO newsItemDTO;

    @Inject  Lazy<DiscussionServiceWrapper> discussionServiceWrapperLazy;
    @Inject  Lazy<TranslationServiceWrapper> translationServiceWrapperLazy;


    public NewsDialogLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        DaggerUtils.inject(this);
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
        SocialNetworkEnum socialNetworkEnum = null;
        switch (position) {
            case 0:
                socialNetworkEnum = SocialNetworkEnum.FB;
                break;
            case 1:
                socialNetworkEnum = SocialNetworkEnum.TW;
                break;
            case 2:
                socialNetworkEnum = SocialNetworkEnum.LI;
                break;
            default:
                break;

        }
        DiscussionKey key = new DiscussionKey(DiscussionType.NEWS,newsItemDTO.id);
        discussionServiceWrapperLazy.get().share(key, new TimelineItemShareRequestDTO(socialNetworkEnum),createShareRequestCallback(socialNetworkEnum));
    }

    private Callback<DiscussionDTO> createShareRequestCallback(final SocialNetworkEnum socialNetworkEnum)
    {
        return new THCallback<DiscussionDTO>()
        {
            @Override protected void success(DiscussionDTO response, THResponse thResponse)
            {
                THToast.show(String.format(getContext().getString(R.string.timeline_post_to_social_network), socialNetworkEnum.getName()));
            }

            @Override protected void failure(THException ex)
            {
                THToast.show("Share error "+socialNetworkEnum.getName());
                Timber.e(ex,"Share error");
                //THToast.show(ex);
            }
        };
    }

    private void handleTranslation() {

        new AsyncTask<Void,Void,TranslationResult>() {
            ProgressDialog dialog;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = ProgressDialogUtil.show(getContext(),null,"Translating...");
            }

            @Override
            protected TranslationResult doInBackground(Void... params) {
                try {
                    TranslationServiceWrapper serviceWrapper = translationServiceWrapperLazy.get();
                    Timber.d("serviceWrapper "+serviceWrapper);
                    return translationServiceWrapperLazy.get().translate(newsItemDTO.languageCode, "zh", newsItemDTO.title);
                }catch (Exception e){
                    Timber.e(e,"Translation Error");
                    return null;
                }

            }

            @Override
            protected void onPostExecute(TranslationResult s) {
                super.onPostExecute(s);
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }

                if (s != null && s.getContent() != null) {
                    THToast.show("Success");
                    showTranslationResult(s.getContent());
                }else {
                    THToast.show("error");
                }
            }
        }.execute();
    }

    private void showTranslationResult(String text) {
        Dialog dialog = THDialog.showCenterDialog(getContext(), "Translation result:", text, null, getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
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


    public void setNewsData(NewsItemDTO data) {
        this.newsItemDTO = data;
    }

    private class MyListAdapter extends ArrayAdapter<String> {


        public MyListAdapter(Context context, int resource, int textViewResourceId, String[] objects) {
            super(context, resource, textViewResourceId, objects);
        }


    }
}
