package car.bkrc.com;  // 定义包名

import androidx.appcompat.app.AppCompatActivity;  // 引入AppCompatActivity类
import android.os.Bundle;  // 引入Bundle类
import android.util.Log;  // 引入Log类，用于调试输出
import android.widget.Button;  // 引入Button类
import android.widget.TextView;  // 引入TextView类，用于显示文本
import org.opencv.android.OpenCVLoader;  // 引入OpenCV的OpenCVLoader类
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import com.googlecode.tesseract.android.TessBaseAPI;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "OCR_APP";
    private static final int PERMISSION_REQUEST = 100;
    private static final int PICK_IMAGE = 200;
    Button selectBtn;

    private Bitmap processedBitmap;
    private TextView resultText;
    private TessBaseAPI tessBaseAPI;

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "初始化失败");
        } else {
            Log.d("OpenCV", "初始化成功");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectBtn = findViewById(R.id.btn_select);
        resultText = findViewById(R.id.tv_result);

        // 初始化Tesseract
        initTesseract();

        selectBtn.setOnClickListener(v -> {
            if (checkPermissions()) {
                selectImage();
            }
        });
    }

    private void initTesseract() {
        tessBaseAPI = new TessBaseAPI();
        String dataPath = getExternalFilesDir("/").getAbsolutePath() + "/";
        File dir = new File(dataPath + "tessdata/");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 复制训练数据（需预先将eng.traineddata放入assets）
        try {
            InputStream in = getAssets().open("eng.traineddata");
            OutputStream out = new FileOutputStream(dataPath + "tessdata/eng.traineddata");
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        tessBaseAPI.init(dataPath, "eng");
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST);
            return false;
        }
        return true;
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            try {
                Uri uri = data.getData();
                Bitmap originalBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                // 预处理流程
                Bitmap rotatedBitmap = autoRotateBitmap(uri, originalBitmap);
                Bitmap preprocessedBitmap = preprocessImage(rotatedBitmap);
                processedBitmap = removeNoise(preprocessedBitmap);

                // OCR识别
                recognizeText(processedBitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap autoRotateBitmap(Uri uri, Bitmap bitmap) throws IOException {
        ExifInterface exif = new ExifInterface(getContentResolver().openInputStream(uri));
        int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            default:
                return bitmap;
        }

        return Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private Bitmap preprocessImage(Bitmap input) {
        Mat src = new Mat();
        Utils.bitmapToMat(input, src);

        // 转为灰度图
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGB2GRAY);

        // 自适应阈值二值化
        Mat binary = new Mat();
        Imgproc.adaptiveThreshold(src, binary, 255,
                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
                Imgproc.THRESH_BINARY, 15, 12);

        Bitmap output = Bitmap.createBitmap(binary.cols(), binary.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(binary, output);
        return output;
    }

    private Bitmap removeNoise(Bitmap input) {
        Mat src = new Mat();
        Utils.bitmapToMat(input, src);

        // 中值滤波降噪
        Mat denoised = new Mat();
        Imgproc.medianBlur(src, denoised, 3);

        // 形态学开运算降噪
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new org.opencv.core.Size(1,1));
        Imgproc.morphologyEx(denoised, denoised, Imgproc.MORPH_OPEN, kernel);

        Bitmap output = Bitmap.createBitmap(denoised.cols(), denoised.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(denoised, output);
        return output;
    }

    private void recognizeText(Bitmap bitmap) {
        tessBaseAPI.setImage(bitmap);
        String result = tessBaseAPI.getUTF8Text();
        resultText.setText(result);
        tessBaseAPI.clear();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tessBaseAPI.end();
    }
}
