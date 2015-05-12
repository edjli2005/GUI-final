package edward.guifinalproject;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.people.PeopleInterface;
import com.flickr4java.flickr.people.User;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;

import java.util.concurrent.ExecutionException;

public class MainActivity extends ListActivity {

    static final int perPage = 8;
    static final int page = 1;
    private static final String TAG = "mainActivity";

    ListView list;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("NASA Image Feed");

        PhotoList<Photo> results = new PhotoList<>();

        // Attempt background Asynctask to retrieve results in PhotoList format
        try {
            results = new APIHitTask().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // Retrieve titles and image URLs from resulting images
        String[] titles = new String[perPage];
        String[] ids = new String[perPage];
        String[] urlsThumb = new String[perPage];
        String[] urlsLarge = new String[perPage];
        for (int i = 0; i < perPage; i++) {

            Photo a = results.get(i);
            String title = a.getTitle();
            String urlThumb = a.getThumbnailUrl();
            String urlLarge = a.getLargeUrl();
            String id = a.getId();

            titles[i] = title;
            urlsThumb[i] = urlThumb;
            urlsLarge[i] = urlLarge;
            ids[i] = id;
            //Log.v(TAG, ids[i]);
        }

        // Set the images and text up in the ListView using custom adapter
        context = this;
        list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(new CustomListAdapter(this, titles, urlsThumb, urlsLarge, ids));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        final MediaPlayer mp = MediaPlayer.create(context, R.raw.blop);

        if (id == R.id.action_settings) {
            Intent goToLogin = new Intent(MainActivity.this, LoginActivity.class);
            MainActivity.this.startActivity(goToLogin);
        }


        return super.onOptionsItemSelected(item);
    }

    // query the Flickr API for requested photo data
    private class APIHitTask extends AsyncTask<Void, Void, PhotoList<Photo>> {
        final String apiKey = "a4510cb0bda48ea5973fb6d9daf1dfd2";
        final String sharedSecret = "f85840639647368d";

        protected PhotoList<Photo> doInBackground(Void... params){
            Flickr f = new Flickr(apiKey, sharedSecret, new REST());
            PeopleInterface peopleInterface= f.getPeopleInterface();

            // first need to find ID of user by username
            User user;
            String id = null;
            try {
                user = peopleInterface.findByUsername("NASA_Images");
                id = user.getId();
            } catch (FlickrException e1) {
                e1.printStackTrace();
            }

            // using Flickr "photos.getPublicPhotos" taking a user ID
            String userId = id;
            PhotoList<Photo> results = null;
            try {
                results = peopleInterface.getPublicPhotos(userId, null, perPage, page);
            } catch (FlickrException e) {
                e.printStackTrace();
            }

            return results;
        }
    }

}
