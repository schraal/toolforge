package nl.toolforge.karma.core.expr;

/**
 * <p>Expression class for a valid version number. Defines to which pattern a version number conforms.
 *
 * <p>The following pattern applies to a version : <code>\\d_[\\d_\\d]+</code>. Examples: <code>0_0</code>,
 * <code>1_0_4</code>.
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class VersionExpression implements Expression
{
    public String getPatternString() {
        return "\\d\\-[\\d\\-\\d]+";
    }
}
