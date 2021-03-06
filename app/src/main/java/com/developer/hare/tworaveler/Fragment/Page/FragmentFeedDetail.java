package com.developer.hare.tworaveler.Fragment.Page;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.developer.hare.tworaveler.Adapter.MypageDetailAdapter;
import com.developer.hare.tworaveler.Data.SessionManager;
import com.developer.hare.tworaveler.Fragment.BaseFragment;
import com.developer.hare.tworaveler.Listener.OnItemDataChangeListener;
import com.developer.hare.tworaveler.Listener.OnProgressAction;
import com.developer.hare.tworaveler.Model.Response.ResponseArrayModel;
import com.developer.hare.tworaveler.Model.ScheduleDayModel;
import com.developer.hare.tworaveler.Model.ScheduleModel;
import com.developer.hare.tworaveler.Net.Net;
import com.developer.hare.tworaveler.R;
import com.developer.hare.tworaveler.UI.AlertManager;
import com.developer.hare.tworaveler.UI.FragmentManager;
import com.developer.hare.tworaveler.UI.Layout.MenuTopTitle;
import com.developer.hare.tworaveler.UI.ProgressManager;
import com.developer.hare.tworaveler.UI.UIFactory;
import com.developer.hare.tworaveler.UI.FontManager;
import com.developer.hare.tworaveler.Util.LogManager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.developer.hare.tworaveler.Data.DataDefinition.Intent.KEY_FEED;
import static com.developer.hare.tworaveler.Data.DataDefinition.Intent.KEY_SCHEDULE_MODEL;
import static com.developer.hare.tworaveler.Data.DataDefinition.Intent.KEY_TRIPDATE;

public class FragmentFeedDetail extends BaseFragment {
    private UIFactory uiFactory;
    private MenuTopTitle menuTopTitle;
    private RecyclerView recyclerView;
    private TextView TV_noItem, TV_date;
    private LinearLayout linearLayout, LL_commnet, LL_like;

    private ProgressManager progressManager;
    private SessionManager sessionManager;
    private ArrayList<ScheduleDayModel> items = new ArrayList<>();
    private int user_no;
    private ScheduleModel scheduleModel;
    private String trip_Date;
    private OnItemDataChangeListener onItemDataChangeListener = new OnItemDataChangeListener() {
        @Override
        public void onChange() {
            updateList();
        }
    };

    public static FragmentFeedDetail newInstance(ScheduleModel scheduleModel, String trip_Date) {
        FragmentFeedDetail fragment = new FragmentFeedDetail();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_SCHEDULE_MODEL, scheduleModel);
        bundle.putString(KEY_TRIPDATE, trip_Date);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feed_detail, container, false);
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
        trip_Date = bundle.getString(KEY_TRIPDATE);

        sessionManager = SessionManager.getInstance();
        progressManager = new ProgressManager(getActivity());
        uiFactory = UIFactory.getInstance(view);
        linearLayout = uiFactory.createView(R.id.fragment_feed_detail$LL_empty);

        menuTopTitle = uiFactory.createView(R.id.fragment_feed_detail$topbar);
        menuTopTitle.getIB_left().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager.getInstance().setFragmentContent(FragmentFeedSchedule.newInstance(scheduleModel));
            }
        });
        menuTopTitle.setTitleText(scheduleModel.getNickname());
        menuTopTitle.setTitleFont("NotoSansKR-Regular-Hestia.otf");

        recyclerView = uiFactory.createView(R.id.fragment_feed_detail$RV_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        TV_noItem = uiFactory.createView(R.id.fragment_feed_detail$TV_noitem);
        TV_date = uiFactory.createView(R.id.fragment_feed_detail$TV_date);
        TV_date.setText(trip_Date);

        FontManager.getInstance().setFont(TV_date, "Roboto-Medium.ttf");

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        FontManager.getInstance().setFont(TV_noItem, "NotoSansKR-Regular-Hestia.otf");
        updateList();
    }

    private void updateList() {
        if (sessionCheck())
            user_no = sessionManager.getUserModel().getUser_no();
        else
            user_no = -1;
        progressManager.actionWithState(new OnProgressAction() {
            @Override
            public void run() {
                Net.getInstance().getFactoryIm().selectDetailSchedule(user_no, scheduleModel.getTrip_no(), trip_Date).enqueue(new Callback<ResponseArrayModel<ScheduleDayModel>>() {
                    @Override
                    public void onResponse(Call<ResponseArrayModel<ScheduleDayModel>> call, Response<ResponseArrayModel<ScheduleDayModel>> response) {
//                        LogManager.logA(FragmentFeedDetail.class, "onResponse(Call<ResponseArrayModel<String>> call, Response<ResponseArrayModel<String>> response)", response);
                        if (response.isSuccessful()) {
                            progressManager.endRunning();
                            ResponseArrayModel<ScheduleDayModel> model = response.body();
                            items = model.getResult();
                            if (items == null)
                                items = new ArrayList<ScheduleDayModel>();
                            itemEmptyCheck(items);
                        } else {
                            LogManager.log(LogManager.LOG_ERROR, FragmentFeedDetail.class, "onResponse(Call<ResponseArrayModel<ScheduleDayRootModel>>, Response<ResponseArrayModel<ScheduleDayRootModel>>)", "response is not Successful");
                            netFail();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseArrayModel<ScheduleDayModel>> call, Throwable t) {
                        LogManager.log(FragmentFeedDetail.class, "onFailure(Call<ResponseArrayModel<ScheduleDayRootModel>> ,Throwable)", "Fail", t);
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
            recyclerView.setAdapter(new MypageDetailAdapter(items, onItemDataChangeListener, KEY_FEED));
        } else {
            linearLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        }
    }

    private boolean sessionCheck() {
        return SessionManager.getInstance().isLogin();
    }
}
