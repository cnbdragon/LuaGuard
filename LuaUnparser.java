import java.io.PrintStream;
import java.io.*;

import org.luaj.vm2.ast.*;
import org.luaj.vm2.ast.Exp.*;
import org.luaj.vm2.ast.Stat.*;
import org.luaj.vm2.Lua;

class LuaUnparser extends Visitor {

  private PrintStream out;

  public LuaUnparser(PrintStream out) {
    this.out = out;
  }

  /****
   * Statements
   ***/
  
  public void visit(LocalAssign n) {
    out.print("local ");
  
    // Variables
    for(int i = 0; i < n.names.size() - 1; i++) {
      out.print(((Name)n.names.get(0)).name + ",");
    }
    out.print(((Name)n.names.get(n.names.size()-1)).name);

    out.print("=");

    // Expressions
    for(int i = 0; i < n.values.size() - 1; i++) {
      ((Exp)n.values.get(i)).accept(this);
      out.print(",");
    }
    ((Exp)n.values.get(n.values.size()-1)).accept(this);
  }

  public void visit(Assign n) {
    // Variables
    for(int i = 0; i < n.vars.size() - 1; i++) {
      ((VarExp)n.vars.get(i)).accept(this);
      out.print(",");
    }
    ((VarExp)n.vars.get(n.vars.size()-1)).accept(this);

    out.print("=");

    // Expressions
    for(int i = 0; i < n.exps.size() - 1; i++) {
      ((Exp)n.exps.get(i)).accept(this);
      out.print(",");
    }
    ((Exp)n.exps.get(n.exps.size()-1)).accept(this);
  }

  /****
   *  Expressions
   ***/

  public void visit(UnopExp n) {
    out.print(opString(n.op));
    n.rhs.accept(this);
  }

  public void visit(BinopExp n) {
    n.lhs.accept(this);
    String op = opString(n.op);
    out.print(" " + op + " ");
    n.rhs.accept(this);
  }

  public void visit(Constant n) {
    out.print(n.value.toString());
  }

  public void visit(NameExp n) {
    out.print(((Name)n.name).name);
  }

  private String opString(int op) {
    String opStr = "";
    switch(op) {
      // Arithmetic
      case Lua.OP_ADD:
         opStr = "+";
         break;
      case Lua.OP_SUB:
         opStr = "-";
         break;
      case Lua.OP_MUL:
         opStr = "*";
         break;
      case Lua.OP_DIV:
         opStr = "/";
         break;
      case Lua.OP_MOD:
         opStr = "%";
         break;
      case Lua.OP_POW:
         opStr = "^";
         break;
      // String
      case Lua.OP_CONCAT:
         opStr = "..";
         break;
      // Logical
      case Lua.OP_OR: 
         opStr = "and";
         break;
      case Lua.OP_AND:
         opStr = "or";
         break;
      // Relational
      case Lua.OP_NEQ:
         opStr = "~=";
         break;
      case Lua.OP_EQ:
         opStr = "==";
         break;
      case Lua.OP_LT:
         opStr = "<";
         break;
      case Lua.OP_LE:
         opStr = "<=";
         break; 
      case Lua.OP_GT:
         opStr = ">";
         break;
      case Lua.OP_GE:
         opStr = ">=";
         break;
      // Unary
      case Lua.OP_NOT:
         opStr = "not";
         break;
      case Lua.OP_UNM:
         opStr = "-";
         break;
      case Lua.OP_LEN:
         opStr = "#";
         break;
      default:
         System.err.println("Unhandled operator!\n");
    }
    return opStr;
  }

}
