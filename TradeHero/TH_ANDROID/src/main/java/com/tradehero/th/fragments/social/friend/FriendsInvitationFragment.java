package com.tradehero.th.fragments.social.friend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.fragments.base.DashboardFragment;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by wanglinag on 14-5-26.
 */
public class FriendsInvitationFragment extends DashboardFragment implements AdapterView.OnItemClickListener
{
    @InjectView(R.id.search_social_friends) EditText searchTextView;
    @InjectView(R.id.social_friend_type_list) ListView socialListView;

    @Inject SocialTypeFactory socialTypeFactory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setTitle(getString(R.string.action_invite));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup v = (ViewGroup)inflater.inflate(R.layout.fragment_invite_friends,container,false);
        ButterKnife.inject(this,v);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View rootView)
    {
        List<SocalTypeItem> socalTypeItemList =  socialTypeFactory.getSocialTypeList();
        SocalTypeListAdapter adapter = new SocalTypeListAdapter(getActivity(),0,socalTypeItemList);
        socialListView.setAdapter(adapter);
        socialListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SocalTypeItem item =  (SocalTypeItem)parent.getItemAtPosition(position);
        THToast.show(item.title);

        pushSocialInvitationFragment(item.socialNetwork);
    }

    private void pushSocialInvitationFragment(SocialNetworkEnum socialNetwork)
    {

    }

    @Override
    public boolean isTabBarVisible() {
        return false;
    }
}
