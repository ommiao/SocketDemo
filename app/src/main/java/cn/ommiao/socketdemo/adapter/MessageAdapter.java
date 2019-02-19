package cn.ommiao.socketdemo.adapter;


import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import cn.ommiao.socketdemo.R;
import cn.ommiao.socketdemo.entity.MessageEntity;

public class MessageAdapter extends BaseMultiItemQuickAdapter<MessageEntity, BaseViewHolder> {

    public MessageAdapter(List<MessageEntity> data) {
        super(data);
        addItemType(MessageEntity.TYPE_OUT, R.layout.item_message_out);
        addItemType(MessageEntity.TYPE_IN, R.layout.item_message_in);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageEntity item) {
        helper.setText(R.id.tv_msg, item.getContent());
        helper.setText(R.id.tv_nickname, item.getNickname());
        helper.setText(R.id.tv_time, item.getTime());
    }
}
