package symtable;
import java.util.*;
import ast.node.*;

import exceptions.InternalException;
import java.io.*;

/** 
 * SymTable
 * ....
 * The symbol table also keeps a mapping of expression nodes to
 * types because that information is needed elsewhere especially
 * when looking up method call information.
 * 
 * @author mstrout
 * WB: Simplified to only expression types
 */
public class SymTable {

    private final Stack<Scope> mScopeStack = new Stack();
    private final Scope  mGlobalScope = new Scope();
    private final HashMap<Node, Type> mExpType = new HashMap();

    public SymTable() {

        mGlobalScope.setScopeID("Global");
        this.mScopeStack.push(this.mGlobalScope);


    }

    /** Lookup a symbol in this symbol table.

     * Starts looking in innermost scope and then

     * look in enclosing scopes.

     * Returns null if the symbol is not found.

     */

    public STE lookup(String sym) {

        Scope scope = this.mScopeStack.peek();
        if (scope.lookup(sym) == null) {
            if (scope.getEnclosing() != null) {
                return scope.getEnclosing().lookup(sym);
            }
        }
        return scope.lookup(sym);


        /* WRITE ME */

    }

 

    /** Lookup a symbol in innermost scope only.

     * return null if the symbol is not found

     */

    public STE lookupInnermost(String sym) {

        Scope currentScope = this.mScopeStack.peek();

        return currentScope.lookupInnermost(sym);

    }

 

    /** When inserting an STE will just insert

     * it into the scope at the top of the scope stack.

     */

    public void insert( STE ste) {
        if (!mScopeStack.isEmpty()) {
            Scope scope = this.mScopeStack.peek();
            scope.insert(ste);
        }

    }

   

    /**

     * Lookup the given method scope and make it the innermost

     * scope.  That is, make it the top of the scope stack.

     */

    public void pushScope(String id) {

        STE ste = this.lookup(id);

        if (ste instanceof MethodSTE) {
            MethodSTE m = (MethodSTE)ste;
            this.mScopeStack.push(m.getScope());
        }
        else
            if(ste instanceof ClassSTE) {
            ClassSTE c = (ClassSTE)ste;
            this.mScopeStack.push(c.getScope());
            }
            else
            System.out.println("No such scope is defined:" + id);
    }

   

    public void popScope() {

        if(!mScopeStack.empty()) {
            this.mScopeStack.pop();
        }
    }

   

    public void setExpType(Node exp, Type t)

    {

            this.mExpType.put(exp, t);

    }

   

    public Type getExpType(IExp exp)

    {
            return this.mExpType.get(exp);

    }

    public Scope getCurrentScope() {
        return this.mScopeStack.peek();
    }

    public Scope getGlobalScope() {

        //System.out.println(mScopeStack.peek().getScopeID());
        return mGlobalScope;
    }

    public Scope getPeek() {
        return mScopeStack.peek();
    }

    public void outputDot(PrintStream printStream) {
        printStream.println("digraph SymTable {");
        printStream.println("\tgraph [rankdir=\"LR\"];");
        printStream.println("\tnode [shape=record];");
        this.mGlobalScope.outputDot(printStream, 0);
        printStream.println("}");
    }


    public void outputDot1(PrintStream printTxt) {
        this.mGlobalScope.outputDot1(printTxt);
        printTxt.flush();
    }
}
