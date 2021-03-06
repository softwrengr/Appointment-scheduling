package com.techease.appointment.fragments.loginSignupFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.techease.appointment.R;
import com.techease.appointment.actvities.MainActivity;
import com.techease.appointment.utilities.AlertUtils;
import com.techease.appointment.utilities.GeneralUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SignUpFragment extends Fragment {
    android.support.v7.app.AlertDialog alertDialog;
    View view;
    @BindView(R.id.tv_already_signin)
    TextView tvSignin;
    @BindView(R.id.et_userEmail)
    EditText etEmail;
    @BindView(R.id.et_userPassword)
    EditText etPassword;
    @BindView(R.id.et_user_name)
    EditText etUserName;
    @BindView(R.id.et_user_phone)
    EditText etUserPhone;
    @BindView(R.id.btn_signup)
    Button btnSignup;

    String strName, strEmail, strPhone, strPassword;

    private boolean valid = false;
    FirebaseAuth auth;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        initUI();
        return view;
    }

    private void initUI() {
        ButterKnife.bind(this, view);
        auth = FirebaseAuth.getInstance();

        tvSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneralUtils.connectFragment(getActivity(), new LoginFragment());
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    alertDialog = AlertUtils.createProgressDialog(getActivity());
                    alertDialog.show();
                    userRegistration();

                    if(GeneralUtils.getUserType(getActivity()).equals("customer")){
                        setProfile("Customer_profile");
                    }
                    else {
                    setProfile("Retailer_profile");
                    }

                }
            }
        });
    }

    private void userRegistration() {

        auth.createUserWithEmailAndPassword(strEmail,strPassword).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    Toast.makeText(getActivity(), "user successfully added", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    GeneralUtils.connectFragment(getActivity(),new LoginFragment());
                }
                else {
                    Toast.makeText(getActivity(), "try with another email", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }


    private boolean validate() {
        valid = true;

        strName = etUserName.getText().toString();
        strPhone = etUserPhone.getText().toString();
        strEmail = etEmail.getText().toString();
        strPassword = etPassword.getText().toString();


        if (strName.isEmpty()) {
            etUserName.setError("enter a valid email address");
            valid = false;
        } else {
            etUserName.setError(null);
        }

        if (strPhone.isEmpty()) {
            etUserPhone.setError("enter a valid email address");
            valid = false;
        } else {
            etUserPhone.setError(null);
        }

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

    private void setProfile(String type){
        String[] splitStr = strEmail.split("@");
        String strProfileNode = splitStr[0];
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Profile").child(type).child(strProfileNode);
        Map<String,String> map = new HashMap<>();
        map.put("name",strName);
        map.put("email",strEmail);
        map.put("phone",strPhone);
        mDatabase.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d("profile","successfully created profile");
                }
            }
        });
    }

}
