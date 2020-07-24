package com.plato.TicTacToe;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.plato.NetworkHandlerThread;
import com.plato.R;

import java.io.IOException;

public class xoGame extends AppCompatActivity {
    private NetworkHandlerThread networkHandlerThread;
    char type;
    char turn;
    Button[] buttons = new Button[9];
    TextView typeText;
    TextView turnText;
    TextView resultText;
    String typeString;
    String opponentString;
    String result;
    String[] btnTexts = new String[9], tvTexts = new String[3];

    @SuppressLint("SetTextI18n")
    @Override

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xo_game);


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Thread networkBackground = new Thread(new Runnable() {
            @Override
            public void run() {

                buttons[0] = findViewById(R.id.button1);
                buttons[1] = findViewById(R.id.button2);
                buttons[2] = findViewById(R.id.button3);
                buttons[3] = findViewById(R.id.button4);
                buttons[4] = findViewById(R.id.button5);
                buttons[5] = findViewById(R.id.button6);
                buttons[6] = findViewById(R.id.button7);
                buttons[7] = findViewById(R.id.button8);
                buttons[8] = findViewById(R.id.button9);

                typeText = findViewById(R.id.type_text);
                turnText = findViewById(R.id.turn_text);
                resultText = findViewById(R.id.result_text);


                try {
            networkHandlerThread = NetworkHandlerThread.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //networkHandlerThread.setDaemon(true);
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
            Log.i("log1",networkHandlerThread.getServerObject().toString());
            networkHandlerThread.sendUTF("make_room");
            networkHandlerThread.getIOHandler().join();
            Log.i("log1", "read1");
            networkHandlerThread.sendUTF("xo");
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

        while(true) {
            String typeAndTurn = null;
            try {
                networkHandlerThread.readUTF();
                networkHandlerThread.getIOHandler().join();
                typeAndTurn = networkHandlerThread.getServerMessage();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }

             type = typeAndTurn.charAt(0);
            typeString = type + "";
            if(type=='X') opponentString = "O";
            else opponentString = "X";

                if (type == 'X') {
                    //tvTexts[2] = "You Are X";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            typeText.setText("You Are X");
                        }
                    });
                }
                else {
                    tvTexts[2] = "You Are O";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            typeText.setText("You Are O");
                        }
                    });

                }

             turn = typeAndTurn.charAt(1);
            if (turn == 'X') {
                //turnText.setText("It's X's Turn");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        turnText.setText("X's Turn");
                    }
                });
                tvTexts[1] = "X's Turn";
            } else {
                tvTexts[1] = "O's Turn";
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        turnText.setText("O's Turn");
                    }
                });
                //turnText.setText("It's O's Turn");
            }

            if (type == turn) {
                for (int i = 0; i < 9; i++) {
                    buttons[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                buttonClicked(v);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                     result = null;
                    try {
                        networkHandlerThread.readUTF();
                        networkHandlerThread.getIOHandler().join();
                        result = networkHandlerThread.getServerMessage();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (result.startsWith(("winner")) || result.startsWith("draw")) {
                        tvTexts[1] = result;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(result.equals("winnerX")) resultText.setText("X wins");
                                else if(result.equals("winnerO")) resultText.setText("O wins");
                            }
                        });
                        break;
                    } else {
                        tvTexts[1] = result;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                resultText.setText(result);
                            }
                        });
                    }
            }
            else{
                String move = null;
                try {
                    networkHandlerThread.readUTF();
                    networkHandlerThread.getIOHandler().join();
                     move = networkHandlerThread.getServerMessage();
                     opponentMove(move);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                 result = null;
                try {
                    networkHandlerThread.readUTF();
                    networkHandlerThread.getIOHandler().join();
                    result = networkHandlerThread.getServerMessage();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (result.startsWith(("winner")) || result.startsWith("draw")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultText.setText(result);
                        }
                    });
                    tvTexts[1] = result;
                    break;
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resultText.setText(result);
                        }
                    });
                    tvTexts[1] = result;
                }

            }
        }
            }
        });
        networkBackground.start();

        for (int i = 0; i < 9; i++) {
            btnTexts[i] = "";
        }
        for (int i = 0; i < 3; i++) {
            tvTexts[i] = "";
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void buttonClicked(View v) throws IOException {
        boolean clicked = false;
        switch (v.getId()){
            case R.id.button1:{
                if(btnTexts[0].equals("") && type==turn) {
                    btnTexts[0] = typeString;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[0].setText(typeString);
                        }
                    });
                    networkHandlerThread.sendUTF("00");
                    clicked = true;
                }
                break;
            }
            case R.id.button2:{
                if(btnTexts[1].equals("") && type==turn) {
                    btnTexts[1] = typeString;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[1].setText(typeString);
                        }
                    });
                    networkHandlerThread.sendUTF("01");
                    clicked = true;
                }
                break;
            }
            case R.id.button3:{
                if(btnTexts[2].equals("") && type==turn) {
                    btnTexts[2] = typeString;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[2].setText(typeString);
                        }
                    });
                    networkHandlerThread.sendUTF("02");
                    clicked = true;
                }
                break;
            }
            case R.id.button4:{
                if(btnTexts[3].equals("") && type==turn) {
                    btnTexts[3] = typeString;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[3].setText(typeString);
                        }
                    });
                    networkHandlerThread.sendUTF("10");
                    clicked = true;
                }
                break;
            }
            case R.id.button5:{
                if(btnTexts[4].equals("") && type==turn) {
                    btnTexts[4] = typeString;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[4].setText(typeString);
                        }
                    });
                    networkHandlerThread.sendUTF("11");
                    clicked = true;
                }
                break;
            }
            case R.id.button6:{
                if(btnTexts[5].equals("") && type==turn) {
                    btnTexts[5] = typeString;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[5].setText(typeString);
                        }
                    });
                    networkHandlerThread.sendUTF("12");
                    clicked = true;
                }
                break;
            }
            case R.id.button7:{
                if(btnTexts[6].equals("") && type==turn) {
                    btnTexts[6] = typeString;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[6].setText(typeString);
                        }
                    });
                    networkHandlerThread.sendUTF("20");
                    clicked = true;
                }
                break;
            }
            case R.id.button8:{
                if(btnTexts[7].equals("") && type==turn) {
                    btnTexts[7] = typeString;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[7].setText(typeString);
                        }
                    });
                    networkHandlerThread.sendUTF("21");
                    clicked = true;
                }
                break;
            }
            case R.id.button9:{
                if(btnTexts[8].equals("") && type==turn) {
                    btnTexts[8] = typeString;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[8].setText(typeString);
                        }
                    });
                    networkHandlerThread.sendUTF("22");
                    clicked = true;
                }
                break;
            }
        }
    }



    public void opponentMove(String move){
        switch(move){
            case "00":
                if(btnTexts[0].equals("") && type !=turn) {
                    btnTexts[0] = opponentString;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[0].setText(opponentString);
                        }
                    });
            }
                break;
            case "01":
                if(btnTexts[1].equals("") && type !=turn) {
                    btnTexts[1] = opponentString;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[1].setText(opponentString);
                        }
                    });
                }
                break;
            case "02":
                if(btnTexts[2].equals("") && type !=turn) {
                    btnTexts[2] = opponentString;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[2].setText(opponentString);
                        }
                    });
                }
                break;
            case "10":
                if(btnTexts[3].equals("") && type !=turn) {
                    btnTexts[3] = opponentString;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[3].setText(opponentString);
                        }
                    });
                }
                break;
            case "11":
                if(btnTexts[4].equals("") && type !=turn) {
                    btnTexts[4] = opponentString;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[4].setText(opponentString);
                        }
                    });
                }
                break;
            case "12":
                if(btnTexts[5].equals("") && type !=turn) {
                    btnTexts[5] = opponentString;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[5].setText(opponentString);
                        }
                    });
                }
                break;
            case "20":
                if(btnTexts[6].equals("") && type !=turn) {
                    btnTexts[6] = opponentString;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[6].setText(opponentString);
                        }
                    });
                }
                break;
            case "21":
                if(btnTexts[7].equals("") && type !=turn) {
                    btnTexts[7] = opponentString;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[7].setText(opponentString);
                        }
                    });
                }
                break;
            case "22":
                if(btnTexts[8].equals("") && type !=turn) {
                    btnTexts[8] = opponentString;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buttons[8].setText(opponentString);
                        }
                    });
                }
                break;
        }
    }

}