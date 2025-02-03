package com.example.myapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    // 上下左右按键的定义
    Button btnUp, btnDown, btnLeft, btnRight;
    static String IPCar;
    TextView tv_alert; // 提示文本

    private User sock_con;
    static ExecutorService executorServicetor = Executors.newCachedThreadPool();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化控件
        control_init();

        // 初始化WiFi
//        wifi_Init();

        // 连接Socket
//        executorServicetor.execute(() -> sock_con.connect(IPCar));
    }

    /**
     * 界面控件初始化
     */
    private void control_init() {
        // 赋值操作
        btnUp = findViewById(R.id.btn2);    // 上
        btnDown = findViewById(R.id.btn8);  // 下
        btnLeft = findViewById(R.id.btn4);  // 左
        btnRight = findViewById(R.id.btn6); // 右
        tv_alert = findViewById(R.id.tv_alert); // 提示文本
    }

    /**
     * WiFi初始化
     * 需要权限：<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     */
    private void wifi_Init() {
        try {
            // 1. 获取WifiManager服务
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiManager == null) {
                Log.e("WiFiInit", "设备不支持WiFi功能");
                IPCar = "设备无WiFi";
                return;
            }

            // 2. 检查WiFi是否已连接（不仅仅是启用）
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiInfo == null || !wifiInfo.isConnected()) {
                Log.e("WiFiInit", "WiFi未连接");
                IPCar = "WiFi未连接";
                return;
            }

            // 3. 获取DHCP信息
            DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
            if (dhcpInfo == null) {
                Log.e("WiFiInit", "DHCP信息为空");
                IPCar = "DHCP错误";
                return;
            }

            // 4. 将整型IP转换为字节数组（修复符号问题）
            int gateway = dhcpInfo.gateway;
            byte[] ipBytes = new byte[]{
                    (byte) (gateway & 0xFF),
                    (byte) ((gateway >> 8) & 0xFF),
                    (byte) ((gateway >> 16) & 0xFF),
                    (byte) ((gateway >> 24) & 0xFF)
            };

            // 5. 转换为InetAddress
            InetAddress inetAddress = InetAddress.getByAddress(ipBytes);
            IPCar = inetAddress.getHostAddress();
            Log.i("WiFiInit", "成功获取网关IP: " + IPCar);

        } catch (Exception e) {
            Log.e("WiFiInit", "致命错误: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            IPCar = "获取失败";
        }
    }

    /**
     * 按钮点击事件处理
     */
    public void myonClick(View v) {
        int id = v.getId();

        // 使用 if-else 代替 switch-case
        if (id == R.id.btn1) {
            tv_alert.setText("按键1 点击");
        } else if (id == R.id.btn2) {
            tv_alert.setText("按键2 点击");
        } else if (id == R.id.btn3) {
            tv_alert.setText("按键3 点击");
        } else if (id == R.id.btn4) {
            tv_alert.setText("按键4 点击");
        } else if (id == R.id.btn5) {
            tv_alert.setText("按键5 点击");
        } else if (id == R.id.btn6) {
            tv_alert.setText("按键6 点击");
        } else if (id == R.id.btn7) {
            tv_alert.setText("按键7 点击");
        } else if (id == R.id.btn8) {
            tv_alert.setText("按键8 点击");
        } else if (id == R.id.btn9) {
            tv_alert.setText("按键9 点击");
        } else if (id == R.id.btn10) {
            tv_alert.setText("按键10 点击");
        } else if (id == R.id.btn11) {
            tv_alert.setText("按键11 点击");
        } else if (id == R.id.btn12) {
            tv_alert.setText("按键12 点击");
        } else {
            tv_alert.setText("未知按键点击");
        }
    }
}