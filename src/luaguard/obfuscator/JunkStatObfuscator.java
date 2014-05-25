package luaguard.obfuscator;

import java.util.ArrayList;
import java.util.List;

import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.ast.Block;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.FuncArgs;
import org.luaj.vm2.ast.FuncBody;
import org.luaj.vm2.ast.FuncName;
import org.luaj.vm2.ast.Name;
import org.luaj.vm2.ast.ParList;
import org.luaj.vm2.ast.Stat;
import org.luaj.vm2.ast.Exp.BinopExp;
import org.luaj.vm2.ast.Exp.Constant;
import org.luaj.vm2.ast.Exp.FuncCall;
import org.luaj.vm2.ast.Exp.NameExp;
import org.luaj.vm2.ast.Exp.PrimaryExp;
import org.luaj.vm2.ast.Stat.Assign;
import org.luaj.vm2.ast.Stat.FuncCallStat;
import org.luaj.vm2.ast.Stat.FuncDef;
import org.luaj.vm2.ast.Stat.IfThenElse;
import org.luaj.vm2.ast.Stat.Return;
import org.luaj.vm2.ast.Stat.WhileDo;

public class JunkStatObfuscator extends Obfuscator {
	 @Override
	    public void visit(Block b){
	        if(b.stats == null) return;
         

	       List<Stat> l = b.stats;
	       //if( (b.stats.get(0)).toString().equals("org.luaj.vm2.ast.Stat$Assign@ff9053"))
	       System.out.println( ((FuncCall)((((Assign)(b.stats.get(0))).exps.get(0)))).isvarexp());
	       //add function define named addfunc
	   	   Name par=new Name("x");
	       List<Name> pas=new ArrayList<Name>();
	       pas.add(par);
	       ParList pars=new ParList(pas,false);
	       NameExp par1=new NameExp("x");
	       Exp ans=new BinopExp(par1,15,par1);
	       Exp ans1= new BinopExp(ans,13,new Constant(LuaNumber.valueOf(1)));
		   // Exp ans2=new BinopExp((ans1),17,new Constant(LuaNumber.valueOf(7)));
	       List<Exp> returns=new ArrayList<Exp>();
	       returns.add(ans1);
	       Return ret=new Return(returns);
	       FuncName funName=new FuncName("addfunc");
	       Block funcBlock=new Block();
	       funcBlock.add(ret);
	       FuncBody funcbody=new FuncBody(pars,funcBlock);
	       FuncDef newfunc=new FuncDef(funName,funcbody);
	       b.stats.add(2,newfunc);
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
	       IfThenElse ifel=new IfThenElse(exps.get(0),InWhile,null,null, InWhile);
	       WhileDo junkWhile=new WhileDo(predicate,InWhile);
	       //add the assignments for the two variables 
	       if(b.stats.size()>20){
	    	   b.stats.add(l.size()/3,assigns);
	       }
	       //add the state of WhileDo 
	       if(b.stats.size()>30){
	    	   b.stats.add(l.size()/2+1,junkWhile);
	       }
	      //add function call
	       NameExp funcname=new NameExp("addfunc");
	       vars.remove(1);
	       FuncCall funcall=new FuncCall((PrimaryExp)(funcname),new FuncArgs(vars));
	       FuncCallStat h=new FuncCallStat(funcall);
	       exps.remove(0);
	       exps.remove(0);
	       exps.add(funcall);
	       Assign funcallex=new Assign(vars,exps);
	       if(l.size()>30){
	    	   b.stats.add(l.size()*2/3,funcallex);
	       }
	       }
	           
	 @Override
  public void visit(Assign assign){
      if(assign == null) return;
      assign.accept(this);
  }

}
