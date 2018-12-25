package edit.qiyue.com.edittextview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import edit.qiyue.com.editlib.ContainsEmojiEditText;
import edit.qiyue.com.editlib.EditTextWithDelete;
import edit.qiyue.com.editlib.SpaceEditText;

public class MainActivity extends AppCompatActivity {
    private ContainsEmojiEditText mContainsEmojiEditText;
    private EditTextWithDelete mEditTextWithDelete;
    private SpaceEditText mSpaceEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContainsEmojiEditText = (ContainsEmojiEditText) findViewById(R.id.containsEmojiEditText);
        mEditTextWithDelete = (EditTextWithDelete) findViewById(R.id.editTextWithDelete);
        mSpaceEditText = (SpaceEditText) findViewById(R.id.spaceEditText);

        //mSpaceEditText.setDeleteIconShow(false);
        mSpaceEditText.setTextChangeListener(new SpaceEditText.TextChangeListener() {
            @Override
            public void textChange(String text) {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
