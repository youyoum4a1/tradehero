package com.tradehero.common.utils;

import java.io.File;

/** Created with IntelliJ IDEA. User: xavier Date: 9/12/13 Time: 6:45 PM To change this template use File | Settings | File Templates. */
public class FileUtils
{
    public static long getFolderSize(File folder)
    {
        long total = 0;
        if (folder.isDirectory())
        {
            for (File file: folder.listFiles())
            {
                if (file.isFile())
                {
                    total += file.length();
                }
                else if (file.isDirectory())
                {
                    total += getFolderSize(file);
                }
                // TODO what do we need to do else?
            }
        }
        return total;
    }
}
