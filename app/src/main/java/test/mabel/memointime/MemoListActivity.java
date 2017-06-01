package test.mabel.memointime;

import android.support.v4.app.Fragment;

public class MemoListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new MemoListFragment();
    }

}
