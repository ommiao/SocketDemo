package cn.ommiao.socketdemo;

import android.widget.Toast;

import cn.ommiao.socketdemo.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private String nicname;

    @Override
    protected void immersionBar() {
        super.immersionBar();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        mBinding.ivEnter.setOnClickListener(v -> startChat());
    }

    private void startChat() {
        if(!isDataChecked()){
            return;
        }
        ChatActivity.start(this, nicname);
    }

    private boolean isDataChecked(){
        nicname = mBinding.etNicname.getText().toString().trim();
        if(nicname.length() == 0){
            Toast.makeText(this, R.string.nicname_required, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
