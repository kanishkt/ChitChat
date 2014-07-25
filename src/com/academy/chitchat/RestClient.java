package com.academy.chitchat;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;

import android.content.Context;

import com.loopj.android.http.*;


public abstract class RestClient {
	public static final String CSRFTOKEN_NAME = "csrfmiddlewaretoken";
	
	protected AsyncHttpClient client = new AsyncHttpClient();
	protected PersistentCookieStore cs ;
	// core methods
	public abstract String get_base_url();
	
	
	protected String getAbsoluteUrl(String relativeUrl) {
		return get_base_url() + relativeUrl;
	}
	public void allowRedirects() {
		client.getHttpClient().getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
	}

	public void initializeCookieStore(Context mContext) {
		PersistentCookieStore myCookieStore = new PersistentCookieStore(mContext);
		cs = myCookieStore;
		client.setCookieStore(myCookieStore);
	}
	public List<Cookie> get_cookies() {
		return cs.getCookies();
	}
	public void get_raw(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.get(url, params, responseHandler);
	}
	public void post_raw(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.post(url, params, responseHandler);
	}
	public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		System.out.println("loading: " + getAbsoluteUrl(url));
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}
	
	public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	public void print(String url){
		get(url,null,new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String arg0) {
				System.out.println(arg0);
				super.onSuccess(arg0);
			}
		});
	}
	private static boolean isQuote(char val) {
		return val=='"' || val == '\'';
	}
	private static String nextStringInQuotes(String string,int index) {
		if(index == -1)
			return "" ;
		String out = "";
		boolean found = false ;
		while(!found || !isQuote(string.charAt(index))) {
			if(isQuote(string.charAt(index))) {
				found = true ;
			} else {
				if(found) {
					out += string.charAt(index);
				}
			}
			index++ ;
			if(index >= string.length()) {
				return "" ;
			}
		}
		return out ;
	}
	//Convenience for calling "nextStringInQuotes" twice and creating a new String array
	private static String [] nextStringsInQuotes(String string, int index1, int index2) {
		String [] out = new String[2] ;
		out[0] = nextStringInQuotes(string,index1);
		out[1] = nextStringInQuotes(string,index2);
		return out ;
	}
	private static String [] parseInput(String input) {
		int nameIndex = input.indexOf("name=");
		if(nameIndex==-1) {
			// "name=" was not found
			return null ;
		}
		int valueIndex = input.indexOf("value=");
		return nextStringsInQuotes(input,nameIndex,valueIndex);
		/*if(valueIndex!=-1) {
			return nextStringsInQuotes(input,nameIndex,valueIndex) ;
		}
		int idIndex = input.indexOf("id=");
		if(idIndex!=-1) {
			return nextStringsInQuotes(input,nameIndex,idIndex);
		}
		return null;*/
		
	}
	public static String getCSRFToken(String form) {
		List<String[]> inputs = parseForm(form);
		for(int i = 0; i < inputs.size() ; i++) {
			String [] temp = inputs.get(i) ;
			if(temp[0].equals(CSRFTOKEN_NAME)) {
				return temp[1] ;
			}
		}
		return "" ;
	}
	
	//looks for the input tag,
	public static List<String[]> parseForm(String form) {
		List<String[]> out = new ArrayList<String[]>();
		String [] inputs = form.replaceAll("(<input)(.+)(>)","SPLIT$2SPLIT").split("SPLIT");
		for(int i = 0 ; i < inputs.length ; i++) {
			if(i%2==1) {
				String [] data = parseInput(inputs[i]);
				if(data!=null) {
					out.add(data);
				}
			}
		}
		return out ;
	}
}