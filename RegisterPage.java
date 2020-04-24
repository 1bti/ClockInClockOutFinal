package com.example.rollinsclock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterPage extends AppCompatActivity {

    //Instantiates key variables
    EditText emailId, passwordId;
    FirebaseAuth mFirebaseAuth;
    TextView signBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        //Places reachable values on design elements and
        //Starts a connection with Firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = (EditText)findViewById(R.id.emailID);
        signBack = findViewById(R.id.loginBack);
        passwordId = (EditText)findViewById(R.id.passwordEditText);
        CardView card = findViewById(R.id.loginBtn);



        card.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                //Gives placeholder values to key variables
                String email = emailId.getText().toString();
                String password = passwordId.getText().toString();

                //Checking the fields to make sure they are all
                // filled in before registering them in Firebase
                // Sends errors if anything is not filled in
                if(email.isEmpty() && password.isEmpty()){
                    Toast.makeText(RegisterPage.this, "Fields are empty", Toast.LENGTH_SHORT);
                }
                else if(email.isEmpty()){
                    emailId.setError("Please Input an Email");
                    emailId.requestFocus();
                }
                else if(password.isEmpty()){
                    passwordId.setError("Please Input a password");
                    passwordId.requestFocus();
                }
                else if (!(email.isEmpty() && password.isEmpty())){
                    mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterPage.this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()){
                                        Toast.makeText(RegisterPage.this, "Registration Unsuccessful, please check your fields", Toast.LENGTH_SHORT);
                                    }
                                    else{
                                       startActivity(new Intent(RegisterPage.this, Clocker.class));
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(RegisterPage.this, "Error Occurred", Toast.LENGTH_SHORT);
                }
            }
        });

    }
}
