package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import co.com.celuweb.carterabaldomero.R;

public class SpinnerStringAdapter extends ArrayAdapter<String> {

    ArrayList<String> states;
    Context mContext;

    public SpinnerStringAdapter(Context context, ArrayList<String> states) {
        super(context, R.layout.spinner_item_general);
        this.states = states;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return states.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.spinner_item_general, parent, false);
            mViewHolder.mTypes = (TextView) convertView.findViewById(R.id.tvClientData);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.mTypes.setText(states.get(position));
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    private static class ViewHolder {
        TextView mTypes;
    }
}
