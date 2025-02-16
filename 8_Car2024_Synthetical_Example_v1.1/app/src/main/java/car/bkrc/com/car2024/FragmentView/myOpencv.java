package car.bkrc.com.car2024.FragmentView;


import android.content.Context;
import android.graphics.Bitmap;  // 导入Bitmap类，用于图像的处理
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ImageView;  // 导入ImageView类，用于显示图像



import org.opencv.android.Utils;  // 导入OpenCV的工具类，用于Mat与Bitmap之间的转换
import org.opencv.core.Mat;  // 导入Mat类，OpenCV中的图像矩阵类

import org.opencv.core.Core;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

class OpencvUtils {

    /**
     * 将Bitmap进行二值化
     * */
    public static Mat matThreshold(Mat src, int min, int max){

        if(src == null){
            throw new NullPointerException("传入的图像对象为空");
        }

        Mat grayMat = new Mat();
        //先将图像进行灰度化
        Imgproc.cvtColor(src,grayMat,Imgproc.COLOR_BGR2GRAY);

        //二值化图像
        Mat binary = new Mat();

        //将灰度化的图像进行颜色处理
        Imgproc.threshold(grayMat,binary,min,max,Imgproc.THRESH_BINARY);
        return binary;

    }


    /**
     * 将Bitmap 根据传入的HSV进行识别
     * */
    public static Mat matColorInRange(Mat src,int[] minHsv, int[] maxHsv){
        if(src == null){
            throw new NullPointerException("传入的图像对象为空");
        }
        Mat hsv = new Mat();
        Imgproc.cvtColor(src,hsv,Imgproc.COLOR_BGR2HSV);
        Mat out = new Mat();
        Core.inRange(hsv,new Scalar(minHsv[0],minHsv[1],minHsv[2]),new Scalar(maxHsv[0],maxHsv[1],maxHsv[2]),out);
        return out;


    }

    /**
     * 将Bitmap图像转换为 BGR通道的图像
     * */
    public static Mat transferBitmapToHsvMat(Bitmap bitmap){
        Mat src = new Mat();
        if(bitmap == null){
            throw new NullPointerException("传入的图像对象为空");
        }
        Utils.bitmapToMat(bitmap,src);
        Mat bgr = new Mat();
        //android上的是RGBA，所以先转成BGR
        Imgproc.cvtColor(src,bgr,Imgproc.COLOR_RGBA2BGR);


        return bgr;


    }

    /**
     * 图像的膨胀操作
     * @param src 传入的BGR格式的图像
     * @param size 膨胀的范围 可以设置为new Size(3,3) 可对这里的参数进行更改 可填null
     * @param location 膨胀的位置 可填null 参考 new Point(-1,-1)
     * @param iterations 膨胀次数
     *
     */
    public static Mat matDilate(Mat src, Size size, Point location,int iterations){
        if(src == null){
            throw new NullPointerException("传入的图像对象为空");
        }
        //图像膨胀
        if(size == null){
            size = new Size(3,3);
        }
        Mat kernel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,size);
        Mat output = new Mat();
        if(location == null){
            location = new Point(-1,-1);
        }
        Imgproc.dilate(src, output, kernel,location,iterations);
        return output;

    }
    /**
     * 图像的膨胀操作
     * @param src 传入的BGR格式的图像
     *
     */
    public static Mat matDilate(Mat src){
        return matDilate(src,null,null,1);

    }


    /**
     * 图像的腐蚀操作
     * @param src 传入的BGR格式的图像
     * @param size 腐蚀的范围 可以设置为new Size(3,3) 可对这里的参数进行更改 可填null
     * @param location 腐蚀的位置 可填null 参考 new Point(-1,-1)
     * @param iterations 腐蚀次数
     *
     */
    public static Mat matErode(Mat src, Size size, Point location,int iterations){
        if(src == null){
            throw new NullPointerException("传入的图像对象为空");
        }
        //图像腐蚀
        if(size == null){
            size = new Size(3,3);
        }

        Mat kernel = Imgproc.getStructuringElement(Imgproc.CV_SHAPE_RECT,size);
        Mat result = new Mat();
        if(location == null){
            location = new Point(-1,-1);
        }
        Imgproc.erode(src, result, kernel, location,iterations);

        return result;
    }

    /**
     * 图像的腐蚀操作
     * @param src 传入的BGR格式的图像
     *
     */
    public static Mat matErode(Mat src){
        return matErode(src,null,null,1);

    }


    /**
     * 检测边缘
     * */
    public static Mat matCannyRect(Bitmap last,Mat src){
        if(src == null){
            throw new NullPointerException("传入的图像对象为空");
        }

        Mat mat = src.clone();
        Imgproc.Canny(src, mat, 75, 200);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        // 寻找轮廓
        Imgproc.findContours(mat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        int index = 0;
        double perimeter = 0;
        // 找出匹配到的最大轮廓
        for (int i = 0; i < contours.size(); i++) {
            // 最大面积
//            double area = Imgproc.contourArea(contours.get(i));
            //最大周长
            MatOfPoint2f source = new MatOfPoint2f();
            source.fromList(contours.get(i).toList());
            double length = Imgproc.arcLength(source,true);
            if(length>perimeter){
                perimeter =  length;
                index = i;
            }
        }

        Mat lastMat = new Mat();
        Utils.bitmapToMat(last,lastMat);
        /*
         * 参数一：image，待绘制轮廓的图像。
         *
         * 参数二：contours，待绘制的轮廓集合。
         *
         * 参数三：contourIdx，要绘制的轮廓在contours中的索引，若为负数，表示绘制全部轮廓。
         *
         * 参数四：color，绘制轮廓的颜色。
         *
         * 参数五：thickness，绘制轮廓的线条粗细。若为负数，那么绘制轮廓的内部。
         *
         * 参数六：lineType，线条类型。FILLED   LINE_4   4连通   LINE_8   8连通  LINE_AA  抗锯齿
         */
        Imgproc.drawContours(
                lastMat,
                contours,
                index,
                new Scalar(255,0, 0),
                5,
                Imgproc.LINE_AA

        );
        return lastMat;



    }



}

public class myOpencv {  // 声明一个名为opencv的公共类
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
