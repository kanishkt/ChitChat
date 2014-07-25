package com.academy.chitchat;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactsFragment extends Fragment {
	View root;
	ContactsAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.address, null);
		return root;
	}
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		final Contacts contacts = new Contacts();
		ListView lv = (ListView) root.findViewById(R.id.listView1);
		mAdapter = new ContactsAdapter(getActivity());
		lv.setAdapter(mAdapter);
		contacts.getName(getActivity());
		for (String name : contacts.names) {
			mAdapter.add(name);
		}
		contacts.getNumbers(getActivity());
		
		Button b1 = (Button) root.findViewById(R.id.button1);
		Log.e("ONE", "WORKING?");
		
		b1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String receivers = mAdapter.getReceivers(contacts.numbers);
				MainActivity.c
						.upload(Record.filePath, "12178192553", receivers);
		        MainActivity maObject  = (MainActivity) getActivity();
		        maObject.goToRecord();
		        Toast recordingSent = Toast.makeText(getActivity(), "Recording Sent!", Toast.LENGTH_SHORT);
		        recordingSent.show();
			}
		});
	}



	class ContactsAdapter extends ArrayAdapter<String> {
		
		
		Activity mActivity;
		Contacts contacts;
		ArrayList<Boolean> selected = new ArrayList<Boolean>();
		
		public ContactsAdapter(Activity activity) {
			super(activity, R.layout.list_item);
			this.mActivity = activity;
		}

		@Override
		public void add(String object) {
			selected.add(false);
			super.add(object);
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			if (convertView == null) {
				// convertView has not been inflated; inflate it
				LayoutInflater inflater = mActivity.getLayoutInflater();
				convertView = inflater.inflate(R.layout.list_item, parent,
						false);
			}
			// convertView has been inflated
			TextView tv = (TextView) convertView.findViewById(R.id.textView1);
			tv.setText(getItem(position));
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					selected.set(position, !selected.get(position));
					updateBackground(v, position);
				}
			});
			updateBackground(convertView, position);
			return convertView;
		}

		public void updateBackground(View v, int position) {
			if (selected.get(position)) {
				v.setBackgroundColor(Color.GREEN);
			} else {
				v.setBackgroundColor(Color.WHITE);
			}
		}

		public String getReceivers(ArrayList <String> numbers) {
			StringBuilder sb = new StringBuilder();
			sb.append("[");
			int position = 0;
			for (boolean s : selected) {
				if (s) {
					sb.append(numbers.get(position));
					sb.append(",");
				}
				position++;
			}
			String returner = sb.toString();
			if (returner.lastIndexOf(',') != -1) {
				return returner.substring(0, returner.length() - 1) + ']';
			} else
				return returner + ']';

		}
	}
}