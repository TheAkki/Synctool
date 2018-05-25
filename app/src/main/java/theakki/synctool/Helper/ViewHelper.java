package theakki.synctool.Helper;

import android.widget.Spinner;

/**
 * Util class for handle View Elements
 * @author theakki
 * @since 0.1
 */
public class ViewHelper
{
    /**
     * This Method select an entry in a combo box by name.
     * @param spn ComboBox
     * @param strValue Name of Entry
     */
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
