package com.academy.chitchat;

import com.loopj.android.http.AsyncHttpResponseHandler;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	
	static FileDownloader filedownloader;
	ContactsFragment contactsFragment = new ContactsFragment();
	static ChitChatRestClient c = new ChitChatRestClient();
	Record record = new Record();

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {


		filedownloader = new FileDownloader(FileDownloader.CHECK_FILE, this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		c.allowRedirects();
		setFragment(record);
		RecentFragment fr = new RecentFragment();
		Contacts contacts = new Contacts();
		contactsFragment = new ContactsFragment();
		c.getData(new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String arg0) {
				setText(arg0);
				super.onSuccess(arg0);
			}
		});

	}

	public void goToContacts() {
		setFragment(contactsFragment);
	}
	public void goToRecord() {
		setFragment(record);
	}

	public void setFragment(Fragment fragment) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction tx = fm.beginTransaction();
		tx.replace(R.id.content_frame, fragment);
		tx.commit();

	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.main, menu); return true; }
	 */
	public void setText(String a) {
		Toast.makeText(this, a, Toast.LENGTH_LONG).show();

	}

}