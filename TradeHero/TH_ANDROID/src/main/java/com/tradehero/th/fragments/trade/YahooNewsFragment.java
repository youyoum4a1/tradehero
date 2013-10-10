package com.tradehero.th.fragments.trade;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockFragment;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.adapters.YahooNewsAdapter;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.yahoo.News;
import com.tradehero.th.network.service.YahooNewsService;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import javax.xml.xpath.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by julien on 10/10/13
 */
public class YahooNewsFragment extends SherlockFragment implements DTOCache.Listener<SecurityId, SecurityCompactDTO>
{
    private final static String TAG = YahooNewsFragment.class.getSimpleName();

    private SecurityId securityId;
    private SecurityCompactDTO securityCompactDTO;
    @Inject protected Lazy<SecurityCompactCache> securityCompactCache;
    @Inject protected Lazy<YahooNewsService> yahooNewsService;

    private ListView listView;
    private YahooNewsAdapter adapter;


    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_yahoo_news, container, false);
        loadViews(view);
        return view;
    }

    private void loadViews(View view)
    {
        listView = (ListView)view.findViewById(R.id.list_yahooNews);
        if (listView != null)
        {
            adapter = new YahooNewsAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.yahoo_news_item);
            listView.setAdapter(adapter);
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        Bundle args = getArguments();
        if (args != null)
        {
            linkWith(new SecurityId(args), true);
        }
        else
        {
            display();
        }
    }

    @Override public void onPause()
    {
        if (securityId != null)
        {
            securityCompactCache.get().unRegisterListener(this);
        }
        super.onPause();
    }


    public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId = securityId;
        if (this.securityId != null)
        {
            securityCompactCache.get().registerListener(this);
            linkWith(securityCompactCache.get().get(this.securityId), andDisplay);
        }
    }

    @Override public void onDTOReceived(SecurityId key, SecurityCompactDTO value)
    {
        if (key.equals(securityId))
        {
            linkWith(value, true);
        }
    }

    public void linkWith(SecurityCompactDTO securityCompactDTO, boolean andDisplay)
    {
        this.securityCompactDTO = securityCompactDTO;
        if (andDisplay)
        {
            display();
        }
    }

    public void display()
    {
        loadNews();
    }

    private void loadNews()
    {
        if (this.securityCompactDTO != null && this.securityCompactDTO.yahooSymbol != null)
        {
            yahooNewsService.get().getNews(this.securityCompactDTO.yahooSymbol, getCallBack());
        }
    }

    private Callback<Response> getCallBack()
    {
        return new Callback<Response>()
        {
            @Override
            public void success(Response response, Response r)
            {
                handleResponse(response);
            }

            @Override
            public void failure(RetrofitError error)
            {
                THLog.e(TAG, "fail to get news from yahoo", error);
            }
        };
    }

    private void handleResponse(Response response)
    {
        try
        {
            List<News> news = tryGettingNewsList(response);
            updateAdapter(news);

        } catch (XPathExpressionException e)
        {
            THLog.e(TAG, "Failed to compile XPath", e);
        }
        catch (IOException e)
        {
            THLog.e(TAG, "Failed to get response body", e);
        }
    }

        private List<News> tryGettingNewsList(Response response) throws XPathExpressionException, IOException
        {
            XPathExpression xpathItems = getxPathExpression();
            InputSource input = new InputSource(response.getBody().in());
            NodeList nodes = (NodeList)xpathItems.evaluate(input, XPathConstants.NODESET);
            List<News> result = processItems(nodes);
            return result;
        }

            private XPathExpression getxPathExpression() throws XPathExpressionException
            {
                XPathFactory factory=XPathFactory.newInstance();
                XPath xPath=factory.newXPath();
                return xPath.compile("//item");
            }

            private List<News> processItems(NodeList nodes)
            {
                List<News> result = new ArrayList<>();
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    result.add(new News(node));
                }
                return result;
            }

    private void updateAdapter(List<News> news)
    {
        adapter.setItems(news);
        adapter.notifyDataSetChanged();
    }
}
