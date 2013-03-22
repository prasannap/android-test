package com.a321apps.test;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity implements
		LoaderManager.LoaderCallbacks<Cursor>,  OnClickListener {
	final String TAG = "MainActivity";
	final String CONTACTID_KEY = "ContactId";
	final int LIST_OF_CONTACTS = 0;
	final int LIST_OF_EMAILS = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public void onClick(View v) {
		//getLoaderManager().initLoader(LIST_OF_CONTACTS, null, this);
		getLoaderManager().initLoader(LIST_OF_EMAILS, null, this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
		Log.d(TAG, "onCreateLoader ");
		int contactId;
		Loader<Cursor> loader = null;

		if (id == LIST_OF_CONTACTS) {
			Log.d(TAG, "onCreateLoader:LIST_OF_CONTACTS");
			final String[] PROJECTION = new String[] { RawContacts.CONTACT_ID, // the
																				// contact
																				// id
																				// column
					RawContacts.DELETED // column if this contact is deleted
			};

			return (new CursorLoader(this, RawContacts.CONTENT_URI,
					PROJECTION, null, null, null));
		} else if (id == LIST_OF_EMAILS) {
			Log.d(TAG, "onCreateLoader:LIST_OF_EMAILS");
			//contactId = bundle.getInt(CONTACTID_KEY);

//			final String[] PROJECTION = new String[] { Email.ADDRESS, // use
//																		// Email.DATA
//																		// for
//																		// API-Level
//																		// less
//																		// than
//																		// 11+
//					Email.TYPE };
			final String[] PROJECTION = new String[] { Email.ADDRESS, 
					ContactsContract.Data.DISPLAY_NAME };

//			loader = new CursorLoader(this, Email.CONTENT_URI, PROJECTION,
//					Data.CONTACT_ID + "=?",
//					new String[] { String.valueOf(contactId) }, null);
//			loader = new CursorLoader(this, Email.CONTENT_URI, PROJECTION, null,null, null);
			loader = new CursorLoader(this, ContactsContract.Data.CONTENT_URI, PROJECTION, 
					ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE + "'",null, null);
		}

		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		Log.d(TAG, "onLoadFinished"); 

		if (loader.getId() == LIST_OF_CONTACTS) {
			Log.d(TAG, "onLoadFinished: LIST_OF_CONTACTS");

			final int contactIdColumnIndex = cursor
					.getColumnIndex(RawContacts.CONTACT_ID);
			final int deletedColumnIndex = cursor
					.getColumnIndex(RawContacts.DELETED);

			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) { // still a valid entry left?
					final int contactId = cursor.getInt(contactIdColumnIndex);
					final boolean deleted = (cursor.getInt(deletedColumnIndex) == 1);
					if (!deleted) {
						Log.d(this.getClass().getName(), "ContactId: " + Integer.toString(contactId));
						Bundle bundle = new Bundle();
						bundle.putInt( CONTACTID_KEY, contactId);
						getLoaderManager().initLoader(LIST_OF_EMAILS, bundle, this);
					}

					cursor.moveToNext(); // move to the next entry
				}
			}
		} else if (loader.getId() == LIST_OF_EMAILS) {
			Log.d(TAG, "onLoadFinished: LIST_OF_EMAILS");

			if(cursor.moveToFirst()) {
			    final int contactEmailColumnIndex = cursor.getColumnIndex(Email.DATA);
			    //final int contactTypeColumnIndex = cursor.getColumnIndex(Email.TYPE);
			    final int displayNameColumnIndex = cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME);
			    
			    while(!cursor.isAfterLast()) {
			        final String address = cursor.getString(contactEmailColumnIndex);
			        //final int type = cursor.getInt(contactTypeColumnIndex);
			        final String displayName = cursor.getString(displayNameColumnIndex);
			        //final int typeLabelResource = Email.getTypeLabelResource(type);
					//Log.d(this.getClass().getName(), "Email address: " + address 
					//		+ " typeLabelResource: " + typeLabelResource);
					Log.d(this.getClass().getName(), "Email address: " + address 
							+ " displayName:: " + displayName);
			        cursor.moveToNext();
			    }
			}

		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}
}
