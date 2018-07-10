package theakki.synctool;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@Ignore("This test will be ignored: Run not with TreeView")
@RunWith(AndroidJUnit4.class)
public class Wizzard_FolderBrowser_Test
{
    @Rule
    public ActivityTestRule<Wizzard_FolderBrowser> mActivityRule = new ActivityTestRule<>(Wizzard_FolderBrowser.class);

    @Test
    public void findAllViews() throws Exception
    {
    }
}