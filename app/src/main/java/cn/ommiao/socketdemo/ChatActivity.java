package cn.ommiao.socketdemo;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gyf.barlibrary.ImmersionBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import cn.ommiao.socketdemo.adapter.MessageAdapter;
import cn.ommiao.socketdemo.databinding.ActivityChatBinding;
import cn.ommiao.socketdemo.entity.MessageEntity;
import cn.ommiao.socketdemo.other.ScrollLinearLayoutManager;
import cn.ommiao.socketdemo.utils.ToastUtil;

public class ChatActivity extends BaseActivity<ActivityChatBinding> implements TextWatcher, View.OnClickListener {

    private String nickname;

    private MessageAdapter adapter;
    private ArrayList<MessageEntity> messages = new ArrayList<>();

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
        mBinding.rvMessage.setAdapter(adapter);
        mBinding.etMsg.addTextChangedListener(this);
        mBinding.btnSend.setOnClickListener(this);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.toolbar_people:
                ToastUtil.show(R.string.chat_toolbar_people);
                break;
            case R.id.toolbar_exit:
                ToastUtil.show(R.string.chat_toolbar_exit);
                break;
        }
        return true;
    }

    @Override
    protected void initDatas() {

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
        //addVirtualIn();
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

}
