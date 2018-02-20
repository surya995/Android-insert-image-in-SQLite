package com.androidsurya.sqliteexample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelper {
	public static final String EMP_ID = "id";
	public static final String EMP_NAME = "name";
	public static final String EMP_AGE = "age";
	public static final String EMP_PHOTO = "photo";

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;

	private static final String DATABASE_NAME = "EmployessDB.db";
	private static final int DATABASE_VERSION = 1;

	private static final String EMPLOYEES_TABLE = "Employees";

	private static final String CREATE_EMPLOYEES_TABLE = "create table "
			+ EMPLOYEES_TABLE + " (" + EMP_ID
			+ " integer primary key autoincrement, " + EMP_PHOTO
			+ " blob not null, " + EMP_NAME + " text not null unique, "
			+ EMP_AGE + " integer );";

	private final Context mCtx;

	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_EMPLOYEES_TABLE);
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + EMPLOYEES_TABLE);
			onCreate(db);
		}
	}

	public void Reset() {
		mDbHelper.onUpgrade(this.mDb, 1, 1);
	}

	public DBhelper(Context ctx) {
		mCtx = ctx;
		mDbHelper = new DatabaseHelper(mCtx);
	}

	public DBhelper open() throws SQLException {
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}

	public void insertEmpDetails(Employee employee) {
		ContentValues cv = new ContentValues();
		cv.put(EMP_PHOTO, Utility.getBytes(employee.getBitmap()));
		cv.put(EMP_NAME, employee.getName());
		cv.put(EMP_AGE, employee.getAge());
		mDb.insert(EMPLOYEES_TABLE, null, cv);
	}

	public Employee retriveEmpDetails() throws SQLException {
		Cursor cur = mDb.query(true, EMPLOYEES_TABLE, new String[] { EMP_PHOTO,
				EMP_NAME, EMP_AGE }, null, null, null, null, null, null);
		if (cur.moveToFirst()) {
			byte[] blob = cur.getBlob(cur.getColumnIndex(EMP_PHOTO));
			String name = cur.getString(cur.getColumnIndex(EMP_NAME));
			int age = cur.getInt(cur.getColumnIndex(EMP_AGE));
			cur.close();
			return new Employee(Utility.getPhoto(blob), name, age);
		}
		cur.close();
		return null;
	}
}
