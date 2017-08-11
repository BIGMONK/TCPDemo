package jason.tcpdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import jason.tcpdemo.funcs.ActivityFuncTcpClient;
import jason.tcpdemo.funcs.ActivityFuncTcpServer;
import jason.tcpdemo.mina.ActivityClientMina;
import jason.tcpdemo.netty.ActivityNettyClient;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    @BindView(R.id.radio_Server)
    RadioButton radioBtnServer;
    @BindView(R.id.radio_Client)
    RadioButton radioBtnClient;
    @BindView(R.id.radio_Client_mina)
    RadioButton radioBtnClientMina;
    @BindView(R.id.radio_Client_netty)
    RadioButton radioBtnClientNetty;
    @BindView(R.id.txt_ShowFunction)
    TextView txtShowFunc;
    @BindView(R.id.btn_FunctionEnsure)
    Button btnFuncEnsure;
    @BindView(R.id.btn_ping)
    Button btnPing;
    @BindView(R.id.tv_ping_receive)
    TextView tvPingReceive;
    @BindView(R.id.et_ip)
    EditText etIp;

    @OnClick(R.id.btn_FunctionEnsure)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_FunctionEnsure:
                if (radioBtnServer.isChecked()) {
                    startActivity(new Intent(MainActivity.this, ActivityFuncTcpServer.class));
                } else if (radioBtnClient.isChecked()) {
                    startActivity(new Intent(MainActivity.this, ActivityFuncTcpClient.class));
                } else if (radioBtnClientMina.isChecked()) {
                    startActivity(new Intent(MainActivity.this, ActivityClientMina.class));
                } else if (radioBtnClientNetty.isChecked()) {
                    startActivity(new Intent(MainActivity.this, ActivityNettyClient.class));
                }
                break;
        }
    }

    @OnCheckedChanged(R.id.radio_Server)
    public void onCheckedChangedServer(boolean b) {
        if (b) {
            txtShowFunc.setText("你选则的功能是：服务器");
        }
    }

    @OnCheckedChanged(R.id.radio_Client)
    public void onCheckedChangedClient(boolean b) {
        if (b) {
            txtShowFunc.setText("你选则的功能是：客户端");
        }
    }

    @OnCheckedChanged(R.id.radio_Client_mina)
    public void onCheckedChangedClientMina(boolean b) {
        if (b) {
            txtShowFunc.setText("你选则的功能是：客户端(Mina)");
        }
    }

    @OnCheckedChanged(R.id.radio_Client_netty)
    public void onCheckedChangedClientNetty(boolean b) {
        if (b) {
            txtShowFunc.setText("你选则的功能是：客户端(Netty)");
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }
     String ip;
    @OnClick(R.id.btn_ping)
    public void onViewClicked() {
       ip= etIp.getText().toString().trim();
        if (TextUtils.isEmpty(ip)){
            Toast.makeText(this,"ip错误",Toast.LENGTH_SHORT).show();
        }
        new Thread(new Runnable() {
            private Process mProcess;
            @Override
            public void run() {
                try {
                    // 其中 -c 1为发送的次数，-w 表示发送后等待响应的时间
                    mProcess = Runtime.getRuntime().exec("ping -c 2 -w 3 "+ip);
                    int result = mProcess.waitFor();
                    byte[] src = new byte[2048];
                    int c = mProcess.getInputStream().read(src);
                    byte[] des = new byte[c];
                    System.arraycopy(src, 0, des, 0, c);
                    final String sss = "Ping"+ip+"返回值为：\n" + result + " \n返回内容：\n" + new String(des);
                    Log.e(TAG, sss);
                    tvPingReceive.post(new Runnable() {
                        @Override
                        public void run() {
                            tvPingReceive.setText(sss);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
