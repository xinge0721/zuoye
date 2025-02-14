package car.bkrc.com;  // 声明当前类所在的包

import static java.lang.Math.abs;

import android.content.Context;
import android.graphics.Bitmap;  // 导入Bitmap类，用于图像的处理
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ImageView;  // 导入ImageView类，用于显示图像

import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.multi.qrcode.QRCodeMultiReader;

import org.opencv.android.Utils;  // 导入OpenCV的工具类，用于Mat与Bitmap之间的转换
import org.opencv.core.Mat;  // 导入Mat类，OpenCV中的图像矩阵类

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import car.bkrc.com.utils.OpencvUtils;  // 导入自定义的OpencvUtils工具类，封装了OpenCV的操作

public class Opencv {  // 声明一个名为opencv的公共类
    private Bitmap bitmap = null;  // 声明一个Bitmap对象用于保存图像数据，初始化为空
    public Mat inrangeMat = null;  // 声明一个Mat对象用于保存图像的处理结果，初始化为空

    /**
     * 检测根据hsv 提取到图像的轮廓
     */
    private void updateCanny(Bitmap bitmap, Mat inrangeMat, ImageView image_by) {  // 声明一个更新Canny边缘检测的私有方法，接收Bitmap图像、Mat图像数据和ImageView作为参数
        if (bitmap != null) {  // 如果传入的Bitmap图像不为空
            Bitmap bcopy = bitmap.copy(Bitmap.Config.ARGB_8888, true);  // 创建一个可修改的Bitmap副本

            Mat q;  // 声明一个Mat变量，用于保存处理后的图像

            if (inrangeMat != null) {  // 如果传入的inrangeMat不为空
                q = OpencvUtils.matCannyRect(bcopy, inrangeMat);  // 使用OpencvUtils工具类的matCannyRect方法进行Canny边缘检测
            } else {  // 如果inrangeMat为空
                q = OpencvUtils.matCannyRect(bcopy, OpencvUtils.transferBitmapToHsvMat(bcopy));  // 将Bitmap转换为HSV格式，然后进行Canny边缘检测
            }

            Utils.matToBitmap(q, bcopy);  // 将处理后的Mat数据转换回Bitmap并存入bcopy
            image_by.setImageBitmap(bcopy);  // 将处理后的Bitmap显示在传入的ImageView中
        }
    }

    public Mat blurMat;  // 声明一个Mat对象用于保存模糊处理后的图像
    public int b_min = 70, b_max = 120;  // 声明两个整数变量，用于控制二值化的最小值和最大值

    /**
     * 对图像进行二值化
     */
    public void updateBlur(Bitmap bitmap, ImageView image_blur) {  // 声明一个更新图像模糊处理的公共方法，接收Bitmap图像和ImageView作为参数
        if (bitmap != null) {  // 如果传入的Bitmap图像不为空
            Bitmap bcopy = bitmap.copy(Bitmap.Config.ARGB_8888, true);  // 创建一个可修改的Bitmap副本
            blurMat = OpencvUtils.matThreshold(OpencvUtils.transferBitmapToHsvMat(bcopy), b_min, b_max);  // 将Bitmap转换为HSV格式，然后进行二值化处理
            Utils.matToBitmap(blurMat, bcopy);  // 将处理后的Mat数据转换回Bitmap并存入bcopy
            image_blur.setImageBitmap(bcopy);  // 将处理后的Bitmap显示在传入的ImageView中
        }
    }

    /**
     * 对图像进行一次腐蚀操作
     */
    public void updateErode(Bitmap bitmap, Mat inrangeMat, ImageView image_hsv) {  // 声明一个更新图像腐蚀操作的公共方法，接收Bitmap图像、Mat图像数据和ImageView作为参数
        if (bitmap != null) {  // 如果传入的Bitmap图像不为空
            Bitmap bcopy = bitmap.copy(Bitmap.Config.ARGB_8888, true);  // 创建一个可修改的Bitmap副本

            if (inrangeMat != null) {  // 如果传入的inrangeMat不为空
                inrangeMat = OpencvUtils.matErode(inrangeMat);  // 使用OpencvUtils工具类的matErode方法进行腐蚀操作
            } else {  // 如果inrangeMat为空
                inrangeMat = OpencvUtils.matErode(OpencvUtils.transferBitmapToHsvMat(bcopy));  // 将Bitmap转换为HSV格式，然后进行腐蚀操作
            }
            Utils.matToBitmap(inrangeMat, bcopy);  // 将处理后的Mat数据转换回Bitmap并存入bcopy
            image_hsv.setImageBitmap(bcopy);  // 将处理后的Bitmap显示在传入的ImageView中
        }
    }

