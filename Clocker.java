package com.example.rollinsclock;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.layout.simple_list_item_1;
import static android.app.PendingIntent.getActivity;

public class Clocker extends AppCompatActivity{

    String sendUsername, sendPassword;
    Switch simpleSwitch;
    Button logOut;
    public int choice;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    String[] jobs;
    ListView list;

    //Helper to send user information to server
    public void sendToPort(){

        BackgroundTask b1 = new BackgroundTask();
            b1.execute(sendUsername,sendPassword);

    }


    //Server Connection
    class BackgroundTask extends AsyncTask<String, Void, Void>{
        Socket s;
        DataInputStream reader;
        DataOutputStream writer;
        String serverMessage;


        @Override
        protected Void doInBackground(String... voids) {
            try {
                //Setting up socket communication
                s = new Socket("52.14.143.239", 4997);
                reader =new DataInputStream(s.getInputStream());
                writer = new DataOutputStream(s.getOutputStream());

                //Sending the username, password, and clock information to server
                writer.writeUTF(sendUsername);
                writer.writeUTF(sendPassword);
                writer.writeUTF(String.valueOf(choice));

                //Checking to see if the user has multiple jobs to run special script
                if(simpleSwitch.isChecked()) {
                    //Receiving information from the server one line at a time
                    // until we find the amount of jobs th user has
                    serverMessage = reader.readUTF();
                    serverMessage = reader.readUTF();
                    serverMessage = reader.readUTF();
                    writer.writeUTF(String.valueOf(1));
                    writer.flush();
                    //Pulling the number of jobs and using it to create an array
                    // from which we will display the job options
                    int numJobs = Integer.parseInt(serverMessage);
                    jobs = new String[numJobs];
                    int i = 0;
                    while (i < numJobs) {
                        serverMessage = reader.readUTF();
                        jobs[i] = serverMessage;
                        i++;
                    }
                }
                //Creating ArrayList to display
                ArrayList<String> help = new ArrayList<String>(Arrays.asList(jobs));
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(Clocker.this, simple_list_item_1, help);
                list.setAdapter(adapter);
                run();
                writer.close();

                s.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }



        //Method helps update and populate the jobs list
        public void run() {

            ArrayList<String> help = new ArrayList<String>(Arrays.asList(jobs));
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(Clocker.this, simple_list_item_1, help);
            list.setAdapter(adapter);

        }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clocker);

        //Instantiates key variable on start up of page
        sendUsername = MainActivity.getUsername();
        sendPassword = MainActivity.getPassword();
        simpleSwitch = findViewById(R.id.switch1);
        logOut = findViewById(R.id.logOutBtn);
        final ListView list = findViewById(R.id.list);
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Job 1");
        arrayList.add("Job 2");

        // Placing values in list for multiple jobs
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,                   android.R.layout.simple_list_item_1, arrayList);
        list.setAdapter(arrayAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String clickedItem = (String) list.getItemAtPosition(position);
                Toast.makeText(Clocker.this, clickedItem, Toast.LENGTH_LONG).show();
            }
        });


        //Setting up Clock in and Clock out buttons for app
        final Button clockInBtn = findViewById(R.id.clockInBtn);
        clockInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = 1;
                //Checks to see if the client has multiple jobs
                if(simpleSwitch.isChecked()){
                    sendToPort();
                    Toast.makeText(Clocker.this, "Successfully Clocked In \n" +
                            "Username: " + sendUsername + "\n Please Wait 30 seconds", Toast.LENGTH_SHORT).show();

                }
                else{
                    sendToPort();
                    Toast.makeText(Clocker.this, "Successfully Clocked In \n" +
                            "Username: " + sendUsername + "\n Please Wait 30 seconds", Toast.LENGTH_SHORT).show();
                }

            }
        });

        Button clockOutBtn = findViewById(R.id.clockOutBtn);
        clockOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = 2;
                sendToPort();
                Toast.makeText(Clocker.this, "Successfully Clocked Out \n" +
                        "Username: " + sendUsername + "\nPassword: " + sendPassword +
                        "", Toast.LENGTH_SHORT).show();

            }
        });

        //Setting up logout button
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Clocker.this, MainActivity.class));
            }
        });


        }
    }

