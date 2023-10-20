package com.example.myapplication.utils;
// 1.与Activity 或 Fragment 生命周期的绑定，界面离开时，取消请求
// 2.占位图，错误图，目标URL
// 3.性能优化，3级缓存，网络缓存，硬盘缓存，内存缓存，LRU
// 4.视图复用的问题
// 5.[Optional] 图片预热


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.LruCache;
import android.view.textclassifier.TextLinks;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.App;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class ImageLoader {
    private static final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>((int) (Runtime.getRuntime().maxMemory() / 1024 / 4)) {
        @Override
        protected int sizeOf(@NonNull String key, @NonNull Bitmap value) {
            return value.getByteCount() / 1024 / 4;
        }
    };

    public static void load(ImageView imageView, String url) {
        load(imageView, url, 0, 0);
    }

    public static void load(ImageView imageView, String url, int placeholder, int error) {
        imageView.setTag(-100, url);
        Bitmap cachedBitmap = lruCache.get(url);
        if (cachedBitmap != null) {
            imageView.setImageBitmap(cachedBitmap);
        }

        new OkHttpClient().newCall(new Request.Builder()
                        .url(url)
                        .build())
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String urlTag = (String) imageView.getTag(-100);
                        if (url.equals(urlTag)) {
                            imageView.setImageResource(error);
                        }
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String urlTag = (String) imageView.getTag(-100);
                        if (!url.equals(urlTag)) {
                            return;
                        }
                        if (response.code() != 200) {
                            imageView.setImageResource(error);
                            return;
                        }
                        ResponseBody responseBody = response.body();
                        if (responseBody == null) {
                            imageView.setImageResource(error);
                            return;
                        }
                        InputStream inputStream = responseBody.byteStream();
                        DiskCache diskCache = new DiskCache(new File(
//                                Environment.getExternalStorageState(),
                                App.get().getCacheDir(),
                                "MeChatDiskCache"
                        ));
                        String tempName = "__temp__" + UUID.randomUUID().toString();
                        OutputStream outputStream = diskCache.createFileOutputStream(tempName);
                        MessageDigest md5 = IoUtil.newMd5Digest();
                        try {
                            IoUtil.copy(inputStream, outputStream, md5);
                        } finally {
                            inputStream.close();
                            outputStream.close();
                        }
                        String realName = Base64.encodeToString(md5.digest(), Base64.URL_SAFE).trim();
                        File imageFile = diskCache.getFile(realName);
                        if (!diskCache.getFile(tempName).renameTo(imageFile)) {
                            throw new IOException("realName file error" + realName);
                        }
                        // TODO
                        load(imageView, imageFile, placeholder, error, url);
                    }

                });

    }

    private static final Executor decodeExecutor = Executors.newFixedThreadPool(2, new ThreadFactory() {

        private int count = 0;
        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable);
            thread.setName("ImageLoader" + (count++));
            return thread;
        }
    });

    public static void load(ImageView imageView, File file, int placeholder, int error, @Nullable String url) {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            new Handler(Looper.getMainLooper()).post(() -> {
                load(imageView, file, placeholder, error, url);
            });
            return;
        }

        Bitmap cachedBitmap = lruCache.get(file.getAbsolutePath());
        if (cachedBitmap != null) {
            imageView.setImageBitmap(cachedBitmap);
            return;
        }
        imageView.setTag(-101, file);
        imageView.setImageResource(placeholder);
        Runnable r = () -> {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            int height = imageView.getHeight();
            int width = imageView.getWidth();
            if (width == 0 || height == 0) {
                options.inSampleSize = 1;
            } else {
                BitmapFactory.Options options0 = new BitmapFactory.Options();
                options0.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(file.getAbsolutePath(), options0);
                int outWidth = options0.outWidth;
                int outHeight = options0.outHeight;
                if (outHeight == 0 || outHeight == 0) {
                    imageView.setImageResource(error);
                    return;
                }
                options.inSampleSize = Math.max(1, Math.min(outWidth / width, outHeight / height));
            }
            decodeExecutor.execute(() -> {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                if (bitmap != null) {
                    if (url != null) {
                        lruCache.put(url, bitmap);
                    } else if (file != null) {
                        lruCache.put(file.getAbsolutePath(), bitmap);
                    }
                }

                imageView.post(() -> {
                    File fileTag = (File) imageView.getTag(-101);
                    if (fileTag == file) {
                        if (bitmap == null) {
                            imageView.setImageResource(error);
                        } else {
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                });
            });
        };
        r.run();
    }

    private ImageLoader() {
        throw new UnsupportedOperationException();
    }
}
