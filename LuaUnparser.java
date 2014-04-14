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
    int numStats = n.stats.size();
    for(int i = 0; i < numStats - 1; i++) {
      ((Stat)n.stats.get(i)).accept(this);
      out.print("\n");
    }
    ((Stat)n.stats.get(numStats - 1)).accept(this);
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
    int numNames = n.names.size();
    for(int i = 0; i < numNames - 1; i++) {
      out.print(((Name)n.names.get(0)).name + ",");
    }
    out.print(((Name)n.names.get(numNames - 1)).name);

    out.print("=");

    // Expressions
    int numVals = n.values.size();
    for(int i = 0; i < numVals - 1; i++) {
      ((Exp)n.values.get(i)).accept(this);
      out.print(",");
    }
    ((Exp)n.values.get(numVals - 1)).accept(this);
  }

  /**
   * Outputs variable assignment.
   *
   * @param n Assignment statement node
   */
  public void visit(Assign n) {

    // Variables
    int numVars = n.vars.size();
    for(int i = 0; i < numVars - 1; i++) {
      ((VarExp)n.vars.get(i)).accept(this);
      out.print(",");
    }
    ((VarExp)n.vars.get(numVars - 1)).accept(this);

    out.print("=");

    // Expressions
    int numExps = n.exps.size();
    for(int i = 0; i < numExps - 1; i++) {
      ((Exp)n.exps.get(i)).accept(this);
      out.print(",");
    }
    ((Exp)n.exps.get(numExps - 1)).accept(this);
  }
  
  /**
   * Outputs if-else condition/statement blocks.
   *
   * @param n If-else node
   */
  public void visit(IfThenElse n) {

    // if-then condition/block
    out.print("if ");
    n.ifexp.accept(this);
    out.print(" then\n");
    n.ifblock.accept(this);
    out.print("\n");

    // else-if conditions/blocks
    if(null != n.elseifexps) {
      for(int i = 0; i < n.elseifexps.size(); i++) {
        out.print("elseif ");
        ((Exp)n.elseifexps.get(i)).accept(this);
        out.print(" then\n");
        ((Block)n.elseifblocks.get(i)).accept(this);
        out.print("\n");
      }
    }

    // else block
    if(null != n.elseblock) {
      out.print("else\n");
      n.elseblock.accept(this);
      out.print("\n");
    }
    out.print("end");
  }

  /**
   * Outputs while loop
   *
   * @param n WhileDo node
   */
  public void visit(WhileDo n) {
    
    // condition
    out.print("while ");
    n.exp.accept(this);
    out.print(" do\n");

    // block
    n.block.accept(this);
    out.print("\n");
    out.print("end");
  }

  /**
   * Outputs repeat until loop
   *
   * @param n RepeatUntil node
   */
  public void visit(RepeatUntil n) {
    
    // block
    out.print("repeat\n");
    n.block.accept(this);

    // condition
    out.print("\n");
    out.print("until ");
    n.exp.accept(this);

  }

  /**
   * Outputs numeric for loop
   *
   * @param n NumericFor node
   */
  public void visit(NumericFor n) {

    // Name
    out.print("for ");
    out.print(n.name.name);
    out.print("=");

    // expression
    n.initial.accept(this);
    out.print(",");
    n.limit.accept(this);

    if (null != n.step) {
      out.print(",");
      n.step.accept(this);
    }

    // block
    out.print(" do\n");
    n.block.accept(this);
    out.print("\n");
    out.print("end");

  }

  /**
   * Outputs goto statement
   *
   * @param n Goto node
   */
  public void visit(Goto n) {
    out.print("goto ");
    out.print(n.name);
  }

  /**
   * Outputs label
   *
   * @param n Label node
   */
  public void visit(Label n) {
    out.print("::");
    out.print(n.name);
    out.print("::");
  }

  /**
   * Outputs break statement
   *
   * @param n Break node
   */
  public void visit(Break n) {
    out.print("break");
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
    out.print(n.name.name);
  }

  /**
   * Outputs parentheses for a parenthesized expression
   *
   * @param n Parenthesized expression node
   */
  public void visit(ParensExp n) {
    out.print("(");
    n.exp.accept(this);
    out.print(")");
  }

  /**
   * Outputs brackets for an index expression
   *
   * @param n Index expression node
   */
  public void visit(IndexExp n) {
    n.lhs.accept(this);
    out.print("[");
    n.exp.accept(this);
    out.print("]");
  }
  
  /**
   * Outputs dot for a field expression
   *
   * @param n Index expression node
   */
  public void visit(FieldExp n) {
    n.lhs.accept(this);
    out.print(".");
    out.print(n.name.name);
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
   * Outputs function call.
   *
   * @param n Function call node
   */
  public void visit(FuncCall n) {
    n.lhs.accept(this);
    out.print("(");
    n.args.accept(this);
    out.print(")");
  }

  /**
   * Visits all expressions used as function arguments.
   *
   * @param n Function arguments node
   */
  public void visit(FuncArgs n) {
    int numExps = n.exps.size();
    for(int i = 0; i < numExps - 1; i++) {
      ((Exp)n.exps.get(i)).accept(this);
      out.print(",");
    }
    ((Exp)n.exps.get(numExps - 1)).accept(this);
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
