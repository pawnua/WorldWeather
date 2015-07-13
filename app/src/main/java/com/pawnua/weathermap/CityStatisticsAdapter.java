package com.pawnua.weathermap;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pawnua.weathermap.model.CityAmount;

import java.util.List;

/**
 * Created by MiK on 14.06.2015.
 */
public class CityStatisticsAdapter extends RecyclerView.Adapter<CityStatisticsAdapter.ViewHolder> {

    private List<CityAmount> cityAmountList;

    public CityStatisticsAdapter(List<CityAmount> cityAmountList) {
        this.cityAmountList = cityAmountList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cityamount, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        CityAmount licensePlate = cityAmountList.get(position);

        holder.cityName.setText(licensePlate.city);
        holder.cityAmount.setText(String.valueOf(licensePlate.amount));

/*
//Handle click event on both title and image click
        holder.cityName.setOnClickListener(clickListener);
        holder.cityAmount.setOnClickListener(clickListener);

        holder.cityName.setTag(holder);
        holder.cityAmount.setTag(holder);
*/

    }

/*    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ViewHolder holder = (ViewHolder) view.getTag();
            int position = holder.getPosition();

        }
    };
*/

    @Override
    public int getItemCount() {
        return cityAmountList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView cityName;
        private TextView cityAmount;

        public ViewHolder(View itemView) {
            super(itemView);
            cityName = (TextView) itemView.findViewById(R.id.cityName);
            cityAmount = (TextView) itemView.findViewById(R.id.cityAmount);
        }
    }
}
