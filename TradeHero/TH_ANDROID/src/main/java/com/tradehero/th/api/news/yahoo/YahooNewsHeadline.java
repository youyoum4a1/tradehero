package com.tradehero.th.api.news.yahoo;

import com.tradehero.th.api.news.NewsHeadline;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import timber.log.Timber;

public class YahooNewsHeadline implements NewsHeadline
{
    private final static DateFormat dateFormat = getDateFormat();
    private String title;
    private String url;
    private Date date;

    public YahooNewsHeadline()
    {
    }

    public YahooNewsHeadline(Node xmlNode)
    {
        NodeList elements = xmlNode.getChildNodes();
        for (int i = 0; i < elements.getLength(); ++i)
        {
            Node n = elements.item(i);
            if (n.getNodeName().equals("title"))
            {
                title = n.getFirstChild().getNodeValue();
            }
            else if (n.getNodeName().equals("pubDate"))
            {
                date = tryGetDate(n);

            }
            else if (n.getNodeName().equals("link"))
            {
                url = n.getFirstChild().getNodeValue();
            }
        }
    }

    private Date tryGetDate(Node n)
    {
        try
        {
            return dateFormat.parse(n.getFirstChild().getNodeValue());
        }
        catch (ParseException e)
        {
            Timber.e(e, "Failed to parse date");
        }
        return null;
    }

    private static SimpleDateFormat getDateFormat()
    {
        SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df;
    }

    public Date getDate()
    {
        return date;
    }

    public String getUrl()
    {
        return url;
    }

    public String getTitle()
    {
        return title;
    }
}
