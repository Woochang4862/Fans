package com.example.jeong_woochang.fans;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class InputPageActivity extends AppCompatActivity {

    EditText pageInput;
    Button btn;
    String page="1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_input_page);

        pageInput=(EditText)findViewById(R.id.Page_input);
        btn=(Button)findViewById(R.id.btn);

        page=getIntent().getStringExtra("pageInfo");
        pageInput.setText(page);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(InputPageActivity.this, MainActivity.class);
                intent.putExtra("pageInfo", pageInput.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
