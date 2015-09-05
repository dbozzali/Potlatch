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
import org.coursera.androidcapstone.common.client.GiftItem;

import java.util.List;

public class GiftItemCollectionAdapter extends ArrayAdapter<GiftItem> {
    private static final String LOG_TAG = GiftItemCollectionAdapter.class.getCanonicalName();

    private final Context context;
    private int resource;
    private final List<GiftItem> items;

    public GiftItemCollectionAdapter(Context context, int resource, List<GiftItem> items) {
        super(context, R.layout.view_gift_listview_row, items);
        Log.d(LOG_TAG, "constructor");
        this.context = context;
        this.resource = resource;
        this.items = items;
    }

    @Override
    public GiftItem getItem(int position) {
       return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        GiftItem item = getItem(position);
        return item.id;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    static class GiftItemHolder {
        ImageView thumbnailIV;
        TextView titleTV;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(LOG_TAG, "getView()");
        LinearLayout giftItemView = null;
        GiftItemHolder holder = null;
        try {
            // reuse views
            if (convertView == null) {
                giftItemView = new LinearLayout(getContext());
                String inflater = Context.LAYOUT_INFLATER_SERVICE;
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
                vi.inflate(R.layout.view_gift_listview_row, giftItemView, true);
                // configure view holder
                holder = new GiftItemHolder();
                holder.thumbnailIV = (ImageView) giftItemView.findViewById(R.id.gift_listview_thumbnail_value);
                holder.titleTV = (TextView) giftItemView.findViewById(R.id.gift_listview_title_value);
                giftItemView.setTag(holder);
            }
			else {
                giftItemView = (LinearLayout) convertView;
                holder = (GiftItemHolder) giftItemView.getTag();
            }
            // fill data
            GiftItem giftItem = getItem(position);
			byte[] thumbnailByteArray = giftItem.thumbnailAsByteArray();
            holder.thumbnailIV.setImageBitmap(BitmapFactory.decodeByteArray(thumbnailByteArray, 0, thumbnailByteArray.length));
            holder.titleTV.setText("" + giftItem.title);
            holder.titleTV.setTag(giftItem.id);
        }
		catch (Exception e) {
            Toast.makeText(getContext(),
                           "exception in GiftItemCollectionAdapter: " + e.getMessage(),
                           Toast.LENGTH_SHORT).show();
        }
        return giftItemView;
    }
}
