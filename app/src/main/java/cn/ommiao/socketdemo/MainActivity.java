package cn.ommiao.socketdemo;


import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;

import com.gyf.barlibrary.ImmersionBar;

import cn.ommiao.socketdemo.databinding.ActivityMainBinding;
import cn.ommiao.socketdemo.socket.message.chat.MessageBody;
import cn.ommiao.socketdemo.socket.message.chat.MessageWrapper;
import cn.ommiao.socketdemo.socket.message.heartbeat.HeartBeatWrapper;
import cn.ommiao.socketdemo.socket.service.IMessageService;
import cn.ommiao.socketdemo.socket.service.MessageService;
import cn.ommiao.socketdemo.socket.Config;
import cn.ommiao.socketdemo.socket.message.Action;
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
