/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */

package luaguard.obfuscator;

import java.util.HashMap;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.luaj.vm2.ast.Exp.FuncCall;
import org.luaj.vm2.ast.Exp.NameExp;
import org.luaj.vm2.ast.Name;
import org.luaj.vm2.ast.Stat;
import org.luaj.vm2.ast.Stat.FuncDef;
import org.luaj.vm2.ast.Stat.LocalFuncDef;

/**
 *
 * @author Will
 */
public class FunRenamerObfuscator extends Obfuscator {
    
    Logger logger = LogManager.getLogger("GLOBAL");
    private HashMap<String,String> dict;
    private String base = "OTOSOTE";
    
    FunRenamerObfuscator(){
        dict = new HashMap<>();
    }
    
       /**
     * Rename the variable name in local assignment
     * 
     * @param la LocalAssign, the ast node object
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
                    tempname = base + dict.size()*2;
                    dict.put(oldname,tempname);
                }
                name.name.name = tempname;
            }
        }
        visitVars(stat.vars);
	visitExps(stat.exps);
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
            tempname = base + dict.size()*2;
            dict.put(oldname, tempname);
        }
        fd.name.name.name = tempname;
        fd.body.accept(this);
    }

    /**
     * Renames names that have already been defined
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
     * Renames function calls
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
     * Renames Local Function Definitions
     * @param stat 
     */
    @Override
    public void visit(LocalFuncDef stat){
        String oldname = stat.name.name;
        String tempname;
        if(dict.containsKey(oldname)){
            tempname = dict.get(oldname);
        }
        else{
            tempname = base + dict.size()*2;
            dict.put(oldname, tempname);
        }
        stat.name.name = tempname;
	visit(stat.name);
	stat.body.accept(this);
    }
}
    
