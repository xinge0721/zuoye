package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Handler;

public class User {
    // 接受传感器
    long psStatus = 0;// 状态
    long UltraSonic = 0;// 超声波
    long Light = 0;// 光照
    long CodedDisk = 0;// 码盘值
    public static DataInputStream bInputStream = null;
    public static DataOutputStream bOutputStream = null;
    public static Socket socket = null;
    private final byte[] rbyte = new byte[40];
    private Handler reHandler;
    public short TYPE = 0xAA;
    public short MAJOR = 0x00;
    public short FIRST = 0x00;
    public short SECOND = 0x00;
    public short THRID = 0x00;
    public short CHECKSUM = 0x00;
    @SuppressLint("HandlerLeak")
    private final Handler recvhandler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                byte[] mByte = (byte[]) msg.obj;
                if (mByte[0] == 0x55) {
                    // 光敏状态
                    psStatus = mByte[3] & 0xff;
                    // 超声波数据
                    UltraSonic = mByte[5] & 0xff;
                    UltraSonic = UltraSonic << 8;
                    UltraSonic += mByte[4] & 0xff;
                    // 光照强度
                    Light = mByte[7] & 0xff;
                    Light = Light << 8;
                    Light += mByte[6] & 0xff;
                    // 码盘
                    CodedDisk = mByte[9] & 0xff;
                    CodedDisk = CodedDisk << 8;
                    CodedDisk += mByte[8] & 0xff;

//                    if (mByte[1] == (byte) 0xaa) {
//                        // 显示数据
//                        Data_show.setText("主车各状态信息: " + "超声波:" + UltraSonic
//                                + "mm 光照:" + Light + "lx" + " 码盘:" + CodedDisk
//                                + " 光敏状态:" + psStatus + " 状态:" + (mByte[2]));
//                    }
//                    if(mByte[1] == (byte) 0x02)
//                    {
//                        // 显示数据
//                        Data_show.setText("从车各状态信息: " + "超声波:" + UltraSonic
//                                + "mm 光照:" + Light + "lx" + " 码盘:" + CodedDisk
//                                + " 光敏状态:" + psStatus + " 状态:" + (mByte[2]));
//                    }
                }
            }
        }
    };


    /**
     * 传入接受数据的接收器和IP地址，用于建立Socket链接，返回相关数据
     *
     * @param reHandler
     * @param IP
     */
    public void connect(Handler reHandler, String IP) {
        try {
            this.reHandler = reHandler;
            int port = 60000;
            socket = new Socket(IP, port);
            bInputStream = new DataInputStream(socket.getInputStream());
            bOutputStream = new DataOutputStream(socket.getOutputStream());
            reThread.start();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 子线程发送数据到主线程显示
     */
    private Thread reThread = new Thread(new Runnable() {
        @Override
        public void run() {
            // TODO Auto1-generated method stub
            while (socket != null && !socket.isClosed()) {
                try {
                    bInputStream.read(rbyte);
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = rbyte;
                    reHandler.sendMessage(msg);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    });

    /**
     * 发送数据使用
     */
    private void send() {
        CHECKSUM = (short) ((MAJOR + FIRST + SECOND + THRID) % 256);
        // 发送数据字节数组

        final byte[] sbyte = {0x55, (byte) TYPE, (byte) MAJOR, (byte) FIRST, (byte) SECOND, (byte) THRID, (byte) CHECKSUM, (byte) 0xBB};

        MainActivity.executorServicetor.execute(new Runnable() {  //开启线程，传输数据
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    if (socket != null && !socket.isClosed()) {

                        bOutputStream.write(sbyte, 0, sbyte.length);
                        bOutputStream.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}