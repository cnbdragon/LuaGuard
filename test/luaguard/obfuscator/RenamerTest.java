package luaguard.obfuscator;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import luaguard.unparser.LuaUnparser;
import org.luaj.vm2.ast.Block;
import org.luaj.vm2.ast.Chunk;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.FuncBody;
import org.luaj.vm2.ast.ParList;
import org.luaj.vm2.ast.Stat;
import org.luaj.vm2.ast.Visitor;
import org.luaj.vm2.parser.LuaParser;
import org.luaj.vm2.parser.ParseException;
/*
 * Here comes the text of your license
 * Each line should be prefixed with  * 
 */

/**
 *
 * @author Will
 */
public class RenamerTest {
    public static void main(String[] argv) throws ParseException, FileNotFoundException{
        String file = "Lua/factors.lua";
        LuaParser parser = new LuaParser(new FileInputStream(file));
        Chunk chunk = parser.Chunk();
        FunRenamerObfuscator renamer1 = new FunRenamerObfuscator();
//        VarRenamerObfu renamer2 = new VarRenamerObfu();
        chunk.accept(renamer1);
//        chunk.accept(renamer2);
        chunk.accept(new LuaUnparser(System.out, false));
    }
}
