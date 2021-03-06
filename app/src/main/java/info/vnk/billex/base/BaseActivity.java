package info.vnk.billex.base;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.shawnlin.preferencesmanager.PreferencesManager;

import info.vnk.billex.R;
import info.vnk.billex.Utilities.Constants;
import info.vnk.billex.Utilities.Preferences;

/**
 * Created by Visak on 14/04/17.
 */

public class BaseActivity extends AppCompatActivity {
    protected PreferencesManager preferencesManager;

    protected void setToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //toolbar.setNavigationIcon(R.drawable.ic_action_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check whether any input is added before leaving the page and allow leave
                finish();
            }
        });
    }

    protected PreferencesManager setPreference(Context mContext){
        preferencesManager = Preferences.initSharedPreference(mContext, Constants.sp_login);
        return preferencesManager;
    }
}
