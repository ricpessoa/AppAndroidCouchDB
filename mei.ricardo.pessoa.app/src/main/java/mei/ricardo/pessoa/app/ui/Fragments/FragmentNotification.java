package mei.ricardo.pessoa.app.ui.Fragments;

import android.support.v4.app.Fragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import mei.ricardo.pessoa.app.R;

/**
 * Created by rpessoa on 14/05/14.
 */
public class FragmentNotification extends Fragment {
    public static Handler handler = new Handler();
    private static ImageView mImageViewNotification;
    private static TextView mTextViewNotification;
    private static String[] notificationTitles;
    private static TypedArray notificationDrawerIcons;
    private static int indexImage = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigator_drawer_notification, container, false);
        notificationTitles = getResources().getStringArray(R.array.notifications_drawer_items);
        notificationDrawerIcons = getResources().obtainTypedArray(R.array.notifications_drawer_icons);

        mImageViewNotification = (ImageView) view.findViewById(R.id.imageViewNotification);
        mTextViewNotification = (TextView) view.findViewById(R.id.textViewNotification);

        //handler.postDelayed(runnable, 3000);
        runnable.run();

        return view;
    }

    private static Runnable runnable = new Runnable() {

        public void run() {

            if (indexImage > notificationDrawerIcons.length() - 1) {
                indexImage = 0;
            }

            mImageViewNotification.setImageDrawable(notificationDrawerIcons.getDrawable(indexImage));
            mTextViewNotification.setText(notificationTitles[indexImage]);
            indexImage++;
            handler.postDelayed(this, 3000);

        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }
}