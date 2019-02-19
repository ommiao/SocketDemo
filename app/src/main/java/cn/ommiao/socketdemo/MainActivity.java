package cn.ommiao.socketdemo;

import android.widget.Toast;

import com.gyf.barlibrary.ImmersionBar;

import cn.ommiao.socketdemo.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private String nickname;

    @Override
    protected void immersionBar() {
        ImmersionBar.with(this).keyboardEnable(true).init();
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
        ChatActivity.start(this, nickname);
    }

    private boolean isDataChecked(){
        nickname = mBinding.etNickname.getText().toString().trim();
        if(nickname.length() == 0){
            Toast.makeText(this, R.string.nickname_required, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
