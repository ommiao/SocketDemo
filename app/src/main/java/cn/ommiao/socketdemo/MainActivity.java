package cn.ommiao.socketdemo;


import com.gyf.barlibrary.ImmersionBar;

import cn.ommiao.socketdemo.databinding.ActivityMainBinding;
import cn.ommiao.socketdemo.utils.ConnectionUtil;
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
    }

    private void startChat() {
        if(!isDataChecked()){
            return;
        }
        //ChatActivity.start(this, nickname);
        testScoket();
    }

    private void testScoket() {
        new ConnectionUtil()
                .create(this)
                .ip("134.175.41.67")
                .port(2692)
                .send(nickname, ToastUtil::show);

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
