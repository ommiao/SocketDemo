package cn.ommiao.socketdemo.other;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

public class ScrollLinearLayoutManager extends LinearLayoutManager {


    private float MILLISECONDS_PER_INCH;
    private Context contxt;

    private Speed speed;

    public enum Speed{
        SPEED_SLOW, SPEED_MEDIAN, SPEED_FAST
    }

    public ScrollLinearLayoutManager(Context context) {
        super(context);
        this.contxt = context;
        setSpeedSlow(4);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller linearSmoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {

                    @Override
                    public PointF computeScrollVectorForPosition(int targetPosition) {
                        return ScrollLinearLayoutManager.this
                                .computeScrollVectorForPosition(targetPosition);
                    }

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        if(speed == Speed.SPEED_FAST){
                            return super.calculateSpeedPerPixel(displayMetrics);
                        } else {
                            return MILLISECONDS_PER_INCH / displayMetrics.density;
                        }
                    }

                };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }

    public void setSpeed(Speed speed){
        this.speed = speed;
        if(speed == Speed.SPEED_MEDIAN){
            setSpeedSlow(0);
        } else {
            setSpeedSlow(4);
        }
    }

    //可以用来设置速度
    public void setSpeedSlow(float x) {
        //自己在这里用density去乘，希望不同分辨率设备上滑动速度相同
        //0.3f是自己估摸的一个值，可以根据不同需求自己修改
        MILLISECONDS_PER_INCH = contxt.getResources().getDisplayMetrics().density * 0.3f + (x);
    }

}