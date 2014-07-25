package com.academy.chitchat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RecentFragment extends Fragment {
	MyCustomAdapter mAdapter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.recent_layout, null);
	}
	
	@Override
	public void onStart() {
		View root=getActivity().findViewById(R.id.recent_layout1);
		ListView lview = (ListView) root.findViewById(R.id.listView1);
		mAdapter = new MyCustomAdapter(getActivity());
		mAdapter.add("Test1");
		mAdapter.add("Test2");
		mAdapter.add("Test3");
		lview.setAdapter(mAdapter);
		root.findViewById(R.id.button1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MainActivity act = (MainActivity) getActivity();
				act.goToContacts();
			}
		});
		super.onStart();
	}
	
class MyCustomAdapter extends ArrayAdapter<String> {
		
		Activity mActivity;
		public MyCustomAdapter(Activity activity) {
			super(activity, R.layout.list_item);
			this.mActivity = activity;
			
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				//convertView has not been inflated; inflate it
				LayoutInflater inflater = mActivity.getLayoutInflater();
				convertView = inflater.inflate(R.layout.list_item, parent,false);	
			}
			//convertView has been inflated
			TextView tv = (TextView)convertView.findViewById(R.id.textView1);
			tv.setText(getItem(position) +" -> " + String.valueOf(position));
			return convertView;
		}
	}
}