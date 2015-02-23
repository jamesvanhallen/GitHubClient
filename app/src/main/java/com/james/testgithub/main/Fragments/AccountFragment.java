package com.james.testgithub.main.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.james.testgithub.R;
import com.james.testgithub.main.Activities.AccountActivity;
import com.james.testgithub.main.Activities.RepositoryActivity;
import com.james.testgithub.main.Another.GitHubLoader;
import com.james.testgithub.main.Another.RequestCallback;
import com.james.testgithub.main.Another.User;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class AccountFragment extends Fragment {

    private ProgressDialog pd;
    public final String ERROR = "fail authorization";

    @InjectView(R.id.login)
    TextView mLogin;
    @InjectView(R.id.password)
    TextView mPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.start_fragment, container, false);
        ButterKnife.inject(this, v);
        pd = new ProgressDialog(getActivity());
        CheckAuthorization();


        return v;
    }

    @OnClick(R.id.logIn_btn)
    void onClick() {

        pd.setMessage(getText(R.string.loading));
        pd.show();

        String login = mLogin.getText().toString();
        String password = mPassword.getText().toString();

        GitHubLoader.getInstance().getAuthorization(login,
                    password, new RequestCallback<User>(){

                @Override
                public void onSuccess(User user) {
                    Intent i = new Intent(getActivity(), RepositoryActivity.class);
                    i.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK );
                    startActivity(i);
                    pd.dismiss();
                }

                @Override
                public void onFailure(int error_code) {
                    pd.dismiss();
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle(getErrorText(error_code))
                            .setNegativeButton(R.string.on_back, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    alert.create();
                    alert.show();
                }
        });
    }



    public String getErrorText(int event_code) {
        String errtxt;
        switch(event_code) {
            case GitHubLoader.ERR_CODE_NO_INTERNET:
                errtxt = getString(R.string.error_msg_no_internet);
                break;
            case GitHubLoader.ERR_CODE_UNKONWN_ERROR:
                errtxt = getString(R.string.error_msg_unknown);
                break;
            case GitHubLoader.ERR_CODE_FORBIDDEN:
                errtxt = getString(R.string.error_msg_limit_reached);
                break;
            case GitHubLoader.ERR_CODE_SERVICE_UNAVAILABLE:
                errtxt = getString(R.string.error_msg_service_unavailable);
                break;
            case GitHubLoader.ERR_CODE_UNAUTH:
                errtxt = getString(R.string.error_msg_unauth);
                break;
            default:
                errtxt = getString(R.string.error_msg_unforseen)+" #"+event_code;
        }
        return errtxt;
    }

    public void CheckAuthorization(){
        if(GitHubLoader.getInstance(getActivity()).isTokenSet())
            GitHubLoader.getInstance().getBasicUserInfo(new RequestCallback<User>() {
                @Override
                public void onSuccess(User user) {
                    Intent i = new Intent(getActivity(), RepositoryActivity.class);
                    startActivity(i);
                    getActivity().finish();
                    pd.dismiss();
                }

                @Override
                public void onFailure(int error_code) {
                    Log.i(ERROR, "error authorization");
                }
            });
    }
}
