package jason.tcpdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import jason.tcpdemo.mina.ActivityClientMina;
import jason.tcpdemo.funcs.ActivityFuncTcpClient;
import jason.tcpdemo.funcs.ActivityFuncTcpServer;
import jason.tcpdemo.netty.NettyClientActivity;

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
                    startActivity(new Intent(MainActivity.this, NettyClientActivity.class));
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

}
