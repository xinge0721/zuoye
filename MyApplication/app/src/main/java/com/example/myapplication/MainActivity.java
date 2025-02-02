package com.example.myapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

//    上下左右按键的定义
    Button btnUp,btnDown,btnLeft,btnRight;
    static String IPCar;

    private User sock_con;
    /**
     * 界面控件初始化
     */
    private void control_init()
    {
        //赋值操作
        btnUp = findViewById(R.id.btnUp);
        btnDown = findViewById(R.id.btnDown);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight  = findViewById(R.id.btnRight);

        // 设置按钮点击事件

        //上
        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sock_con.go(100,100);
            }
        });
        //下
        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sock_con.back(100,100);
            }
        });
        //左
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sock_con.left(100);
            }
        });

        //右
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sock_con.right(100);
            }
        });
    }

    //WiFi初始化
    // 需要权限：<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
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
    static ExecutorService executorServicetor = Executors.newCachedThreadPool();

    //主函数
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        control_init();
        wifi_Init();
//        sock_con = new User();
        executorServicetor.execute(() -> sock_con.connect(IPCar));
    }


}