package se.tabyfkappen.tabyfk.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;

import se.tabyfkappen.tabyfk.helpers.MySQLiteHelper;
import se.tabyfkappen.tabyfk.models.User;

/**
 * Created by Victor on 2016-01-24.
 */
public class UserDataSource {
    private SQLiteDatabase db;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_EMAIL,
            MySQLiteHelper.COLUMN_PASSWORD,
            MySQLiteHelper.COLUMN_TOKEN
    };

    public UserDataSource(Context c) {
        dbHelper = new MySQLiteHelper(c);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Boolean create(String email, String password, String token) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_EMAIL, email);
        values.put(MySQLiteHelper.COLUMN_PASSWORD, password);
        values.put(MySQLiteHelper.COLUMN_TOKEN, token);

        return db.insert(MySQLiteHelper.TABLE_USER, null, values) > 0;
    }

    public void update(String email, String password, String token) {
        long rowId;
        Cursor c = db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_USER, null);

        if(c.moveToFirst()) {
            rowId = c.getLong(c.getColumnIndex(MySQLiteHelper.COLUMN_ID));

            String where = MySQLiteHelper.COLUMN_ID + " = ?";
            String[] whereArgs = { Long.toString(rowId)};

            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_EMAIL, email);
            values.put(MySQLiteHelper.COLUMN_PASSWORD, password);
            values.put(MySQLiteHelper.COLUMN_TOKEN, token);

            db.update(MySQLiteHelper.TABLE_USER, values, where, whereArgs);
        } else {
            create(email, password, token);
        }
        c.close();
    }

    public void dropAll() {
        db.delete(MySQLiteHelper.TABLE_USER,null,null);
    }

    public int size() {
        return db.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_USER, null).getCount();
    }

    public User getUser() {
        String query = "SELECT * FROM " + MySQLiteHelper.TABLE_USER + ";";
        Cursor c = db.rawQuery(query, null);
        User user = new User();

        if(c.moveToFirst()) {
            user.setId(c.getLong(c.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
            user.setEmail(c.getString(c.getColumnIndex(MySQLiteHelper.COLUMN_EMAIL)));
            user.setPassword(c.getString(c.getColumnIndex(MySQLiteHelper.COLUMN_PASSWORD)));
            user.setToken(c.getString(c.getColumnIndex(MySQLiteHelper.COLUMN_TOKEN)));
        }
        c.close();

        return user;
    }
}
