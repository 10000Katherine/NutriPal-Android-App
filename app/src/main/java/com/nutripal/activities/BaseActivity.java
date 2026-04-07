package com.nutripal.activities;

import android.content.Context;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatActivity;

import com.nutripal.utils.PreferenceManager;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        PreferenceManager preferenceManager = new PreferenceManager(newBase);
        float textScale = preferenceManager.getTextScale();
        if (textScale < 0.85f || textScale > 1.3f) {
            textScale = 1.0f;
        }

        Configuration configuration = new Configuration(newBase.getResources().getConfiguration());
        configuration.fontScale = textScale;

        Context scaledContext = newBase.createConfigurationContext(configuration);
        super.attachBaseContext(scaledContext);
    }
}
