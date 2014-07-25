package com.academy.chitchat;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;


public class FileDownloader {
	public static final String CHECK_FILE = "save" ;
	public static final String GET_NEW = "new" ;
	private String save_mode ;
	private Context mContext = null ;
	public FileDownloader() {
		setSaveMode(GET_NEW);
	}
	public FileDownloader(String save_mode) {
		setSaveMode(save_mode);
	}
	public FileDownloader(String save_mode, Context mContext) {
		this.mContext = mContext ;
		setSaveMode(save_mode);
	}
	public void setSaveMode(String save_mode) {
		this.save_mode = save_mode ;
	}
	static FlushedInputStream getSavedFile(String url,Context mContext) {
		if(mContext==null) {
			return null ;
		}
		FileInputStream fis;
		try {
			String filename = URLEncoder.encode(url,"UTF-8");
			fis = mContext.openFileInput(filename);
			return new FlushedInputStream(fis);
		} catch (Exception e) {
			//e.printStackTrace();
		} 
		return null ;
	}
	static void saveStream(FlushedInputStream fis, String url, Context mContext) {
		if(mContext==null || fis==null) {
			return ;
		}
		FileOutputStream fos;
		try {
			String filename = URLEncoder.encode(url,"UTF-8");
			fos = mContext.openFileOutput(filename, Context.MODE_PRIVATE);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			fos.write(stream.toByteArray());
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	static FlushedInputStream downloadFILE(String url) {
		return downloadFile(url,GET_NEW,null,url);
	}
	static FlushedInputStream downloadFile(String url,final String save_mode,Context mContext) {
		return downloadFile(url,save_mode,mContext,url);
	}
	static FlushedInputStream downloadFile(String url,final String save_mode,Context mContext,String initial_url) {
		if(save_mode.equals(CHECK_FILE)) {
			FlushedInputStream fis = getSavedFile(initial_url,mContext);
			if(fis!=null) {
				return fis ;
			}
		}
	    final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
	    final HttpGet getRequest = new HttpGet(url);

	    try {
	        HttpResponse response = client.execute(getRequest);
	        final int statusCode = response.getStatusLine().getStatusCode();
	        if (statusCode != HttpStatus.SC_OK) { 
	        	if(statusCode == HttpStatus.SC_MOVED_TEMPORARILY || statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {
	        		//handle redirect
	        		return downloadFile(response.getFirstHeader("Location").getValue(),save_mode,mContext,initial_url);
	        	}
	            Log.w("FileDownloader", "Error " + statusCode + " while retrieving stream from " + url); 
	            return null;
	        }
	        
	        final HttpEntity entity = response.getEntity();
	        if (entity != null) {
	            InputStream inputStream = null;
	            try {
	                inputStream = entity.getContent(); 
	                FlushedInputStream fis = new FlushedInputStream(inputStream);
	                if(save_mode.equals(CHECK_FILE)) {
	                	saveStream(fis,initial_url,mContext);
	                }
	                return getSavedFile(initial_url, mContext);
	            } finally {
	                if (inputStream != null) {
	                    inputStream.close();  
	                }
	                entity.consumeContent();
	            }
	        }
	    } catch (Exception e) {
	        // Could provide a more explicit error message for IOException or IllegalStateException
	        getRequest.abort();
	        Log.w("FileDownloader", "Error while retrieving stream from " + url, e);
	    } finally {
	        if (client != null) {
	            client.close();
	        }
	    }
	    return null;
	}
	
	static class FlushedInputStream extends FilterInputStream {
	    public FlushedInputStream(InputStream inputStream) {
	        super(inputStream);
	    }

	    @Override
	    public long skip(long n) throws IOException {
	        long totalBytesSkipped = 0L;
	        while (totalBytesSkipped < n) {
	            long bytesSkipped = in.skip(n - totalBytesSkipped);
	            if (bytesSkipped == 0L) {
	                  int bytes = read();
	                  if (bytes < 0) {
	                      break;  // we reached EOF
	                  } else {
	                      bytesSkipped = 1; // we read one byte
	                  }
	           }
	            totalBytesSkipped += bytesSkipped;
	        }
	        return totalBytesSkipped;
	    }
	}
	
	
	
	private static boolean cancelPotentialDownload(String url, FileReceiver fileR) {
	    FileDownloaderTask fileDownloaderTask = getFileDownloaderTask(fileR);

	    if (fileDownloaderTask != null) {
	        String fileUrl = fileDownloaderTask.url;
	        if ((fileUrl == null) || (!fileUrl.equals(url))) {
	            fileDownloaderTask.cancel(true);
	        } else {
	            // The same URL is already being downloaded.
	            return false;
	        }
	    }
	    return true;
	}
	
	private static FileDownloaderTask getFileDownloaderTask(FileReceiver fileR) {
	    if (fileR != null) {
	    	return fileR.getFileDownloaderTask();
	    }
	    return null;
	}

	public void download(String url, FileReceiver fileR) {
		if (cancelPotentialDownload(url, fileR)) {
            FileDownloaderTask task = new FileDownloaderTask(fileR);
            fileR.setFileDownloaderTask(task);
            //DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
            //imageView.setImageDrawable(downloadedDrawable);
            task.execute(url);
        }
	}
	public class FileDownloaderTask extends AsyncTask<String, Void, FlushedInputStream> {
	    private String url;
	    private final WeakReference<FileReceiver> fileReceiverReference;

	    public FileDownloaderTask(FileReceiver fileR) {
	        fileReceiverReference = new WeakReference<FileReceiver>(fileR);
	    }

	    @Override
	    // Actual download method, run in the task thread
	    protected FlushedInputStream doInBackground(String... params) {
	        // params comes from the execute() call: params[0] is the url.
	    	// params[1] is the crop type
	    	return downloadFile(params[0],save_mode,mContext);
	    }

	    @Override
	    // Once the image is downloaded, sends it to the fileReceiver
	    protected void onPostExecute(FlushedInputStream fis) {
	        if (isCancelled()) {
	            fis = null;
	        }

	        if (fileReceiverReference != null) {
	        	FileReceiver fileR = fileReceiverReference.get();
	            if (fileR != null) {
	                fileR.receive(fis);
	            }
	        }
	    }
	}
	static class DownloadedDrawable extends ColorDrawable {
	    private final WeakReference<FileDownloaderTask> fileDownloaderTaskReference;

	    public DownloadedDrawable(FileDownloaderTask fileDownloaderTask) {
	        super(Color.BLACK);
	        fileDownloaderTaskReference =
	            new WeakReference<FileDownloaderTask>(fileDownloaderTask);
	    }

	    public FileDownloaderTask getFileDownloaderTask() {
	        return fileDownloaderTaskReference.get();
	    }
	}
	public interface FileReceiver {
		public void receive(FlushedInputStream fis);
		public void setFileDownloaderTask(FileDownloaderTask bdt);
		public FileDownloaderTask getFileDownloaderTask();
	}
}