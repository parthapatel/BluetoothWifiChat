package info.devexchanges.bluetoothchatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FirstActivity extends AppCompatActivity {

    Button bluetooth, bluetooth2, wifidirect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        //Init
        bluetooth = (Button)findViewById(R.id.bluetooth);
        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FirstActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
        bluetooth2 = (Button)findViewById(R.id.bluetooth2);
        bluetooth2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FirstActivity.this, MainActivityPhoto.class);
                startActivity(i);
            }
        });

        wifidirect = (Button)findViewById(R.id.wifidirect);
        wifidirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FirstActivity.this, WiFiDirectActivity.class);
                startActivity(i);
            }
        });

    }
}
