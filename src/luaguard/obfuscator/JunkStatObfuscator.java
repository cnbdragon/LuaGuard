/*
 * Copyright 2014 Yafei Yao, Jianbin Feng, Jinke Peng.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package luaguard.obfuscator;

import java.util.ArrayList;
import java.util.List;
import org.luaj.vm2.Lua;
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
    public void visit(Block b) {
        if (b.stats == null) {
            return;
        }

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
       
       //build an predicate with an variable and constant
       Constant end=new Constant(LuaNumber.valueOf(40));
       Exp predicate=new BinopExp(var1,Lua.OP_LT,end);
       
       //build and add Block in the WhileDo State
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
       
       //add WhileDo state 
       b.stats.add(l.size()/2+1,junkWhile);
    }

}
	    


