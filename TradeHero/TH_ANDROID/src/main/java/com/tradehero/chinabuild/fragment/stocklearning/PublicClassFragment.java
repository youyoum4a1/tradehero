package com.tradehero.chinabuild.fragment.stocklearning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.chinabuild.data.VideoDTO;
import com.tradehero.chinabuild.data.question.questionUtils.Question;
import com.tradehero.chinabuild.data.question.questionUtils.QuestionLoader;
import com.tradehero.chinabuild.fragment.videoPlay.VideoPlayer;
import com.tradehero.th.R;
import com.tradehero.th.adapters.VideoGridAdapter;
import com.tradehero.th.fragments.base.DashboardFragment;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

/**
 * Created by palmer on 15/3/27.
 */
public class PublicClassFragment extends DashboardFragment
{

    @InjectView(R.id.gridView) GridView gridView;

    private VideoGridAdapter videoGridAdapter;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initGridViewAdapter();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.stock_learning_public_class, container, false);
        ButterKnife.inject(this, view);
        initGridView();
        return view;
    }

    private void initGridViewAdapter()
    {
        videoGridAdapter = new VideoGridAdapter(getActivity());
        ArrayList<VideoDTO> listData = new ArrayList<>();
        for (int i = 0; i < 7; i++)
        {
            VideoDTO videoDTO = new VideoDTO();
            videoDTO.videoText = "视频介绍 " + (i + 1);
            videoDTO.videoUrl = "http://v.youku.com/v_show/id_XOTIyNjkwODk2.html?ev=1&from=y1.1-2.10001-0.1-1";
            listData.add(videoDTO);
        }
        videoGridAdapter.setListData(listData);
    }

    private void initGridView()
    {
        gridView.setAdapter(videoGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Timber.d("GridView Clicked : " + position);
                VideoDTO videoDTO = videoGridAdapter.getItem(position);
                playVideo(videoDTO.videoUrl);
            }
        });
    }

    public void playVideo(String url)
    {
        Bundle bundle = new Bundle();
        //VideoPlayer.putUrl(bundle, url);
        gotoDashboard(VideoPlayer.class, bundle);

        getQuestion();
    }

    public void getQuestion()
    {
        List<Question> arrayList1 = QuestionLoader.getInstance(getActivity()).getQuestionLevelOne();
        List<Question> arrayList2 = QuestionLoader.getInstance(getActivity()).getQuestionLevelTwo();
        List<Question> arrayList3 = QuestionLoader.getInstance(getActivity()).getQuestionLevelThree();
        Timber.d("arrayList loaded!");
    }
}
