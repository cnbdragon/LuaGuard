/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package luaguard.traversal;

import org.luaj.vm2.ast.Exp.FieldExp;
import org.luaj.vm2.ast.Exp.IndexExp;
import org.luaj.vm2.ast.Exp.NameExp;
import org.luaj.vm2.ast.FuncName;
import org.luaj.vm2.ast.Visitor;

/**
 * Visits all NameExp nodes in order to construct the name of a function.
 *
 * @author jgs
 */
public class NameVisitor extends Visitor {

    private String name;

    public NameVisitor() {
        name = "";
    }
    
    public String getName() {
        return name;
    }

    @Override
    public void visit(NameExp n) {
        name = n.name.name;
    }

    @Override
    public void visit(FieldExp n) {
        name = n.name.name;
    }

    @Override
    public void visit(IndexExp n) {
    }
    
    public static String funcName(FuncName n) {
        
        // Build name
        String fName = n.name.name;
        if (null != n.method) {
            fName = n.method;
        }
        else if (null != n.dots && n.dots.size() > 0) {
            fName = n.dots.get(n.dots.size()-1).toString();
        }
        
        return fName;
    }

}
