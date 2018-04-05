package tcking.github.com.giraffeplayer2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.tcking.giraffeplayer2.R;

public class ExitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exit);
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
    }
}
