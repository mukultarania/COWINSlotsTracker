package com.api.slotstracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.api.slotstracker.R;
import com.api.slotstracker.data.Slots;

import java.util.ArrayList;

public class SlotsAdapter extends ArrayAdapter<Slots> implements Filterable {
    private ArrayList<Slots> itemsModelsl;
    private ArrayList<Slots> itemsModelListFiltered;
    private Context context;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.slot_item,
                    parent,
                    false
            );
        }
        Slots currentslot = getItem(position);
        TextView name = (TextView) listItemView.findViewById(R.id.name);
        TextView add = (TextView) listItemView.findViewById(R.id.address);
        TextView vac = (TextView) listItemView.findViewById(R.id.vaccin);
        TextView dose1 = (TextView) listItemView.findViewById(R.id.dose1);
        TextView dose2 = (TextView) listItemView.findViewById(R.id.dose2);
        TextView price = (TextView) listItemView.findViewById(R.id.price);
        TextView minage = (TextView) listItemView.findViewById(R.id.min_age);

        name.setText(currentslot.getName());
        add.setText(currentslot.getAddress());
        vac.setText(currentslot.getVaccin());
        dose1.setText("DOSE 1: "+currentslot.getDose1());
        dose2.setText("DOSE 2: "+currentslot.getDose2());
        if(currentslot.getPrice().equals("0")){
            price.setText("FREE");
        }else{
            price.setText("PAID");
        }
        minage.setText(currentslot.getMinage()+"+");

        return listItemView;
    }

    public SlotsAdapter(@NonNull Context context, ArrayList<Slots> slots) {
        super(context, 0, slots);
        this.context = context;
        this.itemsModelListFiltered = slots;
        this.itemsModelsl = slots;
    }

    @Override
    public int getCount() {
        return itemsModelListFiltered.size();
    }

    @Override
    public Slots getItem(int position) {
        return itemsModelListFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                if(charSequence == null || charSequence.length() == 0){
                    filterResults.count = itemsModelsl.size();
                    filterResults.values = itemsModelsl;
                }else{
                    ArrayList<Slots> resultsModel = new ArrayList<>();
                    String searchStr = charSequence.toString().toLowerCase();

                    for(Slots itemsModel:itemsModelsl){
                        if(itemsModel.getName().toLowerCase().contains(searchStr)){
                            resultsModel.add(itemsModel);

                        }
                        filterResults.count = resultsModel.size();
                        filterResults.values = resultsModel;
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                itemsModelListFiltered = (ArrayList<Slots>) filterResults.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
