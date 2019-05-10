package cn.muse.livedatademo;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * author: wanshi
 * created on: 2019-05-10 12:24
 * description:
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LiveDataBus.get()
                .with("act1", String.class)
                .observe(this, s -> {
                    Toast.makeText(MainActivity.this, s, LENGTH_SHORT).show();
                    Log.e("LiveDataBus", s);
                });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_to_next_act:
                startActivity(new Intent(this, MainActivity2.class));
                break;
            case R.id.btn_send_to_sub:
                LiveDataBus.get().post("act2", "粘性事件");
            break;
            case R.id.btn_sub_thread_send: {
                new Thread(() -> LiveDataBus.get().post("act1", "子线程给自己发消息")).start();
            }
            break;
            default:
        }
    }
}
