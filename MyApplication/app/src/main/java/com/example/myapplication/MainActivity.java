package com.example.myapplication;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bkrcl.control_car_video.camerautil.CameraCommandUtil;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
// 导入自定义库和工具类
import com.bkrcl.control_car_video.camerautil.CameraCommandUtil;
import com.example.myapplication.utils.SearchService;
import com.example.myapplication.utils.TrafficUtil;

public class MainActivity extends AppCompatActivity {

    // 上下左右按键的定义
    static String IPCar;
    TextView tv_alert; // 提示文本

    ImageView imageView;
    private User sock_con;
    private trafficlight tra;
    static ExecutorService executorServicetor = Executors.newCachedThreadPool();


    private final HashMap<Integer, String> buttonFunctions = new HashMap<Integer, String>() {{
        // 数字键示例
        put(R.id.btn1, "trafficlight");
        put(R.id.btn2, "GO");
        put(R.id.btn3, "horn");    // 喇叭
        put(R.id.btn4, "left");   // 车灯
        put(R.id.btn5, "STOP");   // 车灯
        put(R.id.btn6, "right");   // 车灯
        put(R.id.btn7, "light");   // 车灯
        put(R.id.btn8, "back");   // 车灯
        put(R.id.btn9, "light");   // 车灯
        put(R.id.btn10, "light");   // 车灯
        put(R.id.btn11, "light");   // 车灯
        put(R.id.btn12, "light");   // 车灯

        // ... 其他按钮继续添加
    }};


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
    // 步骤2：动态绑定所有按钮
    private void control_init() {


        tv_alert = findViewById(R.id.tv_alert); //初始化文本控件
        imageView = findViewById(R.id.imageView);//初始化图片控件

        // 获取所有需要绑定的按钮ID
        int[] buttonIds = {
                R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8,
                R.id.btn9, R.id.btn10, R.id.btn11, R.id.btn12
                // ... 添加其他按钮ID
        };

        // 统一监听器
        View.OnClickListener buttonListener = v -> {
            // 通过映射表获取指令
            String command = buttonFunctions.get(v.getId());

            if (command != null) {
                executeCommand(command); // 执行对应功能
            }
        };

        // 动态绑定
        for (int id : buttonIds) {
            Button btn = findViewById(id);
            if (btn != null) btn.setOnClickListener(buttonListener);
        }
    }

    // 步骤3：功能执行中心（在这里扩展功能）
    private void executeCommand(String cmd) {
        switch (cmd) {
            case "GO":
//                sock_con.go(100,100);
                tv_alert.setText("前进");
                break;
            case "left":
//                sock_con.left(100);
                tv_alert.setText("左转");
                break;
            case "STOP":
//                sock_con.stop();
                tv_alert.setText("停下");
                break;
            case "right":
//                sock_con.right(100);
                tv_alert.setText("右转");
                break;
            case "back":
//                sock_con.back(100,100);
                tv_alert.setText("后退");
                break;
            case "trafficlight":{
                tra.trafficlight_Init(IPCar);
                break;}
            // ... 其他功能分支
        }
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

}