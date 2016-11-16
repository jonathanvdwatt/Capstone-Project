package local.watt.mzansitravel.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.List;

import local.watt.mzansitravel.Data.DataProviderContract;
import local.watt.mzansitravel.Models.Details;
import local.watt.mzansitravel.R;

/**
 * Created by f4720431 on 2016/10/31.
 */
public class ListViewAdapter extends CursorAdapter {
    private static final String TAG = CursorAdapter.class.getSimpleName();

    public ListViewAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    /*
     * Inflate the new View and return it.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.saved_place_list, parent, false);
    }

    /*
     * Binds a view and a cursor
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView addressTextView = (TextView) view.findViewById(R.id.nameTextView);
        TextView ratingTextView = (TextView) view.findViewById(R.id.ratingTextView);

        String placeName = cursor.getString(cursor.getColumnIndexOrThrow(DataProviderContract.PLACE_NAME_COLUMN));
        String placeRating = cursor.getString(cursor.getColumnIndexOrThrow(DataProviderContract.PLACE_RATING_COLUMN));

        addressTextView.setText(placeName);
        ratingTextView.setText(placeRating);
    }
}
