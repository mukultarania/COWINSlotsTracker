package com.api.slotstracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.api.slotstracker.R;
import com.api.slotstracker.data.State;

import java.util.ArrayList;

public class StateAdapter extends ArrayAdapter<State> {
    public StateAdapter(@NonNull Context context, ArrayList<State> resource) {
        super(context, 0, resource);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.state_item,
                    parent,
                    false
            );
        }
        State state = getItem(position);
        TextView st_name = (TextView) listItemView.findViewById(R.id.state_name);
        TextView st_total = (TextView) listItemView.findViewById(R.id.st_total);
        TextView st_recovered = (TextView) listItemView.findViewById(R.id.st_recovered);
        TextView st_death = (TextView) listItemView.findViewById(R.id.st_death);
        st_name.setText(state.getLoc()+"");
        st_total.setText(state.getTotalConfirmed()+"");
        st_recovered.setText(state.getDischarged()+"");
        st_death.setText(state.getDeaths()+"");
        return listItemView;
    }
}
