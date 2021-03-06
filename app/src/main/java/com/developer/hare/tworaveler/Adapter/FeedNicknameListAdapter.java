package com.developer.hare.tworaveler.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.developer.hare.tworaveler.Activity.Comment;
import com.developer.hare.tworaveler.Data.DataDefinition;
import com.developer.hare.tworaveler.Data.SessionManager;
import com.developer.hare.tworaveler.Fragment.Page.FragmentFeedSchedule;
import com.developer.hare.tworaveler.Model.LikeModel;
import com.developer.hare.tworaveler.Model.Response.ResponseModel;
import com.developer.hare.tworaveler.Model.ScheduleModel;
import com.developer.hare.tworaveler.Net.Net;
import com.developer.hare.tworaveler.R;
import com.developer.hare.tworaveler.UI.FontManager;
import com.developer.hare.tworaveler.UI.FragmentManager;
import com.developer.hare.tworaveler.UI.UIFactory;
import com.developer.hare.tworaveler.Util.Image.ImageManager;
import com.developer.hare.tworaveler.Util.LogManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hare on 2017-08-01.
 */

public class FeedNicknameListAdapter extends RecyclerView.Adapter<FeedNicknameListAdapter.ViewHolder> {
    private ArrayList<ScheduleModel> items;
    private Context context;
    private ImageManager imageManager = ImageManager.getInstance();


    public FeedNicknameListAdapter(ArrayList<ScheduleModel> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.toBind(items.get(position));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_mypage, parent, false);
//        ViewHolder viewHolder = new ViewHolder(view);
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mypage, parent, false), parent.getContext());
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView TV_title, TV_date, TV_like, TV_commenet;
        private LinearLayout  LL_like, LL_comment;
        private ImageView IV_cover, IV_like;
        private ScheduleModel model;
        private Context context;


        public ViewHolder(View itemView, Context context) {
            super(itemView);
            this.context = context;
            UIFactory uiFactory = UIFactory.getInstance(itemView);
            uiFactory.createViewWithRateParams(R.id.item_mypage$Root);
            TV_title = uiFactory.createView(R.id.item_mypage$TV_title);
            TV_date = uiFactory.createView(R.id.item_mypage$TV_date);
            TV_like = uiFactory.createView(R.id.item_mypage$TV_like);
            TV_commenet = uiFactory.createView(R.id.item_mypage$TV_comment);
            LL_like = uiFactory.createView(R.id.item_mypage$LL_like);
            LL_comment = uiFactory.createView(R.id.item_mypage$LL_comment);
            IV_cover = uiFactory.createView(R.id.item_mypage$IV_cover);
            IV_like = uiFactory.createView(R.id.item_mypage$IV_like);

            ArrayList<TextView> textlist1 = new ArrayList<>();
            textlist1.add(TV_date);
            textlist1.add(TV_like);
            textlist1.add(TV_commenet);
            FontManager.getInstance().setFont(textlist1, "Roboto-Medium.ttf");
            FontManager.getInstance().setFont(TV_title, "NotoSansKR-Medium-Hestia.otf");

            LL_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, Comment.class);
                    intent.putExtra(DataDefinition.Intent.KEY_SCHEDULE_MODEL, model);
                    context.startActivity(intent);
                }
            });
        }

        public void toBind(ScheduleModel model) {
            this.model = model;
            IV_cover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager.getInstance().setFragmentContent(FragmentFeedSchedule.newInstance(model));
                }
            });
            ImageManager imageManager = ImageManager.getInstance();
            imageManager.loadImage(imageManager.createRequestCreator(context, model.getTrip_pic_url(), ImageManager.FIT_TYPE).centerCrop(), IV_cover);
            TV_title.setText(model.getTripName());
            TV_date.setText(model.getStart_date() + " ~ " + model.getEnd_date());
            TV_like.setText(model.getLikeCount() + "");
            TV_commenet.setText(model.getCommentCount() + "");
            LL_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    likeClick(model.isLike());
                }
            });
            changeLike(model.isLike());
        }
        private void changeLike(boolean isLike) {
            if (isLike) {
                imageManager.loadImage(imageManager.createRequestCreator(context, R.drawable.icon_heart_click, ImageManager.FIT_TYPE).centerCrop().noFade(), IV_like);
            } else {
                imageManager.loadImage(imageManager.createRequestCreator(context, R.drawable.icon_heart_unclick, ImageManager.FIT_TYPE).centerCrop().noFade(), IV_like);
            }
        }

        private void likeClick(boolean isLike){
            if(isLike){
                Net.getInstance().getFactoryIm().modifyUnLike(SessionManager.getInstance().getUserModel().getUser_no(), model.getTrip_no()).enqueue(new Callback<ResponseModel<LikeModel>>() {
                    @Override
                    public void onResponse(Call<ResponseModel<LikeModel>> call, Response<ResponseModel<LikeModel>> response) {
//                        LogManager.log(FeedNicknameListAdapter.class, "onResponse(Call<ResponseArrayModel<String>> call, Response<ResponseArrayModel<String>> response)", response);

                        if (response.isSuccessful()) {
                            switch (response.body().getSuccess()) {
                                case DataDefinition.Network.CODE_SUCCESS:
                                    changeLike(false);
                                    int likeCount =model.getLikeCount()-1;
                                    TV_like.setText(""+likeCount );
                                    model.setLikeCount(likeCount);
                                    break;

                            }
                        }else{
                            LogManager.log(LogManager.LOG_INFO,FeedNicknameListAdapter.class, "onResponse","onResponse is not successful");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel<LikeModel>> call, Throwable t) {
                        LogManager.log(FeedNicknameListAdapter.class, "onFailure", t);
                    }
                });
            }else {
                Net.getInstance().getFactoryIm().modifyLike(SessionManager.getInstance().getUserModel().getUser_no(), model.getTrip_no()).enqueue(new Callback<ResponseModel<LikeModel>>() {
                    @Override
                    public void onResponse(Call<ResponseModel<LikeModel>> call, Response<ResponseModel<LikeModel>> response) {
                        if(response.isSuccessful()){
                            switch (response.body().getSuccess()){
                                case DataDefinition.Network.CODE_SUCCESS:
                                    changeLike(true);
                                    int likeCount =model.getLikeCount()+1;
                                    TV_like.setText(""+likeCount);
                                    model.setLikeCount(likeCount);
                                    break;
                            }
                        }else{

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel<LikeModel>> call, Throwable t) {

                    }
                });
            }
            model.setLike(!model.isLike());
        }
    }

}
