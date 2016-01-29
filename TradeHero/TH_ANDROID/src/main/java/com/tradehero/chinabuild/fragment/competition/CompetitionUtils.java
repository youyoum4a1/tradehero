package com.tradehero.chinabuild.fragment.competition;

import com.tradehero.th.api.market.ExchangeCompactDTO;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import timber.log.Timber;

import java.util.ArrayList;

/**
 * Created by huhaiping on 14-9-10. 比赛相关常量定义
 */
public class CompetitionUtils
{

    public static final int UGC_PROVIDER_ID = 33;

    public static final int COMPETITION_PAGE_ALL = 0;//所有比赛
    public static final int COMPETITION_PAGE_MINE = 1;//我的比赛
    public static final int COMPETITION_PAGE_SEARCH = 2;//搜索出来的比赛

    public static final String[] strPeriods =
            {
                    "一个周",
                    "二个周",
                    "一个月",
                    "二个月",
                    "三个月",
            };

    public static final int[] strDuration =
            {
                    7, 14, 30, 60, 90,
            };

    public static final int EXCHANGE_CHINA = 0;//沪深
    public static final int EXCHANGE_HK = 1;//港股
    public static final int EXCHANGE_AM = 2;//美股

    public static final int[][] Exchanges = {
            {26, 27},
            {9},
            {1, 4, 20}
    };

    public static String getExchangeShortName(int[] exchangeList)
    {
        boolean ch = false;
        boolean hk = false;
        boolean am = false;
        if (exchangeList != null)
        {
            for (int i = 0; i < exchangeList.length; i++)
            {
                if (exchangeList[i] == 26 || exchangeList[i] == 27)
                {
                    ch = true;
                }
                else if (exchangeList[i] == 9)
                {
                    hk = true;
                }
                else if (exchangeList[i] == 1 || exchangeList[i] == 4 || exchangeList[i] == 20)
                {
                    am = true;
                }
            }
        }
        return "" + (ch ? "沪深" : "") + (hk ? "港" : "") + (am ? "美" : "");
    }

    public static int[] getExchanges(boolean CH, boolean HK, boolean AM)
    {

        ArrayList<Integer> array = new ArrayList<Integer>();
        if (CH)
        {
            array.add(26);
            array.add(27);
        }
        if (HK)
        {
            array.add(9);
        }
        if (AM)
        {
            array.add(1);
            array.add(4);
            array.add(20);
        }

        int sizeExchange = array.size();
        int[] retExchange = new int[sizeExchange];
        for (int i = 0; i < sizeExchange; i++)
        {
            retExchange[i] = array.get(i);
        }

        Timber.d("retExchange:" + retExchange.toString());
        return retExchange;
    }

