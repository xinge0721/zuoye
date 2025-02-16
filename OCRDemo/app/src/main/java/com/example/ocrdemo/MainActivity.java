import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.CvType;
import org.opencv.core.Scalar;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "OCR";
    private static final String TESSDATA_PATH = "/path/to/tessdata"; // Replace with your actual path
    private TessBaseAPI tessBaseAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "OpenCV initialization failed.");
        } else {
            Log.d(TAG, "OpenCV initialized successfully.");
        }

        tessBaseAPI = new TessBaseAPI();
        if (!tessBaseAPI.init(TESSDATA_PATH, "eng")) {
            Log.e(TAG, "Tesseract initialization failed!");
            return;
        }

        // Load and process the image
        Bitmap bitmap = loadImageFromAssets("image.jpg"); // Replace with your image path
        if (bitmap != null) {
            Bitmap processedBitmap = preprocessImage(bitmap);
            String extractedText = performOCR(processedBitmap);
            Log.d(TAG, "Extracted Text: " + extractedText);
        } else {
            Log.e(TAG, "Failed to load image.");
        }
    }

    // Load the image from assets folder (ensure the image exists in the assets folder)
    private Bitmap loadImageFromAssets(String fileName) {
        try {
            FileInputStream inputStream = new FileInputStream(new File(getAssets().toString() + "/" + fileName));
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            Log.e(TAG, "Error loading image from assets", e);
            return null;
        }
    }

    // Preprocess the image (rotate, binarize, denoise)
    private Bitmap preprocessImage(Bitmap bitmap) {
        // Step 1: Auto rotate the image based on EXIF info
        bitmap = autoRotateImage(bitmap);

        // Step 2: Convert Bitmap to OpenCV Mat
        Mat mat = new Mat();
        Bitmap tmpBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        org.opencv.android.Utils.bitmapToMat(tmpBitmap, mat);

        // Step 3: Convert to Grayscale
        Mat grayMat = new Mat();
        Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGB2GRAY);

        // Step 4: Apply Binary Threshold (Local Adaptive Thresholding)
        Mat binaryMat = new Mat();
        Imgproc.adaptiveThreshold(grayMat, binaryMat, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);

        // Step 5: Denoise using 3x3 kernel (OpenCV's fast denoise)
        Mat denoisedMat = new Mat();
        Imgproc.medianBlur(binaryMat, denoisedMat, 3);

        // Step 6: Convert back to Bitmap
        Bitmap finalBitmap = Bitmap.createBitmap(denoisedMat.cols(), denoisedMat.rows(), Bitmap.Config.ARGB_8888);
        org.opencv.android.Utils.matToBitmap(denoisedMat, finalBitmap);

        return finalBitmap;
    }

    // Auto-rotate image based on EXIF data
    private Bitmap autoRotateImage(Bitmap bitmap) {
        try {
            int rotation = getExifRotation(getAssets().open("image.jpg"));
            if (rotation != 0) {
                android.graphics.Matrix matrix = new android.graphics.Matrix();
                matrix.postRotate(rotation);
                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading EXIF data", e);
        }
        return bitmap;
    }

    // Get rotation angle from EXIF data
    private int getExifRotation(java.io.InputStream inputStream) throws IOException {
        android.media.ExifInterface exif = new android.media.ExifInterface(inputStream);
        int orientation = exif.getAttributeInt(android.media.ExifInterface.TAG_ORIENTATION, android.media.ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case android.media.ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case android.media.ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case android.media.ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            default:
                return 0;
        }
    }

    // Perform OCR on the processed image
    private String performOCR(Bitmap bitmap) {
        tessBaseAPI.setImage(bitmap);
        return tessBaseAPI.getUTF8Text();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tessBaseAPI != null) {
            tessBaseAPI.end();
        }
    }
}
