package com.developer.hare.tworaveler.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.developer.hare.tworaveler.Listener.OnSelectNicknameListener;
import com.developer.hare.tworaveler.Model.ScheduleModel;
import com.developer.hare.tworaveler.R;
import com.developer.hare.tworaveler.UI.FontManager;
import com.developer.hare.tworaveler.UI.UIFactory;
import com.developer.hare.tworaveler.Util.Exception.NullChecker;
import com.developer.hare.tworaveler.Util.Image.ImageManager;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Hare on 2017-08-01.
 */

public class NicknameListAdapter extends RecyclerView.Adapter<NicknameListAdapter.ViewHolder> {
    private OnSelectNicknameListener onSelectNicknameListener;
    private ArrayList<ScheduleModel> items;
    private Context context;

    public NicknameListAdapter(OnSelectNicknameListener onSelectNicknameListener, ArrayList<ScheduleModel> items, Context context) {
        this.onSelectNicknameListener = onSelectNicknameListener;
        this.items = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_nickname, parent, false);
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
        private TextView TV_contents;
        private LinearLayout LL_cell;
        private CircleImageView CV_profile;
        private ImageManager imageManager;
        public ViewHolder(View itemView) {
            super(itemView);
            UIFactory uiFactory = UIFactory.getInstance(itemView);
            TV_contents = uiFactory.createView(R.id.item_search_nickname$TV_contents);
            LL_cell = uiFactory.createView(R.id.item_search_nickname$LL_cell);
            CV_profile = uiFactory.createView(R.id.item_search_nickname$CV_profile);

        }

        public void toBind(ScheduleModel model) {
            TV_contents.setText(model.getNickname());
            FontManager.getInstance().setFont(TV_contents, "NotoSansKR-Medium-Hestia.otf");
            LL_cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSelectNicknameListener.onSelectNickname(model);
                }
            });
            imageManager = ImageManager.getInstance();
            if (NullChecker.getInstance().nullCheck(model.getProfile_pic_thumbnail_url())) {
                imageManager.loadImage(imageManager.createRequestCreator(context, R.drawable.image_profile, ImageManager.BASIC_TYPE), CV_profile);
            } else {
                imageManager.loadImage(imageManager.createRequestCreator(context, model.getProfile_pic_thumbnail_url(), ImageManager.FIT_TYPE).placeholder(R.drawable.image_profile), CV_profile);
            }
        }
    }

}
