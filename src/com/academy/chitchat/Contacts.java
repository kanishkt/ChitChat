package com.academy.chitchat;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

public class Contacts {

	ArrayList<String> names;
	ArrayList<String> numbers;
	public Contacts() {
		names = new ArrayList<String>();
		numbers = new ArrayList<String>();
	}
	public void getName(Context mContext) {
		Cursor cursor = mContext.getContentResolver().query(Phone.CONTENT_URI, null, null, null, Phone.DISPLAY_NAME + " ASC");
		while(cursor.moveToNext()){
			names.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
		}
		cursor.close();
	}
	public void getNumbers(Context mContext){
		Cursor cursor = mContext.getContentResolver().query(Phone.CONTENT_URI, null, null, null, Phone.NUMBER);
		while(cursor.moveToNext()){
			numbers.add(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
		}
		cursor.close();
	}

}
