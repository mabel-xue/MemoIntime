package test.mabel.memointime.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import test.mabel.memointime.database.MemoDbSchema.MemoTable;

public class MemoBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "MemoBase.db";

    public MemoBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + MemoTable.NAME + "(" + "_id integer primary key autoincrement, " +
        MemoTable.Cols.UUID + ", " +
        MemoTable.Cols.TITLE + ", " +
        MemoTable.Cols.DATE + ", " +
        MemoTable.Cols.SOLVED + ", " +
        MemoTable.Cols.CONTACTS + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
