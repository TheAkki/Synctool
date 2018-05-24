package theakki.synctool.Job.Settings;

/**
 * This is a helper class to handle android settings
 * @author theakki
 * @since 0.1
 */
public class SettingsHelper
{
    /**
     * Convert a string with a {@link SyncDirection} into enumeration. Conversion is case insensitive.
     * @param direction String with Direction
     * @param throwEx Throw exception when not able to convert
     * @param def Return value when throwEx is false and conversion not possible.
     * @return Converted SyncDirection
     */
    public static SyncDirection SyncDirectionFromString(String direction, boolean throwEx, SyncDirection def)
    {
        final String Booth = SyncDirection.Booth.toString();
        final String ToA = SyncDirection.ToA.toString();
        final String ToB = SyncDirection.ToB.toString();

        if(direction.compareToIgnoreCase(Booth) == 0)
            return SyncDirection.Booth;
        else if(direction.compareToIgnoreCase(ToA) == 0)
            return SyncDirection.ToA;
        else if(direction.compareToIgnoreCase(ToB) == 0)
            return  SyncDirection.ToB;
        else
        {
            if(throwEx)
                throw new IllegalArgumentException("Direction '" + direction + "' is invalid");
            else
                return def;
        }
    }


    /**
     * Convert a string with a {@link OneWayStrategy} into enumeration. Conversion is case insensitive.
     * @param strategy String with Strategy
     * @param throwEx throw exception when not able to convert
     * @param def Return value when throwEx is false and conversion not possible.
     * @return Converted OneWayStrategy
     */
    public static OneWayStrategy OneWayStrategyFromString(String strategy, boolean throwEx, OneWayStrategy def)
    {
        final String Standard = OneWayStrategy.Standard.toString();
        final String Mirror = OneWayStrategy.Mirror.toString();
        final String DateNew = OneWayStrategy.NewFilesInDateFolder.toString();
        final String DateAll = OneWayStrategy.AllFilesInDateFolder.toString();

        if(strategy.compareToIgnoreCase(Standard) == 0)
            return OneWayStrategy.Standard;
        else if(strategy.compareToIgnoreCase(Mirror) == 0)
            return OneWayStrategy.Mirror;
        else if(strategy.compareToIgnoreCase(DateNew) == 0)
            return OneWayStrategy.NewFilesInDateFolder;
        else if (strategy.compareToIgnoreCase(DateAll) == 0)
            return OneWayStrategy.AllFilesInDateFolder;
        else
        {
            if(throwEx)
                throw new IllegalArgumentException("Strategy '" + strategy + "' is invalid");
            else
                return def;
        }
    }


    /**
     * Convert a string with a {@link TwoWayStrategy} into enumeration. Conversion is case insensitive.
     * @param strategy String with Strategy
     * @param throwEx throw exception when not able to convert
     * @param def Return value when throwEx is false and conversion not possible.
     * @return Converted TwoWayStrategy
     */
    public static TwoWayStrategy TwoWayStrategyFromString(String strategy , boolean throwEx, TwoWayStrategy def)
    {
        final String A = TwoWayStrategy.AWins.toString();
        final String B = TwoWayStrategy.BWins.toString();

        if(strategy.compareToIgnoreCase(A) == 0)
            return TwoWayStrategy.AWins;
        else if(strategy.compareToIgnoreCase(B) == 0)
            return TwoWayStrategy.BWins;
        else
        {
            if(throwEx)
                throw new IllegalArgumentException("Strategy '" + strategy + "' is invalid");
            else
                return def;
        }
    }
}
