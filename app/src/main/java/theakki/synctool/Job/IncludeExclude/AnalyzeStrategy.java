package theakki.synctool.Job.IncludeExclude;

/**
 * Define the strategy to check if a file match
 * @author theakki
 * @since 0.1
 */
public enum AnalyzeStrategy
{
    /** File Match allways. E.g. "*" */
    FileMatch,      // "*"
    /** Filename match. E.g. "/file.jpg", "file.jpg" */
    FileNameMatch,
    /** Check end of full path. E.g. "*test/file.jpg" */
    PathEndsWith,
    /** Check end of file name. E.g. "*.jpg" */
    FileEndsWith,
    /** Check start of a file name */
    FileStartsWith,
    /** Check start of a path. Ends with a seperator */
    PathStartsWith,

    /** Check for MimeType E.g. "Mime: text/plain" */
    MimeType,
    /** Check for RegEx */
    RegEx,
    /** Not detected Strategy */
    NotKnown,
    /** Not implemented analysis */
    NotImplemented
}
