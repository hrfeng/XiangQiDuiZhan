package com.hrf.chess.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.hrf.chess.bean.WifiBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SocketService extends Service {

    private boolean isServiceDestoryde;
    private ServerSocket serverSocket;
    private List<Socket> socketList;
    private List<BufferedReader> readerList;
    private List<PrintWriter> writerList;
    private Map<String, Socket> scoketMap;
    private List<String> ipList;
    private WifiBean wifiBean;

    public SocketService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        scoketMap = new HashMap<>();
        ipList = new ArrayList<>();
        socketList = new ArrayList<>();
        writerList = new ArrayList<>();
        readerList = new ArrayList<>();
        init();
        new Thread(new ServerThread()).start();
    }

    private void init() {
        wifiBean = new WifiBean();
        wifiBean.pieceList = new ArrayList<>();
        wifiBean.otherIpList = new ArrayList<>();
        wifiBean.hintList = new ArrayList<>();
        firstRunnable();
    }

    private void addPiece(String name, int x, int y, boolean direction) {
        WifiBean.Piece piece = wifiBean.new Piece();
        piece.name = name;
        piece.x = x;
        piece.y = y;
        piece.direction = direction;
        wifiBean.pieceList.add(piece);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceDestoryde = true;
        Log.i("My", "服务停止了");
    }

    /**
     * 先手随机分配  和重置棋子
     */
    private void firstRunnable() {
        wifiBean.pieceList.clear();
        //红
        addPiece("车", 0, 0, true);
        addPiece("马", 0, 1, true);
        addPiece("相", 0, 2, true);
        addPiece("仕", 0, 3, true);
        addPiece("帅", 0, 4, true);
        addPiece("仕", 0, 5, true);
        addPiece("相", 0, 6, true);
        addPiece("马", 0, 7, true);
        addPiece("车", 0, 8, true);
        addPiece("炮", 2, 1, true);
        addPiece("炮", 2, 7, true);
        addPiece("兵", 3, 0, true);
        addPiece("兵", 3, 2, true);
        addPiece("兵", 3, 4, true);
        addPiece("兵", 3, 6, true);
        addPiece("兵", 3, 8, true);
        //
        addPiece("车", 9, 0, false);
        addPiece("马", 9, 1, false);
        addPiece("象", 9, 2, false);
        addPiece("士", 9, 3, false);
        addPiece("将", 9, 4, false);
        addPiece("士", 9, 5, false);
        addPiece("象", 9, 6, false);
        addPiece("马", 9, 7, false);
        addPiece("车", 9, 8, false);
        addPiece("炮", 7, 1, false);
        addPiece("炮", 7, 7, false);
        addPiece("卒", 6, 0, false);
        addPiece("卒", 6, 2, false);
        addPiece("卒", 6, 4, false);
        addPiece("卒", 6, 6, false);
        addPiece("卒", 6, 8, false);
        Random random = new Random();
        int r = random.nextInt(2);
        if (r == 0) {
            wifiBean.firstIp = wifiBean.oneIp;
        } else {
            wifiBean.firstIp = wifiBean.towIp;
        }
        Log.i("My", "先手" + r);
    }

    private class ServerThread implements Runnable {

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(5000);
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("My", "端口不是5000");
            }

            while (!isServiceDestoryde) {
                try {
                    final Socket socket = serverSocket.accept();
                    Log.i("My", "accept");
                    String remoteIP = socket.getInetAddress().getHostAddress();
                    if (!TextUtils.isEmpty(wifiBean.oneIp) && !TextUtils.isEmpty(wifiBean.towIp)) {
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                        out.print("full" + "\n");
                        out.flush();
                    } else {
                        scoketMap.put(remoteIP, socket);
                        ipList.add(remoteIP);
                        int remotePort = socket.getLocalPort();
                        System.out.println("IP:" + remoteIP + ", Port: " + remotePort);
                        new Thread(new ResponseThrean(socket)).start();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private class ResponseThrean implements Runnable {
            private Socket socket;

            public ResponseThrean(Socket socket) {
                this.socket = socket;
            }

            @Override
            public void run() {
                try {
                    responseClient(socket);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            private void responseClient(Socket socket) throws IOException {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.print("ip" + socket.getInetAddress().getHostAddress() + "\n");
                out.flush();
                readerList.add(in);
                writerList.add(out);
                String json = new Gson().toJson(wifiBean);
                out.print(json + "\n");
                out.flush();
                while (!isServiceDestoryde) {
                    String content = in.readLine();
                    if (!TextUtils.isEmpty(content)) {
                        Log.i("My", content);
                        if (content.length() > 5 && content.substring(0, 5).equals("start")) {
                            //准备开始
                            if (TextUtils.isEmpty(wifiBean.oneIp)) {
                                wifiBean.oneIp = content.substring(5, content.length());
                                wifiBean.firstIp = wifiBean.oneIp;
                                wifiBean.runIp = wifiBean.oneIp;
                            } else if (TextUtils.isEmpty(wifiBean.towIp)) {
                                wifiBean.towIp = content.substring(5, content.length());
                                wifiBean.isStart = true;
                                for (int i = 0; i < writerList.size(); i++) {
                                    String j = new Gson().toJson(wifiBean);
                                    writerList.get(i).print(j + "\n");
                                    writerList.get(i).flush();
                                }
                            }
                        } else if (content.equals("finish")) {
                            //结束游戏
                            for (int i = 0; i < writerList.size(); i++) {
                                init();
                                if (!ipList.get(i).equals(socket.getInetAddress().getHostAddress())) {
                                    String j = "finish";
                                    writerList.get(i).print(j + "\n");
                                    writerList.get(i).flush();
                                }
                            }
                        } else {
                            if (content.equals("restart")) {
                                //重新开始
                                init();
                                for (int i = 0; i < writerList.size(); i++) {
                                    String j = new Gson().toJson(wifiBean);
                                    writerList.get(i).print(j + "\n");
                                    writerList.get(i).flush();
                                }
                            } else {
                                for (int i = 0; i < writerList.size(); i++) {
                                    wifiBean = new Gson().fromJson(content, WifiBean.class);
                                    if (TextUtils.isEmpty(wifiBean.victoryIp)) {
                                        if (!ipList.get(i).equals(socket.getInetAddress().getHostAddress())) {
                                            String j = new Gson().toJson(wifiBean);
                                            writerList.get(i).print(j + "\n");
                                            writerList.get(i).flush();
                                        }
                                    } else {
                                        String j = new Gson().toJson(wifiBean);
                                        writerList.get(i).print(j + "\n");
                                        writerList.get(i).flush();
                                    }
                                }
                            }
                        }
                    }
                }
                in.close();
                out.close();
                socket.close();
                Log.i("My", "关闭");
            }
        }
    }
}
