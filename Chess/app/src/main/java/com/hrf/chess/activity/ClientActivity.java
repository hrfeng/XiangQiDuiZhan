package com.hrf.chess.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hrf.chess.R;
import com.hrf.chess.bean.WifiBean;
import com.hrf.chess.utils.ViewUtils;
import com.hrf.chess.widget.ChessDialog;
import com.hrf.chess.widget.ChessView;
import com.hrf.chess.widget.InfoDialog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientActivity extends AppCompatActivity {
    private Button bt_start;
    private ChessView cv_content;

    private boolean isServiceDestoryde;
    private Socket clientSocket;
    private PrintWriter out;

    boolean tag;

    private WifiBean wifiBean;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    final String content = (String) msg.obj;
                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            if (content.length() > 2 && content.substring(0, 2).equals("ip")) {
                                cv_content.setIp(content.subSequence(2, content.length()).toString());
                            } else if (content.equals("full")) {
                                Toast.makeText(ClientActivity.this, "游戏人数已满", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (content.equals("finish")) {
                                Toast.makeText(ClientActivity.this, "对方退出了游戏", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                wifiBean = new Gson().fromJson(content, WifiBean.class);
                                if (!wifiBean.isStart) {
                                    ViewUtils.show(bt_start);
                                }
                                Log.i("My", "胜利ip" + wifiBean.victoryIp + " 我的ip" + cv_content.getIp());
                                if (!TextUtils.isEmpty(wifiBean.victoryIp) && wifiBean.victoryIp.equals(cv_content.getIp())) {
                                    //胜利
                                    final InfoDialog id = new InfoDialog(ClientActivity.this, R.style.my_dialog, "胜利！");
                                    id.setInfoDialogListener(new InfoDialog.InfoDialogListener() {
                                        @Override
                                        public void onConfirm() {
                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    super.run();
                                                    out.print("restart" + "\n");
                                                    out.flush();
                                                }
                                            }.start();
                                            ViewUtils.show(bt_start);
                                            id.dismiss();
                                        }
                                    });
                                    id.show();
                                } else if (!TextUtils.isEmpty(wifiBean.victoryIp) && !wifiBean.victoryIp.equals(cv_content.getIp())) {
                                    //失败
                                    final InfoDialog id = new InfoDialog(ClientActivity.this, R.style.my_dialog, "失败！");
                                    id.setInfoDialogListener(new InfoDialog.InfoDialogListener() {
                                        @Override
                                        public void onConfirm() {
                                            new Thread() {
                                                @Override
                                                public void run() {
                                                    super.run();
                                                    out.print("restart" + "\n");
                                                    out.flush();
                                                }
                                            }.start();
                                            ViewUtils.show(bt_start);
                                            id.dismiss();
                                        }
                                    });
                                    id.show();
                                }
                                cv_content.setWifiBean(wifiBean);
                            }
                        }
                    });
                    break;
            }
        }
    };

    private class ClientThread implements Runnable {
        @Override
        public void run() {
            Socket socket = null;
            while (socket == null) {
                try {
                    if (tag) {
                        socket = new Socket("localhost", 5000);
                    } else {
                        socket = new Socket("192.168.43.1", 5000);
                    }
                    clientSocket = socket;
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    Log.i("My", "连接成功" + socket.getInetAddress().getHostAddress());
                } catch (IOException e) {
                    Log.i("My", "连接失败");
                    SystemClock.sleep(1000);
                    e.printStackTrace();
                }
            }


            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                while (!isServiceDestoryde) {
                    String content = br.readLine();
                    Log.i("My", content + "");
                    if (!TextUtils.isEmpty(content)) {
                        Message msg = new Message();
                        msg.what = 1;
                        msg.obj = content;
                        handler.dispatchMessage(msg);
                    }
                }
                Log.i("My", "关闭了");
                br.close();
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_client);
        tag = getIntent().getBooleanExtra("creat", false);
        cv_content = (ChessView) findViewById(R.id.cv_content);
        bt_start = (Button) findViewById(R.id.bt_start);
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        if (out != null) {
                            out.print("start" + cv_content.getIp() + "\n");
                            out.flush();
                        }
                    }
                }.start();
                ViewUtils.hide(bt_start);
            }
        });
        cv_content.setChessVhewListener(new ChessView.ChessViewListener() {
            @Override
            public void onSend() {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        if (wifiBean.runIp.equals(wifiBean.oneIp)) {
                            wifiBean.runIp = wifiBean.towIp;
                        } else if (wifiBean.runIp.equals(wifiBean.towIp)) {
                            wifiBean.runIp = wifiBean.oneIp;
                        }
                        if (out != null) {
                            String json = new Gson().toJson(wifiBean);
                            out.print(json + "\n");
                            out.flush();
                        }
                    }
                }.start();
            }
        });
        new Thread(new ClientThread()).start();
    }

    @Override
    protected void onDestroy() {
        isServiceDestoryde = true;
        if (out != null) {
            out.close();
        }
        if (clientSocket != null) {
            try {
                clientSocket.shutdownInput();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                final ChessDialog cd = new ChessDialog(this, R.style.my_dialog);
                cd.setChessDialogListener(new ChessDialog.ChessDialogListener() {
                    @Override
                    public void onCloss() {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                if (out != null) {
                                    out.print("finish" + "\n");
                                    out.flush();
                                }
                            }
                        }.start();
                        cd.dismiss();
                        finish();
                    }

                    @Override
                    public void onRestart() {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                if (out != null) {
                                    out.print("restart" + "\n");
                                    out.flush();
                                }
                            }
                        }.start();
                        cd.dismiss();
                    }
                });
                cd.show();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
