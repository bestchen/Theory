package cn.it.com.theroy.iview;

import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.view.View;

import cn.it.com.theroy.activity.DetailActivity;

public interface IDetailView {

    DetailActivity getActivity();

    void setTitle(String title);

    void setBitmap(Bitmap cur, Bitmap next);

    boolean isTouchWidget(View v);

    void abortAnimation();

    void calcCornerXY(float x, float y);

    boolean DragToRight();

    boolean doTouchEvent(MotionEvent event);

    void setCurrentPosition(long position);

    void setDuration(long duration);

    boolean getProgressVisiable();

    void showProgress();

    boolean audioExit();

    void setAudioExit(boolean exit);

    boolean isPostInvalidate();

    boolean isMotionUp();
}
