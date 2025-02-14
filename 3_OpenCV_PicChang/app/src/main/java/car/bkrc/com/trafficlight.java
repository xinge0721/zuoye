package car.bkrc.com;

import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bkrcl.control_car_video.camerautil.CameraCommandUtil;

import car.bkrc.com.utils.SearchService;
import car.bkrc.com.utils.TrafficUtil;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

class ruy {


    private Timer timer;  // 识别次数限定
    private String result_qr;  // 识别结果统计



    private int qr_flag = 0; // 识别次数
    /**
     * 多二维码识别函数，输入带有多二维码的bitmap即可输出相应结果
     *
     * @param bitmap
     */
     Bitmap Qr_recognition(Bitmap bitmap) {
        // 在识别和绘制矩形框之前先将位图复制一份
        Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        new Thread(() -> {
            // TODO Auto-generated method stub
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Result[] result;
                    result_qr = "";
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    int[] pixels = new int[width * height];
                    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                    Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
                    hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
                    // 新建一个RGBLuminanceSource对象
                    RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                    // 将图片转换成二进制图片
                    BinaryBitmap binaryBitmap = new BinaryBitmap(new
                            GlobalHistogramBinarizer(source));
                    QRCodeMultiReader reader = new QRCodeMultiReader();// 初始化解析对象
                    try {
                        result = reader.decodeMultiple(binaryBitmap,
                                null);// 解析获取一个Result数组
                        if (result != null) {
                            // 创建画布对象并绑定到复制的位图上
                            Canvas canvas = new Canvas(mutableBitmap);
                            // 遍历保存的检测到的二维码结果
                            for (int i = 0; i < result.length; i++) {
                                Result results = result[i];
                                ResultPoint[] points = results.getResultPoints();
                                qr_flag = i + 1;
                                result_qr = result_qr + "二维码" + qr_flag + "：" + results.toString() + "\n";
                                // 获取二维码的四个角点坐标
                                float x1 = points[0].getX();
                                float y1 = points[0].getY();
                                float x2 = points[1].getX();
                                float y2 = points[1].getY();
                                float x3 = points[2].getX();
                                float y3 = points[2].getY();
                                float x4, y4, minX = 0, minY, maxX = 0, maxY;

                                // 找到四个角点中的最小 x 和最小 y，以及最大 x 和最大 y

                                float margin;
                                if (x2 > x1) {
                                    x4 = x3 + abs(x2 - x1);
                                    y4 = y3 + abs(y2 - y1);
                                    minX = Math.min(Math.min(x1, x2), Math.min(x3, x4));
                                    minY = Math.min(Math.min(y1, y2), Math.min(y3, y4));
                                    maxX = Math.max(Math.max(x1, x2), Math.max(x3, x4));
                                    maxY = Math.max(Math.max(y1, y2), Math.max(y3, y4));
                                    margin = abs(maxX - minX) / 4; // 设置边框的边距
                                } else {
                                    x4 = x3 + abs(x1 - x2);
                                    y4 = y3 + abs(y1 - y2);
                                    minX = Math.min(Math.min(x1, x2), Math.min(x3, x4));
                                    minY = Math.min(Math.min(y1, y2), Math.min(y3, y4));
                                    maxX = Math.max(Math.max(x1, x2), Math.max(x3, x4));
                                    maxY = Math.max(Math.max(y1, y2), Math.max(y3, y4));
                                    margin = abs(maxX - minX) / 4; // 设置边框的边距
                                }

                                // 绘制矩形框
                                Paint paint = new Paint();
                                paint.setColor(Color.GREEN);
                                paint.setStyle(Paint.Style.STROKE);
                                paint.setStrokeWidth(5f);
                                canvas.drawRect(minX - margin, minY - margin, maxX + margin, maxY + margin, paint); // 绘制边框

                                // 绘制二维码顺序编号
                                Paint textPaint = new Paint();
                                textPaint.setColor(Color.RED);
                                textPaint.setTextSize(30f);
                                textPaint.setStyle(Paint.Style.FILL);
                                float textHeight = Math.abs(textPaint.getFontMetrics().ascent);
                                float textX = minX;  // 替换为二维码的最小 x 坐标
                                float textY = minY - margin - textHeight + 10;  // 替换为合适的文字位置
                                canvas.drawText("二维码：" + (i + 1), textX, textY, textPaint); // 绘制文字
                            }
                            timer.cancel();
                        } else {
                            qr_flag++;
                            if (qr_flag >= 8) {  // 识别次数设置
                                timer.cancel();
                            }
                        }
                    } catch (NotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 500);
        }).start();
        return mutableBitmap;
    }
}
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
    private CameraCommandUtil cameraCommandUtil; // 摄像头命令工具类
    private Context context; // 新增Context成员变量
    private ruy Ruy; // 新增Context成员变量


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
    public trafficlight(Context context,ImageView image_show,TextView wifi_ip,TextView camera_ip,TextView show_news) {
        this.context = context;
        this.image_show = image_show;
        this.wifi_ip = wifi_ip;
        this.show_news = show_news;
    }
    /**
     * 初始化WiFi设置和摄像头搜索
     */
    public void trafficlight_Init(String IPCar) {
        if (IPCar != null && !IPCar.startsWith("错误")) {
            // 注册广播接收器
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(A_S);
            
            if (Build.VERSION.SDK_INT >= 33) {
                context.registerReceiver(myBroadcastReceiver, intentFilter);
            } else {
                context.registerReceiver(myBroadcastReceiver, intentFilter);
            }
            cameraCommandUtil = new CameraCommandUtil();
            search(); // 启动搜索摄像头
        }
    }



    // 搜索摄像头时的进度条
    private ProgressDialog progressDialog = null;

    /**
     * 启动后台服务搜索摄像头IP
     */
    private void search() {
        progressDialog = new ProgressDialog(context); // 使用正确的上下文
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在搜索摄像头");
        progressDialog.show();
        try {
//        这行才是收索摄像头IP的关键，也是说刚刚收索的那个IP吊用没有
            Intent intent = new Intent(context, SearchService.class); // 确保 SearchService 存在
//        发送广播，启用myBroadcastReceiver
            context.startService(intent); // 启动SearchService服务
            Log.d("SearchService", "启动成功！"); // 成功日志
        } catch (Exception e) {
            // 捕获异常并打印错误信息
            Log.e("SearchService", "服务启动失败: " + e.getMessage());
        }
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
                case 10:
                    show_news.setText("图像已去色"); // 测试用提示
                    image_show.setImageBitmap(bitmap); // 更新显示图像
                    break;
                case 30:
                    camera_ip.setText(cameraIP); // 显示摄像头的IP地址
                    break;
                case 40:
                    show_news.setText("图像已加载"); // 提示图像已加载
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
     * 获取颜色识别结果的getter方法
     * @return 返回红、绿、黄的像素数量数组
     */
    public int[] getColorData() {
        return colorData; // 返回颜色数据数组
    }

    /**
     * 获取识别的信号灯颜色
     * @return 返回信号灯的颜色字符串（红色、绿色、黄色）
     */
    public String getSignalColor() {
        return TrafficUtil.sort(colorData); // 返回颜色识别结果
    }

    /**
     * 第二步：识别交通灯颜色
     */
    private void recognition() {
        new Thread(() -> {
            colorData = TrafficUtil.convertToBlack(bitmap);
            // 使用 Handler 发送消息到主线程
            phHandler.sendEmptyMessage(40); // 新增一个消息类型
        }).start();
    }

    public void myonClick(int v) {
        if (v == 1) { // 第8步：点击“开始识别”按钮
            if (bitmap != null) {
                decolouring(bitmap);
                System.out.println("正在识别");
            } else {
                Log.e("SearchService","无图片加载\n请重启摄像头或加载本地图片"); // 如果没有图片，提示用户
            }

//        } else if (v.getId() == R.id.btn_1) { // 第10步：加载本地红灯图片
//            bitmap = getBitmap(getApplication(), R.mipmap.redlight); // 获取红灯图片
//            image_show.setImageBitmap(bitmap); // 设置ImageView的显示图像
//            phHandler.sendEmptyMessage(40); // 第11步：更新UI显示“图像已加载”
//        } else if (v.getId() == R.id.btn_2) { // 第12步：加载黄灯图片
//            bitmap = getBitmap(getApplication(), R.mipmap.yellowlight); // 获取黄灯图片
//            image_show.setImageBitmap(bitmap); // 设置ImageView的显示图像
//            phHandler.sendEmptyMessage(40); // 第13步：更新UI显示“图像已加载”
//        } else if (v.getId() == R.id.btn_3) { // 第14步：加载绿灯图片
//            bitmap = getBitmap(getApplication(), R.mipmap.greenlight); // 获取绿灯图片
//            image_show.setImageBitmap(bitmap); // 设置ImageView的显示图像
//            phHandler.sendEmptyMessage(40); // 第15步：更新UI显示“图像已加载”
        }
        else if(v == 2)
            if (bitmap != null) {
                Ruy.Qr_recognition(bitmap);
                System.out.println("正在识别");
            }
            else {
                Log.e("SearchService","无图片加载\n请重启摄像头或加载本地图片"); // 如果没有图片，提示用户
            }
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
