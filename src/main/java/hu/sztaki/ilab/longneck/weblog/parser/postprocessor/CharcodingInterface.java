package hu.sztaki.ilab.longneck.weblog.parser.postprocessor;

/**
 * Interface to manage (set, get) character encoding.
 * Classes requiring character encoding must implement this interface.
 * Characterset2 is a second Charset, to be used for mixed encodings.
 *
 * @author Gábor Lukács
 */
public interface CharcodingInterface {

    public void setCharset(String userdefined_charset);
    public void setCharset2(String userdefined_charset);
    public String getCharset();
    public String getCharset2();

}
