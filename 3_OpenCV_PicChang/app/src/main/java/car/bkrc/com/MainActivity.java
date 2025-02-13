package car.bkrc.com;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import android.widget.TextView;



import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    TextView tv_alert1; // 提示文本
    TextView tv_alert2; // 提示文本

    ImageView imageView;
    // 广播名称
    public static final String A_S = "com.a_s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OpenCVLoader.initDebug();
        control_init();
        initOpenCV();
    }
    private final HashMap<Integer, String> buttonFunctions = new HashMap<Integer, String>() {{
        // 数字键示例
        put(R.id.trafficlight, "trafficlight");//摄像头开启
        put(R.id.go, "GO");
        put(R.id.left, "left");   // 车灯
        put(R.id.Stop, "STOP");   // 车灯
        put(R.id.right, "right");   // 车灯
        put(R.id.back, "back");   // 车灯

        put(R.id.btn3, "horn");    // 喇叭
        put(R.id.btn7, "light");   // 车灯
        put(R.id.btn9, "light");   // 车灯
        put(R.id.btn10, "light");   // 车灯
        put(R.id.btn11, "light");   // 车灯
        put(R.id.btn12, "light");   // 车灯

        // ... 其他按钮继续添加
    }};

    private void initOpenCV() {
        if (OpenCVLoader.initDebug()) {
            // 使用JniLibs文件夹下的动态库初始化OpenCV
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {

        }
    };
    /**
     * 界面控件初始化
     */
    // 步骤2：动态绑定所有按钮
    private void control_init() {


        tv_alert1 = findViewById(R.id.tv_alert1); //初始化文本控件
        tv_alert2 = findViewById(R.id.tv_alert2); //初始化文本控件
        imageView = findViewById(R.id.imageView);//初始化图片控件

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
                tv_alert1.setText("前进");
                break;
            case "left":
//                sock_con.left(100);
                tv_alert1.setText("左转");
                break;
            case "STOP":
//                sock_con.stop();
                tv_alert1.setText("停下");
                break;
            case "right":
//                sock_con.right(100);
                tv_alert1.setText("右转");
                break;
            case "back":
//                sock_con.back(100,100);
                tv_alert1.setText("后退");
                break;
            case "trafficlight":{
                if (OpenCVLoader.initDebug()) {
                    tv_alert1.setText("OpenCV加载成功!");
                }
                else
                {
                    tv_alert1.setText("OpenCV加载失败!");
                }
                break;}
            // ... 其他功能分支
        }
    }

}
