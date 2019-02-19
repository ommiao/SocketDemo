package cn.ommiao.socketdemo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;

import com.gyf.barlibrary.ImmersionBar;

import java.util.ArrayList;
import java.util.Random;

import cn.ommiao.socketdemo.adapter.MessageAdapter;
import cn.ommiao.socketdemo.databinding.ActivityChatBinding;
import cn.ommiao.socketdemo.entity.MessageEntity;

public class ChatActivity extends BaseActivity<ActivityChatBinding> {

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
        mBinding.rvMessage.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(messages);
        mBinding.rvMessage.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_toolbar, menu);
        return true;
    }

    @Override
    protected void initDatas() {
        for(int i = 0; i < 10; i++){
            MessageEntity entity = new MessageEntity();
            entity.setNickname(nickname);
            entity.setTime("15:3" + i);
            entity.setContent(randomContent());
            entity.setType(randomType());
            messages.add(entity);
        }
        adapter.notifyDataSetChanged();
    }

    private int randomType(){
        return new java.util.Random().nextBoolean() ? 1 : 0;
    }

    private String randomContent(){
        Random random = new Random();
        int len = random.nextInt(10) + 1;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < len; i++){
            builder.append("我是一条消息。");
        }
        return builder.toString();
    }
}
