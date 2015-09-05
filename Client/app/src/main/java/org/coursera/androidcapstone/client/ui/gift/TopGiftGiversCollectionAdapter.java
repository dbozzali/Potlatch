package org.coursera.androidcapstone.client.ui.gift;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.coursera.androidcapstone.client.R;
import org.coursera.androidcapstone.common.client.TopGiftGiver;

import java.util.List;

public class TopGiftGiversCollectionAdapter extends ArrayAdapter<TopGiftGiver> {
    private static final String LOG_TAG = TopGiftGiversCollectionAdapter.class.getCanonicalName();

    private final Context context;
    private int resource;
    private final List<TopGiftGiver> items;

    public TopGiftGiversCollectionAdapter(Context context, int resource, List<TopGiftGiver> items) {
        super(context, R.layout.top_gift_giver_listview_row, items);
        Log.d(LOG_TAG, "constructor");
        this.context = context;
        this.resource = resource;
        this.items = items;
    }

    @Override
    public TopGiftGiver getItem(int position) {
        return this.items.get(position);
    }

    static class TopGiftGiverHolder {
        ImageView userAvatarIV;
        TextView usernameTV;
        TextView touchesTV;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(LOG_TAG, "getView()");
        LinearLayout topGiftGiverView = null;
        TopGiftGiverHolder holder = null;
        try {
            // reuse views
            if (convertView == null) {
                topGiftGiverView = new LinearLayout(getContext());
                String inflater = Context.LAYOUT_INFLATER_SERVICE;
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
                vi.inflate(R.layout.top_gift_giver_listview_row, topGiftGiverView, true);
                // configure view holder
                holder = new TopGiftGiverHolder();
                holder.userAvatarIV = (ImageView) topGiftGiverView.findViewById(R.id.top_gift_giver_user_avatar);
                holder.usernameTV = (TextView) topGiftGiverView.findViewById(R.id.top_gift_giver_username);
                holder.touchesTV = (TextView) topGiftGiverView.findViewById(R.id.top_gift_giver_touches);
                topGiftGiverView.setTag(holder);
            }
            else {
                topGiftGiverView = (LinearLayout) convertView;
                holder = (TopGiftGiverHolder) topGiftGiverView.getTag();
            }
            // fill data
            TopGiftGiver topGiftGiver = getItem(position);
            byte[] avatarByteArray = topGiftGiver.avatarAsByteArray();
            holder.userAvatarIV.setImageBitmap(BitmapFactory.decodeByteArray(avatarByteArray, 0, avatarByteArray.length));
            holder.usernameTV.setText("" + topGiftGiver.username);
            holder.touchesTV.setText("" + topGiftGiver.touches);
        }
        catch (Exception e) {
            Toast.makeText(getContext(),
                           "exception in TopGiftGiversCollectionAdapter: " + e.getMessage(),
                           Toast.LENGTH_SHORT).show();
        }
        return topGiftGiverView;
    }
}
