package theakki.synctool.Helper;

/**
 * Utilclass for strings
 * @author theakki
 * @since 0.1
 */
public class StringHelper
{
    /**
     * This function test if a char is an whitespace.
     * @param c Charakter which is to test
     * @return True when tested character is a whitespace.
     */
    static private boolean isWhiteSpace(char c)
    {
        switch(c)
        {
            case '\t':
            case ' ':
                return true;

            default:
                return false;
        }
    }


    /**
     * This Method reduce the whitespaces in a string. No more whitespaces at begin and end.
     * Also there are no whitespace after an other.
     * @param value String which is to reduce.
     * @return Reduced String
     */
    static public String reduceSpaces(String value)
    {
        boolean bSpaceActive = false;
        String strValue = value.trim();

        String result = "";
        for(int i = 0; i < strValue.length(); ++i)
        {
            char cTemp = strValue.charAt(i);
            if(bSpaceActive)
            {
                if(isWhiteSpace(cTemp) == false)
                {
                    result += cTemp;
                    bSpaceActive = false;
                }
            }
            else
            {
                if(isWhiteSpace(cTemp))
                    bSpaceActive = true;
                result += cTemp;
            }
        }
        return result;
    }
}
