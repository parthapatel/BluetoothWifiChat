package info.devexchanges.bluetoothchatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class WifiSelectorActivity extends AppCompatActivity implements View.OnClickListener {
    private Button text, photo, audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        text = (Button) findViewById(R.id.send_text);
        photo = (Button) findViewById(R.id.send_photo);
        audio = (Button) findViewById(R.id.send_audio);

        text.setOnClickListener(this);
        photo.setOnClickListener(this);
        audio.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.send_text:
                intent = new Intent(this, WiFiDirectActivity.class);
                break;
            case R.id.send_photo:
                break;
            case R.id.send_audio:
                break;
        }

        if (intent != null) {
            startActivity(intent);
        }
    }
}
