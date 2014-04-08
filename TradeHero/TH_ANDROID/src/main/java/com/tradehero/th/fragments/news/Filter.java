package com.tradehero.th.fragments.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tradehero on 14-3-14.
 */
public interface Filter {


    //int getCurrentPage();
    boolean hasSpinner(int page);
    List<SpinnerItemData> getSpinnerData(int page);
    boolean isSubTitleVisible(int page);
    String getTitle(int page);
    String getSubTitle(int page);
    int getSpinnerLayout(int page);

}

class SpinnerItemData {
    int id;
    String title;
    int iconRes;

    public SpinnerItemData(){
    }
    public SpinnerItemData(int id,String title,  int iconRes) {
        this.title = title;
        this.id = id;
        this.iconRes = iconRes;
    }

    @Override
    public String toString() {
        return title;
    }
}

class PageData {
    int page;
    boolean hasSpinner;
    int spinnerItemLayout;
    List<SpinnerItemData> spinnerData;
    String title;
    String subTitle;
    boolean hasSubTitle;


    PageData() {
    }

    /**
     *
     * @param page
     * @param hasSpinner
     * @param spinnerItemLayout
     * @param title
     * @param spinnerData
     * @param subTitle
     * @param hasSubTitle
     */
    PageData(int page, String title ,boolean hasSubTitle,String subTitle, boolean hasSpinner, int spinnerItemLayout,List<SpinnerItemData> spinnerData) {
        this.page = page;
        this.hasSpinner = hasSpinner;
        this.spinnerItemLayout = spinnerItemLayout;
        this.title = title;
        this.spinnerData = spinnerData;
        this.subTitle = subTitle;
        this.hasSubTitle = hasSubTitle;
    }
}

class BaseFilter implements  Filter{


    private int currentPage = 0;

    protected Map<Integer,PageData> dataMap = new HashMap<Integer,PageData>();

    public BaseFilter(){
    }

    public BaseFilter(Map<Integer,PageData> dataMap){
        dataMap.putAll(dataMap);
    }

    public void setPageData(int page,PageData data){
        dataMap.put(page,data);
    }

    public void setPageData(Map<Integer,PageData> dataMap){
        dataMap.clear();
        dataMap.putAll(dataMap);
    }

//    @Override
//    public int getCurrentPage() {
//        return currentPage;
//    }

    @Override
    public boolean hasSpinner(int page) {
        PageData data = getPageData(page);
        return data.hasSpinner;
    }

    @Override
    public List<SpinnerItemData> getSpinnerData(int page) {
        PageData data = getPageData(page);
        List<SpinnerItemData> dataCopy = new ArrayList<SpinnerItemData>(data.spinnerData.size());
        dataCopy.addAll(data.spinnerData);
        return dataCopy;
    }

    @Override
    public boolean isSubTitleVisible(int page) {
        PageData data = getPageData(page);
        return data.hasSubTitle;
    }

    @Override
    public String getTitle(int page) {
        PageData data = getPageData(page);
        return data.title;
    }

    @Override
    public String getSubTitle(int page) {
        PageData data = getPageData(page);
        return data.subTitle;
    }

    @Override
    public int getSpinnerLayout(int page) {
        PageData data = getPageData(page);
        return data.spinnerItemLayout;
    }

    private PageData getPageData(int page){
        PageData data = dataMap.get(page);
        return data;
    }
}
