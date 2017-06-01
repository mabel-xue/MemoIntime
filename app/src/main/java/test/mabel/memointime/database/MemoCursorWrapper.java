package test.mabel.memointime.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import test.mabel.memointime.Memo;

import java.util.Date;
import java.util.UUID;

import test.mabel.memointime.database.MemoDbSchema.MemoTable;


public class MemoCursorWrapper extends CursorWrapper {

    public MemoCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Memo getMemo() {
        String uuidString = getString(getColumnIndex(MemoTable.Cols.UUID));
        String title = getString(getColumnIndex(MemoTable.Cols.TITLE));
        long date = getLong(getColumnIndex(MemoTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(MemoTable.Cols.SOLVED));
        String contact = getString(getColumnIndex(MemoTable.Cols.CONTACTS));

        Memo memo = new Memo(UUID.fromString(uuidString));
        memo.setTitle(title);
        memo.setDate(new Date(date));
        memo.setSolved(isSolved != 0);
        memo.setContacts(contact);

        return memo;
    }
}
