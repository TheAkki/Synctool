package theakki.synctool;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class Wizzard_NewOwnCloudConnection_Test
{
    @Rule
    public ActivityTestRule<Wizzard_NewOwnCloudConnection> mActivityRule = new ActivityTestRule<>(Wizzard_NewOwnCloudConnection.class);

    @Test
    public void findAllViews() throws Exception
    {
    }
}