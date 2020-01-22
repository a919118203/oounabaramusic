package com.oounabaramusic.android.widget.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.oounabaramusic.android.R;

import androidx.annotation.Nullable;

public class PlayButton extends View{

    private Paint paint;
    private float width,height;
    private float progress=80;
    private float maxProgress=100;
    private Path start;
    private Path stop;
    private Path outSide;
    private RectF rectF;                 //定型进度条圆弧
    private boolean status =false;         //true - > start  false - > stop
    public PlayButton(Context context) {
        super(context);
        init(null);
    }

    public PlayButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PlayButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public PlayButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs){
        paint=new Paint();
        TypedArray ta=getContext().obtainStyledAttributes(attrs, R.styleable.PlayButton);
        width=ta.getDimension(R.styleable.PlayButton_width,0f);
        height=ta.getDimension(R.styleable.PlayButton_height,0f);
        ta.recycle();

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        //初始化外圈
        float r=(width>height?height:width)/2;
        outSide=new Path();
        outSide.addCircle(0,0,r*0.98f,Path.Direction.CW);

        //初始化开始按钮
        start=new Path();
        float gh3= (float) Math.sqrt(3);
        r*=0.4f;
        start.moveTo(-1f*r/2,(-1f*gh3/2)*r);
        start.lineTo(-1f*r/2,(gh3/2)*r);
        start.lineTo(r,0);
        start.lineTo(-1f*r/2,(-1f*gh3/2)*r);
        start.close();

        //初始化暂停按钮
        r=(width>height?height:width)/2;
        stop=new Path();

        stop.moveTo(-1f*r/4,-1f*r/3);
        stop.lineTo(-1f*r/4,r/3);
        stop.moveTo(r/4,-1f*r/3);
        stop.lineTo(r/4,r/3);

        rectF=new RectF(-r,-r,r,r);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec((int)(width/0.4),MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec((int)(height/0.4),MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width=getWidth();
        float height=getHeight();
        canvas.translate(width/2f,height/2f);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getContext().getResources().getColor(R.color.colorPrimaryDark));
        canvas.drawPath(outSide,paint);
        if(status){
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(getContext().getResources().getColor(R.color.negative));
            canvas.drawPath(outSide,paint);
            paint.setColor(getContext().getResources().getColor(R.color.colorPrimary));
            canvas.drawPath(stop,paint);
        }else{
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.BLACK);
            canvas.drawPath(outSide,paint);
            canvas.drawPath(start,paint);
        }


        paint.setColor(getContext().getResources().getColor(R.color.colorPrimary));
        canvas.drawArc(rectF,-90,(progress/maxProgress)*360,false,paint);
    }

    public void addOnClickPlayButtonListener(final OnClickPlayButtonListener listener){
        if(listener!=null){
            setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    if(status){
                        status =false;
                        listener.onStop();
                        invalidate();
                    }else{
                        status =true;
                        listener.onStart();
                        invalidate();
                    }
                }
            });
        }
    }

    public void setProgress(float progress) {
        if(progress==0){
            this.progress = progress;
        }else{
            if(progress%maxProgress==0){
                this.progress=maxProgress;
            }else{
                this.progress=progress%maxProgress;
            }
        }
        invalidate();
    }

    public void setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
    }

    public void setStatus(boolean status) {
        this.status = status;
        invalidate();
    }

    public interface OnClickPlayButtonListener{
        //从停止到开始后，该干的事情
        void onStart();
        //从开始到停止后，该干的事情
        void onStop();
    }
}
