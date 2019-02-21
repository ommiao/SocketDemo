package cn.ommiao.socketdemo.socket.handler;


import android.os.Handler;
import android.os.Looper;

import cn.ommiao.socketdemo.socket.client.Client;
import cn.ommiao.socketdemo.socket.message.MessageBase;


/**
 * description:
 * Created by WJD on 2018/1/20.
 */

public abstract class ThreadSafeMessageHandler extends BaseMessageHandler{
    private Handler handler = new Handler(Looper.getMainLooper());

    /**
     * handle the message.
     * @param client the Socket Client associated to this handler.
     * @param received the Message to handle.
     */
    public void handleMessage(final Client client, final MessageBase received){
        handler.post(new Runnable() {
            @Override
            public void run() {
                handleMessageOnUiThread(client,received);
            }
        });
    }

    /**
     * handle the message on UI Thread.
     * @param client the Socket Client associated to this handler.
     * @param received the Message to handle.
     */
    protected abstract void handleMessageOnUiThread(Client client, MessageBase received);

}
