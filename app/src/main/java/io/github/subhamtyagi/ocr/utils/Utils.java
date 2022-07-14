package io.github.subhamtyagi.ocr.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;


import com.googlecode.leptonica.android.AdaptiveMap;
import com.googlecode.leptonica.android.Binarize;
import com.googlecode.leptonica.android.Convert;
import com.googlecode.leptonica.android.Enhance;
import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.leptonica.android.Rotate;
import com.googlecode.leptonica.android.Skew;
import com.googlecode.leptonica.android.WriteFile;
import java.util.Set;

public class Utils {

    private static final String DEFAULT_LANGUAGE = "eng";

    @SuppressLint("DefaultLocale")
    public static String getSize(int size) {
        String s = "";
        double kb = size / 1024;
        double mb = kb / 1024;
        if (size < 1024) {
            s = "$size Bytes";
        } else if (size < 1024 * 1024) {
            s = String.format("%.2f", kb) + " KB";
        } else if (size < 1024 * 1024 * 1024) {
            s = String.format("%.2f", mb) + " MB";
        }
        return s;
    }
    /*public static Bitmap preProcessBitmap(Bitmap bitmap){
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        int width, height;
        height = bitmap.getHeight();
        width = bitmap.getWidth();

        int pixel_array = bitmap[height][width];
        int pixel_value = 120;
        for(int i=0; i<width; i++) {
            for (int j = 0; j<height; j++) {

                if (pixel_array[0]>pixel_value||pixel_array[1]>pixel_value||pixel_array[2]>pixel_value)
                {


                    bitmap[i][j] = int a ([255, 255, 255]);
                }
            }
        }*/
        /*//else:
        //img_copy[row_index][cols_index] = np.array([255,0,0])
                return WriteFile.writeBitmap(bitmap);
          public void convGray(View view) {
          Mat Rgba = new Mat();
          Mat grayMat = new Mat();
          Mat imageBW = new Mat();
          BitmapFactory.Options o = new BitmapFactory.Options();
          o.inDither=false;
          o.inSampleSize=4;

          int width =   imageBitmap.getWidth();
          int height = imageBitmap.getHeight();

          grayBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565);

    //bitmap to Mat

          Utils.bitmapToMat(imageBitmap,Rgba);
          Imgproc.cvtColor(Rgba,grayMat,Imgproc.COLOR_RGB2GRAY);
          Imgproc.threshold(grayMat,imageBW,100,255,Imgproc.THRESH_BINARY);
          Utils.matToBitmap(imageBW,grayBitmap);
          imageView.setImageBitmap(grayBitmap);


          urrently looks like:


    }*/
    /*public static boolean[][] createBinaryImage( Bitmap bm )
    {
        int[] pixels = new int[bm.getWidth()*bm.getHeight()];
        bm.getPixels( pixels, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight() );
        int w = bm.getWidth();

        // Calculate overall lightness of image
        long gLightness = 0;
        int lLightness;
        int c;
        for ( int x = 0; x < bm.getWidth(); x++ )
        {
            for ( int y = 0; y < bm.getHeight(); y++ )
            {
                c = pixels[x+y*w];
                lLightness = ((c&0x00FF0000 )>>16) + ((c & 0x0000FF00 )>>8) + (c&0x000000FF);
                pixels[x+y*w] = lLightness;
                gLightness += lLightness;
            }
        }
        gLightness /= bm.getWidth() * bm.getHeight();
        gLightness = gLightness * 5 / 6;

        // Extract features
        boolean[][] binaryImage = new boolean[bm.getWidth()][bm.getHeight()];

        for ( int x = 0; x < bm.getWidth(); x++ )
            for ( int y = 0; y < bm.getHeight(); y++ )
                binaryImage[x][y] = pixels[x+y*w] <= gLightness;
        Pix pix = ReadFile.readBytes8();
        return pix;
    }*/
    //Anurag
    public static Bitmap preProcessBitmap(Bitmap bitmap) {
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Pix pix = ReadFile.readBitmap(bitmap);
        pix = Convert.convertTo8(pix);
        if (SpUtil.getInstance().getBoolean(Constants.KEY_CONTRAST, true)) {
            //pix=AdaptiveMap.backgroundNormMorph(pix);
            pix = AdaptiveMap.pixContrastNorm(pix);
        }

        if (SpUtil.getInstance().getBoolean(Constants.KEY_UN_SHARP_MASKING, true))
            pix = Enhance.unsharpMasking(pix);

        if (SpUtil.getInstance().getBoolean(Constants.KEY_OTSU_THRESHOLD, true))
            pix = Binarize.sauvolaBinarizeTiled(pix);

        //if (SpUtil.getInstance().getBoolean(Constants.KEY_FIND_SKEW_AND_DESKEW, true)) {
            //float f = Skew.findSkew(pix);
            //pix = Rotate.rotate(pix, f);
        //}

        return WriteFile.writeBitmap(pix);
    }


