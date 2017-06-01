package test.mabel.memointime;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;


public class MemoPagerActivity extends AppCompatActivity {

    private static final String EXTRA_MEMO_ID = "test.mabel.memointime.memo_id";

    private ViewPager mViewPager;
    private List<Memo> mMemos;

    public static Intent newIntent(Context packageContext, UUID memoId) {
        Intent intent = new Intent(packageContext, MemoPagerActivity.class);
        intent.putExtra(EXTRA_MEMO_ID, memoId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_pager);

        UUID memoId = (UUID)getIntent().getSerializableExtra(EXTRA_MEMO_ID);

        mViewPager = (ViewPager)findViewById(R.id.activity_memo_pager_view_pager);

        mMemos = MemoLab.get(this).getMemos();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Memo memo = mMemos.get(position);
                return MemoFragment.newInstance(memo.getId());
            }

            @Override
            public int getCount() {
                return mMemos.size();
            }
        });

        for (int i = 0; i < mMemos.size(); i++) {
            if (mMemos.get(i).getId().equals(memoId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }

    }
}
