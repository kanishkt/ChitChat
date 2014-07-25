package com.academy.chitchat;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;

public class MessageList extends Fragment {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}



 class MessageListAdapter extends ArrayAdapter<String> {
	 
	 public MessageListAdapter(Activity activity){
			super(activity, R.layout.list_item);

		
	 }
	
}
}