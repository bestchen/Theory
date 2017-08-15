package cn.it.com.theroy.presenter;

import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;



public interface IDetailPresenter {

    void init();

    void initData();

    boolean widgetOnTouch(View v, MotionEvent e);

    SeekBar.OnSeekBarChangeListener getOnSeekBarChangeListener();

    void onClickPause();

    String getFormatTime(long time);

    void onDestroy();
}
