package luaguard.obfuscator;
import java.util.ArrayList;
import java.util.List;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.ast.Block;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.Exp.BinopExp;
import org.luaj.vm2.ast.Exp.Constant;
import org.luaj.vm2.ast.Exp.NameExp;
import org.luaj.vm2.ast.Stat;
import org.luaj.vm2.ast.Stat.Assign;
import org.luaj.vm2.ast.Stat.WhileDo;
/**
 * Add junk code to the source code
 */
public class JunkStatObfuscator extends Obfuscator{
    @Override
    public void visit(Block b){
        if(b.stats == null) return;


       List<Stat> l = b.stats;
       //add two variables for junk block 
       NameExp var1=new NameExp("_unusual1");
       NameExp var2=new NameExp("_unusual2");
       //build two constant for the two variables
       Constant val1=new Constant(LuaNumber.valueOf(34));
       Constant val2=new Constant(LuaNumber.valueOf(1));
       //define variable list and expriment list for the assignment
       List<NameExp> vars=new ArrayList<NameExp>();
       List<Exp> exps=new ArrayList<Exp>();
       vars.add(var1);
       vars.add(var2);
       exps.add(val1);
       exps.add(val2);
       //build assignment with the two variables has been build
       Assign assigns=new Assign(vars,exps);
       //make an State of WhileDo
       //build an predicate with an variable and constant
       Constant end=new Constant(LuaNumber.valueOf(40));
       Exp predicate=new BinopExp(var1,25,end);
       //build Block in the WhileDo State
       Block InWhile=new Block();
       Exp rsd1=new BinopExp(var1,13,var2);
       Exp rsd2=new BinopExp(var2,13,var2);
       List<Exp> vals=new ArrayList<Exp>();
       vals.add(rsd1);
       vals.add(rsd2);
       Assign ValOp=new Assign(vars,vals);
       InWhile.add(ValOp);
       WhileDo junkWhile=new WhileDo(predicate,InWhile);
       //add the assignments for the two variables 
       b.stats.add(l.size()/3,assigns);
       //add the state of WhileDo 
       b.stats.add(l.size()/2+1,junkWhile);
    }
    
    @Override
    public void visit(Assign assign){
        if(assign == null) return;
        assign.accept(this);
    }	           
}
	    


