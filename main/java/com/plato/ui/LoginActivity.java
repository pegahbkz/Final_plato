package com.plato.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.plato.NetworkHandlerThread;
import com.plato.R;

import com.plato.server.* ;
public class LoginActivity extends AppCompatActivity {

    private Button singUpButton;
    private Button continueBtn;
    private NetworkHandlerThread networkHandlerThread = null;
    private EditText username , password;
    private String svMessage;
    private boolean correctUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_login);

//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);

        try {
            networkHandlerThread = NetworkHandlerThread.getInstance();
            networkHandlerThread.start();


            Thread.sleep(100);
            Log.i("Thread","Start");

        } catch (Exception e) {

            e.printStackTrace();
        }

        username = findViewById(R.id.editText_username);
        password = findViewById(R.id.editText_password);
        continueBtn = findViewById(R.id.continue_button);
        singUpButton = findViewById(R.id.sing_up_instead);
        singUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });


        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("svUser", String.valueOf(correctUser));
                if(correctUser){

                    try {
                        networkHandlerThread.sendUTF("login");
                        networkHandlerThread.getIOHandler().join();
                        networkHandlerThread.sendUTF(username.getText().toString());
                        networkHandlerThread.getIOHandler().join();
                        networkHandlerThread.sendUTF(password.getText().toString());
                        networkHandlerThread.getIOHandler().join();

                        networkHandlerThread.readObject();
                        networkHandlerThread.getIOHandler().join();

                        User user = (User) networkHandlerThread.getServerObject();


                        Log.i("svUser",user.toString());


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
            }
        });

        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // after user changes focus from entering username
                if(!hasFocus){



                    try {
                        networkHandlerThread.sendUTF("checkUsername");
                        networkHandlerThread.getIOHandler().join();

                        String enteredUsername = username.getText().toString();

                        Log.i("username:", enteredUsername);
                        networkHandlerThread.sendUTF(enteredUsername);
                        networkHandlerThread.getIOHandler().join();

                        networkHandlerThread.readUTF();
                        networkHandlerThread.getIOHandler().join();


                        svMessage = networkHandlerThread.getServerMessage();

                        if(svMessage.equals("username-false")) {
                            username.setError("Username not found");
                            correctUser = false;
                        }
                        else if(svMessage.equals("username-true"))
                            correctUser = true;

                        Log.i("svMessage", svMessage);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }



            }
        });







    }
}
