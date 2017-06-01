package test.mabel.memointime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class MemoListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private RecyclerView mMemoRecyclerView;
    private MemoAdapter mAdapter;
    private boolean mSubtitleVisible;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_memo_list, container, false);

        mMemoRecyclerView = (RecyclerView)view.findViewById(R.id.memo_recycler_view);
        mMemoRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_memo_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_memo:
                Memo memo = new Memo();
                MemoLab.get(getActivity()).addMemo(memo);
                Intent intent = MemoPagerActivity.newIntent(getActivity(), memo.getId());
                startActivity(intent);
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        MemoLab memoLab = MemoLab.get(getActivity());
        int memoCount = memoLab.getMemos().size();
        //Challenge 13.7 优化字符串资源显示
//        @SuppressLint("StringFormatMatches") String subtitle = getString(R.string.subtitle_format, memoCount);
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, memoCount, memoCount);
        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        MemoLab memoLab = MemoLab.get(getActivity());
        List<Memo> memos = memoLab.getMemos();

        if (mAdapter == null) {
            mAdapter = new MemoAdapter(memos);
            mMemoRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setMemos(memos);
            mAdapter.notifyItemChanged(memos.indexOf(mAdapter.mMemo));
        }
        updateSubtitle();
    }

    //定义ViewHolder内部类
    private class MemoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Memo mMemo;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;

        public MemoHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView)itemView.findViewById(R.id.list_item_memo_title_text_view);
            mDateTextView = (TextView)itemView.findViewById(R.id.list_item_memo_date_text_view);
            mSolvedCheckBox = (CheckBox)itemView.findViewById(R.id.list_item_memo_solved_check_box);
        }

        public void bindMemo(Memo memo) {
            mMemo = memo;
            mTitleTextView.setText(mMemo.getTitle());
            mDateTextView.setText(DateFormat.format("EEEE, yyyy-MM-dd, HH:mm", mMemo.getDate()));
            mSolvedCheckBox.setChecked(mMemo.isSolved());
        }

        @Override
        public void onClick(View v) {
            Intent intent = MemoPagerActivity.newIntent(getActivity(), mMemo.getId());
            startActivity(intent);
        }
    }

    //创建adapter内部类
    private class MemoAdapter extends RecyclerView.Adapter<MemoHolder> {

        private List<Memo> mMemos;
        private Memo mMemo;

        public MemoAdapter(List<Memo> memos) {
            mMemos = memos;
        }

        @Override
        public MemoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_item_memo, parent, false);
            return new MemoHolder(view);
        }

        @Override
        public void onBindViewHolder(MemoHolder holder, int position) {
            mMemo = mMemos.get(position);
            holder.bindMemo(mMemo);

        }

        @Override
        public int getItemCount() {
            return mMemos.size();
        }

        public void setMemos(List<Memo> memos) {
            mMemos = memos;
        }
    };

}
