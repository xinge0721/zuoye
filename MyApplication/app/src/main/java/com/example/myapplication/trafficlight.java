package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;


// 导入必要的Android类和工具
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.text.format.Formatter;

import android.widget.Toast;

// 导入自定义库和工具类
import com.bkrcl.control_car_video.camerautil.CameraCommandUtil;
import com.example.myapplication.utils.SearchService;
import com.example.myapplication.utils.TrafficUtil;

public class trafficlight {
    // UI组件
    private ImageView image_show;        // 显示图像的ImageView
    private TextView wifi_ip;            // 显示WiFi IP的TextView
    private TextView camera_ip;          // 显示摄像头IP的TextView
    private TextView show_news;          // 显示识别结果的TextView

    // 网络和摄像头相关
    private String cameraIP;             // 摄像头IP地址
    private Bitmap bitmap = null;        // 当前显示的图像Bitmap
    private boolean flag = true;         // 控制摄像头图像获取线程的标志
    private WifiManager wifiManager;     // WiFi管理器
    private DhcpInfo dhcpInfo;           // DHCP信息（用于获取网关IP）
    private String IPCar;                // 小车的网关IP（WiFi路由器IP）
    private CameraCommandUtil cameraCommandUtil; // 摄像头命令工具类

    // 广播相关
    public static final String A_S = "com.a_s"; // 自定义广播的Action名称
    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // 接收SearchService发来的摄像头IP广播
            cameraIP = arg1.getStringExtra("IP"); // 获取IP并拼接端口
            progressDialog.dismiss();            // 关闭搜索进度条
            phThread.start();                    // 启动摄像头图像获取线程
            phHandler.sendEmptyMessage(30);      // 更新UI显示摄像头IP
        }
    };

    /**
     * 初始化WiFi设置和摄像头搜索
     */
    private void wifi_Init() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled()) {
            // 获取DHCP信息（网关、子网掩码等）
            dhcpInfo = wifiManager.getDhcpInfo();
            IPCar = Formatter.formatIpAddress(dhcpInfo.gateway); // 网关IP即小车IP
            phHandler.sendEmptyMessage(20); // 更新UI显示WiFi IP
            // 注册广播接收器，监听摄像头IP搜索结果
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(A_S);
            registerReceiver(myBroadcastReceiver, intentFilter);
            cameraCommandUtil = new CameraCommandUtil(); // 初始化摄像头工具类
            search(); // 启动后台服务搜索摄像头
        } else {
            Toast.makeText(this, "请开启WIFI并重启应用", Toast.LENGTH_LONG).show();
        }
    }


    // 搜索摄像头时的进度条
    private ProgressDialog progressDialog = null;

    /**
     * 启动后台服务搜索摄像头IP
     */
    private void search() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在搜索摄像头");
        progressDialog.show();
        Intent intent = new Intent(this, SearchService.class);
        startService(intent); // 启动SearchService
    }

    /**
     * 线程：循环从摄像头获取图像
     */
    private Thread phThread = new Thread(new Runnable() {
        @Override
        public void run() {
            Looper.prepare(); // 准备线程的Looper（用于Handler）
            while (true) {
                if (flag) {
                    // 通过HTTP请求获取摄像头图像
                    bitmap = cameraCommandUtil.httpForImage(cameraIP);
                    phHandler.sendEmptyMessage(10); // 通知Handler更新图像
                }
            }
        }
    });

    /**
     * Handler：处理线程发送的消息并更新UI
     */
    @SuppressLint("HandlerLeak")
    public Handler phHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 10: // 图像更新
                    show_news.setText("图像已去色"); // 测试用提示
                    image_show.setImageBitmap(bitmap);
                    break;
                case 20: // 显示WiFi IP
                    wifi_ip.setText(IPCar);
                    break;
                case 30: // 显示摄像头IP
                    camera_ip.setText(cameraIP);
                    break;
                case 40: // 本地图片加载完成
                    show_news.setText("图像已加载");
                    break;
            }
        }
    };

    /**
     * 第一步：图像去色处理（保留高亮区域）
     * @param recbitmap 原始图像
     */
    public void decolouring(final Bitmap recbitmap) {
        new Thread(() -> {
            try {
                // 调用TrafficUtil进行去色
                bitmap = TrafficUtil.convertToLight(recbitmap);
                phHandler.sendEmptyMessage(10); // 更新图像
                recognition(); // 触发颜色识别
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private int colorData[] = new int[3]; // 存储红、绿、黄的像素数量

    /**
     * 第二步：识别交通灯颜色
     */
    private void recognition() {
        new Thread(() -> {
            // 统计颜色像素数量
            colorData = TrafficUtil.convertToBlack(bitmap);
            runOnUiThread(() -> {
                // 在UI线程更新识别结果
                show_news.setText("红色：" + colorData[0] + ", 绿色：" + colorData[1] + ", 黄色：" + colorData[2] + "\n信号灯颜色：" + TrafficUtil.sort(colorData));
            });
        }).start();
    }

    /**
     * 将本地图片资源转换为Bitmap
     * @param context 上下文
     * @param vectorDrawableId 图片资源ID
     * @return 转换后的Bitmap
     */
    private static Bitmap getBitmap(Context context, int vectorDrawableId) {
        // 兼容不同Android版本加载矢量图
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Drawable vectorDrawable = context.getDrawable(vectorDrawableId);
            Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
            return bitmap;
        } else {
            return BitmapFactory.decodeResource(context.getResources(), vectorDrawableId);
        }
    }

    // 注意：需在onDestroy中解注册广播和终止线程（代码未实现）
}
