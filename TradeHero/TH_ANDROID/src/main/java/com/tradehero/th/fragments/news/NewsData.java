package com.tradehero.th.fragments.news;

import android.text.TextUtils;
import com.tradehero.th.R;
import com.tradehero.th.api.news.CountryLanguagePairDTO;
import com.tradehero.th.api.news.NewsItemCategoryDTO;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NewsData
{
    /**
     * Regions of news
     */
    static String countriesPairString = "[" +
            "        {" +
            "            'name': '中国 (China)'," +
            "            'countryCode': 'CN'," +
            "            'languageCode': 'zh'" +
            "        }," +
            "        {" +
            "            'name': 'Argentina'," +
            "            'countryCode': 'AR'," +
            "            'languageCode': 'es'" +
            "        }," +
            "        {" +
            "            'name': 'Australia'," +
            "            'countryCode': 'AU'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'U.K.'," +
            "            'countryCode': 'GB'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'België'," +
            "            'countryCode': 'BE'," +
            "            'languageCode': 'nl'" +
            "        }," +
            "        {" +
            "            'name': 'Belgique'," +
            "            'countryCode': 'BE'," +
            "            'languageCode': 'fr'" +
            "        }," +
            "        {" +
            "            'name': 'Botswana'," +
            "            'countryCode': 'BW'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'Brasil'," +
            "            'countryCode': 'BR'," +
            "            'languageCode': 'pt'" +
            "        }," +
            "        {" +
            "            'name': 'Canada English'," +
            "            'countryCode': 'CA'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'Canada Français'," +
            "            'countryCode': 'CA'," +
            "            'languageCode': 'fr'" +
            "        }," +
            "        {" +
            "            'name': 'Ceská republika:'," +
            "            'countryCode': 'CZ'," +
            "            'languageCode': 'cs'" +
            "        }," +
            "        {" +
            "            'name': 'Chile:'," +
            "            'countryCode': 'CL'," +
            "            'languageCode': 'es'" +
            "        }," +
            "        {" +
            "            'name': 'Colombia'," +
            "            'countryCode': 'CO'," +
            "            'languageCode': 'es'" +
            "        }," +
            "        {" +
            "            'name': 'Cuba'," +
            "            'countryCode': 'CU'," +
            "            'languageCode': 'es'" +
            "        }," +
            "        {" +
            "            'name': 'Deutschland'," +
            "            'countryCode': 'DE'," +
            "            'languageCode': 'de'" +
            "        }," +
            "        {" +
            "            'name': 'España'," +
            "            'countryCode': 'ES'," +
            "            'languageCode': 'es'" +
            "        }," +
            "        {" +
            "            'name': 'Estados Unidos'," +
            "            'countryCode': 'US'," +
            "            'languageCode': 'es'" +
            "        }," +
            "        {" +
            "            'name': 'Ethiopia'," +
            "            'countryCode': 'ET'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'France'," +
            "            'countryCode': 'FR'," +
            "            'languageCode': 'fr'" +
            "        }," +
            "        {" +
            "            'name': 'Ghana'," +
            "            'countryCode': 'GH'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'India'," +
            "            'countryCode': 'IN'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'Ireland'," +
            "            'countryCode': 'IE'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'Israel English'," +
            "            'countryCode': 'IL'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'Italia'," +
            "            'countryCode': 'IT'," +
            "            'languageCode': 'it'" +
            "        }," +
            "        {" +
            "            'name': 'Kenya'," +
            "            'countryCode': 'KE'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'Magyarország'," +
            "            'countryCode': 'HU'," +
            "            'languageCode': 'hu'" +
            "        }," +
            "        {" +
            "            'name': 'Malaysia'," +
            "            'countryCode': 'MY'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'Maroc'," +
            "            'countryCode': 'MA'," +
            "            'languageCode': 'fr'" +
            "        }," +
            "        {" +
            "            'name': 'México'," +
            "            'countryCode': 'MX'," +
            "            'languageCode': 'es'" +
            "        }," +
            "        {" +
            "            'name': 'Namibia'," +
            "            'countryCode': 'NA'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'Nederland'," +
            "            'countryCode': 'NL'," +
            "            'languageCode': 'nl'" +
            "        }," +
            "        {" +
            "            'name': 'New Zealand'," +
            "            'countryCode': 'NZ'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'Nigeria'," +
            "            'countryCode': 'NG'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'Norge'," +
            "            'countryCode': 'NO'," +
            "            'languageCode': 'nb'" +
            "        }," +
            "        {" +
            "            'name': 'Österreich'," +
            "            'countryCode': 'AT'," +
            "            'languageCode': 'de'" +
            "        }," +
            "        {" +
            "            'name': 'Pakistan'," +
            "            'countryCode': 'PK'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'Perú'," +
            "            'countryCode': 'PE'," +
            "            'languageCode': 'es'" +
            "        }," +
            "        {" +
            "            'name': 'Philippines'," +
            "            'countryCode': 'PH'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'Polska'," +
            "            'countryCode': 'PL'," +
            "            'languageCode': 'pl'" +
            "        }," +
            "        {" +
            "            'name': 'Portugal'," +
            "            'countryCode': 'PT'," +
            "            'languageCode': 'pt'" +
            "        }," +
            "        {" +
            "            'name': 'Schweiz'," +
            "            'countryCode': 'CH'," +
            "            'languageCode': 'de'" +
            "        }," +
            "        {" +
            "            'name': 'Sénégal'," +
            "            'countryCode': 'SN'," +
            "            'languageCode': 'fr'" +
            "        }," +
            "        {" +
            "            'name': 'Singapore'," +
            "            'countryCode': 'SG'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'South Africa'," +
            "            'countryCode': 'ZA'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'Suisse'," +
            "            'countryCode': 'CH'," +
            "            'languageCode': 'fr'" +
            "        }," +
            "        {" +
            "            'name': 'Sverige'," +
            "            'countryCode': 'SE'," +
            "            'languageCode': 'sv'" +
            "        }," +
            "        {" +
            "            'name': 'Tanzania'," +
            "            'countryCode': 'TZ'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'Türkiye'," +
            "            'countryCode': 'TR'," +
            "            'languageCode': 'tr'" +
            "        }," +
            "        {" +
            "            'name': 'U.S.'," +
            "            'countryCode': 'US'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'Uganda'," +
            "            'countryCode': 'UG'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'Venezuela'," +
            "            'countryCode': 'VE'," +
            "            'languageCode': 'es'" +
            "        }," +
            "        {" +
            "            'name': 'Vi?t Nam (Vietnam)'," +
            "            'countryCode': 'VN'," +
            "            'languageCode': 'vi'" +
            "        }," +
            "        {" +
            "            'name': 'Zimbabwe'," +
            "            'countryCode': 'ZW'," +
            "            'languageCode': 'en'" +
            "        }," +
            "        {" +
            "            'name': 'Ελλάδα (Greece)'," +
            "            'countryCode': 'GR'," +
            "            'languageCode': 'el'" +
            "        }," +
            "        {" +
            "            'name': 'Россия (Russia)'," +
            "            'countryCode': 'RU'," +
            "            'languageCode': 'ru'" +
            "        }," +
            "        {" +
            "            'name': 'Србија (Serbia)'," +
            "            'countryCode': 'RS'," +
            "            'languageCode': 'sr'" +
            "        }," +
            "        {" +
            "            'name': 'Украина/русский (Ukraine)'," +
            "            'countryCode': 'UA'," +
            "            'languageCode': 'ru'" +
            "        }," +
            "        {" +
            "            'name': 'Україна/українська (Ukraine)'," +
            "            'countryCode': 'UA'," +
            "            'languageCode': 'uk'" +
            "        }," +
            "        {" +
            "            'name': 'ישראל (Israel)'," +
            "            'countryCode': 'IL'," +
            "            'languageCode': 'he'" +
            "        }," +
            "        {" +
            "            'name': 'الإمارات (UAE)'," +
            "            'countryCode': 'AE'," +
            "            'languageCode': 'ar'" +
            "        }," +
            "        {" +
            "            'name': 'السعودية (KSA)'," +
            "            'countryCode': 'SA'," +
            "            'languageCode': 'ar'" +
            "        }," +
            "        {" +
            "            'name': 'العالم العربي (Arabic)'," +
            "            'countryCode': 'ME'," +
            "            'languageCode': 'ar'" +
            "        }," +
            "        {" +
            "            'name': 'لبنان (Lebanon)'," +
            "            'countryCode': 'LB'," +
            "            'languageCode': 'ar'" +
            "        }," +
            "        {" +
            "            'name': 'مصر (Egypt)'," +
            "            'countryCode': 'EG'," +
            "            'languageCode': 'ar'" +
            "        }," +
            "        {" +
            "            'name': 'हिन्दी (India)'," +
            "            'countryCode': 'IN'," +
            "            'languageCode': 'hi'" +
            "        }," +
            "        {" +
            "            'name': 'தமிழ் (India)'," +
            "            'countryCode': 'IN'," +
            "            'languageCode': 'ta'" +
            "        }," +
            "        {" +
            "            'name': 'తెలుగు (India)'," +
            "            'countryCode': 'IN'," +
            "            'languageCode': 'te'" +
            "        }," +
            "        {" +
            "            'name': 'മലയാളം (India)'," +
            "            'countryCode': 'IN'," +
            "            'languageCode': 'ml'" +
            "        }," +
            "        {" +
            "            'name': '한국 (Korea)'," +
            "            'countryCode': 'KR'," +
            "            'languageCode': 'ko'" +
            "        }," +
            "        {" +
            "            'name': '台灣 (Taiwan)'," +
            "            'countryCode': 'TW'," +
            "            'languageCode': 'zh'" +
            "        }," +
            "        {" +
            "            'name': '香港 (Hong Kong)'," +
            "            'countryCode': 'HK'," +
            "            'languageCode': 'zh'" +
            "        }" +
            "    ]";

    /**
     * Categories of social media news
     */
    static String categoriesString = "[" +
            "        {" +
            "            'id': 6," +
            "            'name': 'Business_Finance'" +
            "        }," +
            "        {" +
            "            'id': 8," +
            "            'name': 'Politics'" +
            "        }," +
            "        {" +
            "            'id': 2," +
            "            'name': 'Technology_Internet'" +
            "        }" +
            "    ]";

    public static final List<CountryLanguagePairDTO> buildCountriesPair()
    {
        List<CountryLanguagePairDTO> languagePairDTOList = null;
        try
        {

            JSONArray jsonArray = new JSONArray(countriesPairString);
            int len = jsonArray.length();
            languagePairDTOList = new ArrayList<CountryLanguagePairDTO>(len);
            for (int i = 0; i < len; i++)
            {
                JSONObject o = jsonArray.getJSONObject(i);
                String name = o.getString("name");
                String countryCode = o.getString("countryCode");
                String languageCode = o.getString("languageCode");

                CountryLanguagePairDTO dto =
                        new CountryLanguagePairDTO(name, countryCode, languageCode);
                languagePairDTOList.add(dto);
            }
        } catch (JSONException e)
        {

        }
        return languagePairDTOList;
    }

    public static final List<NewsItemCategoryDTO> buildSocialCategories()
    {
        List<NewsItemCategoryDTO> list = null;
        try
        {

            JSONArray jsonArray = new JSONArray(categoriesString);
            int len = jsonArray.length();
            list = new ArrayList<NewsItemCategoryDTO>(len);
            for (int i = 0; i < len; i++)
            {
                JSONObject o = jsonArray.getJSONObject(i);
                int id = o.getInt("id");
                String name = o.getString("name");

                NewsItemCategoryDTO dto = new NewsItemCategoryDTO(id, name);
                list.add(dto);
            }
        } catch (JSONException e)
        {

        }
        return list;
    }

    /**
     * News filter
     */
    public static enum PageTab
    {

        REGION_NEWS(0, "Regional", null, true, R.layout.trending_filter_spinner_dropdown_item),
        MY_HEADLINE_NEWS(1, "My Headline", "From Portfolios and Watchlist", false,
                R.layout.trending_filter_spinner_dropdown_item),
        SOCIAL_NEWS(2, "Social", "From Social Media", true,
                R.layout.trending_filter_spinner_dropdown_item);

        public final int page;
        public final String title;
        public final String desc;
        public final boolean haveSubFilter;
        public final int spinnerItemLayout;
        public final boolean haveDesc;

        private PageTab(int page, String title, String desc, boolean haveSubFilter,
                int spinnerItemLayout)
        {
            this.page = page;
            this.title = title;
            this.desc = desc;
            this.haveDesc = !TextUtils.isEmpty(desc);
            this.haveSubFilter = haveSubFilter;
            this.spinnerItemLayout = spinnerItemLayout;
        }
    }
}
