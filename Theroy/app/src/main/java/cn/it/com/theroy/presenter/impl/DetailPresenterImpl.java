package cn.it.com.theroy.presenter.impl;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import cn.it.com.theroy.R;
import cn.it.com.theroy.bean.TheoryItemBean;
import cn.it.com.theroy.iview.IDetailView;
import cn.it.com.theroy.presenter.IDetailPresenter;
import cn.it.com.theroy.uitls.BookPageFactory;
import cn.it.com.theroy.uitls.PreferencesUtils;
import cn.it.com.theroy.uitls.UIUitls;


public class DetailPresenterImpl implements IDetailPresenter {

    private String path, title, audioPath;
    private Bitmap mCurPageBitmap, mNextPageBitmap;
    private Canvas mCurPageCanvas, mNextPageCanvas;
    private BookPageFactory pagefactory;
    private WeakReference<IDetailView> iView;
    private MediaPlayer mediaPlayer;

    private Timer timer;
    private boolean isSeekBarChanging;//互斥变量，防止进度条与定时器冲突。
    private int currentPosition;//当前音乐播放的进度

    public DetailPresenterImpl(IDetailView iView) {
        this.iView = new WeakReference<IDetailView>(iView);
    }

    @Override
    public void init() {
        Intent intent = iView.get().getActivity().getIntent();
        path = intent.getStringExtra("path");
        title = intent.getStringExtra("title");
        audioPath = intent.getStringExtra("audioPath");
    }

    @Override
    public void initData() {
        if (TextUtils.isEmpty(path) || iViewNull()) {
            return;
        }
        iView.get().getActivity().setTitle(title);
        mCurPageBitmap = Bitmap.createBitmap(UIUitls.getScreenWidth(), UIUitls.getScreenHeight(), Bitmap.Config.ARGB_8888);
        mNextPageBitmap = Bitmap
                .createBitmap(UIUitls.getScreenWidth(), UIUitls.getScreenHeight(), Bitmap.Config.ARGB_8888);

        mCurPageCanvas = new Canvas(mCurPageBitmap);
        mNextPageCanvas = new Canvas(mNextPageBitmap);
        pagefactory = new BookPageFactory(UIUitls.getScreenWidth(), UIUitls.getScreenHeight() - UIUitls.getPixels(80));

        pagefactory.setBgBitmap(BitmapFactory.decodeResource(
                UIUitls.getResources(), R.color.color_336699));

        try {
            pagefactory.openbook(path);
            pagefactory.onDraw(mCurPageCanvas);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        iView.get().getActivity().setBitmap(mCurPageBitmap, mCurPageBitmap);

        play();
    }

    @Override
    public boolean widgetOnTouch(View v, MotionEvent e) {
        boolean ret = false;
        if (iView.get().isTouchWidget(v)) {
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                iView.get().abortAnimation();
                if (!iView.get().getProgressVisiable() && iView.get().audioExit()) {
                    iView.get().showProgress();
//                    return false;
                }

                if (iView.get().isPostInvalidate() && iView.get().isMotionUp()) {
                    iView.get().calcCornerXY(e.getX(), e.getY());
                    pagefactory.onDraw(mCurPageCanvas);
                    if (iView.get().DragToRight()) {
                        try {
                            pagefactory.prePage();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        if (pagefactory.isfirstPage()) return false;
                        pagefactory.onDraw(mNextPageCanvas);
                    } else {
                        try {
                            pagefactory.nextPage();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        if (pagefactory.islastPage()) return false;
                        pagefactory.onDraw(mNextPageCanvas);
                    }
                    iView.get().setBitmap(mCurPageBitmap, mNextPageBitmap);
                }
            }

            ret = iView.get().doTouchEvent(e);
            return ret;
        }
        return false;
    }

    @Override
    public SeekBar.OnSeekBarChangeListener getOnSeekBarChangeListener() {
        return new MySeekBar();
    }

    @Override
    public void onClickPause() {
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            timer.purge();
        } else {
            mediaPlayer.seekTo(currentPosition);
            mediaPlayer.start();
        }
    }

    @Override
    public String getFormatTime(long time) {
        long fixedTime = time / 1000;
        long second = fixedTime % 60;
        long min = fixedTime / 60;
        String fotmat = min + ":";
        if (min < 10) {
            fotmat = "0" + fotmat;
        }
        if (second < 10) {
            fotmat = fotmat + "0" + second;
        } else {
            fotmat = fotmat + second;
        }
        return fotmat;
    }

    private boolean iViewNull() {
        return iView == null || iView.get() == null;
    }

    @Override
    public void onDestroy() {
        stop();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 播放
     */
    public void play() {
        File file = new File(audioPath);
        if (file.exists()) {
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(audioPath);//设置播放的数据源。
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepareAsync();//数据缓冲
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        mp.seekTo(currentPosition);
                        iView.get().setDuration(mediaPlayer.getDuration());
                        iView.get().setCurrentPosition(currentPosition);
                    }
                });

                //监听播放时回调函数
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!isSeekBarChanging && mediaPlayer != null) {
                            currentPosition = mediaPlayer.getCurrentPosition();
                            iView.get().setCurrentPosition(currentPosition);
                        }
                    }
                }, 0, 1000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (TextUtils.equals(TheoryItemBean.TAG_RECORD, PreferencesUtils.getString(PreferencesUtils.SPKeys.CHAPTER_TAG, ""))) {
                Toast.makeText(iView.get().getActivity(), "音频文件没有下载", Toast.LENGTH_SHORT).show();
            }
            iView.get().setAudioExit(false);
        }
    }

    /**
     * 停止
     */
    public void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /*进度条处理*/
    public class MySeekBar implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
        }

        /*滚动时,应当暂停后台定时器*/
        public void onStartTrackingTouch(SeekBar seekBar) {
            isSeekBarChanging = true;
        }

        /*滑动结束后，重新设置值*/
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (mediaPlayer == null) {
                return;
            }
            isSeekBarChanging = false;
            final int progress = seekBar.getProgress();
            currentPosition = progress * mediaPlayer.getDuration() / 100;
            iView.get().setCurrentPosition(currentPosition);
            mediaPlayer.seekTo(currentPosition);
        }
    }
}
