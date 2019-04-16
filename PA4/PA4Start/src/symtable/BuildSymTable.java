package symtable;

import ast.visitor.DepthFirstVisitor;
import ast.node.*;
import exceptions.SemanticException;

import java.util.*;

/**
 * Created by apple on 3/20/19.
 */
public class BuildSymTable extends DepthFirstVisitor {


    private SymTable symtable;
    private String currentClass;
    private boolean flag = true;


    public BuildSymTable() {
        this.symtable = new SymTable();

    }
    public SymTable getSymTable() {
        return this.symtable;
    }

    public Type getType(IType node){

        if(node instanceof BoolType)
            return Type.BOOL;

        if(node instanceof IntType)
            return Type.INT;

        if(node instanceof ByteType)
            return Type.BYTE;

        if(node instanceof ColorType)
            return Type.COLOR;

        if(node instanceof ButtonType)
            return Type.BUTTON;

        if(node instanceof ToneType)
            return Type.TONE;

        if(node instanceof VoidType)
            return Type.VOID;
        if(node instanceof IntArrayType)
            return Type.INTARRAY;
        if(node instanceof ColorArrayType)
            return Type.COLORARRAY;

        if(node instanceof ClassType)
        {
            ClassType a = (ClassType)node;
            return new Type(a.getName());
        }
        return null;

    }

    public void inVarDecl(VarDecl varDecl) {
        VarSTE varSTE;
        Scope currentScope = this.symtable.getCurrentScope();
        if (! this.isDuplicate(varDecl.getName())) {
            varSTE = new VarSTE(varDecl.getName(), getType(varDecl.getType()), null, 0);
            //System.out.println(varDecl.getType());
            currentScope.insert(varSTE);
        }
        else
            throw new SemanticException(
                    "Duplicate Variable",
                    varDecl.getLine(),
                    varDecl.getPos()
            );
    }

    public void inMethodDecl(MethodDecl methodDecl) {
        if(this.flag) {
            MethodSTE methodSTE;
            Formal formal;
            Iterator iterator = methodDecl.getFormals().iterator();
            LinkedList<Type> linkedList = new LinkedList<Type>();
            while(iterator.hasNext()) {
                formal = (Formal)iterator.next();
                linkedList.add(this.getType(formal.getType()));
            }
            Signature signature = new Signature(linkedList,getType(methodDecl.getType()));

            if(!this.isDuplicate(methodDecl.getName()))
                methodSTE = new MethodSTE(methodDecl.getName(),signature,methodDecl.getName());
            else
                throw new SemanticException(
                        "Duplicate Method",
                        methodDecl.getLine(),
                        methodDecl.getPos()
                );

            methodSTE.getScope().setEnclosing(symtable.getPeek());
            this.symtable.insert(methodSTE);
            methodSTE.setVarSTE(new VarSTE("This",Type.getClassType(this.currentClass),null,0));
            for (Formal formal1 : methodDecl.getFormals()) {
                //System.out.println(formal1.getName());
                if(methodSTE.getScope().lookup(formal1.getName())==null)
                    methodSTE.setVarSTE(new VarSTE(formal1.getName(), this.getType(formal1.getType()),null,0));
                else
                    throw new SemanticException(
                            "Duplicate Variable",
                            formal1.getLine(),
                            formal1.getPos()
                    );
            }
            this.symtable.pushScope(methodDecl.getName());

        }
    }

    public void outMethodDecl(MethodDecl methodDecl) {
        this.symtable.popScope();
    }


    public void inTopClassDecl(TopClassDecl topClassDecl) {
            symtable.getGlobalScope();

            this.currentClass = topClassDecl.getName();
            //System.out.println(currentClass);
            if (!this.isDuplicate(topClassDecl.getName())) {
                this.symtable.insert(new ClassSTE(this.currentClass, false, null, this.currentClass));
                this.symtable.pushScope(this.currentClass);
            }
            else {
                throw new SemanticException(
                        "Duplicate Class Declaration",
                        topClassDecl.getLine(),
                        topClassDecl.getPos()
                );
            }


    }
    public void outTopClassDecl(TopClassDecl topClassDecl) {
        symtable.popScope();
    }

    public void inChildClassDecl(ChildClassDecl childClassDecl) {
        this.currentClass = childClassDecl.getName();
        if (!this.isDuplicate(childClassDecl.getName())) {
            ClassSTE childclass = new ClassSTE(this.currentClass, false, null, childClassDecl.getParent());
            this.symtable.pushScope(this.currentClass);
            this.symtable.insert(childclass);}
        else {
            throw new SemanticException(
                    "Duplicatee Class Declaration",
                    childClassDecl.getLine(),
                    childClassDecl.getPos()
            );
        }


    }

    public void outChildClassDecl(ChildClassDecl childClassDecl) {
        this.symtable.popScope();
    }

    public void inPlusExp(PlusExp node) {
        symtable.setExpType(node.getLExp(), Type.INT);
        symtable.setExpType(node.getRExp(), Type.INT);
    }

    public void inMinusExp(MinusExp node) {
        symtable.setExpType(node.getLExp(), Type.INT);
        symtable.setExpType(node.getRExp(), Type.INT);
    }

    public void inMulExp(MulExp node) {
        symtable.setExpType(node.getLExp(), Type.INT);
        symtable.setExpType(node.getRExp(), Type.INT);
    }

    public void inAndExp(AndExp node) {
        symtable.setExpType(node.getLExp(), Type.BOOL);
        symtable.setExpType(node.getRExp(), Type.BOOL);
    }
    public void inLtExp(LtExp node) {

        symtable.setExpType(node.getLExp(), Type.INT);
        symtable.setExpType(node.getRExp(), Type.INT);
    }

    public void inNotExp(NotExp node) {

        symtable.setExpType(node.getExp(), Type.INT);
    }

    public void inTrueExp(TrueLiteral node) {
        symtable.setExpType(node, Type.BOOL);
    }

    public void inFalseExp(FalseLiteral node) {
        symtable.setExpType(node, Type.BOOL);
    }

    public void inMeggyCheckButton(MeggyCheckButton node) {
        symtable.setExpType(node,Type.BUTTON);
    }


    public void inColorExp(ColorLiteral node) {
        symtable.setExpType(node,Type.COLOR);
    }

    public void inToneExp(ToneLiteral node) {
        symtable.setExpType(node,Type.TONE);
    }

    public void inIntegerExp(IntLiteral node) {
        symtable.setExpType(node,Type.INT);
    }

    public void inButtonExp(ButtonLiteral node) {
        symtable.setExpType(node,Type.BUTTON);
    }


    public void inByteCast(ByteCast node) {
        symtable.setExpType(node,Type.BYTE);
    }

    private boolean isDuplicate(String name) {
        if(this.symtable.lookupInnermost(name) != null) {
            return true;
        }
        return false;
    }




/*
    public void inProgram(Program node) {
        MainClass mainClass = node.getMainClass();
        ClassSTE mainSTE = new ClassSTE(mainClass.getName(),true,null,null);
        //symtable.insert(mainSTE);
        LinkedList<IClassDecl> classList = node.getClassDecls();
        for(int i = 0; i < classList.size();i++) {
            IClassDecl currentClass = classList.get(i);
            ClassSTE currentSTE = new ClassSTE(((TopClassDecl)currentClass).getName(),false,null,null);
            symtable.insert(currentSTE);

        }


    }
    public void outProgram(Program node) {
        symtable.popScope();
    }

*/

}
