package com.xns.pluginapkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_mdl_a).setOnClickListener(this);
        findViewById(R.id.btn_mdl_b).setOnClickListener(this);
        TextView tvShow = (TextView) findViewById(R.id.tv_show);
        StringBuilder sb = new StringBuilder();
        tvShow.setText(sb.toString());
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.btn_mdl_a:
//                Class<Activity> aClass = null;
//                try {
//                    aClass = (Class<Activity>) Class.forName("me.xms.module_a.AActivity");
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                    Toast.makeText(this, "me.xms.module_a.AActivity not find", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                Intent intenta = new Intent();
                intenta.setClassName(this, "com.xns.module.a.AActivity");
                startActivity(intenta);
                break;
            case R.id.btn_mdl_b:
//                Class<Activity> bClass = null;
//                try {
//                    bClass = (Class<Activity>) Class.forName("me.xms.module_b.BActivity");
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                    Toast.makeText(this, "me.xms.module_b.BActivity not find", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                Intent intentb = new Intent();
                intentb.setClassName(this, "com.xns.module.b.BActivity");
                startActivity(intentb);
                break;
        }
    }
}
