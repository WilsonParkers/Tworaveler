package com.developer.hare.tworaveler.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.developer.hare.tworaveler.Listener.OnSelectCityListener;
import com.developer.hare.tworaveler.Model.CityModel;
import com.developer.hare.tworaveler.R;
import com.developer.hare.tworaveler.UI.UIFactory;
import com.developer.hare.tworaveler.UI.FontManager;

import java.util.ArrayList;

/**
 * Created by Hare on 2017-08-01.
 */

public class CityListAdapter extends RecyclerView.Adapter<CityListAdapter.ViewHolder> {
    private OnSelectCityListener onSelectCityModel;
    private ArrayList<CityModel> items;
    private Context context;

    public CityListAdapter(OnSelectCityListener onSelectCityModel, ArrayList<CityModel> items, Context context) {
        this.onSelectCityModel = onSelectCityModel;
        this.items = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_city, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.toBind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout LL_form;
        private TextView TV_country, TV_city;

        public ViewHolder(View itemView) {
            super(itemView);
            UIFactory uiFactory = UIFactory.getInstance(itemView);
            LL_form = uiFactory.createView(R.id.item_search_city$LL_form);
            TV_country = uiFactory.createView(R.id.item_search_city$TV_country);
            TV_city = uiFactory.createView(R.id.item_search_city$TV_city);
        }

        public void toBind(CityModel model) {
            TV_country.setText(model.getCountry());
            TV_city.setText(model.getCity());
            FontManager.getInstance().setFont(TV_country, "NotoSansKR-Medium-Hestia.otf");
            LL_form.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSelectCityModel.onSelectCity(model);
                }
            });
        }
    }

}
