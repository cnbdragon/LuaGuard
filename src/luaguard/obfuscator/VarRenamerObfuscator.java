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
public class VarRenamerObfuscator extends Obfuscator{
    //function name dictionary
    private HashMap<String, String> dict;
    //fobfuscator name base
    private String base = "OTOSOTE";
    /**
     * Constructor of the variable renamer obfuscator
     */
    public VarRenamerObfuscator(){
        dict = new HashMap<String, String>();
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
        String tempname = null;
        //if the name is in dictionary
        if(dict.containsKey(oldName)){
            tempname = dict.get(oldName);
            name.name = tempname;
        }else{
            
        }
        /*if(name.variable != null)
            name.variable.name = tempname;*/
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
            if(as.vars.get(i).getClass().toString().contains("NameExp")){
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
        }
        // Expressions
        int numExps = as.exps.size();
        for(int i = 0; i < numExps; i++) {
            ((Exp)as.exps.get(i)).accept(this);
        }
    }
}
