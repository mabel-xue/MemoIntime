package test.mabel.memointime;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.UUID;


public class MemoFragment extends Fragment {

    private static final String ARG_MEMO_ID = "memo_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_PHOTO = "DialogPhoto";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO = 2;

    private Memo mMemo;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mContactsButton;
    private Button mReportButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    public static MemoFragment newInstance(UUID memoId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_MEMO_ID, memoId);

        MemoFragment fragment = new MemoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Challenge 13.6 删除memo记录
        setHasOptionsMenu(true);
        UUID memoId = (UUID)getArguments().getSerializable(ARG_MEMO_ID);
        mMemo = MemoLab.get(getActivity()).getMemo(memoId);
        //获取图片位置
        mPhotoFile = MemoLab.get(getActivity()).getPhotoFile(mMemo);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_memo, container, false);

        mTitleField = (EditText)v.findViewById(R.id.memo_title);
        mTitleField.setText(mMemo.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mMemo.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mDateButton = (Button)v.findViewById(R.id.memo_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mMemo.getDate());
                dialog.setTargetFragment(MemoFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.memo_solved);
        mSolvedCheckBox.setChecked(mMemo.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mMemo.setSolved(isChecked);
            }
        });

        mContactsButton = (Button)v.findViewById(R.id.memo_contacts);
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mMemo.getContacts() != null) {
            mContactsButton.setText(mMemo.getContacts());
        }

        mReportButton = (Button)v.findViewById(R.id.memo_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getMemoReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.memo_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, packageManager.MATCH_DEFAULT_ONLY) == null) {
            mContactsButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton)v.findViewById(R.id.memo_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView)v.findViewById(R.id.memo_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                String path = mPhotoFile.getPath();
                Log.d("MemoFragment", path);
                PhotoFragment dialog = PhotoFragment.newInstance(path);
                dialog.setTargetFragment(MemoFragment.this, REQUEST_PHOTO);
                dialog.show(manager, DIALOG_PHOTO);
            }
        });
        updatePhotoView();

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();

        MemoLab.get(getActivity()).updateMemo(mMemo);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.delete_memo, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete_memo:
                MemoLab.get(getActivity()).deleteMemo(mMemo);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mMemo.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            //指定需查询的数据库
            String[] queryFields = new String[] { ContactsContract.Contacts.DISPLAY_NAME };
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);

            try {
                if (c.getCount() == 0) {
                    return;
                }
                c.moveToFirst();
                String contacts = c.getString(0);
                mMemo.setContacts(contacts);
                mContactsButton.setText(contacts);
            } finally {
                c.close();
            }
        } else if (requestCode == REQUEST_PHOTO) {
            updatePhotoView();
        }
    }

    private void updateDate() {
        mDateButton.setText(DateFormat.format("EEEE, yyyy-MM-dd, HH:mm", mMemo.getDate()));
    }

    private String getMemoReport() {
        String solvedString = null;
        if (mMemo.isSolved()) {
            solvedString = getString(R.string.memo_report_solved);
        } else {
            solvedString = getString(R.string.memo_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mMemo.getDate()).toString();

        String contacts = mMemo.getContacts();
        if (contacts == null) {
            contacts = getString(R.string.memo_report_no_contacts);
        } else {
            contacts = getString(R.string.memo_report_contacts, contacts);
        }

        String report = getString(R.string.memo_report, mMemo.getTitle(), dateString, solvedString, contacts);

        return report;
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }
}
