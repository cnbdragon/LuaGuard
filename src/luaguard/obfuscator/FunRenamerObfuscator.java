/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */

package luaguard.obfuscator;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
public class FunRenamerObfuscator extends Obfuscator {
    
    Logger logger = LogManager.getLogger("GLOBAL");
    private HashMap<String,String> dict;
    private String base = "OTOSOTE";
    
    FunRenamerObfuscator(){
        dict = new HashMap<>();
    }
    
    FunRenamerObfuscator(List<String> blacklist) {
        dict = new HashMap<>();
        for (String blacklist1  : blacklist) {
            dict.put(blacklist1 , blacklist1 );
        }
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
            else{
                dict.put(oldname,oldname);
            }
            nm.name = tempname;
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
    
