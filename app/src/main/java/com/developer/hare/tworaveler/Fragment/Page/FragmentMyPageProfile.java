package com.developer.hare.tworaveler.Fragment.Page;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.developer.hare.tworaveler.Activity.MyProfileSet;
import com.developer.hare.tworaveler.Data.SessionManager;
import com.developer.hare.tworaveler.Fragment.BaseFragment;
import com.developer.hare.tworaveler.Fragment.Menu.FragmentMyPage;
import com.developer.hare.tworaveler.Model.UserModel;
import com.developer.hare.tworaveler.R;
import com.developer.hare.tworaveler.UI.FragmentManager;
import com.developer.hare.tworaveler.UI.Layout.MenuTopTitle;
import com.developer.hare.tworaveler.UI.UIFactory;
import com.developer.hare.tworaveler.Util.FontManager;
import com.developer.hare.tworaveler.Util.Image.ImageManager;

import java.util.ArrayList;

public class FragmentMyPageProfile extends BaseFragment {
    private static FragmentMyPageProfile fragment = new FragmentMyPageProfile();
    private UIFactory uiFactory;
    private MenuTopTitle menuTopTitle;
    private TextView TV_cntFollower, TV_cntFollowing, TV_nickname, TV_message;
    private ImageView IV_profile;
    private Class myClass = getClass();

    public static FragmentMyPageProfile newInstance() {
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_page_profile, null);
    }

    @Override
    protected void init(View view) {
        uiFactory = UIFactory.getInstance(getActivity());
        menuTopTitle = uiFactory.createView(R.id.fragment_mypage_profile$topbar);
        menuTopTitle.getIB_left().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager.getInstance().setFragmentContent(FragmentMyPage.newInstance());
            }
        });
        menuTopTitle.getIB_right().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MyProfileSet.class));
            }
        });
        IV_profile = uiFactory.createView(R.id.fragment_mypage_profile$IV_profile);
        TV_cntFollower = uiFactory.createView(R.id.fragment_mypage_profile$TV_cntfollower);
        TV_cntFollowing = uiFactory.createView(R.id.fragment_mypage_profile$TV_cntfollowing);
        TV_nickname = uiFactory.createView(R.id.fragment_mypage_profile$TV_nickname);
        TV_message = uiFactory.createView(R.id.fragment_mypage_profile$TV_message);
        ArrayList<TextView> textViews = new ArrayList<>();
        textViews.add(uiFactory.createView(R.id.fragment_mypage_profile$TV_follower));
        textViews.add(uiFactory.createView(R.id.fragment_mypage_profile$TV_following));
        textViews.add(TV_nickname);
        textViews.add(TV_message);
        FontManager.getInstance().setFont(textViews, "NotoSansCJKkr-Medium.otf");
        textViews.clear();
        textViews.add(TV_cntFollower);
        textViews.add(TV_cntFollowing);
        FontManager.getInstance().setFont(textViews, "NotoSansCJKkr-Bold.otf");
    }

    @Override
    public void onResume() {
        super.onResume();
        setData();
    }

    private void setData() {
        UserModel userModel = SessionManager.getInstance().getUserModel();
//        Log_HR.log(Log_HR.LOG_INFO, myClass, "setData()", "TV_cntFollower is Null? : " + (TV_cntFollower == null));
//        Log_HR.log(Log_HR.LOG_INFO, myClass, "setData()", "getFollowers is Null? : " + (userModel.getFollowers() == null));
        TV_cntFollower.setText(userModel.getFollowers().size() + "");
        TV_cntFollowing.setText(userModel.getFollowees().size() + "");
        TV_nickname.setText(userModel.getNickname());
        TV_message.setText(userModel.getStatus_message());
        ImageManager imageManager = ImageManager.getInstance();
        imageManager.loadImage(imageManager.createRequestCreator(getActivity(), userModel.getProfile_pic_url_thumbnail(), ImageManager.THUMBNAIL_TYPE).placeholder(R.drawable.image_profile), IV_profile);
    }
}