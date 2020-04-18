package com.zxing.support.library.qrcode;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

public final class QRCodeEncode {

    public static final String TAG = QRCodeDecode.class.getSimpleName();

    private final Builder mConfigBuilder;
    private final MultiFormatWriter mMultiFormatWriter;

    private QRCodeEncode(Builder configBuilder) {
        mConfigBuilder = configBuilder;
        mMultiFormatWriter = new MultiFormatWriter();
    }

    public Bitmap encode(final String content) {
        return encode(content, mConfigBuilder.mOutputBitmapWidth, mConfigBuilder.mOutputBitmapHeight);
    }

    public Bitmap encode(final String content, int width, int height) {
        if (TextUtils.isEmpty(content)) {
            throw new IllegalArgumentException("QRCode encode content CANNOT be empty");
        }
        final long start = System.currentTimeMillis();
        final Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, mConfigBuilder.mCharset);
        if (mConfigBuilder.mHintMargin >= 0) {
            hints.put(EncodeHintType.MARGIN, mConfigBuilder.mHintMargin);
        }
        BitMatrix result;
        try {
            result = mMultiFormatWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (Exception e) {
            return null;
        }
        int finalBitmapWidth = result.getWidth();
        int finalBitmapHeight = result.getHeight();
        final int[] pixels = new int[finalBitmapWidth * finalBitmapHeight];
        for (int y = 0; y < finalBitmapHeight; y++) {
            int offset = y * finalBitmapWidth;
            for (int x = 0; x < finalBitmapWidth; x++) {
                pixels[offset + x] = result.get(x, y) ? mConfigBuilder.mCodeColor : mConfigBuilder.mBackgroundColor;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(finalBitmapWidth, finalBitmapHeight, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, finalBitmapWidth, 0, 0, finalBitmapWidth, finalBitmapHeight);

        final long end = System.currentTimeMillis();
        return bitmap;
    }


    public static class Builder {

        private int mBackgroundColor = 0xFFFFFFFF;
        private int mCodeColor = 0xFF000000;
        private String mCharset = "UTF-8";
        private int mOutputBitmapWidth;
        private int mOutputBitmapHeight;
        private int mHintMargin = -1;

        public Builder setBackgroundColor(int backgroundColor) {
            mBackgroundColor = backgroundColor;
            return this;
        }

        public Builder setCodeColor(int codeColor) {
            mCodeColor = codeColor;
            return this;
        }

        public Builder setCharset(String charset) {
            if (TextUtils.isEmpty(charset)) {
                throw new IllegalArgumentException("Illegal charset: " + charset);
            }
            mCharset = charset;
            return this;
        }

        public Builder setOutputBitmapWidth(int outputBitmapWidth) {
            mOutputBitmapWidth = outputBitmapWidth;
            return this;
        }

        public Builder setOutputBitmapHeight(int outputBitmapHeight) {
            mOutputBitmapHeight = outputBitmapHeight;
            return this;
        }

        public Builder setOutputBitmapPadding(int hintMargin) {
            mHintMargin = hintMargin;
            return this;
        }

        public QRCodeEncode build() {
            return new QRCodeEncode(this);
        }
    }
}
