package cn.ommiao.socketdemo;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gyf.barlibrary.ImmersionBar;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import cn.ommiao.socketdemo.adapter.MessageAdapter;
import cn.ommiao.socketdemo.databinding.ActivityChatBinding;
import cn.ommiao.socketdemo.entity.MessageEntity;
import cn.ommiao.socketdemo.other.ScrollLinearLayoutManager;
import cn.ommiao.socketdemo.socket.message.ActionDefine;
import cn.ommiao.socketdemo.socket.message.chat.MessageBody;
import cn.ommiao.socketdemo.socket.message.chat.MessageWrapper;
import cn.ommiao.socketdemo.socket.message.heartbeat.HeartBeatWrapper;
import cn.ommiao.socketdemo.socket.message.user.EventDefine;
import cn.ommiao.socketdemo.socket.message.user.User;
import cn.ommiao.socketdemo.socket.message.user.UserBody;
import cn.ommiao.socketdemo.socket.message.user.UserWrapper;
import cn.ommiao.socketdemo.socket.service.IMessageService;
import cn.ommiao.socketdemo.socket.service.MessageService;
import cn.ommiao.socketdemo.utils.ToastUtil;

public class ChatActivity extends BaseActivity<ActivityChatBinding> implements TextWatcher, View.OnClickListener {

    private enum ConnectionStatus{
        Unconnected, Connecting, Connected, Disconnected
    }

    private String nickname;

    private MessageAdapter adapter;
    private ArrayList<MessageEntity> messages = new ArrayList<>();

    private Intent mServiceIntent;
    private IMessageService iMessageService;

    private IntentFilter mIntentFilter;
    private LocalBroadcastManager mLocalBroadcastManager;
    private MessageReceiver mReciver;

    private ConnectionStatus status = ConnectionStatus.Unconnected;

    private MenuItem friendItem, exitItem;

    public static void start(Context context, String nickname) {
        Intent starter = new Intent(context, ChatActivity.class);
        starter.putExtra("nickname", nickname);
        context.startActivity(starter);
    }

