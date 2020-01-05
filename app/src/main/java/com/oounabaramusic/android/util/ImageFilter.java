package com.oounabaramusic.android.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class ImageFilter {

    public static Bitmap blurBitmap(Context context,Bitmap bitmap,float blurRadius){
        Bitmap in=bitmap.copy(Bitmap.Config.ARGB_8888,true);
        Bitmap out=Bitmap.createBitmap(in);

        RenderScript rs=RenderScript.create(context);
        ScriptIntrinsicBlur sib=ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        Allocation aOut;
        sib.setRadius(blurRadius);
        sib.setInput(Allocation.createFromBitmap(rs,in));
        sib.forEach(aOut=Allocation.createFromBitmap(rs,out));
        aOut.copyTo(out);
        return out;
    }

}
