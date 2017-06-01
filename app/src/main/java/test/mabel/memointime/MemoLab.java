package test.mabel.memointime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import test.mabel.memointime.database.MemoBaseHelper;
import test.mabel.memointime.database.MemoCursorWrapper;
import test.mabel.memointime.database.MemoDbSchema.MemoTable;

/**
 * 单例模型MemoLab
 */

public class MemoLab {

    private static MemoLab sMemoLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private MemoLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new MemoBaseHelper(mContext).getWritableDatabase();
    }

    public static MemoLab get(Context context) {
        if (sMemoLab == null) {
            sMemoLab = new MemoLab(context);
        }
        return sMemoLab;
    }

    public void addMemo(Memo c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(MemoTable.NAME, null, values);
    }

    //Challenge 14.8 : 删除Memo记录
    public void deleteMemo(Memo c) {
        UUID id = c.getId();
        mDatabase.delete(MemoTable.NAME, MemoTable.Cols.UUID + " = ?", new String[] { id.toString() });
    }

    public List<Memo> getMemos() {
        List<Memo> memos = new ArrayList<>();
        MemoCursorWrapper cursor = queryMemos(null, null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                memos.add(cursor.getMemo());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return memos;
    }

    public Memo getMemo(UUID id) {
        MemoCursorWrapper cursor = queryMemos(
                MemoTable.Cols.UUID + " = ?", new String[] { id.toString() });

        try {
            if (cursor.getCount() == 0) {
                return null;
            }

            cursor.moveToFirst();
            return cursor.getMemo();
        } finally {
            cursor.close();
        }
    }

    //找到要保存文件的目录
    public File getPhotoFile(Memo memo) {
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, memo.getPhotoFilename());
    }

    public void updateMemo(Memo memo) {
        String uuidString = memo.getId().toString();
        ContentValues values = getContentValues(memo);

        mDatabase.update(MemoTable.NAME, values, MemoTable.Cols.UUID + " = ?", new String[] { uuidString });
    }

    private static ContentValues getContentValues(Memo memo) {
        ContentValues values = new ContentValues();
        values.put(MemoTable.Cols.UUID, memo.getId().toString());
        values.put(MemoTable.Cols.TITLE, memo.getTitle());
        values.put(MemoTable.Cols.DATE, memo.getDate().getTime());
        values.put(MemoTable.Cols.SOLVED, memo.isSolved() ? 1 : 0);
        values.put(MemoTable.Cols.CONTACTS, memo.getContacts());

        return values;
    }

    private MemoCursorWrapper queryMemos(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                MemoTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new MemoCursorWrapper(cursor);
    }


}
