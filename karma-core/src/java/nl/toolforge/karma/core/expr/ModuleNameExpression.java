package nl.toolforge.karma.core.expr;

/**
 * <p>Expression class for a module name. Defines to which pattern a module name conforms.
 *
 * <p>The following pattern applies to a module name : <code>[^_\\s\\d\\-]+</code>
 *
 * @author D.A. Smedes
 *
 * @version $Id$
 */
public class ModuleNameExpression implements Expression
{
    public String getPatternString() {
        return "[^_\\s\\d\\-]+";
    }
}
