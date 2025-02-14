package car.bkrc.com;  // 定义包名

import androidx.appcompat.app.AppCompatActivity;  // 引入AppCompatActivity类
import android.os.Bundle;  // 引入Bundle类
import android.util.Log;  // 引入Log类，用于调试输出
import android.view.View;  // 引入View类，表示UI组件
import android.widget.Button;  // 引入Button类
import android.widget.ImageView;  // 引入ImageView类，用于显示图片
import android.widget.TextView;  // 引入TextView类，用于显示文本
import org.opencv.android.BaseLoaderCallback;  // 引入OpenCV的BaseLoaderCallback类
import org.opencv.android.LoaderCallbackInterface;  // 引入OpenCV的LoaderCallbackInterface类
import org.opencv.android.OpenCVLoader;  // 引入OpenCV的OpenCVLoader类
import java.util.HashMap;  // 引入HashMap类，用于映射按钮ID和功能
import java.util.concurrent.ExecutorService;  // 引入ExecutorService类，用于线程池管理
import java.util.concurrent.Executors;  // 引入Executors类，用于创建线程池

public class MainActivity extends AppCompatActivity {  // 主活动类，继承AppCompatActivity
    private User sock_con = new User();  // 声明User对象，用于与硬件设备通信

    public TextView tv_alert1;  // 声明TextView对象，显示提示文本1
    public TextView tv_alert2;  // 声明TextView对象，显示提示文本2
    static String IPCar;  // 声明IP地址字符串，指向车载设备

    ImageView imageView;  // 声明ImageView对象，用于显示摄像头画面
    public static final String A_S = "com.a_s";  // 定义广播名称A_S
    static ExecutorService executorServicetor = Executors.newCachedThreadPool();  // 创建一个线程池

    @Override
    protected void onCreate(Bundle savedInstanceState) {  // onCreate方法，用于初始化活动
        super.onCreate(savedInstanceState);  // 调用父类onCreate方法
        setContentView(R.layout.activity_main);  // 设置活动布局
        OpenCVLoader.initDebug();  // 初始化OpenCV
        control_init();  // 调用控件初始化方法
        initOpenCV();  // 调用OpenCV初始化方法
        executorServicetor.execute(() -> sock_con.connect(IPCar));  // 使用线程池连接到IPCar
    }

    // 创建按钮ID与功能命令的映射关系
    private final HashMap<Integer, String> buttonFunctions = new HashMap<Integer, String>() {{
        put(R.id.trafficlight, "camera");  // 按钮：交通信号灯
        put(R.id.go, "GO");  // 按钮：前进
        put(R.id.left, "left");  // 按钮：左转
        put(R.id.Stop, "STOP");  // 按钮：停车
        put(R.id.right, "right");  // 按钮：右转
        put(R.id.back, "back");  // 按钮：后退

        put(R.id.btn3, "horn");  // 按钮：喇叭
        put(R.id.btn7, "light");  // 按钮：车灯
        put(R.id.btn9, "light");  // 按钮：车灯
        put(R.id.btn10, "light");  // 按钮：车灯
        put(R.id.btn11, "light");  // 按钮：车灯
        put(R.id.btn12, "light");  // 按钮：车灯
        // 可以继续添加其他按钮
    }};

    /**
     * 界面控件初始化
     */
    private void control_init() {  // 控件初始化方法

        tv_alert1 = findViewById(R.id.tv_alert1);  // 获取提示文本1控件
        tv_alert2 = findViewById(R.id.tv_alert2);  // 获取提示文本2控件
        imageView = findViewById(R.id.imageView);  // 获取图片显示控件

        // 获取所有需要绑定的按钮ID
        int[] buttonIds = {
                R.id.trafficlight,
                R.id.go,
                R.id.left,
                R.id.Stop,
                R.id.right,
                R.id.back,
                R.id.btn3,
                R.id.btn7,
                R.id.btn9,
                R.id.btn10,
                R.id.btn11,
                R.id.btn12
        };

        // 创建按钮点击监听器
        View.OnClickListener buttonListener = v -> {
            String command = buttonFunctions.get(v.getId());  // 获取按钮对应的命令
            if (command != null) {
                executeCommand(command);  // 执行命令
            }
        };

        // 动态绑定按钮点击事件
        for (int id : buttonIds) {  // 遍历所有按钮ID
            Button btn = findViewById(id);  // 获取按钮对象
            if (btn != null) btn.setOnClickListener(buttonListener);  // 为按钮设置点击监听器
        }
    }

    // 执行命令的功能中心
    private void executeCommand(String cmd) {  // 根据命令执行不同功能
        switch (cmd) {  // 根据命令不同执行不同操作
            case "GO":  // 前进命令
                sock_con.go(100,100);  // 执行前进操作
                tv_alert1.setText("前进");  // 显示前进提示
                break;
            case "left":  // 左转命令
                sock_con.left(100);  // 执行左转操作
                tv_alert1.setText("左转");  // 显示左转提示
                break;
            case "STOP":  // 停止命令
                sock_con.stop();  // 执行停止操作
                tv_alert1.setText("停下");  // 显示停止提示
                break;
            case "right":  // 右转命令
//                sock_con.right(100);  // 执行右转操作
                tv_alert1.setText("右转");  // 显示右转提示
                break;
            case "back":  // 后退命令
                sock_con.back(100,100);  // 执行后退操作
                tv_alert1.setText("后退");  // 显示后退提示
                break;
            case "camera": {  // 交通信号灯命令
                initOpenCV();  // 初始化OpenCV
                break;
            }
            // 可以继续添加其他功能
        }
    }

    /**
     * 初始化OpenCV
     */
    private void initOpenCV() {  // 初始化OpenCV的方法
        if (OpenCVLoader.initDebug()) {  // 检查OpenCV是否成功初始化
            Log.d("OpenCV", "初始化成功");  // 如果初始化成功，输出日志
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);  // 调用成功回调
        } else {  // 如果初始化失败
            Log.d("OpenCV", "初始化失败");  // 输出失败日志
        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {  // 创建OpenCV加载回调
        @Override
        public void onManagerConnected(int status) {  // 连接成功后的回调方法
        }
    };
}
