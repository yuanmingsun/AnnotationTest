package com.seven.annotationtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.seven.annotation.BindView;
import com.seven.api.InjectHelper;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv)
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InjectHelper.Inject(this);
        tv.setText("zhujie");
    }
}
