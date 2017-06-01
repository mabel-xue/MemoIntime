package test.mabel.memointime;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;


public class PhotoFragment extends DialogFragment {
    public static final String EXTRA_PHOTO_PATH = "test.mabel.memointime.photo_path";
    private ImageView mImageView;

    public static PhotoFragment newInstance(String photoPath) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_PHOTO_PATH, photoPath);

        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo, null);
        mImageView = (ImageView)v.findViewById(R.id.memo_photo);
        String path = getArguments().getString(EXTRA_PHOTO_PATH);
        Bitmap bitmap = PictureUtils.getScaledBitmap(path, getActivity());
        mImageView.setImageBitmap(bitmap);

        return new AlertDialog.Builder(getActivity()).setView(v).create();
    }
}
