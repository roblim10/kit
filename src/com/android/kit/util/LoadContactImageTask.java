package com.android.kit.util;

import java.io.InputStream;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.LruCache;
import android.widget.ImageView;

import com.android.kit.R;

public class LoadContactImageTask extends AsyncTask<Void, Void, Bitmap> {
	private static LruCache<Integer, Bitmap> lruCache = new LruCache<Integer, Bitmap>(2048);
	
	private Context context;
	private ImageView imageView;
	private int contactId;
	
	public LoadContactImageTask(Context context, ImageView imageView, int contactId)  {
		this.context = context;
		this.imageView = imageView;
		this.contactId = contactId;
	}
	
	@Override
	protected Bitmap doInBackground(Void... params) {
		Bitmap bitmap = lruCache.get(contactId);
		if(bitmap == null)  {
			bitmap = loadContactPhoto();
		}
		return bitmap;
	}
	
	@Override
	protected void onPostExecute(Bitmap bitmap)  {
		if (bitmap != null)  {
			lruCache.put(contactId, bitmap);
			imageView.setImageBitmap(bitmap);
		}
		else  {
			imageView.setImageResource(R.drawable.no_photo);
		}
	}
	
	private Bitmap loadContactPhoto()  {
		Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
		ContentResolver cr = context.getContentResolver();
		InputStream is = ContactsContract.Contacts.openContactPhotoInputStream(cr, contactUri);
		if (is != null)  {
			return BitmapFactory.decodeStream(is);
		}
		return null;
	}

}
