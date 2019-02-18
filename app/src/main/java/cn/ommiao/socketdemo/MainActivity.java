package cn.ommiao.socketdemo;

import com.gyf.barlibrary.BarHide;
import com.gyf.barlibrary.ImmersionBar;

import cn.ommiao.socketdemo.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    @Override
    protected void immersionBar() {
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_BAR).init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {

    }
}
