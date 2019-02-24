package cn.ommiao.socketdemo;


import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.gyf.barlibrary.ImmersionBar;

import cn.ommiao.socketdemo.databinding.ActivityMainBinding;
import cn.ommiao.socketdemo.service.IMessageService;
import cn.ommiao.socketdemo.service.MessageService;
import cn.ommiao.socketdemo.socket.Config;
import cn.ommiao.socketdemo.socket.connection.ConnectionUtil;
import cn.ommiao.socketdemo.utils.ToastUtil;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private String nickname;

    private Intent mServiceIntent;
    private IMessageService iMessageService;

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

    @Override
    protected void initDatas() {
        mServiceIntent = new Intent(this, MessageService.class);

    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(mServiceIntent, conn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(conn);
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
                .ip(Config.IP)
                .port(Config.PORT)
                .send(nickname);

    }

    private boolean isDataChecked(){
        nickname = mBinding.etNickname.getText().toString().trim();
        if(nickname.length() == 0){
            ToastUtil.show(R.string.nickname_required);
            return false;
        }
        return true;
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            iMessageService = IMessageService.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            iMessageService = null;
        }
    };
}
