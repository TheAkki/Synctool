package theakki.synctool.Helper;

import android.widget.Spinner;

public class ViewHelper
{
    public static void selectSpinnerElementByString(Spinner spn, String strValue)
    {
        for(int i = 0; i < spn.getAdapter().getCount(); ++i)
        {
            final String value = (String) spn.getAdapter().getItem(i);
            if(value.equals(strValue))
            {
                spn.setSelection(i, false);
                spn.setPrompt(strValue);
                //spn.invalidate();
                //spn.postInvalidate();
                //spn.
                return;
            }
        }
    }
}
