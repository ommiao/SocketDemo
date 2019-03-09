package cn.ommiao.socketdemo.ui;


import com.gyf.barlibrary.ImmersionBar;

import cn.ommiao.socketdemo.R;
import cn.ommiao.socketdemo.databinding.ActivityMainBinding;
import cn.ommiao.socketdemo.socket.Config;
import cn.ommiao.socketdemo.utils.ToastUtil;

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
        String server = Config.IP + ":" + Config.PORT;
        mBinding.tvServer.setText(server);
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
            ToastUtil.show(R.string.nickname_required);
            return false;
        }
        return true;
    }
}
