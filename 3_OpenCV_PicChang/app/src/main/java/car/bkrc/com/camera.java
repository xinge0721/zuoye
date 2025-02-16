//package car.bkrc.com; // 包声明，表示此类属于 car.bkrc.com 包
//
//import static java.lang.Math.abs; // 引入静态方法 abs，用于计算绝对值
//
//import android.annotation.SuppressLint; // 引入注解，表示忽略特定的警告
//import android.app.ProgressDialog; // 引入ProgressDialog，用于显示进度条
//import android.content.BroadcastReceiver; // 引入广播接收器，用于接收广播消息
//import android.content.Context; // 引入Context，用于访问应用程序级资源
//import android.content.Intent; // 引入Intent，用于启动服务或活动
//import android.content.IntentFilter; // 引入IntentFilter，用于过滤接收广播
//import android.graphics.Bitmap; // 引入Bitmap，用于处理图像数据
//import android.os.Build; // 引入Build，用于获取Android版本信息
//import android.os.Handler; // 引入Handler，用于处理线程消息
//import android.os.Looper; // 引入Looper，用于准备消息循环
//import android.os.Message; // 引入Message，用于传递消息
//import android.util.Log; // 引入Log，用于日志打印
//import android.widget.ImageView; // 引入ImageView，用于显示图片
//import android.widget.TextView; // 引入TextView，用于显示文本
//
//import com.bkrcl.control_car_video.camerautil.CameraCommandUtil; // 引入摄像头命令工具类
//
////import car.bkrc.com.utils.SearchService; // 引入搜索服务类
//
//
//public class camera { // 类声明，定义交通信号灯相关功能
//    // UI组件
//    private ImageView image_show;        // 显示图像的ImageView
//    private TextView result;            // 显示识别结果\任务结果的TextView
//    private TextView IP;                 // 显示摄像头IP的TextView
//    private TextView object;            // 显示当前对象P的TextView
//
//
//    // 网络和摄像头相关
//    private String cameraIP;             // 摄像头IP地址
//    private Bitmap bitmap = null;        // 当前显示的图像Bitmap
//    private boolean flag = true;         // 控制摄像头图像获取线程的标志
//    private CameraCommandUtil cameraCommandUtil; // 摄像头命令工具类
//    private Context context; // 新增Context成员变量，用于获取上下文
//    private Opencv opencv = new Opencv(); // 新增Opencv成员变量，用于图像处理
//
//
//    // 广播相关
//    public static final String A_S = "com.a_s"; // 自定义广播的Action名称
//    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() { // 创建广播接收器
//        @Override
//        public void onReceive(Context arg0, Intent arg1) { // 接收到广播时的回调
//            // 接收SearchService发来的摄像头IP广播
//            cameraIP = arg1.getStringExtra("IP"); // 获取IP并拼接端口
//            progressDialog.dismiss();            // 关闭搜索进度条
//            phThread.start();                    // 启动摄像头图像获取线程
//            phHandler.sendEmptyMessage(30);      // 更新UI显示摄像头IP
//        }
//    };
//
//    public camera(Context context, ImageView image_show, TextView result, TextView IP, TextView object) { // 构造函数，初始化UI组件和上下文
//        this.context = context;
//        this.image_show = image_show;
//        this.result = result;
//        this.IP = IP;
//        this.object = object;
//    }
//
//    /**
//     * 初始化WiFi设置和摄像头搜索
//     */
//    public void camera_Init(String IPCar) { // 初始化函数，传入摄像头IP
//        if (IPCar != null && !IPCar.startsWith("错误")) { // 检查IP是否有效
//            // 注册广播接收器
//            IntentFilter intentFilter = new IntentFilter(); // 创建Intent过滤器
//            intentFilter.addAction(A_S); // 添加Action
//
//            if (Build.VERSION.SDK_INT >= 33) { // 判断Android版本
//                context.registerReceiver(myBroadcastReceiver, intentFilter); // 注册广播接收器
//            } else {
//                context.registerReceiver(myBroadcastReceiver, intentFilter); // 注册广播接收器
//            }
//            cameraCommandUtil = new CameraCommandUtil(); // 初始化摄像头命令工具类
//            search(); // 启动搜索摄像头
//        }
//    }
//
//
//    // 搜索摄像头时的进度条
//    private ProgressDialog progressDialog = null; // 定义进度条
//
//    /**
//     * 启动后台服务搜索摄像头IP
//     */
//    private void search() { // 搜索摄像头IP
//        progressDialog = new ProgressDialog(context); // 使用正确的上下文
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // 设置进度条为旋转样式
//        progressDialog.setMessage("正在搜索摄像头"); // 设置进度条信息
//        progressDialog.show(); // 显示进度条
//        try {
//            // 启动SearchService服务进行摄像头IP搜索
//            Intent intent = new Intent(context, SearchService.class); // 创建搜索服务的Intent
//            context.startService(intent); // 启动服务
//            Log.d("SearchService", "启动成功！"); // 打印日志
//        } catch (Exception e) { // 捕获异常
//            // 捕获异常并打印错误信息
//            Log.e("SearchService", "服务启动失败: " + e.getMessage()); // 打印错误日志
//        }
//    }
//
//    /**
//     * 线程：循环从摄像头获取图像
//     */
//    private Thread phThread = new Thread(new Runnable() { // 创建线程用于循环获取图像
//        @Override
//        public void run() { // 线程执行的代码
//            Looper.prepare(); // 准备线程的Looper（用于Handler）
//            while (true) { // 循环获取图像
//                if (flag) { // 如果标志为true，则获取图像
//                    // 通过HTTP请求获取摄像头图像
//                    bitmap = cameraCommandUtil.httpForImage(cameraIP); // 获取图像
//                    phHandler.sendEmptyMessage(10); // 通知Handler更新图像
//                }
//            }
//        }
//    });
//
//    /**
//     * Handler：处理线程发送的消息并更新UI
//     */
//    @SuppressLint("HandlerLeak") // 忽略Handler内存泄漏的警告
//    public Handler phHandler = new Handler() { // 创建Handler处理UI更新
//        @Override
//        public void handleMessage(Message msg) { // 处理消息
//            switch (msg.what) { // 根据消息的类型更新UI
//                case 10:
//                    result.setText("图像已去色"); // 测试用提示
//                    image_show.setImageBitmap(bitmap); // 更新显示图像
//                    break;
//                case 30:
//                    IP.setText(cameraIP);              // 显示摄像头的IP地址
//                    break;
//                case 40:
//                    result.setText("图像已加载");        // 提示图像已加载
//                    break;
//            }
//        }
//    };
//
//    /**
//     * 第一步：图像去色处理（保留高亮区域）
//     * @param recbitmap 原始图像
//     */
//    public void decolouring(final Bitmap recbitmap) {
//        new Thread(() -> {
//            bitmap = opencv.decolouring(recbitmap); // 调用Opencv的去色方法
//            phHandler.sendEmptyMessage(10);
//            recognition(); // 触发颜色识别
//        }).start();
//    }
//    /**
//     * 第二步：颜色识别
//     */
//    private void recognition() {
//        new Thread(() -> {
//            opencv.detectColor(bitmap); // Opencv进行颜色统计
//            phHandler.sendEmptyMessage(40); // 更新结果
//        }).start();
//    }
//
//    /**
//     * 获取颜色识别结果的getter方法
//     * @return 返回红、绿、黄的像素数量数组
//     */
//    public int[] getColorData() { // 获取颜色数据
//        return opencv.colorData; // 返回颜色数据数组
//    }
//
//    /**
//     * 获取识别的信号灯颜色
//     * @return 返回信号灯的颜色字符串（红色、绿色、黄色）
//     */
//    public String getSignalColor() {
//        return opencv.getSignalColor(); // 从Opencv获取结果
//    }
//
//
//public void myonClick(int v) {
//        if (v == 1) { // 第8步：点击“开始识别”按钮
//            if (bitmap != null) {
//                decolouring(bitmap);
//                System.out.println("正在识别");
//            } else {
//                Log.e("SearchService","无图片加载\n请重启摄像头或加载本地图片"); // 如果没有图片，提示用户
//            }
//        }
//        else if(v == 2)
//            if (bitmap != null) {
//                opencv.Qr_recognition(bitmap);
//                System.out.println("正在识别");
//            }
//            else {
//                Log.e("SearchService","无图片加载\n请重启摄像头或加载本地图片"); // 如果没有图片，提示用户
//            }
//    }
//
//
//    // 注意：需在onDestroy中解注册广播和终止线程（代码未实现）
//}
