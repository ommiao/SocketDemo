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
import com.orhanobut.logger.Logger;

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

    private Intent mServiceIntent;
    private IMessageService iMessageService;

    private IntentFilter mIntentFilter;
    private LocalBroadcastManager mLocalBroadcastManager;
    private MessageReceiver mReciver;

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
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mReciver = new MessageReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(Action.ACTION_HEART_BEAT);
        mIntentFilter.addAction(Action.ACTION_MESSAGE_SEND);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(mServiceIntent, conn, BIND_AUTO_CREATE);
        mLocalBroadcastManager.registerReceiver(mReciver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(conn);
        mLocalBroadcastManager.unregisterReceiver(mReciver);
    }

    private void startChat() {
        if(!isDataChecked()){
            return;
        }
        //ChatActivity.start(this, nickname);
        testScoket();
    }

    private void testScoket() {
        MessageBody body = new MessageBody();
        body.setContent(nickname);
        MessageWrapper wrapper = new MessageWrapper().action(Action.ACTION_MESSAGE_SEND);
        wrapper.setBody(body);
        try {
            iMessageService.sendMessage(wrapper.getStringMessage());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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

    class MessageReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String message = intent.getStringExtra("message");
            assert action != null;
            switch (action){
                case Action.ACTION_HEART_BEAT:
                    handleHeartBeat(new HeartBeatWrapper(message));
                    break;
                case Action.ACTION_MESSAGE_SEND:
                    handleMessageReceived(new MessageWrapper(message));
                    break;
            }
        }
    }

    private void handleHeartBeat(HeartBeatWrapper wrapper){

    }

    private void handleMessageReceived(MessageWrapper wrapper){
        ToastUtil.show(wrapper.getContent());
    }
}
