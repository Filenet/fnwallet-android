package com.zxing.support.library.qrcode;

import android.graphics.Bitmap;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

public class QRCodeDecode {

    private final MultiFormatReader mMultiFormatReader;

    private QRCodeDecode(Builder builder) {
        mMultiFormatReader = new MultiFormatReader();
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        Collection<BarcodeFormat> formats = new ArrayList<>();
        formats.add(BarcodeFormat.QR_CODE);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, formats);
        hints.put(DecodeHintType.CHARACTER_SET, builder.mCharset);
        mMultiFormatReader.setHints(hints);
    }

    public String decode(final Bitmap image){
        final long start = System.currentTimeMillis();
        final int width = image.getWidth(), height = image.getHeight();
        final int[] pixels = new int[width * height];
        image.getPixels(pixels, 0, width, 0, 0, width, height);
        final RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        final BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            Result rawResult = mMultiFormatReader.decodeWithState(bitmap);
            final long end = System.currentTimeMillis();
            return rawResult.getText();
        } catch (NotFoundException re) {
            return null;
        }finally {
            mMultiFormatReader.reset();
        }
    }

    public static class Builder{

        private String mCharset = "UTF-8";

        public Builder setCharset(String charset) {
            if (TextUtils.isEmpty(charset)){
                throw new IllegalArgumentException("Illegal charset: " + charset);
            }
            mCharset = charset;
            return this;
        }

        public QRCodeDecode build(){
            return new QRCodeDecode(this);
        }
    }

}
