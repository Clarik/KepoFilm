package com.app.huaweiapp.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.huaweiapp.HomeActivity;
import com.app.huaweiapp.MainActivity;
import com.app.huaweiapp.R;
import com.bumptech.glide.Glide;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;

public class FragmentAccount extends Fragment {

    View v;

    TextView tvAccountName;
    ImageView ivAccountProfilePicture;
    Button btnLogOut;

    public FragmentAccount() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_account, container, false);

        setUpTextView(v);
        setUpImageView(v);
        setUpButton(v);

        setUpAccountData();

        return v;
    }

    void setUpAccountData(){
        AccountAuthService mAuthService;
        AccountAuthParams mAuthParam;
        mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).setEmail().createParams();
        mAuthService = AccountAuthManager.getService(getActivity(), mAuthParam);
        Task<AuthAccount> task = mAuthService.silentSignIn();
        task.addOnSuccessListener(new OnSuccessListener<AuthAccount>() {
            @Override
            public void onSuccess(AuthAccount authAccount) {
                tvAccountName.setText(authAccount.getDisplayName());
                Glide.with(getContext())
                        .load(authAccount.getAvatarUri())
                        .into(ivAccountProfilePicture);
            }
        });
    }

    void setUpTextView(View v){
        tvAccountName = v.findViewById(R.id.tv_account_name);
    }

    void setUpImageView(View v){
        ivAccountProfilePicture = v.findViewById(R.id.iv_account_profile_picture);
    }

    void setUpButton(View v){
        btnLogOut = v.findViewById(R.id.HuaweiIdSignOutButton);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }

    void signOut() {
        AccountAuthService mAuthService;
        AccountAuthParams mAuthParam;

        mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setEmail()
                .setAuthorizationCode()
                .createParams();

        mAuthService = AccountAuthManager.getService( getActivity(), mAuthParam);

        Task<Void> signOutTask = mAuthService.signOut();
        signOutTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("Loglog", "signOut Success");
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.i("Loglog", "signOut fail");
            }
        });
    }


}