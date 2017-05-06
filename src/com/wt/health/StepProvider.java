package com.wt.health;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class StepProvider extends ContentProvider{
	private DatabaseHelper mOpenHelper;
	private SQLiteDatabase mDB;
	private static final String DB_NAME = "step.db";
	
	@Override
	public boolean onCreate() {
		mOpenHelper = new DatabaseHelper(getContext());
		mDB = mOpenHelper.getWritableDatabase();
		if(mDB == null){
			return false;
		}
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
	    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
	    qb.setTables(args.table);
		
		Cursor cursor = qb.query(mDB, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		SqlArguments args = new SqlArguments(uri, null, null);
        if (TextUtils.isEmpty(args.where)) {
            return "vnd.android.cursor.dir/" + args.table;
        } else {
            return "vnd.android.cursor.item/" + args.table;
        }
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SqlArguments args = new SqlArguments(uri);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final long rowId = db.insert(args.table, null, values);
        if (rowId <= 0) return null;

        uri = ContentUris.withAppendedId(uri, rowId);
        getContext().getContentResolver().notifyChange(uri, null);
		return uri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = db.delete(args.table, args.where, args.args);
        if (count > 0){
        	getContext().getContentResolver().notifyChange(uri, null);
        }
		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = db.update(args.table, values, args.where, args.args);
        if (count > 0){
        	getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
	}

	static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("create table "+StepHealth.TABLE_NAME
					+" (_id integer primary key autoincrement, steps integer not null, date date not null,diatance real,kcal real,long_time integer);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS "+StepHealth.TABLE_NAME);
		     onCreate(db);
		}
		
	}
	
	static class SqlArguments {
        public final String table;
        public final String where;
        public final String[] args;

        SqlArguments(Uri url, String where, String[] args) {
            if (url.getPathSegments().size() == 1) {
                this.table = url.getPathSegments().get(0);
                this.where = where;
                this.args = args;
            } else if (url.getPathSegments().size() != 2) {
                throw new IllegalArgumentException("Invalid URI: " + url);
            } else if (!TextUtils.isEmpty(where)) {
                throw new UnsupportedOperationException("WHERE clause not supported: " + url);
            } else {
                this.table = url.getPathSegments().get(0);
                this.where = "_id=" + ContentUris.parseId(url);
                this.args = null;
            }
        }

        SqlArguments(Uri url) {
            if (url.getPathSegments().size() == 1) {
                table = url.getPathSegments().get(0);
                where = null;
                args = null;
            } else {
                throw new IllegalArgumentException("Invalid URI: " + url);
            }
        }
    }
}
