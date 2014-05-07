package luaguard.obfuscator;
import java.util.List;
import org.luaj.vm2.ast.Block;
import org.luaj.vm2.ast.Stat;
import org.luaj.vm2.ast.Stat.Assign;
/**
 * Add junk code to the source code
 *Junk code is from the source code 
 */
public class JunkStatObfuscator extends Obfuscator{
	 @Override
	    public void visit(Block b){
	        //get the statement list of source code
	        if(b.stats == null) return;
            //ArrayList<Name> namelist=new ArrayList<Name>();
            //Name name=new Name("_unuse"); 
            //namelist.add(name);
            //ArrayList<String> value=new ArrayList<String>();
            //value.add("value");
// Assign assin=new Assign(namelist,value);
 //Stat juckass=(Stat)assin;

 //b.add(juckass);
 
 //System.out.println("hcsjkfnd1"+juckass.toString())
 
	       List<Stat> l = b.stats;
 
	        for(int i =0; i<l.size(); i++){
	            if(l.get(i).getClass().getName().contains("Assign")){
	                Stat old =  l.get(i);
	                b.add(old);
	                break;
	               
	            } 
	        }
	            }
	           
	 @Override
     public void visit(Assign assign){
         if(assign == null) return;
         assign.accept(this);
     }
	           
	        }
	    


