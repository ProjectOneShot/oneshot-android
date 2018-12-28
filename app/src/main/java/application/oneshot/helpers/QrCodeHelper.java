package application.oneshot.helpers;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class QrCodeHelper {

    @Inject
    QrCodeHelper() {
    }

    public Bitmap encodeQr(String contents)
            throws WriterException, NullPointerException {

        final int stride = 512; // px

        final BitMatrix bitMatrix;

        try {
            bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, stride, stride, null);
        } catch (IllegalArgumentException e) {
            return null;
        }

        final int bitMatrixWidth = bitMatrix.getWidth();
        final int bitMatrixHeight = bitMatrix.getHeight();

        final int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            final int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {
                pixels[offset + x] = bitMatrix.get(x, y) ? Color.alpha(Color.TRANSPARENT) : Color.WHITE;
            }
        }

        final Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels, 0, stride, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}
