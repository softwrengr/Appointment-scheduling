package com.techease.appointment.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.techease.appointment.R;
import com.techease.appointment.utilities.GeneralUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LoginFragment extends Fragment {
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.tv_new_user)
    TextView tvNewUser;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_password)
    EditText etPassword;
    View view;
    private FirebaseAuth auth;
    String  strEmail, strPassword;
    private boolean valid = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_login, container, false);
        auth = FirebaseAuth.getInstance();
        initUI();
        return view;
    }

    private void initUI(){
        ButterKnife.bind(this, view);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    customerLogin();
                }
            }
        });

        tvNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtils.connectFragment(getActivity(),new SignUpFragment());
            }
        });
    }

    private void customerLogin() {

      auth.signInWithEmailAndPassword(strEmail,strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
          @Override
          public void onComplete(@NonNull Task<AuthResult> task) {
              if(!task.isSuccessful()){
                  Toast.makeText(getActivity(), "your email or password is incorrect", Toast.LENGTH_SHORT).show();
              }
              else {
                  GeneralUtils.connectFragment(getActivity(),new HomeFragment());
              }
          }
      });
    }

    private boolean validate() {
        valid = true;

        strEmail = etEmail.getText().toString();
        strPassword = etPassword.getText().toString();
        GeneralUtils.putStringValueInEditor(getActivity(),"email",strEmail);

        if (strEmail.isEmpty()) {
            etEmail.setError("enter a valid email address");
            valid = false;
        } else {
            etEmail.setError(null);
        }

        if (strPassword.isEmpty() || strPassword.length() < 6) {
            etPassword.setError("Please enter a strong password");
            valid = false;
        } else {
            etPassword.setError(null);
        }
        return valid;
    }
}
