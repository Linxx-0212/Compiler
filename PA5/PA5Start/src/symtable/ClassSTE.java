package symtable;

import java.io.PrintStream;
import java.util.*;
import ast.node.*;
/**
 * Created by apple on 3/20/19.
 */
public class ClassSTE extends STE{

    private boolean mMain;
    private String mSuperClass;
    private Scope mScope;

    public ClassSTE(String name, boolean Main, String SuperClass,String scope) {
        super(name);
        mMain = Main;
        mSuperClass = SuperClass;
        mScope = new Scope();
        mScope.setScopeID(scope);
    }

    public String getScopeName() {
        return mScope.getScopeID();
    }


    public Scope getScope() {
        return mScope;
    }

    public void setScope(Scope scope) {
        mScope = scope;
    }

    public void setSuperClass(String superClass) {
        mSuperClass = superClass;
    }

    public String getSuperClass() {
        return mSuperClass;
    }
    public void setMethodSTE(MethodSTE method) {
        mScope.insert(method);
    }
    public MethodSTE getMethodSTE(String method) {
        return (MethodSTE) mScope.lookup(method);
    }

    public int outputDot(PrintStream printStream, int n) {
        int n2 = n++;
        String string = "\t" + n2 + " [label=\" <f0> ClassSTE " + "| <f1> mName = " + this.mName + "| <f2> mMain = " + this.mMain + "| <f3> mSuperClass = " + this.mSuperClass + "| <f4> mScope \"];";
        printStream.println(string);
        printStream.println("\t" + n2 + ":<f4> -> " + n + ":<f0>;");
        return this.mScope.outputDot(printStream, n);
    }

    public void outputDot1(PrintStream printTxt) {

        printTxt.print("In class "+this.mName+" Scope\n");
        this.mScope.outputDot1(printTxt);
        return;
    }

}
