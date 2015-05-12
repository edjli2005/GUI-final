package edward.guifinalproject;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

// This class contains the custom adapter setup to show a custom row format as defined in list_row.xml
public class CustomListAdapter extends BaseAdapter {

    final Context context;
    final String [] titles;
    final String [] urlsThumb;
    final String [] urlsLarge;
    final String [] ids;
    private static LayoutInflater inflater = null;

    public CustomListAdapter(MainActivity mainActivity, String[] titles, String[] urlsThumb, String[] urlsLarge, String[] ids) {
        // TODO Auto-generated constructor stub
        context = mainActivity;
        this.titles=titles;
        this.urlsThumb = urlsThumb;
        this.urlsLarge = urlsLarge;
        this.ids = ids;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // Getters and setters
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
    }

    // Set the view to display data
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final Holder holder = new Holder();
        final View rowView;
        rowView = inflater.inflate(R.layout.list_row, null);

        // Set the text and attempt background Asynctask to retrieve images
        holder.tv=(TextView) rowView.findViewById(R.id.item);
        holder.img=(ImageView) rowView.findViewById(R.id.icon);
        holder.tv.setText(titles[position]);
        new DownloadImageTask(holder.img).execute(urlsThumb[position]);

        // Actions that occur on click
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Go to details view
                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                intent.putExtra("title", titles[position]);
                intent.putExtra("id", ids[position]);
                intent.putExtra("url", urlsLarge[position]);
                context.startActivity(intent);
            }
        });
        return rowView;
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