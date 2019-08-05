package io.github.iamyours.flingx;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView img1 = findViewById(R.id.img1);
        ImageView img2 = findViewById(R.id.img2);
        ImageView img3 = findViewById(R.id.img3);
        ImageView img_tab = findViewById(R.id.img_tab);
        Glide.with(img1).load(R.drawable.img1).into(img1);
        Glide.with(img2).load(R.drawable.img2).into(img2);
        Glide.with(img3).load(R.drawable.img3).into(img3);
        Glide.with(img_tab).load(R.drawable.tab).into(img_tab);
    }
}
