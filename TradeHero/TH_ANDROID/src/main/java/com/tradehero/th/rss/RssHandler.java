
package com.tradehero.th.rss;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class RssHandler extends DefaultHandler
{

    private RssFeed rssFeed;
    private RssItem rssItem;
    private StringBuilder stringBuilder;
    private static SimpleDateFormat dateFormat = null;

    @Override
    public void startDocument()
    {
        rssFeed = new RssFeed();
        dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
    }

    /** Return the parsed RssFeed with it's RssItems */
    public RssFeed getResult()
    {
        return rssFeed;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
    {
        stringBuilder = new StringBuilder();

        if (qName.equals("item") && rssFeed != null)
        {
            rssItem = new RssItem();
            //rssItem.setFeed(rssFeed);
            rssFeed.addRssItem(rssItem);
        }
        else
        {

            if (rssItem != null)
            {
                if (localName.equals("content"))
                {
                    rssItem.setThumbnail(attributes.getValue("url"));
                }
                else if (localName.equals("thumbnail"))
                {
                    rssItem.setThumbnail(attributes.getValue("url"));
                }
                else if (localName.equals("enclosure"))
                {
                    rssItem.setThumbnail(attributes.getValue("url"));
                }
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
    {
        stringBuilder.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName)
    {

        if (rssFeed != null && rssItem == null)
        {
            // Parse feed properties

            try
            {

                String methodName =
                        "set" + qName.substring(0, 1).toUpperCase() + qName.substring(1);
                Method method = rssFeed.getClass().getMethod(methodName, String.class);
                method.invoke(rssFeed, stringBuilder.toString());
            } catch (SecurityException e)
            {
            } catch (NoSuchMethodException e)
            {
            } catch (IllegalArgumentException e)
            {
            } catch (IllegalAccessException e)
            {
            } catch (InvocationTargetException e)
            {
            }
        }
        else if (rssItem != null)
        {
            // Parse item properties

            try
            {

                if (qName.equals("content:encoded"))
                {
                    qName = "content";
                }

                String methodName =
                        "set" + qName.substring(0, 1).toUpperCase() + qName.substring(1);
                Method method = rssItem.getClass().getMethod(methodName, String.class);
                method.invoke(rssItem, stringBuilder.toString());
            } catch (SecurityException e)
            {
            } catch (NoSuchMethodException e)
            {
            } catch (IllegalArgumentException e)
            {
            } catch (IllegalAccessException e)
            {
            } catch (InvocationTargetException e)
            {
            }
        }
    }

    public static SimpleDateFormat getRssDateFormat()
    {

        if (dateFormat != null)
        {
            return dateFormat;
        }
        else
        {
            return dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        }
    }
}
