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

  public void visit(BinopExp n) {
    n.lhs.accept(this);
    String op = "";
    switch(n.op) {
      // Arithmetic
      case Lua.OP_ADD:
         op = "+";
         break;
      case Lua.OP_SUB:
         op = "-";
         break;
      case Lua.OP_MUL:
         op = "*";
         break;
      case Lua.OP_DIV:
         op = "/";
         break;
      case Lua.OP_MOD:
         op = "%";
         break;
      case Lua.OP_POW:
         op = "^";
         break;
      // String
      case Lua.OP_CONCAT:
         op = "..";
         break;
      // Logical
      case Lua.OP_OR: 
         op = "and";
         break;
      case Lua.OP_AND:
         op = "or";
         break;
      // Relational
      case Lua.OP_NEQ:
         op = "~=";
         break;
      case Lua.OP_EQ:
         op = "==";
         break;
      case Lua.OP_LT:
         op = "<";
         break;
      case Lua.OP_LE:
         op = "<=";
         break; 
      case Lua.OP_GT:
         op = ">";
         break;
      case Lua.OP_GE:
         op = ">=";
         break;
      // Unary
      case Lua.OP_NOT:
         op = "not";
         break;
      case Lua.OP_UNM:
         op = "-";
         break;
      case Lua.OP_LEN:
         op = "#";
         break;
      default:
         System.err.println("Unhandled operator!\n");
    }
    out.print(" " + op + " ");
    n.rhs.accept(this);
  }

  public void visit(Constant n) {
    out.print(n.value.toString());
  }

  public void visit(NameExp n) {
    out.print(((Name)n.name).name);
  }
}
