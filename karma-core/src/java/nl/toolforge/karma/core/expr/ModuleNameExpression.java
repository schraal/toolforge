package nl.toolforge.karma.core.expr;

/**
 * <p>Expression class for a module name. Defines to which pattern a module name conforms.
 *
 * <p>Refer to {@link #getPatternString} to chechk the pattern for a module name.
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
