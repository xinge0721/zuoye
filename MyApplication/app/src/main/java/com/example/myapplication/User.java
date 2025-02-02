package com.example.myapplication;

import android.annotation.SuppressLint;
import android.os.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import android.os.Handler;
import android.os.Message;  // 确保 Message 也来自 android.os
import android.util.Log;
import android.widget.Toast;

public class User {
    // 接受传感器
    long psStatus = 0;// 状态
    long UltraSonic = 0;// 超声波
    long Light = 0;// 光照
    long CodedDisk = 0;// 码盘值
    public DataInputStream bInputStream = null;
    public DataOutputStream bOutputStream = null;
    public static Socket socket = null;
    private final byte[] rbyte = new byte[40];
    private Handler reHandler;

    public short TYPE = 0xAA;
    //设备类型标识（如 0xAA 表示主车）
    public short MAJOR = 0x00;
//    主指令码
    public short FIRST = 0x00;
//    速度高八位
    public short SECOND = 0x00;
//    速度低八位
    public short THRID = 0x00;
//    码盘值
    public short CHECKSUM = 0x00;
//    校验和
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
     * @param IP
     */
    public void connect(String IP) {
        try {
            this.reHandler = recvhandler;
            int port = 60000;
            socket = new Socket(IP, port);
            bInputStream = new DataInputStream(socket.getInputStream());
            bOutputStream = new DataOutputStream(socket.getOutputStream());
            reThread.start();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("SocketError", "连接失败: " + e.getMessage());
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

        final byte[] sbyte = {0x55, (byte) TYPE, (byte) MAJOR, (byte) FIRST, (byte) SECOND,
                                    (byte) THRID, (byte) CHECKSUM, (byte) 0xBB};

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

    // 前进
    public void go(int sp_n, int en_n) {
        MAJOR = 0x02;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = (byte) (en_n & 0xff);
        THRID = (byte) (en_n >> 8);
        send();
    }

    // 后退
    public void back(int sp_n, int en_n) {
        MAJOR = 0x03;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = (byte) (en_n & 0xff);
        THRID = (byte) (en_n >> 8);
        send();
    }

    //左转
    public void left(int sp_n) {

        MAJOR = 0x04;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    // 右转
    public void right(int sp_n) {

        MAJOR = 0x05;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    // 停车
    public void stop() {
        MAJOR = 0x01;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    // 循迹
    public void line(int sp_n) {  //寻迹
        MAJOR = 0x06;
        FIRST = (byte) (sp_n & 0xFF);
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    //清除码盘值
    public void clear() {
        MAJOR = 0x07;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }


    // 蜂鸣器
    public void buzzer(int i) {
        if (i == 1)
            FIRST = 0x01;
        else if (i == 0)
            FIRST = 0x00;
        MAJOR = 0x30;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    public void picture(int i) {// 图片上翻下翻
        if (i == 1)
            MAJOR = 0x50;
        else
            MAJOR = 0x51;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    public void gear(int i) {// 光照档位加
        if (i == 1)
            MAJOR = 0x61;
        else if (i == 2)
            MAJOR = 0x62;
        else if (i == 3)
            MAJOR = 0x63;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    public void fan() {// 风扇
        MAJOR = 0x70;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
    }

    //立体显示
    public void infrared_stereo(short[] data) {
        MAJOR = 0x10;
        FIRST = 0xff;
        SECOND = data[0];
        THRID = data[1];
        send();
        yanchi(500);
        MAJOR = 0x11;
        FIRST = data[2];
        SECOND = data[3];
        THRID = data[4];
        send();
        yanchi(500);
        MAJOR = 0x12;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        yanchi(500);
    }

    public void gate(int i) {// 闸门
        byte type = (byte) TYPE;
        if (i == 1) {
            TYPE = 0x03;
            MAJOR = 0x01;
            FIRST = 0x01;
            SECOND = 0x00;
            THRID = 0x00;
            send();
        } else if (i == 2) {
            TYPE = 0x03;
            MAJOR = 0x01;
            FIRST = 0x02;
            SECOND = 0x00;
            THRID = 0x00;
            send();
        }
        TYPE = type;
    }

    //LCD 显示标志物进入计时模式
    public void digital_close() {//数码管关闭
        byte type = (byte) TYPE;
        TYPE = 0x04;
        MAJOR = 0x03;
        FIRST = 0x00;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = type;
    }

    public void digital_open() {//数码管打开
        byte type = (byte) TYPE;
        TYPE = 0x04;
        MAJOR = 0x03;
        FIRST = 0x01;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = type;
    }

    public void digital_clear() {//数码管清零
        byte type = (byte) TYPE;
        TYPE = 0x04;
        MAJOR = 0x03;
        FIRST = 0x02;
        SECOND = 0x00;
        THRID = 0x00;
        send();
        TYPE = type;
    }

    public void digital_dic(int dis) {//LCD显示标志物第二排显示距离

        byte type = (byte) TYPE;
        int a = 0, b = 0, c = 0;
        a = (dis / 100) & (0xF);
        b = (dis % 100 / 10) & (0xF);
        c = (dis % 10) & (0xF);
        b = b << 4;
        b = b | c;
        TYPE = 0x04;
        MAJOR = 0x04;
        FIRST = 0x00;
        SECOND = (short) (a);
        THRID = (short) (b);
        send();
        TYPE = type;
    }

    public void digital(int i, int one, int two, int three) {// 数码管
        byte type = (byte) TYPE;
        TYPE = 0x04;
        if (i == 1) {//数据写入第一排数码管
            MAJOR = 0x01;
        } else if (i == 2) {//数据写入第二排数码管
            MAJOR = 0x02;
        }
        FIRST = (byte) one;
        SECOND = (byte) two;
        THRID = (byte) three;
        send();
        TYPE = type;
    }

    public void TFT_LCD(int MAIN, int KIND, int COMMAD, int DEPUTY)  //tft lcd
    {
        byte type = (byte) TYPE;
        TYPE = (short) 0x0B;
        MAJOR = (short) MAIN;
        FIRST = (byte) KIND;
        SECOND = (byte) COMMAD;
        THRID = (byte) DEPUTY;
        send();
        TYPE = type;
    }

    public void magnetic_suspension(int MAIN, int KIND, int COMMAD, int DEPUTY) //无线充电
    {
        byte type = (byte) TYPE;
        TYPE = (short) 0x0A;
        MAJOR = (short) MAIN;
        FIRST = (byte) KIND;
        SECOND = (byte) COMMAD;
        THRID = (byte) DEPUTY;
        send();
        TYPE = type;
    }

    // 线程暂停，产生延时
    public void yanchi(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}