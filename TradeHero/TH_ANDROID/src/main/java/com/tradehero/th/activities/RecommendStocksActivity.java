package com.tradehero.th.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.utils.DaggerUtils;


/**
 * Created by palmer on 14-10-29.
 */
public class RecommendStocksActivity extends Activity implements View.OnClickListener{

    @InjectView(R.id.textview_recommend_follow)TextView followTV;
    @InjectView(R.id.tvHeadLeft)TextView tvHeadLeft;
    @InjectView(R.id.tvHeadRight0)TextView tvHeadRight;

    private String jumpStr = "";
    private String followStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommmend_stock);

        DaggerUtils.inject(this);
        ButterKnife.inject(this);

        initViews();
    }

    private void initViews(){
        jumpStr = getResources().getString(R.string.recommend_next);
        followStr = getResources().getString(R.string.recommend_follow);
        tvHeadLeft.setVisibility(View.GONE);
        tvHeadRight.setVisibility(View.VISIBLE);
        tvHeadRight.setText(jumpStr);
        tvHeadRight.setOnClickListener(this);
        followTV.setText(followStr);
    }


    @Override
    public void onBackPressed(){
        gotoNextActivity();
    }


    private void gotoNextActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
        finish();
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if(viewId == R.id.tvHeadRight0){
            gotoNextActivity();
            return;
        }
        if(viewId == R.id.textview_recommend_follow){
            return;
        }
    }
}
