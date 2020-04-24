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
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {
    //Setting up key variables for the Login Page
    public EditText usernameText, passwordText;
    public static String username, password;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //Setting up static methods to retrieve the username and password
    public static String getUsername() {
        return username;
    }
    public static String getPassword() {
        return password;
    }

    // Updates the username and password variables, while
    // also running through the firebase database to check
    // if the user has an account. It wil deny access to the
    // clock in and out feature
    public void updateValues(){
        username = usernameText.getText().toString();
        password = passwordText.getText().toString();
        if(username.isEmpty() && password.isEmpty()){
            Toast.makeText(MainActivity.this, "Fields are empty", Toast.LENGTH_SHORT);
        }
        else if(username.isEmpty()){
            usernameText.setError("Please Input an Email");
            usernameText.requestFocus();
        }
        else if(password.isEmpty()){
            passwordText.setError("Please Input a password");
            passwordText.requestFocus();
        }
        else if (!(username.isEmpty() && password.isEmpty())){
            mFirebaseAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        Toast.makeText(MainActivity.this, "Login Unsuccessful, please check your fields", Toast.LENGTH_SHORT);
                    }
                    else{
                        startActivity(new Intent(MainActivity.this, Clocker.class));
                    }
                }
            });
        }
        else{
            Toast.makeText(MainActivity.this, "Error Occurred", Toast.LENGTH_SHORT);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Places placeholder values on key variables and
        // opens Firebase communication on start up of app
        usernameText = findViewById(R.id.usernameEditText);
        username = "";
        passwordText = findViewById(R.id.passwordEditText);
        password = "";
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {

            //Checks to see if the user is already logged in.
            // If logged in the user will be brought to the clock in and out page
            FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(mFirebaseUser != null){
                    Toast.makeText(MainActivity.this, "You are logged in", Toast.LENGTH_SHORT);
                    startActivity(new Intent(MainActivity.this, Clocker.class));
                }
            }
        };

        //Sets up login button and register hypertext
        CardView card = findViewById(R.id.loginBtn);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateValues();

            }
        });

        TextView register = findViewById(R.id.textView2);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterPage.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
