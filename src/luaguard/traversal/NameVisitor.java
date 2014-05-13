/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */
package luaguard.traversal;

import org.luaj.vm2.ast.Exp.FieldExp;
import org.luaj.vm2.ast.Exp.IndexExp;
import org.luaj.vm2.ast.Exp.NameExp;
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

}
