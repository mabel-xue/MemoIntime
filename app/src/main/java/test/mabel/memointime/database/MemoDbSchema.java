package test.mabel.memointime.database;


public class MemoDbSchema {

    public static final class MemoTable {

        public static final String NAME = "memos";

        public static final class Cols {

            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String CONTACTS = "contact";
        }
    }
}
