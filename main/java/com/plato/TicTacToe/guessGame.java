package com.plato.TicTacToe;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.plato.NetworkHandlerThread;
import com.plato.R;

import java.io.IOException;

public class guessGame extends AppCompatActivity {
    private NetworkHandlerThread networkHandlerThread;
    Button submitBtn;
    TextView wordText;
    TextView guessText;
    TextView chanceText;
    EditText editText;
    String guessedChar;
    int chances;
    String role;
    String answer;
    String hiddenAnswer;
    String result;
    char[] answerArr;
    char[] hiddenAnswerArr;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guess_game);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        Thread networkBackground = new Thread(new Runnable() {


            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        guessText = findViewById(R.id.guess_text);
                        wordText = findViewById(R.id.word_text);
                        editText = findViewById(R.id.editText);
                        chanceText = findViewById(R.id.chanceText);
                        submitBtn = findViewById(R.id.submitBtn);
                    }
                });



                try {
                    networkHandlerThread = NetworkHandlerThread.getInstance();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                networkHandlerThread.start();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    networkHandlerThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                try {
                    Log.i("log1", "read0");
                    networkHandlerThread.sendUTF("login");
                    networkHandlerThread.getIOHandler().join();
                    Log.i("log1", "read00");
                    networkHandlerThread.sendUTF("amir");
                    networkHandlerThread.getIOHandler().join();
                    networkHandlerThread.sendUTF("1234");
                    networkHandlerThread.getIOHandler().join();
                    networkHandlerThread.readObject();
                    networkHandlerThread.getIOHandler().join();
                    Log.i("log1", networkHandlerThread.getServerObject().toString());
                    networkHandlerThread.sendUTF("make_room");
                    networkHandlerThread.getIOHandler().join();
                    Log.i("log1", "read1");
                    networkHandlerThread.sendUTF("guessWord");
                    networkHandlerThread.getIOHandler().join();
                    Log.i("log2", "read2");
                    networkHandlerThread.sendUTF("casual");
                    networkHandlerThread.getIOHandler().join();
                    Log.i("log3", "read3");
                    networkHandlerThread.sendInt(2);
                    networkHandlerThread.getIOHandler().join();
                    Log.i("log4", "read4");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                role = null;
                for (int i = 0; i < 2; i++) {
                    try {
                        networkHandlerThread.readUTF();
                        networkHandlerThread.getIOHandler().join();
                        role = networkHandlerThread.getServerMessage();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    answer = "";
                    hiddenAnswer = "";
                    chances = 0;

                    //usernames!!!!!!!!!!!!!!!!!!

                    if (role.equals("word")) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                guessText.setText("Choose a word");
                            }
                        });

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                answer = editText.getText().toString();
                            }
                        });
                    }
                    if(!answer.equals("")) answerArr = answer.toCharArray();
                    chances = answer.length();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chanceText.setText("hi");
                        }
                    });
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    answer = editText.getText().toString();
                                    if (!answer.equals("")) answerArr = answer.toCharArray();
                                    chances = answer.length();
                                    chanceText.setText("hi");
                                }
                            });
                        }
                    });


                    submitBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                networkHandlerThread.sendUTF(answer);
                                networkHandlerThread.getIOHandler().join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                    for (int j = 0; j < answer.length(); j++) {
                        hiddenAnswer += "*";
                    }
                    if(!hiddenAnswer.equals("")) hiddenAnswerArr = hiddenAnswer.toCharArray();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            wordText.setText(hiddenAnswer);
                        }
                    });
                }

                if (role.equals("guess")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            guessText.setText("Player 2 must guess the word");
                        }
                    });
                    try {
                        networkHandlerThread.readInt();
                        networkHandlerThread.getIOHandler().join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    while (chances > 0) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                guessedChar = editText.getText().toString();
                            }
                        });

                        for (int i = 0; i < answer.length(); i++) {
                            if (answerArr[i] == hiddenAnswerArr[i]) {
                                hiddenAnswerArr[i] = answerArr[i];
                            }
                            chances--;
                        }
                        hiddenAnswer = String.valueOf(hiddenAnswerArr);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                wordText.setText(hiddenAnswer);
                            }
                        });

//                        try {
//                            networkHandlerThread.getOos().writeChar(guessedChar.charAt(0));
//                            networkHandlerThread.getOos().flush();
//                            networkHandlerThread.getIOHandler().join();
//                        } catch (InterruptedException | IOException e) {
//                            e.printStackTrace();
//                        }
                    }

                }
                try {
                    networkHandlerThread.readUTF();
                    networkHandlerThread.getIOHandler().join();
                    networkHandlerThread.readUTF();
                    networkHandlerThread.getIOHandler().join();
                    result = networkHandlerThread.getServerMessage();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        guessText.setText(result);
                    }
                });

            }
        });
        networkBackground.start();
    }
}
