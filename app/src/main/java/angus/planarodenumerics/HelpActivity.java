package angus.planarodenumerics;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setSupportActionBar((Toolbar) findViewById(R.id.help_toolbar));
        ActionBar ab =  getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //textView.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
    }
}