    /**
     * 对图像进行一次膨胀操作
     */
    public void updateDilate(Bitmap bitmap, Mat inrangeMat, ImageView image_hsv) {  // 声明一个更新图像膨胀操作的公共方法，接收Bitmap图像、Mat图像数据和ImageView作为参数
        if (bitmap != null) {  // 如果传入的Bitmap图像不为空
            Bitmap bcopy = bitmap.copy(Bitmap.Config.ARGB_8888, true);  // 创建一个可修改的Bitmap副本
            if (inrangeMat != null) {  // 如果传入的inrangeMat不为空
                inrangeMat = OpencvUtils.matDilate(inrangeMat);  // 使用OpencvUtils工具类的matDilate方法进行膨胀操作
            } else {  // 如果inrangeMat为空
                inrangeMat = OpencvUtils.matDilate(OpencvUtils.transferBitmapToHsvMat(bcopy));  // 将Bitmap转换为HSV格式，然后进行膨胀操作
            }

            Utils.matToBitmap(inrangeMat, bcopy);  // 将处理后的Mat数据转换回Bitmap并存入bcopy
            image_hsv.setImageBitmap(bcopy);  // 将处理后的Bitmap显示在传入的ImageView中
        }
    }

    /**
     * 根据HSV查找色块
     */
    public int hmin = 0, hmax = 0, smin = 0, smax = 0, vmin = 0, vmax = 0;  // 声明6个整数变量，用于设置HSV色调范围的最小值和最大值

    public void updateHsv(Bitmap bitmap, Mat inrangeMat, ImageView image_hsv) {  // 声明一个根据HSV查找色块的公共方法，接收Bitmap图像、Mat图像数据和ImageView作为参数
        if (bitmap != null) {  // 如果传入的Bitmap图像不为空
            Bitmap bcopy = bitmap.copy(Bitmap.Config.ARGB_8888, true);  // 创建一个可修改的Bitmap副本

            inrangeMat = OpencvUtils.matColorInRange(OpencvUtils.transferBitmapToHsvMat(bcopy), new int[]{hmin, smin, vmin}, new int[]{hmax, smax, vmax});  // 将Bitmap转换为HSV格式，并根据设置的HSV范围提取色块
            Utils.matToBitmap(inrangeMat, bcopy);  // 将处理后的Mat数据转换回Bitmap并存入bcopy
            image_hsv.setImageBitmap(bcopy);  // 将处理后的Bitmap显示在传入的ImageView中
        }
    }

     private Timer timer;  // 识别次数限定
    private String result_qr;  // 识别结果统计
    private int qr_flag = 0; // 识别次数
    /**
     * 多二维码识别函数，输入带有多二维码的bitmap即可输出相应结果
     *
     * @param bitmap
     */
    public  Bitmap Qr_recognition(Bitmap bitmap) {
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
    public int[] colorData = new int[3]; // 存储颜色数据
    /**
     * 图像去色处理（提取高亮区域）
     */
    public Bitmap decolouring(Bitmap recbitmap) {
        int width = recbitmap.getWidth();
        int height = recbitmap.getHeight();
        Bitmap grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = recbitmap.getPixel(x, y);
                int r = Color.red(pixel);
                int g = Color.green(pixel);
                int b = Color.blue(pixel);

                // 保留高亮区域（白色或其他亮色）
                if (r > 200 && g > 200 && b > 200) {
                    grayBitmap.setPixel(x, y, Color.rgb(255, 255, 255));
                } else {
                    grayBitmap.setPixel(x, y, Color.rgb(0, 0, 0));
                }
            }
        }
        return grayBitmap;
    }
    /**
     * 颜色识别（统计红、绿、黄色块）
     */
    public int[] detectColor(Bitmap bitmap) {
        colorData = new int[] {0, 0, 0};
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = bitmap.getPixel(x, y);
                int r = Color.red(pixel);
                int g = Color.green(pixel);
                int b = Color.blue(pixel);

                if (r > 200 && g < 50 && b < 50) { // 红色
                    colorData[0]++;
                } else if (g > 200 && r < 50 && b < 50) { // 绿色
                    colorData[1]++;
                } else if (r > 200 && g > 200 && b < 50) { // 黄色
                    colorData[2]++;
                }
            }
        }
        return colorData;
    }

    /**
     * 获取信号灯颜色结果
     */
    public String getSignalColor() {
        int maxIndex = 0;
        for (int i = 0; i < colorData.length; i++) {
            if (colorData[i] > colorData[maxIndex]) {
                maxIndex = i;
            }
        }
        switch (maxIndex) {
            case 0: return "红色";
            case 1: return "绿色";
            case 2: return "黄色";
            default: return "未知";
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
}
