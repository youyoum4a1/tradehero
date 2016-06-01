package com.ayondo.academy.fragments.discovery;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnItemSelected;
import com.ayondo.academy.R;

public final class NewsPagerFragment extends Fragment
{
    @Bind(R.id.spinner_news) Spinner newsSpinner;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.discovery_news_pager, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        newsSpinner.setAdapter(new NewsSpinnerAdapter(getActivity(), NewsType.values()));
    }

    @Override public void onDestroyView()
    {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @SuppressWarnings("unused")
    @OnItemSelected(value = R.id.spinner_news, callback = OnItemSelected.Callback.ITEM_SELECTED)
    public void onNewsItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        Fragment f = NewsHeadlineFragment.newInstance(NewsType.values()[position]);
        getChildFragmentManager().beginTransaction().replace(R.id.news_container, f).addToBackStack(null).commitAllowingStateLoss();
    }

    class NewsSpinnerAdapter extends ArrayAdapter<NewsType>
    {
        public NewsSpinnerAdapter(Context context, NewsType[] objects)
        {
            super(context, 0, objects);
        }

        @Override public View getView(int position, View convertView, ViewGroup parent)
        {
            NewsType type = getItem(position);
            if (convertView == null)
            {
                convertView = getActivity().getLayoutInflater().inflate(type.titleViewResourceId, parent, false);
            }
            return convertView;
        }

        @Override public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            NewsType type = getItem(position);
            View rootView = getActivity().getLayoutInflater().inflate(type.titleViewResourceId, parent, false);
            View view = rootView.findViewById(R.id.spinner_arrow);
            if (view != null)
            {
                view.setVisibility(View.GONE);
            }
            return rootView;
        }
    }
}
