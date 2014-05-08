/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */

package luaguard.obfuscator;

import java.util.HashMap;
import java.util.List;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.Exp.NameExp;
import org.luaj.vm2.ast.Exp.VarExp;
import org.luaj.vm2.ast.Name;
import org.luaj.vm2.ast.Stat.Assign;
import org.luaj.vm2.ast.Stat.LocalAssign;

/**
 * Obfuscator to modify the variables
 * @author Hanqing & Will
 */
public class VarRenamerObfu extends Obfuscator{
    //function name dictionary
    private final HashMap<String, String> dict;
    private final String base = "OTOSOTE";//fobfuscator name base
    /**
     * Constructor of the variable renamer obfuscator
     */
    public VarRenamerObfu(){
        dict = new HashMap<>();
    }
    /**
     * If meet a new name than check its name in the hashmap, if it exists,
     * then modify it.
     * @param name ast name node
     */
    @Override
    public void visit(Name name) {
        if(name == null) return;
        String oldName = name.name;
        String tempname = oldName;
        //if the name is in dictionary
        if(dict.containsKey(oldName)){
            tempname = dict.get(oldName);
            name.name = tempname;
        }
        name.variable.name = tempname;
    }
    /**
     * Rename the variable name in local assignment
     * 
     * @param la LocalAssign, the ast node object
     */
    @Override
    public void visit(LocalAssign la){
        //NameResolver nsolver = new NameResolver();
        if(la == null) return;
        //variable
        for(int j = 0; j < la.names.size(); j++){
            
            String oldName = ((Name)(la.names.get(j))).name; 
            //nsolver.resolveNameReference(((Name)(la.names.get(j))));
            
            String tempname;
            //if the name is in dictionary
            if(dict.containsKey(oldName)){
                tempname = dict.get(oldName);
            }else{
                //create new entry
                tempname = base + (dict.size() * 2 + 1);
                //put the new hashmap entry
                dict.put(oldName, tempname);
            }
            ((Name)(la.names.get(j))).name = tempname;
            //Variable v = ((Name)(la.names.get(j))).variable;
           // if(v == null) return;
           // v.name = tempname;
        }
        //check the right side
        if (null != la.values ) {
            // Expressions
            for (int i = 0; i < la.values.size(); i++) {
                ((Exp) la.values.get(i)).accept(this);
            }
        }
    }
    
    /**
     * Rename the assign
     * 
     * @param as Assign, the ast node object for assignment
     */
    
    @Override
    public void visit(Assign as){
        //check empty error
        if(as == null) return;
        // Variables
        List<VarExp> vars = as.vars;
        for(int i = 0; i < vars.size(); i++) {
            String oldName = ((NameExp)(as.vars.get(i))).name.name;
            String tempname;
            if(dict.containsKey(oldName)){
                tempname = dict.get(oldName);
            }else{
                tempname = base + (dict.size()*2+1);
                dict.put(oldName, tempname);
                tempname = oldName;
            }
            ((NameExp)(as.vars.get(i))).name.name = tempname;
            ((VarExp)as.vars.get(i)).accept(this);
        }
        // Expressions
        int numExps = as.exps.size();
        for(int i = 0; i < numExps; i++) {
            /*
            String tempname;
            String oldName = ((NameExp)(as.vars.get(i))).name.name;
            if(dict.containsKey(oldName)){
                tempname = dict.get(oldName);
            }else{
                tempname = oldName;
            }
            ((NameExp)(as.vars.get(i))).name.name = tempname;
            */
            ((Exp)as.exps.get(i)).accept(this);
        }
    }
//    /**
//     * Rename the function call such as animate = function(...);
//     * @param n 
//     */
//    @Override
//    public void visit(FuncCall n){
//        //rename left hand side of the method call
//        if(n == null || n.lhs == null) return;
//        //search from dictionary
//         //search from dictionary
//        if(n.lhs.isfunccall()){
//            String tempname = null;
//            String oldName = ((NameExp)(n.lhs)).name.name;
//            if(dict.containsKey(oldName)){
//            tempname = dict.get(oldName);
//            }else{
//                tempname = oldName;
//            }
//            //rename
//            ((NameExp)(n.lhs)).name.name = tempname;     
//            n.lhs.accept(this);
//            n.args.accept(this);
//        } 
//    }
//    @Override
//    public void visit(Exp.UnopExp n) {
//        if(n.rhs == null) return;
//        
//    }
//    
//    @Override
//    public void visit(NameExp n) {
//        String oldName = n.name.name;     
//        String tempname;
//        //if the name is in dictionary
//        if(dict.containsKey(oldName)){
//            tempname = dict.get(oldName);
//        }else{
//            tempname = oldName;
//        }
//        //put the name into the name list
//        n.name.name = tempname;
//    }
//    
//    /**
//     * rename the variable name and parameter name in method call
//     * @param n MethodCall, the node of ast methodcall
//     */
//    @Override
//    public void visit(MethodCall n) {
//        //rename left hand side of the method call
//        if(n == null || n.lhs == null) return;
//        //search from dictionary
//        String tempname = null;
//        String oldName = ((NameExp)(n.lhs)).name.name;
//        if(dict.containsKey(oldName)){
//            tempname = dict.get(oldName);
//        }else{
//            tempname = oldName;
//        }
//        //rename
//        ((NameExp)(n.lhs)).name.name = tempname;     
//        n.lhs.accept(this);      
//        if(n.args == null) return;
//        n.args.accept(this);
//        
//    }
//    /**
//     * Rename all Expressions
//     */
//    
//    /**
//    * Outputs brackets for an index expression
//    *
//    * @param n Index expression node
//    */
//    @Override
//    public void visit(Exp.IndexExp n) {
//        //search from dictionary
//        String tempname = null;
//        String oldName = ((NameExp)(n.lhs)).name.name;
//        if(dict.containsKey(oldName)){
//            tempname = dict.get(oldName);
//        }else{
//            tempname = oldName;
//        }
//        ((NameExp)(n.lhs)).name.name = tempname;
//        n.lhs.accept(this);
//        n.exp.accept(this);
//    }
//    
    /**
    * rename a field expression
    *
    * @param n Index expression node
    */
//    @Override
//    public void visit(Exp.FieldExp n) {
//        //rename left hand side of the method call
//        if(n == null || n.lhs == null) return;
//        //search from dictionary
//        String tempname;
//        String oldName = ((NameExp)(n.lhs)).name.name;
//        if(dict.containsKey(oldName)){
//            tempname = dict.get(oldName);
//            //System.out.println(tempname);
//        }else{
//            tempname = oldName;
//        }
//        ((NameExp)(n.lhs)).name.name = tempname;
//        n.lhs.accept(this);
//    }
//    /**
//    * rename all the variables as function arguments
//    *
//    * @param n Function arguments node
//    */
//    @Override
//    public void visit(FuncArgs n) {
//        if (null != n.exps) {
//            int numExps = n.exps.size();
//            for(int i = 0; i < numExps; i++) {
//                ((Exp)n.exps.get(i)).accept(this);
//            }
//        }
//    }
    
}
