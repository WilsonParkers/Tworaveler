package com.developer.hare.tworaveler.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.developer.hare.tworaveler.Data.DataDefinition;
import com.developer.hare.tworaveler.Data.SessionManager;
import com.developer.hare.tworaveler.Listener.OnProgressAction;
import com.developer.hare.tworaveler.Model.Request.UserReqModel;
import com.developer.hare.tworaveler.Model.Response.ResponseModel;
import com.developer.hare.tworaveler.Model.UserModel;
import com.developer.hare.tworaveler.Net.Net;
import com.developer.hare.tworaveler.R;
import com.developer.hare.tworaveler.UI.AlertManager;
import com.developer.hare.tworaveler.UI.FontManager;
import com.developer.hare.tworaveler.UI.ProgressManager;
import com.developer.hare.tworaveler.UI.UIFactory;
import com.developer.hare.tworaveler.Util.ResourceManager;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.developer.hare.tworaveler.Data.DataDefinition.Network.CODE_EMAIL_CONFLICT;
import static com.developer.hare.tworaveler.Data.DataDefinition.Network.CODE_NICKNAME_CONFLICT;
import static com.developer.hare.tworaveler.Data.DataDefinition.Network.CODE_SUCCESS;

public class SignUp extends AppCompatActivity {
    private EditText ET_email, ET_password, ET_nickName;
    private Button BT_signUp;

    private ResourceManager resourceManager;
    private ProgressManager progressManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();
    }

    private void init() {
        UIFactory uiFactory = UIFactory.getInstance(this);
        resourceManager = ResourceManager.getInstance();
        progressManager = new ProgressManager(this);

        ET_email = uiFactory.createView(R.id.signUp$ET_email);
        ET_password = uiFactory.createView(R.id.signUp$ET_password);
        ET_nickName = uiFactory.createView(R.id.signUp$ET_nickname);
        ET_nickName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_DONE){
                    BT_signUp.callOnClick();
                }
                return true;
            }
        });
        BT_signUp = uiFactory.createView(R.id.signUp$BT_signUp);
        BT_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validCheck()) {
                    signUp();
                }
            }
        });

        ArrayList<TextView> textViews = new ArrayList<>();
        textViews.add(ET_email);
        textViews.add(ET_password);
        FontManager.getInstance().setFont(textViews, "Roboto-Medium.ttf");
        FontManager.getInstance().setFont(ET_nickName, "NotoSansCJKkr-Medium.ttf");
    }

    private boolean validCheck() {
        String email = ET_email.getText().toString(), password = ET_password.getText().toString(), nickName = ET_nickName.getText().toString();
        boolean result = false;
        if (email.isEmpty() | !email.matches(DataDefinition.RegularExpression.REG_EMAIL)) {
            AlertManager.getInstance().createAlert(SignUp.this, SweetAlertDialog.WARNING_TYPE, resourceManager.getResourceString(R.string.signUp_alert_title_fail), resourceManager.getResourceString(R.string.signUp_alert_content_fail4)).show();
        } else if (password.isEmpty() | !password.matches(DataDefinition.RegularExpression.REG_PASSWORD)) {
            AlertManager.getInstance().createAlert(SignUp.this, SweetAlertDialog.WARNING_TYPE, resourceManager.getResourceString(R.string.signUp_alert_title_fail), resourceManager.getResourceString(R.string.signUp_alert_content_fail5)).show();
        } else if (nickName.isEmpty() | !nickName.matches(DataDefinition.RegularExpression.REG_NICKNAME)) {
            AlertManager.getInstance().createAlert(SignUp.this, SweetAlertDialog.WARNING_TYPE, resourceManager.getResourceString(R.string.signUp_alert_title_fail), resourceManager.getResourceString(R.string.signUp_alert_content_fail6)).show();
        } else {
            result = true;
        }
        return result;
    }

    private void signUp() {
        progressManager.actionWithState(new OnProgressAction() {
            @Override
            public void run() {
                UserReqModel model = new UserReqModel(ET_email.getText().toString(), ET_password.getText().toString(), ET_nickName.getText().toString());
                Net.getInstance().getFactoryIm().userSignUp(model).enqueue(new Callback<ResponseModel<UserModel>>() {
                    @Override
                    public void onResponse(Call<ResponseModel<UserModel>> call, Response<ResponseModel<UserModel>> response) {
                        UserModel result = response.body().getResult();
//                        LogManager.log(LogManager.LOG_INFO, SignUp.class, "signUp - onResponse(Call, Response)", "body : " + result);
                        if (response.isSuccessful()) {
                            progressManager.endRunning();
//                            LogManager.log(LogManager.LOG_INFO, SignUp.class, "signUp - onResponse(Call, Response)", "isSuccess ");
                            switch (response.body().getSuccess()) {
                                case CODE_SUCCESS:
                                    AlertManager.getInstance().createAlert(SignUp.this, SweetAlertDialog.SUCCESS_TYPE
                                            , resourceManager.getResourceString(R.string.signUp_alert_title_success)
                                            , resourceManager.getResourceString(R.string.signUp_alert_content_success), "확인", new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    SessionManager.getInstance().setUserModel(result);
                                                    finish();
                                                }
                                            }).show();
                                    break;
                                case CODE_EMAIL_CONFLICT:
                                    AlertManager.getInstance().createAlert(SignUp.this, SweetAlertDialog.WARNING_TYPE
                                            , resourceManager.getResourceString(R.string.signUp_alert_title_fail)
                                            , resourceManager.getResourceString(R.string.signUp_alert_content_fail2), "확인").show();
                                    break;
                                case CODE_NICKNAME_CONFLICT:
                                    AlertManager.getInstance().createAlert(SignUp.this, SweetAlertDialog.ERROR_TYPE
                                            , resourceManager.getResourceString(R.string.signUp_alert_title_fail)
                                            , resourceManager.getResourceString(R.string.signUp_alert_content_fail3), "확인").show();
                                    break;
                                default:
                                    netFail();
                                    break;
                            }

                        } else {
//                            LogManager.log(LogManager.LOG_INFO, SignUp.class, "signUp - onResponse(Call, Response)", "isFail");
                            netFail();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseModel<UserModel>> call, Throwable t) {
//                        LogManager.log(SignUp.class, "onFailure()", t);
                        netFail();
                    }
                });
            }
        });
    }

    private void netFail() {
        progressManager.endRunning();
        AlertManager.getInstance().showNetFailAlert(this, R.string.signUp_alert_title_fail, R.string.signUp_alert_content_fail7);
    }

}