    @Override
    protected void immersionBar() {
        ImmersionBar.with(this).titleBar(R.id.toolbar).keyboardEnable(true).init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initViews() {
        setSupportActionBar(mBinding.toolbar);
        nickname = getIntent().getStringExtra("nickname");
        String subTitle = "I'm " + nickname + ".";
        mBinding.toolbar.setSubtitle(subTitle);
        ScrollLinearLayoutManager layoutManager = new ScrollLinearLayoutManager(this);
        layoutManager.setStackFromEnd(false);
        layoutManager.setSpeed(ScrollLinearLayoutManager.Speed.SPEED_SLOW);
        mBinding.rvMessage.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(messages);
        mBinding.rvMessage.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvMessage.setAdapter(adapter);
        mBinding.etMsg.addTextChangedListener(this);
        mBinding.btnSend.setOnClickListener(this);
        mBinding.btnSend.setEnabled(false);
        mBinding.rvMessage.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if(lastVisibleItemPosition == messages.size() - 1){
                    layoutManager.setSpeed(ScrollLinearLayoutManager.Speed.SPEED_SLOW);
                } else if(messages.size() - 1 - lastVisibleItemPosition <= 5) {
                    layoutManager.setSpeed(ScrollLinearLayoutManager.Speed.SPEED_MEDIAN);
                } else {
                    layoutManager.setSpeed(ScrollLinearLayoutManager.Speed.SPEED_FAST);
                }
            }
        });
        onConnecting();
    }

    private void onConnecting() {
        mBinding.flLoading.setVisibility(View.VISIBLE);
        mBinding.tvTips.setText("正在建立连接");
    }

    private void onLogon(){
        mBinding.flLoading.setVisibility(View.VISIBLE);
        mBinding.tvTips.setText("正在加入群聊");
    }

    private void onLogonSuccess(){
        mBinding.flLoading.setVisibility(View.VISIBLE);
        mBinding.tvTips.setText("加入群聊成功");
        friendItem.setEnabled(true);
        exitItem.setEnabled(true);
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> mBinding.flLoading.setVisibility(View.INVISIBLE));
        }).start();
    }

    private void onLogout(){
        mBinding.flLoading.setVisibility(View.VISIBLE);
        mBinding.tvTips.setText("正在退出群聊");
    }

    private void onLogoutSuccess(){
        mBinding.flLoading.setVisibility(View.VISIBLE);
        mBinding.tvTips.setText("退出群聊成功");
        new Thread(() -> {
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finish();
        }).start();
    }

    private void onDisconnected(){
        mBinding.flLoading.setVisibility(View.VISIBLE);
        mBinding.tvTips.setText("掉线重连中...");
    }

    private void onReconnected(){
        mBinding.flLoading.setVisibility(View.VISIBLE);
        mBinding.tvTips.setText("重连成功");
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> mBinding.flLoading.setVisibility(View.INVISIBLE));
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_toolbar, menu);
        friendItem = menu.findItem(R.id.toolbar_friend);
        exitItem = menu.findItem(R.id.toolbar_exit);
        friendItem.setEnabled(false);
        exitItem.setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.toolbar_friend:
                ToastUtil.show(R.string.chat_toolbar_friend);
                break;
            case R.id.toolbar_exit:
                onExitClick();
                break;
        }
        return true;
    }

    private void onExitClick() {
        if(status != ConnectionStatus.Connected){
            exit();
        } else {
            exit();
        }
    }

    private void exit(){
        logout();
    }

    @Override
    public void onBackPressed() {
        onExitClick();
    }

    @Override
    protected void initDatas() {
        initBroadcast();
        bindService();
    }

    private void initBroadcast() {
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mReciver = new MessageReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ActionDefine.ACTION_HEART_BEAT);
        mIntentFilter.addAction(ActionDefine.ACTION_MESSAGE_SEND);
        mIntentFilter.addAction(ActionDefine.ACTION_USER_CHANGED);
        mIntentFilter.addAction(ActionDefine.ACTION_DISCONNECTED);
    }

    private void bindService(){
        mServiceIntent = new Intent(this, MessageService.class);
        bindService(mServiceIntent, conn, BIND_AUTO_CREATE);
        mLocalBroadcastManager.registerReceiver(mReciver, mIntentFilter);
        status = ConnectionStatus.Connecting;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
        stopService(mServiceIntent);
        mLocalBroadcastManager.unregisterReceiver(mReciver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send:
                sendMsg();
                break;
        }
    }

    private void sendMsg() {
        String content = mBinding.etMsg.getText().toString();
        MessageEntity entity = new MessageEntity();
        entity.setType(MessageEntity.TYPE_OUT);
        entity.setContent(content);
        entity.setNickname(nickname);
        SimpleDateFormat sf = new SimpleDateFormat("hh:mm", Locale.CHINESE);
        entity.setTime(sf.format(new Date()));
        messages.add(entity);
        adapter.notifyItemInserted(messages.size() - 1);
        mBinding.rvMessage.smoothScrollToPosition(messages.size());
        resetEtMsg();
        sendChatMessage(content);
        //addVirtualIn();
    }

    private void sendChatMessage(String content) {
        MessageBody body = new MessageBody();
        body.setContent(content);
        User user = new User();
        user.setUserCode(MessageService.userCode);
        user.setNickname(nickname);
        body.setUser(user);
        MessageWrapper wrapper = new MessageWrapper().action(ActionDefine.ACTION_MESSAGE_SEND);
        wrapper.setBody(body);
        sendSocketMessage(wrapper.getStringMessage());
    }

    private void sendSocketMessage(String json){
        try {
            iMessageService.sendMessage(json);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void addVirtualIn() {
        new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> {
                MessageEntity entity = new MessageEntity();
                entity.setType(MessageEntity.TYPE_IN);
                entity.setContent("哈哈");
                entity.setNickname(nickname);
                SimpleDateFormat sf = new SimpleDateFormat("hh:mm", Locale.CHINESE);
                entity.setTime(sf.format(new Date()));
                messages.add(entity);
                adapter.notifyItemInserted(messages.size() - 1);
                mBinding.rvMessage.smoothScrollToPosition(messages.size());
            });
        }).start();
    }

    private void resetEtMsg() {
        mBinding.etMsg.setText("");
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(s.length() > 0){
            mBinding.btnSend.setEnabled(true);
        } else {
            mBinding.btnSend.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

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

    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String message = intent.getStringExtra("message");
            assert action != null;
            switch (action){
                case ActionDefine.ACTION_HEART_BEAT:
                    handleHeartBeat(new HeartBeatWrapper(message));
                    break;
                case ActionDefine.ACTION_MESSAGE_SEND:
                    handleMessageReceived(new MessageWrapper(message));
                    break;
                case ActionDefine.ACTION_DISCONNECTED:
                    handleDisconnected();
                    break;
                case ActionDefine.ACTION_USER_CHANGED:
                    handleUserChanged(new UserWrapper(message));
                    break;
            }
        }


    }
    private void handleUserChanged(UserWrapper wrapper) {
        UserBody body = wrapper.getWrapperBody();
        if(body.isLogonSuccess()){ //加入群聊成功
            handleLogonSuccess(body.getCurrentUsers());
        } else if(body.isLogoutSuccess()){ //退出群聊成功
            handleLogoutSuccess();
        } else if(body.isUserAdded()){ //用户增加
            handleUserAdded(body.getChangedUser());
        } else if(body.isUserQuited()){ //用户退出
            handleUserQuited(body.getChangedUser());
        }
    }

    private void handleLogonSuccess(ArrayList<User> currentUsers) {
        onLogonSuccess();
    }

    private void handleLogoutSuccess() {
        mHandler.removeCallbacks(logoutRunanable);
        onLogoutSuccess();
    }

    private void handleUserAdded(User changedUser) {

    }

    private void handleUserQuited(User changedUser) {

    }

    private void handleDisconnected() {
        status = ConnectionStatus.Disconnected;
    }

    private void handleHeartBeat(HeartBeatWrapper wrapper){
        if(status != ConnectionStatus.Connected){
            logon();
        }
        status = ConnectionStatus.Connected;
    }

    private void handleMessageReceived(MessageWrapper wrapper){
        MessageEntity entity = new MessageEntity();
        entity.setType(MessageEntity.TYPE_IN);
        entity.setContent(wrapper.getContent());
        entity.setNickname(wrapper.getNickname());
        SimpleDateFormat sf = new SimpleDateFormat("hh:mm", Locale.CHINESE);
        entity.setTime(sf.format(new Date()));
        messages.add(entity);
        adapter.notifyItemInserted(messages.size() - 1);
        mBinding.rvMessage.smoothScrollToPosition(messages.size());
    }

    private void logon() {
        onLogon();
        UserBody body = new UserBody();
        User user = new User();
        user.setNickname(nickname);
        user.setUserCode(MessageService.userCode);
        body.setChangedUser(user);
        body.setEvent(EventDefine.EVENT_USER_LOGON);
        UserWrapper wrapper = new UserWrapper().action(ActionDefine.ACTION_USER_CHANGED);
        wrapper.setBody(body);
        sendSocketMessage(wrapper.getStringMessage());
    }

    private void logout(){
        onLogout();
        UserBody body = new UserBody();
        User user = new User();
        user.setNickname(nickname);
        user.setUserCode(MessageService.userCode);
        body.setChangedUser(user);
        body.setEvent(EventDefine.EVENT_USER_LOGOUT);
        UserWrapper wrapper = new UserWrapper().action(ActionDefine.ACTION_USER_CHANGED);
        wrapper.setBody(body);
        sendSocketMessage(wrapper.getStringMessage());
        mHandler.postDelayed(logoutRunanable, 2000);
    }

    private Handler mHandler = new Handler();
    Runnable logoutRunanable = () -> runOnUiThread(this::onLogoutSuccess);
}
