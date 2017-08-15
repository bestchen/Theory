package cn.it.com.theroy.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import cn.it.com.theroy.R;
import cn.it.com.theroy.iview.IDetailView;
import cn.it.com.theroy.mamager.MainThreadExecutor;
import cn.it.com.theroy.presenter.IDetailPresenter;
import cn.it.com.theroy.presenter.impl.DetailPresenterImpl;
import cn.it.com.theroy.widget.PageWidget;

public class DetailActivity extends BaseActivity implements IDetailView {

    private TextView tvTitle;
    private PageWidget mPageWidget;
    private View llProgressContainer;
    private TextView tvCurrentPosition;
    private TextView tvDuration;
    private SeekBar seekBar;
    private View btnPause;

    private boolean audioExit = true;


    private IDetailPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_detail;
    }

    @Override
    protected void init() {
        presenter = new DetailPresenterImpl(this);
        presenter.init();
    }

    @Override
    protected void initView() {
        tvTitle = (TextView) findViewById(R.id.titile);
        mPageWidget = (PageWidget) findViewById(R.id.pagewidget);
        llProgressContainer = findViewById(R.id.ll_progress_container);
        tvCurrentPosition = (TextView) findViewById(R.id.tv_cur_time);
        seekBar = (SeekBar) findViewById(R.id.playSeekBar);
        tvDuration = (TextView) findViewById(R.id.tv_total_time);
        btnPause = findViewById(R.id.replayButton);

        seekBar.setOnSeekBarChangeListener(presenter.getOnSeekBarChangeListener());
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onClickPause();
            }
        });
    }

    @Override
    protected void initData() {
        showProgress();
        presenter.initData();
        mPageWidget.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent e) {
                return presenter.widgetOnTouch(v, e);
            }
        });
    }

    @Override
    public DetailActivity getActivity() {
        return this;
    }

    @Override
    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    @Override
    public void setBitmap(Bitmap cur, Bitmap next) {
        mPageWidget.setBitmaps(cur, next);
    }

    @Override
    public boolean isTouchWidget(View v) {
        return v == mPageWidget;
    }

    @Override
    public void abortAnimation() {
        mPageWidget.abortAnimation();
    }

    @Override
    public void calcCornerXY(float x, float y) {
        mPageWidget.calcCornerXY(x, y);
    }

    @Override
    public boolean DragToRight() {
        return mPageWidget.DragToRight();
    }

    @Override
    public boolean doTouchEvent(MotionEvent event) {
        return mPageWidget.doTouchEvent(event);
    }

    @Override
    public void setCurrentPosition(final long position) {
        MainThreadExecutor.post(getTask(), new Runnable() {
            @Override
            public void run() {
                tvCurrentPosition.setText(presenter.getFormatTime(position));
            }
        });
    }

    @Override
    public void setDuration(final long duration) {
        MainThreadExecutor.post(getTask(), new Runnable() {
            @Override
            public void run() {
                tvDuration.setText(presenter.getFormatTime(duration));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
        MainThreadExecutor.cancelAllRunnables(getTask());
    }

    @Override
    public void showProgress() {
        llProgressContainer.setVisibility(View.VISIBLE);
        MainThreadExecutor.postDelayed(getTask(), new Runnable() {
            @Override
            public void run() {
                llProgressContainer.setVisibility(View.INVISIBLE);
            }
        }, 3 * 1000);
    }

    @Override
    public boolean audioExit() {
        return audioExit;
    }

    @Override
    public void setAudioExit(boolean audioExit) {
        llProgressContainer.setVisibility(audioExit ? View.VISIBLE : View.GONE);
        this.audioExit = audioExit;
    }

    @Override
    public boolean getProgressVisiable() {
        return llProgressContainer.getVisibility() == View.VISIBLE;
    }

    @Override
    public boolean isPostInvalidate() {
        return mPageWidget.isPostInvalidate();
    }

    @Override
    public boolean isMotionUp() {
        return mPageWidget.isMotionUp();
    }

    private Object getTask() {
        return this.getClass().hashCode();
    }
}
