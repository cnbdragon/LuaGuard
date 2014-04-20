import java.io.PrintStream;
import java.io.*;
import java.util.List;

import org.luaj.vm2.ast.*;
import org.luaj.vm2.ast.Exp.*;
import org.luaj.vm2.ast.Stat.*;
import org.luaj.vm2.Lua;
import org.luaj.vm2.LuaString;

class LuaUnparser extends Visitor {

  private PrintStream out;
  private boolean isCompressed; // true if newlines should NOT be printed.

  public LuaUnparser(PrintStream out) {
    this.out = out;
    this.isCompressed = true;
  }
  
  public LuaUnparser(PrintStream out, boolean isCompressed) {
    this.out = out;
    this.isCompressed = isCompressed;
  }

  /**
   * Visits each statement outputing line terminator, i.e. semi-colons
   *
   * @param n Block node
   */
  public void visit(Block n) {

    // Determine if a do block vs. regular block
    if (n.isDoBlock) {
      out.print("do ");
      newline();
    }

    // Block statements
    int numStats = n.stats.size();
    for(int i = 0; i < numStats - 1; i++) {
      ((Stat)n.stats.get(i)).accept(this);
      out.print(";");
      newline();
    }
    ((Stat)n.stats.get(numStats - 1)).accept(this);
    out.print(";");

    if (n.isDoBlock) {
      newline();
      out.print("end");
    }
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
      out.print(((Name)n.names.get(i)).name + ",");
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
    out.print(" then");
    newline();
    n.ifblock.accept(this);
    newline();

    // else-if conditions/blocks
    if(null != n.elseifexps) {
      for(int i = 0; i < n.elseifexps.size(); i++) {
        out.print("elseif ");
        ((Exp)n.elseifexps.get(i)).accept(this);
        out.print(" then");
        newline();
        ((Block)n.elseifblocks.get(i)).accept(this);
        newline();
      }
    }

    // else block
    if(null != n.elseblock) {
      out.print("else");
      newline();
      n.elseblock.accept(this);
      newline();
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
    out.print(" do");
    newline();

    // block
    n.block.accept(this);
    newline();
    out.print("end");
  }

  /**
   * Outputs repeat until loop
   *
   * @param n RepeatUntil node
   */
  public void visit(RepeatUntil n) {
    
    // block
    out.print("repeat");
    newline();
    n.block.accept(this);

    // condition
    newline();
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
    out.print(" do");
    newline();
    n.block.accept(this);
    newline();
    out.print("end");

  }

  /**
   * Outputs generic for loop
   *
   * @param n GenericFor node
   */
  public void visit(GenericFor n) {
    int numNames = n.names.size();
    int numExps = n.exps.size();

    // Names
    out.print("for ");
    for(int i = 0; i < numNames - 1; i++) {
      out.print(((Name)n.names.get(i)).name);
      out.print(",");
    }
    out.print(((Name)n.names.get(numNames - 1)).name);
    out.print(" in ");

    // Expressions
    for(int i = 0; i < numExps - 1; i++) {
      ((Exp)n.exps.get(i)).accept(this);
      out.print(",");
    }
    ((Exp)n.exps.get(numExps-1)).accept(this);

    // block
    out.print(" do");
    newline();
    n.block.accept(this);
    newline();
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
   
  /**
   * Outputs return statement
   *
   * @param n Return node
   */ 
  public void visit(Return n) {
    out.print("return ");

    // Expressions
    int numVals = n.values.size();
    for(int i = 0; i < numVals - 1; i++) {
      ((Exp)n.values.get(i)).accept(this);
      out.print(",");
    }
    ((Exp)n.values.get(numVals - 1)).accept(this);
  }

  /****
   * Tables
   ***/

  /**
   * Outputs table constructor. Table fields are included as part of the constructor.
   *
   * @param n TableConstructor node
   */
  public void visit(TableConstructor n) {
    out.print("{");
    if (null != n.fields) {
      for (int i = 0; i < n.fields.size(); i++) {
        TableField f = (TableField)((List<?>)n.fields).get(i);

        // keyed field
        if (null != f.index) {
          out.print("[");
          f.index.accept(this);
          out.print("]=");
        }
        // named field
        else if (null != f.name) {
          out.print(f.name);
          out.print("=");
        }
        // list field (implicit)
        f.rhs.accept(this);
        out.print(";");
      }
    }
    out.print("}");
  }

  /**
   * Outputs a parameter list
   *
   * @param n ParList node
   */
  public void visit(ParList n) {

    // Names
    if (null != n.names) {
      int numNames = n.names.size();
      if (numNames > 0) {
        out.print(((Name)n.names.get(0)).name);
        for (int i = 1; i < numNames; i++) {
          out.print(",");
          out.print(((Name)n.names.get(i)).name);
        }

        if (n.isvararg) {
          out.print(",");
        }
      }
    }

    // varargs
    if (n.isvararg) {
      out.print("...");
    }
  }

  /**
   * Outputs function body which includes the parameter list as well as the function block
   *
   * @param n FuncBody node
   */
  public void visit(FuncBody n) {
    
    // ParList
    out.print("(");
    n.parlist.accept(this);
    out.print(")");
    newline();

    // block
    n.block.accept(this);

  }

  /**
   * Outputs a global function declaration
   *
   * @param n FuncDef node
   */
  public void visit(FuncDef n) {
    out.print("function ");
    
    // Name
    out.print(n.name.name.name);
    if (null != n.name.dots) {
      for (int i = 0; i < n.name.dots.size(); i++) {
        out.print(".");
        out.print(n.name.dots.get(i));
      }
    }
    if (null != n.name.method) {
      out.print(":");
      out.print(n.name.method);
    }

    // Parameter list + body
    n.body.accept(this);

    newline();
    out.print("end");
  }
  
  /**
   * Outputs a local function declaration
   *
   * @param n LocalFuncDef node
   */
  public void visit(LocalFuncDef n) {
    out.print("local function ");

    // Name
    out.print(n.name.name);

    // Parameter list + body
    n.body.accept(this);

    newline();
    out.print("end");
  }

  /****
   * Expressions
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
   * Outputs a method call.
   *
   * @param n Method call node
   */
  public void visit(MethodCall n) {
    n.lhs.accept(this);
    out.print(":");
    out.print(n.name);
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
    if (null != n.exps) {
      int numExps = n.exps.size();
      for(int i = 0; i < numExps - 1; i++) {
        ((Exp)n.exps.get(i)).accept(this);
        out.print(",");
      }
      ((Exp)n.exps.get(numExps - 1)).accept(this);
    }
  }

  /**
   * Outputs anonymous function declaration.
   *
   * @param n AnonFuncDef
   */
  public void visit(AnonFuncDef n) {
    out.print("function");
    n.body.accept(this);
    newline();
    out.print("end");
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
  private static String unescape(String s) {
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

  /**
   * Prints newline if output does not need to be compressed, otherwise does nothing.
   */
  private void newline() {
    if (!isCompressed)
      out.print("\n");
  }
}
