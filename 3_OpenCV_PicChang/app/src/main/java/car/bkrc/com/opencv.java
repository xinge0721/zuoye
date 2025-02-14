package car.bkrc.com;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import car.bkrc.com.utils.OpencvUtils;

public class opencv {
    private Bitmap bitmap = null;
    public Mat inrangeMat = null;
    /**
     * 检测根据hsv 提取到图像的轮廓
     * */
    private void updateCanny(Bitmap bitmap, Mat inrangeMat, ImageView image_by) {
        if(bitmap != null) {
            Bitmap bcopy = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            Mat q;
            if(inrangeMat != null){
                q = OpencvUtils.matCannyRect(bcopy,inrangeMat);
            }else{

                q = OpencvUtils.matCannyRect(bcopy,OpencvUtils.transferBitmapToHsvMat(bcopy));
            }


            Utils.matToBitmap(q,bcopy);
            image_by.setImageBitmap(bcopy);
        }

    }

    public Mat blurMat;

    public int b_min = 70,b_max = 120;

    /**
     * 对图像进行二值化
     * */
    public void updateBlur(Bitmap bitmap, ImageView image_blur){
        if(bitmap != null){
            Bitmap bcopy = bitmap.copy(Bitmap.Config.ARGB_8888,true);
            blurMat = OpencvUtils.matThreshold(OpencvUtils.transferBitmapToHsvMat(bcopy),b_min,b_max);
            Utils.matToBitmap(blurMat,bcopy);
            image_blur.setImageBitmap(bcopy);

        }

    }


    /**
     * 对图像进行一次腐蚀操作
     * */
    public void updateErode(Bitmap bitmap,Mat inrangeMat, ImageView image_hsv){
        if(bitmap != null){
            Bitmap bcopy = bitmap.copy(Bitmap.Config.ARGB_8888,true);

            if(inrangeMat != null){
                inrangeMat = OpencvUtils.matErode(inrangeMat);
            }else{

                inrangeMat = OpencvUtils.matErode(OpencvUtils.transferBitmapToHsvMat(bcopy));
            }
            Utils.matToBitmap(inrangeMat,bcopy);
            image_hsv.setImageBitmap(bcopy);

        }

    }
    /**
     * 对图像进行一次膨胀操作
     * */
    public void updateDilate(Bitmap bitmap,Mat inrangeMat, ImageView image_hsv){
        if(bitmap != null){
            Bitmap bcopy = bitmap.copy(Bitmap.Config.ARGB_8888,true);
            if(inrangeMat != null){
                inrangeMat = OpencvUtils.matDilate(inrangeMat);
            }else{

                inrangeMat = OpencvUtils.matDilate(OpencvUtils.transferBitmapToHsvMat(bcopy));
            }

            Utils.matToBitmap(inrangeMat,bcopy);
            image_hsv.setImageBitmap(bcopy);

        }

    }

    /**
     * 根据HSV查找色块
     * */
    public int hmin = 0,hmax = 0,smin = 0,smax = 0,vmin = 0,vmax = 0;

    public void updateHsv(Bitmap bitmap , Mat inrangeMat, ImageView image_hsv){
        if(bitmap != null){
            Bitmap bcopy = bitmap.copy(Bitmap.Config.ARGB_8888,true);

            inrangeMat = OpencvUtils.matColorInRange(OpencvUtils.transferBitmapToHsvMat(bcopy),new int[]{hmin,smin,vmin},new int[]{hmax,smax,vmax});
            Utils.matToBitmap(inrangeMat,bcopy);
            image_hsv.setImageBitmap(bcopy);

        }
    }
}
