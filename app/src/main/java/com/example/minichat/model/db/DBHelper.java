package com.example.minichat.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.minichat.model.dao.ContactTable;
import com.example.minichat.model.dao.InviteTable;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(@Nullable Context context, @Nullable String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建联系人的表
        sqLiteDatabase.execSQL(ContactTable.CREATE_TAB);
        //创建邀请信息的表
        sqLiteDatabase.execSQL(InviteTable.CREATE_TAB);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
