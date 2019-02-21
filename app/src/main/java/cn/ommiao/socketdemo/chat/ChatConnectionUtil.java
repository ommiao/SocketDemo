package cn.ommiao.socketdemo.chat;

import com.orhanobut.logger.Logger;

import cn.ommiao.socketdemo.BaseActivity;
import cn.ommiao.socketdemo.socket.client.Client;
import cn.ommiao.socketdemo.socket.client.ConnectionManager;
import cn.ommiao.socketdemo.utils.ToastUtil;

public class ChatConnectionUtil {

    private static ChatConnectionUtil chatConnectionUtil;
    private BaseActivity context;
    private ConnectionManager connectionManager;
    private Client client;

    private ChatConnectionUtil(BaseActivity context){
        this.context = context;
    }

    public static ChatConnectionUtil getInstance(BaseActivity context){
        if(chatConnectionUtil == null){
            chatConnectionUtil = new ChatConnectionUtil(context);
        }
        return chatConnectionUtil;
    }

    public void tryConnect(ChatConnectionListener listener){
        if(connectionManager != null){
            connectionManager.disconnect();
        }
        try {
            connectionManager = ConnectionManager.newConnection();
        } catch (Exception e) {
            ToastUtil.show("ConnectionManager create fail.");
            e.printStackTrace();
        }
        connectionManager.connect(new ConnectionManager.OnSocketStatusListener() {
            @Override
            public void onConnected(Client client) {
                Logger.d("ConnectionManager onConnected.");
            }

            @Override
            public void onConnectFail(int errorCode, String error) {
                Logger.d("ConnectionManager onConnectFail.");
            }

            @Override
            public void onReconnecting(int retryTimes) {
                Logger.d("ConnectionManager onReconnecting.");
            }

            @Override
            public void onDisconnected() {
                Logger.d("ConnectionManager onDisconnected.");
            }

            @Override
            public void onReconnected(Client client) {
                Logger.d("ConnectionManager onReconnected.");
            }

            @Override
            public void onReconnectFail() {
                Logger.d("ConnectionManager onReconnectFail.");
            }
        });
    }

    public interface ChatConnectionListener{
        void onConnected();
        void onConnectFail();
    }

}
