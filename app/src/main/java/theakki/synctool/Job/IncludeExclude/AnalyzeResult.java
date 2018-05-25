package theakki.synctool.Job.IncludeExclude;

/**
 * Class to store the parameter to check if a path or file match
 * @author theakki
 * @since 0.1
 */
public class AnalyzeResult
{
    /**
     * Constructor
     * @param strategy  Strategy witch is to store
     * @param parameter Parameter which is to Store
     */
    public AnalyzeResult(AnalyzeStrategy strategy, String parameter)
    {
        this.Strategy = strategy;
        this.Parameter = parameter;
    }


    /**
     * Constructor. Set Strategy to NotKnown
     */
    public AnalyzeResult()
    {
        Strategy = AnalyzeStrategy.NotKnown;
        Parameter = "";
    }

    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof AnalyzeResult)
        {
            AnalyzeResult o = (AnalyzeResult)obj;
            if(Parameter.equals(o.Parameter) == false)
                return false;
            if(Strategy != o.Strategy)
                return false;

            return true;
        }
        return false;
    }

    public AnalyzeStrategy Strategy;
    public String Parameter;
}
