package angus.planarodenumerics;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setSupportActionBar((Toolbar) findViewById(R.id.help_toolbar));
        ActionBar ab =  getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
