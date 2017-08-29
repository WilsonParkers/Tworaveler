package com.developer.hare.tworaveler.Fragment.Page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.developer.hare.tworaveler.Activity.RegistDayDetail;
import com.developer.hare.tworaveler.Adapter.MypageDetailAdapter;
import com.developer.hare.tworaveler.Data.DataDefinition;
import com.developer.hare.tworaveler.Data.SessionManager;
import com.developer.hare.tworaveler.Fragment.BaseFragment;
import com.developer.hare.tworaveler.Listener.OnProgressAction;
import com.developer.hare.tworaveler.Model.Response.ResponseArrayModel;
import com.developer.hare.tworaveler.Model.ScheduleDayModel;
import com.developer.hare.tworaveler.Model.ScheduleModel;
import com.developer.hare.tworaveler.Model.UserModel;
import com.developer.hare.tworaveler.Net.Net;
import com.developer.hare.tworaveler.R;
import com.developer.hare.tworaveler.UI.AlertManager;
import com.developer.hare.tworaveler.UI.FragmentManager;
import com.developer.hare.tworaveler.UI.Layout.MenuTopTitle;
import com.developer.hare.tworaveler.UI.ProgressManager;
import com.developer.hare.tworaveler.UI.UIFactory;
import com.developer.hare.tworaveler.Util.FontManager;
import com.developer.hare.tworaveler.Util.Log_HR;
import com.developer.hare.tworaveler.Util.ResourceManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.developer.hare.tworaveler.Data.DataDefinition.Intent.KEY_SCHEDULE_MODEL;
import static com.developer.hare.tworaveler.Data.DataDefinition.Intent.KEY_TRIPDATE;

public class FragmentMypageDetail extends BaseFragment {
    private MenuTopTitle menuTopTitle;
    private RecyclerView recyclerView;
    private TextView TV_noItem;
    private LinearLayout linearLayout;


    private UIFactory uiFactory;
    private ResourceManager resourceManager;
    private MypageDetailAdapter mypageDetailAdapter;
    private ProgressManager progressManager;
    private ArrayList<ScheduleDayModel> items;
    private ScheduleModel scheduleModel;
    private String trip_date;
    private UserModel userModel;

    public static FragmentMypageDetail newInstance(ScheduleModel scheduleModel, String trip_date) {
        FragmentMypageDetail fragment = new FragmentMypageDetail();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_SCHEDULE_MODEL, scheduleModel);
        bundle.putString(KEY_TRIPDATE, trip_date);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mypage_detail, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    protected void init(View view) {
        Bundle bundle = getArguments();
        scheduleModel = (ScheduleModel) bundle.getSerializable(KEY_SCHEDULE_MODEL);
        trip_date = bundle.getString(KEY_TRIPDATE);
        sessionCheck();

        resourceManager = ResourceManager.getInstance();
        progressManager = new ProgressManager(getActivity());
        uiFactory = UIFactory.getInstance(view);
        linearLayout = uiFactory.createView(R.id.fragment_mypage_detail$LL_empty);

        menuTopTitle = uiFactory.createView(R.id.fragment_mypage_detail$topbar);
        menuTopTitle.getIB_left().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager.getInstance().setFragmentContent(FragmentMyPageSchedule.newInstance(scheduleModel));
            }
        });
        menuTopTitle.getIB_right().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRegister();
            }
        });
        menuTopTitle.getTV_title().setText(userModel.getNickname());
        recyclerView = uiFactory.createView(R.id.fragment_mypage_detail$RV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mypageDetailAdapter = new MypageDetailAdapter(items);
        recyclerView.setAdapter(mypageDetailAdapter);

        TV_noItem = uiFactory.createView(R.id.fragment_mypage_detail$TV_noitem);
        uiFactory.createView(R.id.fragment_mypage_detail$noimage).setOnClickListener(new View.OnClickListener() {
                                                                                         @Override
                                                                                         public void onClick(View view) {
                                                                                             onRegister();
                                                                                         }
                                                                                     }
        );
        FontManager.getInstance().setFont(TV_noItem, "NotoSansCJKkr-Regular.otf");
    }

    private void updateList() {
        progressManager.actionWithState(new OnProgressAction() {
            @Override
            public void run() {
                Net.getInstance().getFactoryIm().selectDetailSchedule(scheduleModel.getTrip_no(), trip_date).enqueue(new Callback<ResponseArrayModel<ScheduleDayModel>>() {
                    @Override
                    public void onResponse(Call<ResponseArrayModel<ScheduleDayModel>> call, Response<ResponseArrayModel<ScheduleDayModel>> response) {
                        if (response.isSuccessful()) {
                            progressManager.endRunning();
                            ResponseArrayModel<ScheduleDayModel> model = response.body();
                            items = model.getResult();
                            if (items == null)
                                items = new ArrayList<ScheduleDayModel>();
                            itemEmptyCheck(items);
                        } else {
                            Log_HR.log(Log_HR.LOG_ERROR, FragmentMypageDetail.class, "onResponse(Call<ResponseArrayModel<ScheduleDayRootModel>>, Response<ResponseArrayModel<ScheduleDayRootModel>>)", "response is not Successful");
                            netFail();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseArrayModel<ScheduleDayModel>> call, Throwable t) {
                        Log_HR.log(FragmentMypageDetail.class, "onFailure(Call<ResponseArrayModel<ScheduleDayRootModel>> ,Throwable)", "Fail", t);
                        netFail();
                    }
                });
            }
        });
    }

    private void netFail() {
        progressManager.endRunning();
        AlertManager.getInstance().showNetFailAlert(getActivity(), R.string.fragmentMyPageDetail_alert_title_fail, R.string.fragmentMyPageDetail_alert_content_fail);
        itemEmptyCheck(items);
    }

    private void itemEmptyCheck(ArrayList<ScheduleDayModel> items) {
        if (items != null && items.size() > 0) {
            linearLayout.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
    }

    private boolean sessionCheck() {
        userModel = SessionManager.getInstance().getUserModel();
        return SessionManager.getInstance().isLogin();
    }

    private void onRegister() {
        Intent intent = new Intent(getActivity(), RegistDayDetail.class);
        intent.putExtra(DataDefinition.Intent.KEY_DATE, trip_date);
        intent.putExtra(DataDefinition.Intent.KEY_SCHEDULE_MODEL, scheduleModel);
        startActivity(intent);
    }
}
