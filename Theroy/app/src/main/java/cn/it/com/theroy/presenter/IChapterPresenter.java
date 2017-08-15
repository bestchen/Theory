package cn.it.com.theroy.presenter;

import android.widget.LinearLayout;

import java.util.List;

import cn.it.com.theroy.widget.ChapterItemView;

public interface IChapterPresenter {

    void initIntent();

    ChapterItemView buildChapterItem();

    LinearLayout buildRowContainer();

    LinearLayout.LayoutParams buildItemLayoutParams();

    void onClick(String path,String name,String audioPath);
}
