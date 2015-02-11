package cn.kasogg.common.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageUtils {
    private volatile static ImageLoader mImageLoader;

    private ImageUtils() {
        throw new AssertionError();
    }

    public static void init(Context context) {
        mImageLoader = ImageLoader.getInstance();
        if (!mImageLoader.isInited()) {
            mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
        }
    }

    public static void displayImage(String uri, ImageView imageView) {
        mImageLoader.displayImage(uri, imageView);
    }

    public static void displayImage(String uri, ImageView imageView, DisplayImageOptions options) {
        mImageLoader.displayImage(uri, imageView, options);
    }

    /**
     * 从URL中获取Bitmap(需单独开启线程)
     *
     * @param picURL
     * @return 成功返回Bitmap, 失败返回null
     */
    public static Bitmap getBitmapFromUrl(String picURL) {
        try {
            URL url = new URL(picURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10 * 1000);
            Bitmap bitmap = null;
            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            }
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 90°翻转图像
     *
     * @param angle
     * @return Bitmap
     */
    public static Bitmap rotate(Bitmap bitmap, int angle) {
        Matrix m = new Matrix();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        m.setRotate(angle); // 旋转angle度
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);// 从新生成图片
        return bitmap;
    }

    /*
     * 圆角图片
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /*
     * resource转uri
     */
    public static Uri getDrawableUri(Context mContext, int id) {
        Resources r = mContext.getResources();
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + r.getResourcePackageName(id) + "/"
                + r.getResourceTypeName(id) + "/" + r.getResourceEntryName(id));
        return uri;
    }

    public static void saveBitmap(File f, Bitmap mBitmap) {
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(f));
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            if (bos != null) {
                bos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if (bos != null) {
                    bos.close();
                }
                ;
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 缩放Bitmap
     *
     * @param bitmap
     * @param width
     * @param height
     * @return Bitmap
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * 等比缩放Bitmap
     *
     * @param bitmap
     * @param ratio
     * @return Bitmap
     */
    public static Bitmap zoomRatioBitmap(Bitmap bitmap, int ratio) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(ratio, ratio);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }
}