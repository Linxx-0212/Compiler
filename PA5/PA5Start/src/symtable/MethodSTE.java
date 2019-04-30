package symtable;


import java.io.PrintStream;
import java.util.*;
import ast.node.*;

/**
 * Created by apple on 3/20/19.
 */
public class MethodSTE extends STE {
    private Signature mSignature;
    private Scope mScope;

    public MethodSTE(String Name, Signature signature,String scope) {
        super(Name);
        mSignature = signature;
        mScope = new Scope();
        mScope.setScopeID(scope);
    }

    public String getScopeName() {
        return mScope.getScopeID();
    }

    public Signature getSignature() {
        return mSignature;
    }

    public Scope getScope() {
        return mScope;
    }
    public void setScope(Scope scope) {
        mScope = scope;
    }

    public void setVarSTE(VarSTE variable) {
        mScope.insert(variable);
    }
    public VarSTE getVarSTE(String variable) {
        return (VarSTE) mScope.lookup(variable);
    }

    public int outputDot(PrintStream printStream, int n) {
        int n2 = n;
        String signature = this.mSignature == null ? "null" : this.mSignature.getSig();
        String string2 = "\t" + n2 + " [label=\" <f0> MethodSTE " + "| <f1> mName = " + this.mName + "| <f2> mSignature = " + signature + "| <f3> mScope \"];";
        printStream.println(string2);
        printStream.println("\t" + n2 + ":<f3> -> " + ++n + ":<f0>;");
        return this.mScope.outputDot(printStream, n);
    }

    public void outputDot1(PrintStream printTxt) {
        printTxt.print("In method "+this.mName+" Scope\n");
        printTxt.print("Method Signature"+ this.mSignature.getSig()+"\n");
        this.mScope.outputDot1(printTxt);
        return;
    }

}
