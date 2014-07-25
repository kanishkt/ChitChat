package com.academy.chitchat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.http.Header;
import org.json.JSONObject;

import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.academy.chitchat.RestClient;

public class ChitChatRestClient extends RestClient {
	final static String BASE_URL = "http://mwesly.com/chitchat/";
	final static String UPLOAD_URL = "upload/";
	final static String SOUND_KEY = "sound";
	final static String SENDER_KEY = "sender";
	final static String RECEIVERS_KEY = "receivers";
	@Override
	public String get_base_url() {
		return BASE_URL;
	}

	public void getData(AsyncHttpResponseHandler a) {
		get_raw("http://biocito.com/accounts/login/", null, a);

	}
	public void upload_csrf_endpoint(AsyncHttpResponseHandler responseHandler) {
		get(UPLOAD_URL,null,responseHandler);
	}
	public void upload_voice(InputStream is, String sender, String receivers, String csrfToken, AsyncHttpResponseHandler responseHandler) {
		
		RequestParams params = new RequestParams();
		
		
		params.put(SOUND_KEY, is, "file.txt");
		params.put(CSRFTOKEN_NAME,csrfToken);
		params.put(SENDER_KEY, sender);
		params.put(RECEIVERS_KEY, receivers);
		post(UPLOAD_URL,params,responseHandler);
	}
	public void upload_voice(String filename, String sender, String receivers, String csrfToken, AsyncHttpResponseHandler responseHandler) {
		
		File file = new File (filename);
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			upload_voice(fis,  sender,  receivers, csrfToken, responseHandler);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/*RequestParams params = new RequestParams();
		try {
			params.put(SOUND_KEY, file);
			params.put(CSRFTOKEN_NAME,csrfToken);
			post(UPLOAD_URL,params,responseHandler);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	public void upload(final String filename,final String sender, final String receivers) {
		// TODO Auto-generated method stub
		upload_csrf_endpoint(new AsyncHttpResponseHandler(){
			@Override
			@Deprecated
			public void onSuccess(String form) {
				System.out.println("GOT CSRF TOKEN");
				final String csrfToken = RestClient.getCSRFToken(form);
				System.out.println("IT IS: " + csrfToken);
				upload_voice(filename, sender, receivers, csrfToken, new JsonHttpResponseHandler(){

					@Override
					public void onSuccess(JSONObject content) {
						Log.e("one", "upload worked?");
						Log.e("two", content.toString());
						Log.e("three", "upload worked?");
						super.onSuccess(content);
					}
					@Override
					public void onFailure(Throwable error) {
						// TODO Auto-generated method stub
						System.out.println("UPLOAD FAILED!!");
						error.printStackTrace();
						super.onFailure(error);
					}
				});
				super.onSuccess(form);
			}
		});
	}


	}