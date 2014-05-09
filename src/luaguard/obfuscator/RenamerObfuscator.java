/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */

package luaguard.obfuscator;

import java.util.HashMap;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.Exp.AnonFuncDef;
import org.luaj.vm2.ast.Exp.FuncCall;
import org.luaj.vm2.ast.Exp.NameExp;
import org.luaj.vm2.ast.Exp.PrimaryExp;
import org.luaj.vm2.ast.Name;
import org.luaj.vm2.ast.Stat;
import org.luaj.vm2.ast.Stat.FuncDef;
import org.luaj.vm2.ast.Stat.LocalFuncDef;

/**
 *
 * @author Will
 */
public class RenamerObfuscator extends Obfuscator {
    
    Logger logger = LogManager.getLogger("GLOBAL");
    private HashMap<String,String> dict;
    private String base = "OTOSOTE";
    
    RenamerObfuscator(){
        dict = new HashMap<>();
    }
    
    /**
     * Renames function definition
     * @param fd 
     */
    @Override
    public void visit(FuncDef fd){
        String oldname = fd.name.name.name;
        String tempname;
        if(dict.containsKey(oldname)){
            tempname = dict.get(oldname);
        }
        else{
            tempname = base + dict.size();
            dict.put(oldname, tempname);
        }
        fd.name.name.name = tempname;
        fd.body.accept(this);
    }

    /**
     *
     * @param nm
     */
    @Override
    public void visit(Name nm){
            String oldname = nm.name;
            String tempname = oldname;
            if(dict.containsKey(oldname)){
                tempname = dict.get(oldname);
            }
            nm.name = tempname;
    }
    /**
     * Renames local assignment 
     * @param la 
     */
    @Override
    public void visit(Stat.LocalAssign la){
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
                tempname = base + (dict.size());
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
     * Rename the variable name in local assignment
     * 
     * @param stat
     */
    @Override
    public void visit(Stat.Assign stat){
        for(int i =0; i< stat.vars.size(); i++){
            if(stat.vars.get(i).toString().contains("$NameExp")){
                NameExp name = (NameExp) stat.vars.get(i);
                String oldname = name.name.name;
                String tempname;
                if(dict.containsKey(oldname)){
                    tempname = dict.get(oldname);
                }
                else{
                    tempname = oldname;
                    dict.put(oldname,tempname);
                }
                name.name.name = tempname;
            }
        }
        visitVars(stat.vars);
	visitExps(stat.exps);
    }
    
    /**
     * Visits FuncCall and changes name of funccall
     * 
     * @param fc 
     */
    @Override
    public void visit(FuncCall fc){
        for(int i=0; i<fc.args.exps.size(); i++){
            if(fc.args.exps.get(i).getClass().getName().contains("$NameExp")){
                NameExp name = (NameExp) fc.args.exps.get(i);
                String oldname = name.name.name;
                String tempname;
                if(dict.containsKey(oldname)){
                    tempname = dict.get(oldname);
                    name.name.name = tempname;
                }
            }
        }
        fc.lhs.accept(this);
        fc.args.accept(this);
    }
    
    
    /**
     * Renames local function definitions
     *@param stat
     */
    @Override
    public void visit(LocalFuncDef stat){
        String oldname = stat.name.name;
        String tempname;
        if(dict.containsKey(oldname)){
            tempname = dict.get(oldname);
        }
        else{
            tempname = base + dict.size();
            dict.put(oldname, tempname);
        }
        stat.name.name = tempname;
	visit(stat.name);
	stat.body.accept(this);
    }
}
    
