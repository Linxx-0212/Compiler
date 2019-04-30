package symtable;

import java.io.PrintStream;

/**
 * Created by apple on 3/20/19.
 */
public class VarSTE extends STE{
    private Type mType;
    private String mBase;
    private int mOffset=3;
    private int para;

    public VarSTE(String name,Type type, String Base, int offset,int para) {
        super(name);
        mBase = Base;
        mOffset = offset;
        mType = type;
    }

    public Type getType() {
        return mType;
    }
    public String getBase() {
        return mBase;
    }
    public int getOffset() {
        return mOffset;
    }
    public void setOffset(int offset) {
        mOffset = offset;
    }
    public void setBase(String base) {
        mBase = base;
    }
    public boolean isMember() {
        if(mBase.equals("Z"))
            return true;
        else
            return false;

    }
    public boolean isPara() {
        if(para == 1)
            return true;
        else
            return false;
    }

    public int outputDot(PrintStream printStream, int n) {
        int n2 = n;
        //System.out.println(this.getName());
        String out = "\t" + n2 + " [label=\" <f0> VarSTE " + "| <f1> mName = " + this.getName() + "| <f2> mType = " + this.mType.toString() + "| <f3> mBase = " + this.mBase + "| <f4> mOffset = " + this.mOffset + "\"];";
        printStream.println(out);
        return n++;
    }
    public void outputDot1(PrintStream printTxt) {
        printTxt.print(this.getName()+":"+this.mType.toString()+" ");
        return;
    }

}
