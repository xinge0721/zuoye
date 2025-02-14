package com.example.myapplication.utils;

import android.graphics.Bitmap;
import android.util.Log;


public class TrafficUtil {

    /**
     * 第一步 像素处理背景变为白色，红、绿、蓝、黄、品、青、黑色，白色不变
     * @param bip
     * @return
     */
    public static Bitmap convertToLight(Bitmap bip) {
        int width = bip.getWidth();
        int height = bip.getHeight();
        int[] pixels = new int[width * height];
        bip.getPixels(pixels, 0, width, 0, 0, width, height);
        int[] pl = new int[bip.getWidth() * bip.getHeight()];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                int pixel = pixels[offset + x];
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = pixel & 0xff;
                int bright = (int) (0.229 * r + 0.587 * g + 0.114 * b);
                if (bright < 256/2)
                    pl[offset + x] = 0xff000000;
                 else if (bright < 256/5*4)
                    pl[offset + x] = pixel;
                else
                    pl[offset + x] = pixel;
            }
        }
        Bitmap result = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);//把颜色值重新赋给新建的图片 图片的宽高为以前图片的值
        result.setPixels(pl, 0, width, 0, 0, width, height);
        return result;
    }

    /**
     * 第二步：检测图像中包含的灯光颜色信息
     * @param bip
     * @return
     */
    public static int[] convertToBlack(Bitmap bip) {
        int[] colorNum = new int[3];
        int width = bip.getWidth();
        int height = bip.getHeight();
        int[] pixels = new int[width * height];
        bip.getPixels(pixels, 0, width, 0, 0, width, height);
        int[] pl = new int[bip.getWidth() * bip.getHeight()];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                int pixel = pixels[offset + x];
                int r = (pixel >> 16) & 0xff;
                int g = (pixel >> 8) & 0xff;
                int b = pixel & 0xff;

                pl[offset + x] = pixel;
                if (r > 240 && b < 220 && g < 220)
                    colorNum[0]++;            //红色
                else if (r < 220 && b < 220 && g > 240)
                    colorNum[1]++;            //绿色
                if (r > 240 && g > 240 && b < 220){
                    colorNum[2]++;            //黄色
                }
            }
        }
        return colorNum;
    }

    /**
     * 排序
     */
    public static String sort(int[] colorNum) {
        Log.e("TAG", "colorNum[0]" + colorNum[0] + ", colorNum[1]" + colorNum[1] + ", colorNum[2]" + colorNum[2]);
        String result = (colorNum[0] > colorNum[1] && colorNum[0] > colorNum[2]) ? "红色" :
                (colorNum[1] > colorNum[0] && colorNum[1] > colorNum[2]) ? "绿色" : "黄色";
        for (int i = 0; i < 3; i++) {
            colorNum[i] = 0;
        }
        return result;
    }
}
