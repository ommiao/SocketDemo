package cn.ommiao.socketdemo;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gyf.barlibrary.ImmersionBar;

public abstract class BaseActivity<D extends ViewDataBinding> extends AppCompatActivity {

    protected D mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, getLayoutId());
        immersionBar();
        initViews();
        initDatas();
    }

    protected void immersionBar(){
        ImmersionBar.with(this).init();
    }
    protected abstract  @LayoutRes int getLayoutId();
    protected abstract void initViews();

    protected void initDatas(){

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }
}
