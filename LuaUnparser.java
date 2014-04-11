import java.io.PrintStream;
import java.io.*;

import org.luaj.vm2.ast.*;
import org.luaj.vm2.ast.Exp.*;
import org.luaj.vm2.ast.Stat.*;
import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaString;

class LuaUnparser extends Visitor {

  private PrintStream out;
  private int indentLevel;

  public LuaUnparser(PrintStream out) {
    this.out = out;
    this.indentLevel = 0;
  }

  /**
   * Visits each statement outputing line terminator, i.e. semi-colons
   *
   * @param n Block node
   */
  public void visit(Block n) {
    for(int i = 0; i < n.stats.size() - 1; i++) {
      ((Stat)n.stats.get(i)).accept(this);
      out.print(";\n");
    }
    ((Stat)n.stats.get(n.stats.size() - 1)).accept(this);
  }

  /****
   * Statements
   ***/

  /**
   * Outputs a local variable assignment.
   *
   * @param n Local assignment statement node
   */
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

  /**
   * Outputs variable assignment.
   *
   * @param n Assignment statement node
   */
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

  /**
   * Outputs a unary expression.
   *
   * @param n Unary expression node
   */
  public void visit(UnopExp n) {
    out.print(opString(n.op));
    n.rhs.accept(this);
  }

  /**
   * Outputs a binary expression
   *
   * @param n Binary expression node
   */
  public void visit(BinopExp n) {
    n.lhs.accept(this);
    String op = opString(n.op);
    out.print(" " + op + " ");
    n.rhs.accept(this);
  }

  /**
   * Outputs the variable name referred by the node.
   *
   * @param n Node that contains a variable name
   */
  public void visit(NameExp n) {
    out.print(((Name)n.name).name);
  }

  /**
   * Outputs a vararg, "..."
   *
   * @param n Node that indicates it's a vararg
   */
  public void visit(VarargsExp n) {
    out.print("...");
  }

  /**
   * Outputs constant values.
   *
   * @param n Constant AST node
   */
  public void visit(Constant n) {
    if (n.value.typename() == "string") {
      out.print("'" + unescape(n.value.toString()) + "'");
    }
    else {
      out.print(n.value.toString());
    }
  }

  /**
   * Outputs if-else condition/statement blocks.
   *
   * @param n If-else node
   */
  public void visit(IfThenElse n) {
    out.print("if ");
    n.ifexp.accept(this);

    out.print(" then\n");
    n.ifblock.accept(this);
    out.print("\n");

    for(int i = 0; i < n.elseifexps.size(); i++) {
      out.print("elseif ");
      ((Exp)n.elseifexps.get(i)).accept(this);
      out.print(" then\n");
      ((Block)n.elseifblocks.get(i)).accept(this);
      out.print("\n");
    }
    out.print("else\n");
    out.print("end\n");
  }

  /**
   * Converts integer value of operators to the string representation.
   *
   * @param op integer value of operator tokens
   * @return string representation of the operator
   */
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

  /**
   * Takes input string and produces a string with all escape characters unescaped.
   *
   * @param s String to unescape
   */
  public static String unescape(String s) {
    StringBuilder sb = new StringBuilder();
    char[] c = s.toCharArray();
    int n = c.length;
    for ( int i=0; i<n; i++ ) {
      switch ( c[i] ) {
        case 7:  sb.append( "\\a" );    continue;
        case '\b':  sb.append( "\\b" ); continue;
        case '\f':  sb.append( "\\f" ); continue;
        case '\n':  sb.append( "\\n" ); continue;
        case '\r':  sb.append( "\\r" ); continue;
        case '\t':  sb.append( "\\t" ); continue;
        case 11:    sb.append( "\\v" );   continue;
        case '"':   sb.append( '"' );  continue;
        case '\'':  sb.append( "\\'" ); continue;
        case '\\':  sb.append( "\\\\" ); continue;
        default: sb.append( c[i] ); break;
      }
    }
    return sb.toString();
  }
}
