package com.androidth.general.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.androidth.general.R;
import com.androidth.general.api.fx.FXCandleDTO;
import com.androidth.general.fragments.base.BaseFragment;
import com.androidth.general.widget.KChartsView;
import java.util.ArrayList;
import java.util.List;

public class TestKChartsFragment extends BaseFragment
{
    private KChartsView mMyChartsView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_kcharts, null);
        mMyChartsView = (KChartsView) view.findViewById(R.id.my_charts_view);

        List<FXCandleDTO> ohlc = new ArrayList<>();
        ohlc.add(createDTO(240, 245, 230, 235, "20110827"));
        ohlc.add(createDTO(235, 245, 230, 240, "20110826"));
        ohlc.add(createDTO(246, 248, 235, 235, "20110825"));
        ohlc.add(createDTO(240, 242, 236, 242, "20110824"));
        ohlc.add(createDTO(236, 240, 235, 240, "20110823"));
        ohlc.add(createDTO(232, 236, 231, 236, "20110822"));
        ohlc.add(createDTO(240, 240, 235, 235, "20110819"));
        ohlc.add(createDTO(240, 241, 239, 240, "20110818"));
        ohlc.add(createDTO(242, 243, 240, 240, "20110817"));
        ohlc.add(createDTO(239, 242, 238, 242, "20110816"));
        ohlc.add(createDTO(239, 240, 238, 239, "20110815"));
        ohlc.add(createDTO(230, 238, 230, 238, "20110812"));
        ohlc.add(createDTO(236, 237, 234, 234, "20110811"));
        ohlc.add(createDTO(226, 233, 223, 232, "20110810"));
        ohlc.add(createDTO(239, 241, 229, 232, "20110809"));
        ohlc.add(createDTO(242, 244, 240, 242, "20110808"));
        ohlc.add(createDTO(248, 249, 247, 248, "20110805"));
        ohlc.add(createDTO(245, 248, 245, 247, "20110804"));
        ohlc.add(createDTO(249, 249, 245, 247, "20110803"));
        ohlc.add(createDTO(249, 251, 248, 250, "20110802"));
        ohlc.add(createDTO(250, 252, 248, 250, "20110801"));
        ohlc.add(createDTO(250, 251, 248, 250, "20110729"));
        ohlc.add(createDTO(249, 252, 248, 252, "20110728"));
        ohlc.add(createDTO(248, 250, 247, 250, "20110727"));
        ohlc.add(createDTO(256, 256, 248, 248, "20110726"));
        ohlc.add(createDTO(257, 258, 256, 257, "20110725"));
        ohlc.add(createDTO(259, 260, 256, 256, "20110722"));
        ohlc.add(createDTO(261, 261, 257, 259, "20110721"));
        ohlc.add(createDTO(260, 260, 259, 259, "20110720"));
        ohlc.add(createDTO(262, 262, 260, 261, "20110719"));
        ohlc.add(createDTO(260, 262, 259, 262, "20110718"));
        ohlc.add(createDTO(259, 261, 258, 261, "20110715"));
        ohlc.add(createDTO(255, 259, 255, 259, "20110714"));
        ohlc.add(createDTO(258, 258, 255, 255, "20110713"));
        ohlc.add(createDTO(258, 260, 258, 260, "20110712"));
        ohlc.add(createDTO(259, 260, 258, 259, "20110711"));
        ohlc.add(createDTO(261, 262, 259, 259, "20110708"));
        ohlc.add(createDTO(261, 261, 258, 261, "20110707"));
        ohlc.add(createDTO(261, 261, 259, 261, "20110706"));
        ohlc.add(createDTO(257, 261, 257, 261, "20110705"));
        ohlc.add(createDTO(256, 257, 255, 255, "20110704"));
        ohlc.add(createDTO(253, 257, 253, 256, "20110701"));
        ohlc.add(createDTO(255, 255, 252, 252, "20110630"));
        ohlc.add(createDTO(256, 256, 253, 255, "20110629"));
        ohlc.add(createDTO(254, 256, 254, 255, "20110628"));
        ohlc.add(createDTO(247, 256, 247, 254, "20110627"));
        ohlc.add(createDTO(244, 249, 243, 248, "20110624"));
        ohlc.add(createDTO(244, 245, 243, 244, "20110623"));
        ohlc.add(createDTO(242, 244, 241, 244, "20110622"));
        ohlc.add(createDTO(243, 243, 241, 242, "20110621"));
        ohlc.add(createDTO(246, 247, 244, 244, "20110620"));
        ohlc.add(createDTO(248, 249, 246, 246, "20110617"));
        ohlc.add(createDTO(251, 253, 250, 250, "20110616"));
        ohlc.add(createDTO(249, 253, 249, 253, "20110615"));
        ohlc.add(createDTO(248, 250, 246, 250, "20110614"));
        ohlc.add(createDTO(249, 250, 247, 250, "20110613"));
        ohlc.add(createDTO(254, 254, 250, 250, "20110610"));
        ohlc.add(createDTO(254, 255, 251, 255, "20110609"));
        ohlc.add(createDTO(252, 254, 251, 254, "20110608"));
        ohlc.add(createDTO(250, 253, 250, 252, "20110607"));
        ohlc.add(createDTO(251, 252, 247, 250, "20110603"));
        ohlc.add(createDTO(253, 254, 252, 254, "20110602"));
        ohlc.add(createDTO(250, 254, 250, 254, "20110601"));
        ohlc.add(createDTO(250, 252, 248, 250, "20110531"));
        ohlc.add(createDTO(253, 254, 250, 251, "20110530"));
        ohlc.add(createDTO(255, 256, 253, 253, "20110527"));
        ohlc.add(createDTO(256, 257, 253, 254, "20110526"));
        ohlc.add(createDTO(256, 257, 254, 256, "20110525"));
        ohlc.add(createDTO(265, 265, 257, 257, "20110524"));
        ohlc.add(createDTO(265, 266, 265, 265, "20110523"));
        ohlc.add(createDTO(267, 268, 265, 266, "20110520"));
        ohlc.add(createDTO(264, 267, 264, 267, "20110519"));
        ohlc.add(createDTO(264, 266, 262, 265, "20110518"));
        ohlc.add(createDTO(266, 267, 264, 264, "20110517"));
        ohlc.add(createDTO(264, 267, 263, 267, "20110516"));
        ohlc.add(createDTO(266, 267, 264, 264, "20110513"));
        ohlc.add(createDTO(269, 269, 266, 268, "20110512"));
        ohlc.add(createDTO(267, 269, 266, 269, "20110511"));
        ohlc.add(createDTO(266, 268, 266, 267, "20110510"));
        ohlc.add(createDTO(264, 268, 263, 266, "20110509"));
        ohlc.add(createDTO(265, 268, 265, 267, "20110506"));
        ohlc.add(createDTO(271, 271, 266, 266, "20110505"));
        ohlc.add(createDTO(271, 273, 269, 273, "20110504"));
        ohlc.add(createDTO(268, 271, 267, 271, "20110503"));
        ohlc.add(createDTO(273, 275, 268, 268, "20110429"));
        ohlc.add(createDTO(274, 276, 270, 272, "20110428"));
        ohlc.add(createDTO(275, 277, 273, 273, "20110427"));
        ohlc.add(createDTO(280, 280, 276, 276, "20110426"));
        ohlc.add(createDTO(282, 283, 280, 281, "20110425"));
        ohlc.add(createDTO(282, 283, 281, 282, "20110422"));
        ohlc.add(createDTO(280, 281, 279, 280, "20110421"));
        ohlc.add(createDTO(283, 283, 279, 279, "20110420"));
        ohlc.add(createDTO(284, 286, 283, 285, "20110419"));
        ohlc.add(createDTO(283, 286, 282, 285, "20110418"));
        ohlc.add(createDTO(285, 285, 283, 284, "20110415"));
        ohlc.add(createDTO(280, 285, 279, 285, "20110414"));
        ohlc.add(createDTO(281, 283, 280, 282, "20110413"));
        ohlc.add(createDTO(283, 286, 282, 282, "20110412"));
        ohlc.add(createDTO(280, 283, 279, 283, "20110411"));
        ohlc.add(createDTO(280, 281, 279, 280, "20110408"));
        ohlc.add(createDTO(276, 280, 276, 280, "20110407"));
        ohlc.add(createDTO(273, 276, 272, 276, "20110406"));
        ohlc.add(createDTO(275, 276, 271, 272, "20110404"));
        ohlc.add(createDTO(275, 276, 273, 275, "20110401"));

        mMyChartsView.setOHLCData(ohlc);
        mMyChartsView.postInvalidate();

        return view;
    }

    private FXCandleDTO createDTO(float open, float high, float low, float close, String date)
    {
        FXCandleDTO fxCandleDTO = new FXCandleDTO();
        fxCandleDTO.openMid = open;
        fxCandleDTO.highMid = high;
        fxCandleDTO.lowMid = low;
        fxCandleDTO.closeMid = close;
        fxCandleDTO.time = date;
        return fxCandleDTO;
    }
}
