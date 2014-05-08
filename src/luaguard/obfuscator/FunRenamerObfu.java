/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */

package luaguard.obfuscator;

import java.util.HashMap;
import java.util.List;
import org.luaj.vm2.ast.Name;
import org.luaj.vm2.ast.Stat.FuncDef;
import org.luaj.vm2.ast.Stat.LocalFuncDef;

/**
 *
 * @author Will
 */
public class FunRenamerObfu extends Obfuscator {
    
    private HashMap<String,String> dict;
    private String base = "OTOSOTE";
    
    FunRenamerObfu(){
        dict = new HashMap<>();
    }
    
    @Override
    public void visit (FuncDef fd){
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
    
    @Override
    public void visitNames(List names){
        for(int i=0; i<names.size(); i++){
            Name nm;
            nm = (Name) names.get(i);
            String oldname = nm.name;
            String tempname = oldname;
            if(dict.containsKey(oldname)){
                tempname = dict.get(oldname);
            }
            nm.name = tempname;
            visit((Name) nm);
        }
    }
//    @Override
//    public void visit(LocalFuncDef fd){
//        String oldname = fd.name.name;
//        String tempname = null;
//        String oldvar = fd.name.variable.name;
//        System.out.println("Name " +oldname);
//        System.out.println("var  "+ oldvar);
//        fd.body.accept(this);
//    }
    
}
