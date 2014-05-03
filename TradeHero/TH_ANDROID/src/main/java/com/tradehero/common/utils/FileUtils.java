package com.tradehero.common.utils;

import java.io.File;


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
