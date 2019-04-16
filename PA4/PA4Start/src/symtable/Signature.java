package symtable;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by apple on 3/20/19.
 */
public class Signature {
    private Type mReturn_type;
    private List<Type> mTypes;

    public Signature(List<Type> types, Type type) {
        mReturn_type = type;
        if(types == null)
            mTypes = new LinkedList<Type>();
        mTypes = types;
    }

    public Type getReturn_type() {
        return mReturn_type;
    }

    public List<Type> getFormals() {
        return mTypes;
    }

    public String getSig() {
        String ans="(";
        for (int i = 0; i <mTypes.size()-1;i++) {
            ans +=mTypes.get(i).toString();
            ans +=", ";
        }
        if(mTypes.size()>0)
            ans +=mTypes.get(mTypes.size()-1);
        ans +=") ";

        if (mReturn_type == Type.VOID)
            ans +="returns class_null;";
        else {
            ans += "returns ";
            ans += mReturn_type.toString();
        }
        return ans;
    }

}
