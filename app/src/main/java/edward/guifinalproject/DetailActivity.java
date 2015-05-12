package edward.guifinalproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.people.User;
import com.flickr4java.flickr.photos.Photo;

import java.io.InputStream;
import java.util.Date;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "detailActivity";
    private String[] results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // receive intent and information from CustomListAdapter implemented in MainActivity
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String photoId = intent.getStringExtra("id");
        String photoURL = intent.getStringExtra("url");

        // setup views in window
        TextView photoTitle = (TextView) findViewById(R.id.textviewtitle);
        TextView photoDetails = (TextView) findViewById(R.id.textviewinfo);
        ImageView photo = (ImageView) findViewById(R.id.imageView);

        // set title of details view
        setTitle(title);

        // change title typeface
        Typeface keepCalm = Typeface.createFromAsset(getAssets(), "KeepCalm-Medium.ttf");
        photoTitle.setTypeface(keepCalm);

        // attempt background Asynctask to display information
        new PhotoTitleTask(photoTitle).execute(photoId);
        new PhotoInfoTask(photoDetails).execute(photoId);
        new DownloadImageTask(photo).execute(photoURL);

    }

    // query the Flickr API for requested photo data
    private class PhotoTitleTask extends AsyncTask<String, Void, String> {
        final String apiKey = "a4510cb0bda48ea5973fb6d9daf1dfd2";
        final String sharedSecret = "f85840639647368d";

        String title;
        TextView photoTitle;

        public PhotoTitleTask(TextView photoTitle) {
            this.photoTitle = photoTitle;
        }

        // attempt textview update with information from API
        protected String doInBackground(String... photoId){
            Flickr f = new Flickr(apiKey, sharedSecret, new REST());
            PhotosInterface photoInterface= f.getPhotosInterface();

            Photo result;
            try {
                result = photoInterface.getInfo(photoId[0], null);
                title = result.getTitle();
            } catch (FlickrException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return title;
        }

        protected void onPostExecute(String title) {
            photoTitle.setText(title);
        }
    }


    // query the Flickr API for requested photo data
    private class PhotoInfoTask extends AsyncTask<String, Void, String[]> {
        final String apiKey = "a4510cb0bda48ea5973fb6d9daf1dfd2";
        final String sharedSecret = "f85840639647368d";

        Date dateTaken;
        int viewCount;
        String username;
        TextView photoInfo;

        public PhotoInfoTask(TextView photoInfo) {
            this.photoInfo = photoInfo;
        }

        // attempt textview update with information from API
        protected String[] doInBackground(String... photoId){
            Flickr f = new Flickr(apiKey, sharedSecret, new REST());
            PhotosInterface photoInterface= f.getPhotosInterface();
            String[] results = new String[3];

            Photo result;
            try {
                result = photoInterface.getInfo(photoId[0], null);
                dateTaken = result.getDateTaken();
                viewCount = result.getViews();
                User user = result.getOwner();
                username = user.getUsername();

            } catch (FlickrException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            results[0] = dateTaken.toString();
            results[1] = username;
            results[2] = String.valueOf(viewCount);

            return results;
        }

        protected void onPostExecute(String[] results) {

            SpannableString ss = new SpannableString("Date taken: ");
            ss.setSpan(new StyleSpan(Typeface.BOLD), 0, ss.length(), 0);
            SpannableString ss1 = new SpannableString("Taken by: ");
            ss1.setSpan(new StyleSpan(Typeface.BOLD), 0, ss1.length(), 0);
            SpannableString ss2 = new SpannableString("Views: ");
            ss2.setSpan(new StyleSpan(Typeface.BOLD), 0, ss2.length(), 0);

            photoInfo.append(ss);
            photoInfo.append(results[0]);
            photoInfo.append("\n");
            photoInfo.append(ss1);
            photoInfo.append(results[1]);
            photoInfo.append("\n");
            photoInfo.append(ss2);
            photoInfo.append(results[2]);
        }
    }

    // Asynctask to download image thumbnails in the background
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        // attempt image download from url
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
