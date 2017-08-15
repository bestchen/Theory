package cn.it.com.theroy.activity;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public abstract class BaseActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(getLayoutRes());
        init();
        initView();
        initData();
    }

    protected abstract int getLayoutRes();

    protected void init() {
    }

    protected abstract void initView();

    protected abstract void initData();
}
