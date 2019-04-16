package symtable;

import java.io.PrintStream;

/**
 * Created by apple on 3/20/19.
 */
public abstract class STE {
    public String mName;

    public STE(String name) {
        this.mName = name;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public abstract int outputDot(PrintStream var1, int var2);

    public abstract void outputDot1(PrintStream var1);
}
