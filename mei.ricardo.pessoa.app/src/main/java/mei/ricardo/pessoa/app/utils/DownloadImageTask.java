package mei.ricardo.pessoa.app.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

import mei.ricardo.pessoa.app.Application;
import mei.ricardo.pessoa.app.R;

/**
 * Created by rpessoa on 14/07/14.
 */

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        if (result == null) {
            bmImage.setImageDrawable(Application.getmContext().getResources().getDrawable(R.drawable.ic_img_not_found));
            return;
        }
        bmImage.setImageBitmap(result);
    }
}
