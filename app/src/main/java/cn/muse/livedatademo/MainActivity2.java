package cn.muse.livedatademo;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * author: wanshi
 * created on: 2019-05-10 15:43
 * description:
 */
public class MainActivity2 extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        LiveDataBus.get()
                .with("act2", String.class)
                .observeSticky(this, s -> Toast.makeText(MainActivity2.this, s, Toast.LENGTH_SHORT).show());
    }

    public void onClick(View view) {
        switch (view.getId())  {
            case R.id.btn_send_to_main: {
                LiveDataBus.get().post("act1", "子页面传来的");
            }
            break;
        }
    }
}