    public static boolean isPreProcessImage() {
        return SpUtil.getInstance().getBoolean(Constants.KEY_GRAYSCALE_IMAGE_OCR, true);
    }

    public static boolean isPersistData() {
        return SpUtil.getInstance().getBoolean(Constants.KEY_PERSIST_DATA, true);
    }

    public static String getTesseractStringForMultipleLanguages(Set<String> langs) {
        if (langs == null) return DEFAULT_LANGUAGE;
        StringBuilder rLanguage = new StringBuilder();
        for (String lang : langs) {
            rLanguage.append(lang);
            rLanguage.append("+");
        }
        return rLanguage.subSequence(0, rLanguage.toString().lastIndexOf('+')).toString();
    }

    public static String getTrainingDataType() {
        return SpUtil.getInstance().getString(Constants.KEY_TESS_TRAINING_DATA_SOURCE, "best");
    }

    public static String getTrainingDataLanguage() {
        if (SpUtil.getInstance().getBoolean(Constants.KEY_ENABLE_MULTI_LANG)) {
            return getTesseractStringForMultipleLanguages(SpUtil.getInstance().getStringSet(Constants.KEY_LANGUAGE_FOR_TESSERACT_MULTI, null));
        } else {
            return SpUtil.getInstance().getString(Constants.KEY_LANGUAGE_FOR_TESSERACT, DEFAULT_LANGUAGE);
        }

    }

    public static String setTrainingDataLanguage(String language) {
        if (SpUtil.getInstance().getBoolean(Constants.KEY_ENABLE_MULTI_LANG)) {
            return getTesseractStringForMultipleLanguages(SpUtil.getInstance().getStringSet(Constants.KEY_LANGUAGE_FOR_TESSERACT_MULTI, null));
        } else {
            return SpUtil.getInstance().getString(Constants.KEY_LANGUAGE_FOR_TESSERACT, DEFAULT_LANGUAGE);
        }

    }

    public static int getPageSegMode() {
        return Integer.parseInt(SpUtil.getInstance().getString(Constants.KEY_PAGE_SEG_MODE, "1"));
    }

    public static void putLastUsedText(String text) {
        SpUtil.getInstance().putString(Constants.KEY_LAST_USE_IMAGE_TEXT, text);
    }

    public static String getLastUsedText() {
        return SpUtil.getInstance().getString(Constants.KEY_LAST_USE_IMAGE_TEXT, "");
    }

    public static String[] getLast3UsedLanguage() {
        return new String[]{
                SpUtil.getInstance().getString(Constants.KEY_LAST_USED_LANGUAGE_1, "eng"),
                SpUtil.getInstance().getString(Constants.KEY_LAST_USED_LANGUAGE_2, "hin"),
                SpUtil.getInstance().getString(Constants.KEY_LAST_USED_LANGUAGE_3, "deu")
        };
    }

    public static void setLastUsedLanguage(String lastUsedLanguage) {
        String l1 = SpUtil.getInstance().getString(Constants.KEY_LAST_USED_LANGUAGE_1, "eng");
        if (lastUsedLanguage.contentEquals(l1)) {
            return;
        }
        String l2 = SpUtil.getInstance().getString(Constants.KEY_LAST_USED_LANGUAGE_2, "hin");
        if (l2.contentEquals(lastUsedLanguage)) {
            SpUtil.getInstance().putString(Constants.KEY_LAST_USED_LANGUAGE_2, l1);
            SpUtil.getInstance().putString(Constants.KEY_LAST_USED_LANGUAGE_1, lastUsedLanguage);
        } else {
            SpUtil.getInstance().putString(Constants.KEY_LAST_USED_LANGUAGE_3, l2);
            SpUtil.getInstance().putString(Constants.KEY_LAST_USED_LANGUAGE_2, l1);
            SpUtil.getInstance().putString(Constants.KEY_LAST_USED_LANGUAGE_1, lastUsedLanguage);
        }

    }

    public static void putLastUsedImageLocation(String imageURI) {
        SpUtil.getInstance().putString(Constants.KEY_LAST_USE_IMAGE_LOCATION, imageURI);
    }


}
