package symtable;

import java.io.*;
import java.util.*;

import exceptions.*;

import symtable.*;

/**
 * Created by apple on 3/20/19.
 */
public class Scope {
    private HashMap<String, STE> dictionary = new HashMap<String, STE>();
    private Scope mEnclosing;
    private String scopeID;
    private List<String> mDeclOrder = new LinkedList<String>();
    boolean error;
    public Scope () {
        error =false;

    }

    public STE lookup(String string) {
        if (this.dictionary.containsKey(string)) {
            return this.dictionary.get(string);
        }

        if (this.mEnclosing!=null) {
            return this.mEnclosing.lookup(string);
        }

        return null;
    }
    public STE lookupInnermost(String string) {
        if (this.dictionary.containsKey(string)) {
            return this.dictionary.get(string);
        }
        else
            return null;
    }

    public void setEnclosing(Scope scope) {
            this.mEnclosing = scope;

    }

    public Scope getEnclosing() {
        return this.mEnclosing;
    }


    public String getScopeID() {
        return this.scopeID;
    }
    public void setScopeID(String id) {
        this.scopeID = id;
    }
    public void insert(STE ste) {
        if (this.dictionary.containsKey(ste.getName())) {
            error = true;
        }
        else {
            this.dictionary.put(ste.getName(), ste);
            this.mDeclOrder.add(ste.getName());
        }
    }

    public int outputDot(PrintStream printStream, int n) {
        String method;
        int n2 = n;
        String output = "\t" + n2 + " [label=\" <f0> Scope ";
        Iterator<String> iterator = this.mDeclOrder.iterator();
        int n3 = 1;
        while (iterator.hasNext()) {
            method = iterator.next();
            output += "| <f" + n3 + "> " + "dictionary\\[" + method + "\\] ";
            ++n3;
        }
        output += "\"];";
        printStream.println(output);
        iterator = this.mDeclOrder.iterator();
        n3 = 1;
        while (iterator.hasNext()) {
            method = iterator.next();
            STE sTE = this.dictionary.get(method);
            printStream.println("\t" + n2 + ":<f" + n3 + "> -> " + ++n + ":<f0>;");
            n = sTE.outputDot(printStream, n);
            ++n3;
        }
        return n;
    }

    public void outputDot1(PrintStream printTxt) {

        String method;
        LinkedList<String> For_sort0 = new LinkedList<>();
        LinkedList<String> For_sort1 = new LinkedList<>();
        LinkedList<String> For_sort2 = new LinkedList<>();
        Iterator<String> iterator = this.mDeclOrder.iterator();
        //printTxt.print("Methods in current scope: ");

        int i = 0,j=0,k = 0;
        int[] flag = new int[3];
        while(iterator.hasNext()) {
            //System.out.println(iterator.next().toString());
            method = iterator.next();
            if (this.dictionary.get(method) instanceof ClassSTE) {
                flag[0]++;
                k++;
                For_sort0.add(i, method);
            }
            if (this.dictionary.get(method) instanceof MethodSTE) {
                flag[1]++;
                For_sort1.add(i,method);
                i++;
            }
            if(this.dictionary.get(method) instanceof VarSTE) {
                flag[2]++;
                For_sort2.add(j,method);
                j++;
            }
            //printTxt.print(method);

        }
        Collections.sort(For_sort1);
        Collections.sort(For_sort0);
        Collections.sort(For_sort2);
        if (flag[0] > 0) {
            printTxt.print("Classes in global Scope: ");
            iterator = For_sort0.iterator();

                while (iterator.hasNext()) {
                    method = iterator.next();
                    printTxt.print(method+" ");
                }
                    printTxt.println();
                printTxt.println();

            iterator = For_sort0.iterator();
            while(iterator.hasNext()) {
                method = iterator.next();
                STE ste = this.dictionary.get(method);
                ste.outputDot1(printTxt);
            }
            printTxt.println();


        }

        if (flag[2] > 0) {
            printTxt.print("Vars in current Scope: ");
            iterator = For_sort2.iterator();

            while(iterator.hasNext()) {
                method = iterator.next();
                STE ste = this.dictionary.get(method);
                ste.outputDot1(printTxt);
                //System.out.println(method);
            }
                printTxt.println();
        }

        if (flag[1] > 0) {
            printTxt.print("Methods in current Scope: ");
            iterator = For_sort1.iterator();
            while (iterator.hasNext()) {
                method = iterator.next();
                printTxt.print(method+" ");
            }
            printTxt.println();

            iterator = For_sort1.iterator();
            while(iterator.hasNext()) {
                method = iterator.next();
                STE ste = this.dictionary.get(method);

                ste.outputDot1(printTxt);
                //printTxt.println();
            }
           // printTxt.println();


        }



        return;


    }



}
