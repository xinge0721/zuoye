package com.example.myapplication;

import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import com.bkrcl.control_car_video.camerautil.CameraCommandUtil;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;
public class ruy {


    private Timer timer;  // 识别次数限定
    private String result_qr;  // 识别结果统计
    private int qr_flag = 0; // 识别次数
    /**
     * 多二维码识别函数，输入带有多二维码的bitmap即可输出相应结果
     *
     * @param bitmap
     */
    private Bitmap Qr_recognition(Bitmap bitmap) {
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
                            qrHandler.sendEmptyMessage(20);  // 检测到二维码
                            timer.cancel();
                        } else {
                            qr_flag++;
                            qrHandler.sendEmptyMessage(15);  // 没检测到
                            if (qr_flag >= 8) {  // 识别次数设置
                                timer.cancel();
                                qrHandler.sendEmptyMessage(25);
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
    /**
     * 二维码数据显示处理
     */
    @SuppressLint("HandlerLeak")
    Handler qrHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 10:
                    System.out.println("Handler已经接收");
                    break;
                case 15:
//                    show_news.setText("正在进行第" + qr_flag + "次识别");
                    break;
                case 20:
//                    show_news.setText("识别结果:" + "\n" + result_qr);
                    result_qr = null;
                    break;
                case 25:
//                    show_news.setText("未能识别成功");
                    qr_flag = 0;
                    break;
                default:
                    break;
            }
        }
    };
}
