package com.prasad.helpactivities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.headeractionbar.R;

public class TransparentActivity extends Activity {

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_transparent);
    Button b=(Button)findViewById(R.id.button1);
    b.setOnClickListener(new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Toast.makeText(TransparentActivity.this, "You are ready to start", Toast.LENGTH_SHORT).show();
            finish();
        }
    });
}
}