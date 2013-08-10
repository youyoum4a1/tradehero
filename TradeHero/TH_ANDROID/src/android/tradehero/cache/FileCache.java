package android.tradehero.cache;

import java.io.File;

import android.content.Context;

public class FileCache {
    
    private File cacheDir;
    private final static String DIRECTORY = "TH";
    public FileCache(Context context){
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(), DIRECTORY);
        else
            cacheDir=context.getCacheDir();
        
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
    
    public File getFile(String url){
        //I identify images by hashcode. Not a perfect solution, good for the demo.
    	File f = null;
        //Another possible solution (thanks to grantland)
        //String filename = URLEncoder.encode(url);
        if(url!= null)
        {
        	String filename=String.valueOf(url.hashCode());
            
        	
        }
        	
        return f;
    }
    
    
    public void clear(){
        File[] files=cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }

}