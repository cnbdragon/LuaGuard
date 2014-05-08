package luaguard.obfuscator;
import java.util.HashMap;
import java.util.List;
import org.luaj.vm2.ast.Block;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.Exp.FuncCall;
import org.luaj.vm2.ast.Exp.MethodCall;
import org.luaj.vm2.ast.Exp.PrimaryExp;
import org.luaj.vm2.ast.FuncArgs;
import org.luaj.vm2.ast.FuncName;
import org.luaj.vm2.ast.Name;
import org.luaj.vm2.ast.Stat;
import org.luaj.vm2.ast.Stat.FuncCallStat;
import org.luaj.vm2.ast.Stat.FuncDef;
import org.luaj.vm2.ast.Stat.LocalFuncDef;

/**
 * For renamer the function name
 * @author Hanqing and Will
 *
 */
public class FunRenamerObfuscator extends Obfuscator{
    //function name dictionary
    private HashMap<String, String> dict;
    private String base = "OTOSOTE";//fobfuscator name base
    /**
     * Constructor of the function renamer obfuscator
     */
    public FunRenamerObfuscator(){
        dict = new HashMap<String, String>();
    }    
    
    /**
     * Override the visit block to modify all function statement inside the block
     * @param n
     */
    @Override
    public void visit(Block b){
        //get the statement list of source code
        if(b.stats == null) return;
        List<Stat> l = b.stats;
        //traverse the statement and change the function name
        for(int i =0; i<l.size(); i++){
            System.out.println(l.get(i).getClass().toString()+" "+l.get(i).beginLine);
            //System.out.print(" " + l.get(i).beginLine + l.get(i).getClass().getName() + "\n");
            //rename the funcion definition
            if(l.get(i).getClass().getName().contains("$FuncDef")){
                FuncDef old = (FuncDef) l.get(i);
                String tempname = null;
                //if the name is in dictionary
                if(dict.containsKey(old.name.name.name)){
                    tempname = dict.get(old.name.name.name);
                }else{
                    //create new entry
                    tempname = base + dict.size() * 2;
                    //put the new hashmap entry
                    dict.put(old.name.name.name, tempname);
                }
                //create new function with new name and the old body
                FuncDef newfun = new FuncDef(new FuncName(tempname), old.body);
                //replace with the new function
                l.set(i, newfun);
            }
            //rename the function call
            if(l.get(i).getClass().getName().contains("$FuncCallStat")){
                FuncCallStat old = (FuncCallStat)l.get(i);
                if(old.funccall.isfunccall()){
                    //System.out.println(old.funccall.getClass().getName());
                    if(old.funccall.lhs.getClass().getName().contains("NameExp")){
                    Exp.NameExp name = (Exp.NameExp)old.funccall.lhs;
                    //if the name in the dictionary
                    if(dict.containsKey(name.name.name)){
                        String newname = dict.get(name.name.name);
                        PrimaryExp newlhs = new Exp.NameExp(newname);
                        FuncArgs args = old.funccall.args;
                        FuncCall fun = new FuncCall(newlhs, args);
                        FuncCallStat newcall = new FuncCallStat(fun);
                        l.set(i, newcall);
                    }else{
                        //doing nothing
                    }
                    }
                    if(old.funccall.getClass().getName().contains("MethodCall")){
                        MethodCall mc = (MethodCall)old.funccall;
                        //System.out.println(mc.name);
                    }
                    
                }
            }
            //rename the funcion definition
            if(l.get(i).getClass().getName().contains("$LocalFuncDef")){
                LocalFuncDef old = (LocalFuncDef)l.get(i);
                System.out.print(l.get(i).getClass().getName());
                String tempname = null;
                //if the name is in dictionary
                if(dict.containsKey(old.name.name)){
                    tempname = dict.get(old.name.name);
                }else{
                    //create new entry
                    tempname = base + dict.size();
                    //put the new hashmap entry
                    dict.put(old.name.name, tempname);
                }
                //create new function with new name and the old body
                LocalFuncDef newfun = new LocalFuncDef(tempname, old.body);
                //replace with the new function
                l.set(i, newfun);
            }
            
            //recursive
            ((Stat)b.stats.get(i)).accept(this);

        }
    }
}
