package local.watt.mzansitravel.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import local.watt.mzansitravel.Models.MzansiPlaces;
import local.watt.mzansitravel.R;

/**
 * Created by f4720431 on 2016/09/12.
 */
public class PlacesAdapter extends ArrayAdapter<MzansiPlaces> {
    private static final String TAG = PlacesAdapter.class.getSimpleName();

    private Context mContext;
    private int layoutResourceId;
    private List<MzansiPlaces> mMzansiPlaces = new ArrayList<>();
    private LayoutInflater mInflater;


    public PlacesAdapter(Context context, int layoutResourceId, List<MzansiPlaces> mzansiPlaces) {
        super(context, layoutResourceId, mzansiPlaces);
        Log.d(TAG, "########## PlacesAdapter() constructor");
        this.mContext = context;
        this.layoutResourceId = layoutResourceId;
        this.mMzansiPlaces = mzansiPlaces;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mMzansiPlaces.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public MzansiPlaces getItem(int position) {
        return mMzansiPlaces.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MzansiPlaces mzansiPlace = mMzansiPlaces.get(position);

        if (convertView == null) {
            convertView = mInflater.inflate(layoutResourceId, parent, false);
            convertView.setTag(R.id.textView, convertView.findViewById(R.id.textView));
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.placeIcon);
        Picasso.with(mContext)
                .load(mzansiPlace.getIcon())
                .into(imageView);

        TextView textView = (TextView) convertView.findViewById(R.id.textView);
        textView.setText(mzansiPlace.getName());

        return convertView;
    }
}