    //
    //[{"id":1,"name":"NYSE","countryCode":"US","desc":"美国 NYSE","isInternal":false,"isIncludedInTrending":true},
    // {"id":2,"name":"LSE","countryCode":"GB","desc":"英国 LSE","isInternal":false,"isIncludedInTrending":true},
    // {"id":3,"name":"SGX","countryCode":"SG","desc":"新加坡 SGX","isInternal":false,"isIncludedInTrending":true},
    // {"id":4,"name":"NASDAQ","countryCode":"US","desc":"美国 NASDAQ","isInternal":false,"isIncludedInTrending":true},
    // {"id":5,"name":"ASX","countryCode":"AU","desc":"澳大利亚 ASX","isInternal":false,"isIncludedInTrending":true},
    // {"id":6,"name":"OTCBB","countryCode":"US","desc":"美国 OTCBB","isInternal":false,"isIncludedInTrending":true},
    // {"id":9,"name":"HKEX","countryCode":"HK","desc":"香港 HKEX","isInternal":false,"isIncludedInTrending":true},
    // {"id":10,"name":"PAR","countryCode":"FR","desc":"法国 PAR","isInternal":false,"isIncludedInTrending":true},
    // {"id":11,"name":"AMS","countryCode":"NL","desc":"荷兰 AMS","isInternal":false,"isIncludedInTrending":false},
    // {"id":12,"name":"BRU","countryCode":"BE","desc":"比利时 BRU","isInternal":false,"isIncludedInTrending":false},
    // {"id":13,"name":"LIS","countryCode":"PT","desc":"葡萄牙 LIS","isInternal":false,"isIncludedInTrending":false},
    // {"id":16,"name":"MLSE","countryCode":"IT","desc":"意大利 MLSE","isInternal":false,"isIncludedInTrending":true},
    // {"id":18,"name":"TSX","countryCode":"CA","desc":"加拿大 TSX","isInternal":false,"isIncludedInTrending":true},
    // {"id":19,"name":"TSXV","countryCode":"CA","desc":"加拿大 TSXV","isInternal":false,"isIncludedInTrending":true},
    // {"id":20,"name":"AMEX","countryCode":"US","desc":"美国 AMEX","isInternal":false,"isIncludedInTrending":true},
    // {"id":25,"name":"NZX","countryCode":"NZ","desc":"新西兰 NZX","isInternal":false,"isIncludedInTrending":true},
    // {"id":26,"name":"SHA","countryCode":"CN","desc":"中国上海 SHA","isInternal":false,"isIncludedInTrending":true},
    // {"id":27,"name":"SHE","countryCode":"CN","desc":"中国深圳 SHE","isInternal":false,"isIncludedInTrending":true},
    // {"id":29,"name":"JKT","countryCode":"ID","desc":"印度尼西亚 JKT","isInternal":false,"isIncludedInTrending":true},
    // {"id":32,"name":"KDQ","countryCode":"KR","desc":"韩国 KDQ","isInternal":false,"isIncludedInTrending":true},
    // {"id":34,"name":"KRX","countryCode":"KR","desc":"韩国 KRX","isInternal":false,"isIncludedInTrending":true},
    // {"id":37,"name":"TPE","countryCode":"TW","desc":"台湾 TPE","isInternal":false,"isIncludedInTrending":true},
    // {"id":38,"name":"SET","countryCode":"TH","desc":"泰国 SET","isInternal":false,"isIncludedInTrending":true},
    // {"id":39,"name":"PSE","countryCode":"PH","desc":"菲律宾 PSE","isInternal":false,"isIncludedInTrending":true},
    // {"id":41,"name":"MYX","countryCode":"MY","desc":"马来西亚 MYX","isInternal":false,"isIncludedInTrending":true},
    // {"id":42,"name":"NSE","countryCode":"IN","desc":"印度 NSE","isInternal":false,"isIncludedInTrending":true},
    // {"id":43,"name":"INDEX","countryCode":"GB","desc":"英国 INDEX","isInternal":true,"isIncludedInTrending":false},
    // {"id":44,"name":"BSE","countryCode":"IN","desc":"印度 BSE","isInternal":false,"isIncludedInTrending":true},
    // {"id":45,"name":"TSE","countryCode":"JP","desc":"日本 TSE","isInternal":false,"isIncludedInTrending":true}]

    public static final String[][] strExchanges =
            {
                    {"1", "NYSE"},
                    {"2", "LSE"},
                    {"3", "SGX"},
                    {"4", "NASDAQ"},
                    {"5", "ASX"},
                    {"6", "OTCBB"},
                    {"9", "HKEX"},
                    {"10", "PAR"},
                    {"11", "AMS"},
                    {"12", "BRU"},
                    {"13", "LIS"},
                    {"16", "MLSE"},
                    {"18", "TSX"},
                    {"19", "TSXV"},
                    {"20", "AMEX"},
                    {"25", "NZX"},
                    {"26", "SHA"},
                    {"27", "SHE"},
                    {"29", "JKT"},
                    {"32", "KDQ"},
                    {"34", "KRX"},
                    {"37", "TPE"},
                    {"38", "SET"},
                    {"39", "PSE"},
                    {"41", "MYX"},
                    {"42", "NSE"},
                    {"43", "INDEX"},
                    {"44", "BSE"},
                    {"45", "TSE"},
            };


    public static ExchangeCompactDTOList exchangeCompactDTOs;
    public static ExchangeCompactDTOList getExchangeList()
    {
        if(exchangeCompactDTOs!=null)return exchangeCompactDTOs;
        else
        {
            exchangeCompactDTOs = new ExchangeCompactDTOList();
            exchangeCompactDTOs.add(new ExchangeCompactDTO(1,"NYSE","US",0,"美国 NYSE",false,true,false));
            exchangeCompactDTOs.add(new ExchangeCompactDTO(6,"OTCBB","US",0,"美国 OTCBB",false,true,false));
            exchangeCompactDTOs.add(new ExchangeCompactDTO(20,"AMEX","US",0,"美国 AMEX",false,true,false));
            exchangeCompactDTOs.add(new ExchangeCompactDTO(4,"NASDAQ","US",0,"美国 NASDAQ",false,true,false));
            return exchangeCompactDTOs;
        }
    }
}
