package ast_visitors;


import ast.visitor.*;
import java.io.*;
import java.util.*;

import exceptions.InternalException;
import symtable.*;
import ast.node.*;
import label.Label;

/**
 * Created by apple on 4/13/19.
 */
public class AVRgenVisitor extends DepthFirstVisitor {

    private final SymTable mCurrentST;
    private final PrintWriter output;
    private String currentClass;

    public AVRgenVisitor(PrintWriter output, SymTable symTable) {
        this.mCurrentST = symTable;
        this.output = output;
        this.currentClass = "";
        try {
            InputStream stream = this.getClass().getClassLoader().getResourceAsStream("avrH.rtl.s");
            this.output.println(this.convertStreamToString(stream));
            stream.close();
        } catch (IOException var4) {
            var4.printStackTrace();
        }

        output.flush();
    }




    private void promoteReg(AVRgenVisitor.RegPair reg) {
        Label one = new Label();
        Label two = new Label();
        this.output.println("    # promoting a byte to an int");
        this.output.println("    tst     " + reg.lowbit);
        this.output.println("    brlt     " + one);
        this.output.println("    ldi    " + reg.highbit + ", 0");
        this.output.println("    jmp    " + two);
        this.output.println(one + ":");
        this.output.println("    ldi    " + reg.highbit + ", hi8(-1)");
        this.output.println(two + ":");
        this.output.flush();
    }
    private AVRgenVisitor.RegPair getExpReg(Node var1) {
        AVRgenVisitor.RegPair var2 = null;
        Integer var3 = 24;
        if(var3 != null) {
            int var4 = var3.intValue() + 1;
            var2 = new AVRgenVisitor.RegPair("r" + var3, "r" + var4);
        }

        return var2;
    }

    public void outIntegerExp(IntLiteral var1) {
        this.output.println();
        int var2 = var1.getIntValue();
        this.output.println("    # Load constant int " + var2);
        this.output.println("    ldi    r24,lo8(" + var2 + ")");
        this.output.println("    ldi    r25,hi8(" + var2 + ")");
        this.genStoreExp(var1, new AVRgenVisitor.RegPair("r24", "r25"));
        this.output.flush();
    }

    private AVRgenVisitor.RegPair genLoadExp(IExp iexp, int value) {
        AVRgenVisitor.RegPair var3 = this.getExpReg(iexp);
        if(var3 != null) {
            var3 = new AVRgenVisitor.RegPair("r" + value, "r" + (value + 1));
            if(this.mCurrentST.getExpType(iexp).getAVRTypeSize() == 2) {
                this.output.println("    # load a two byte expression off stack");
                this.output.println("    pop    " + var3.lowbit);
                this.output.println("    pop    " + var3.highbit);
            } else {
                this.output.println("    # load a one byte expression off stack");
                this.output.println("    pop    " + var3.lowbit);
            }
        }

        return var3;
    }

    private void genStoreExp(IExp iExp, AVRgenVisitor.RegPair val) {
        AVRgenVisitor.RegPair reg = this.getExpReg(iExp);
        if(reg != null) {
            if(this.mCurrentST.getExpType(iExp).getAVRTypeSize() == 2) {
                this.output.println("    # push two byte expression onto stack");
                this.output.println("    push   " + val.highbit);
                this.output.println("    push   " + val.lowbit);
            } else {
                this.output.println("    # push one byte expression onto stack");
                this.output.println("    push   " + val.lowbit);
            }
        } else if(!reg.lowbit.equals(val.lowbit)) {
            this.genMoveReg(reg, val);
        }

    }


private void genMoveReg(AVRgenVisitor.RegPair reg1, AVRgenVisitor.RegPair reg2) {
    if(!reg1.lowbit.equals(reg2.lowbit)) {
        this.output.println("    # move low byte src into dest reg");
        this.output.println("    mov    " + reg1.lowbit + ", " + reg2.lowbit);
    }

    if(reg1.highbit != null && reg2.highbit != null && !reg1.lowbit.equals(reg2.lowbit)) {
        this.output.println("    # move hi byte src into dest reg");
        this.output.println("    mov    " + reg1.highbit + ", " + reg2.highbit);
    }

}

    public void outMainClass(MainClass mainClass) {
        try {
            InputStream var2 = this.getClass().getClassLoader().getResourceAsStream("avrF.rtl.s");
            this.output.println(this.convertStreamToString(var2));
            var2.close();
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        this.output.flush();
    }

    public void visitAndExp(AndExp andExp) {

        this.output.println();
        this.output.println("    #### short-circuited && operation");
        this.output.println("    # &&: left operand");
        if(andExp.getLExp() != null) {
            andExp.getLExp().accept(this);
        }

        Label one = new Label();
        Label two = new Label();
        this.output.println();
        this.output.println("    # &&: if left operand is false do not eval right");
        this.output.println("    # load a one byte expression off stack");
        this.output.println("    pop    r24");
        this.output.println("    # push one byte expression onto stack");
        this.output.println("    push   r24");
        this.output.println("    # compare left exp with zero");
        this.output.println("    ldi r25, 0");
        this.output.println("    cp    r24, r25");
        this.output.println("    # Want this, breq " + one);
        this.output.println("    brne  " + two);
        this.output.println("    jmp   " + one);
        this.output.println();
        this.output.println(two + ":");
        this.output.println("    # right operand");
        this.output.println("    # load a one byte expression off stack");
        this.output.println("    pop    r24");
        if(andExp.getRExp() != null) {
            andExp.getRExp().accept(this);
        }


        this.output.println("    # load a one byte expression off stack");
        this.output.println("    pop    r24");
        this.output.println("    # push one byte expression onto stack");
        this.output.println("    push   r24");
        this.output.println();
        this.output.println(one + ":");

    }

    public void outEqualExp(EqualExp equalExp) {
        Label one = new Label();
        Label two = new Label();
        Label three = new Label();
        this.output.println();
        this.output.println("    # equality check expression");
        AVRgenVisitor.RegPair right = this.genLoadExp(equalExp.getRExp(),18);
        AVRgenVisitor.RegPair left = this.genLoadExp(equalExp.getLExp(),24);
        if (this.mCurrentST.getExpType(equalExp.getLExp()).getAVRTypeSize() == 1 && this.mCurrentST.getExpType(equalExp.getRExp()).getAVRTypeSize() == 1) {
            this.output.println("    cp    " + left.lowbit + ", " + right.lowbit);
        } else {
            if (this.mCurrentST.getExpType(equalExp.getLExp()).getAVRTypeSize() == 1) {
                this.promoteReg(left);
            }
            if (this.mCurrentST.getExpType(equalExp.getRExp()).getAVRTypeSize() == 1) {
                this.promoteReg(right);
            }
            this.output.println("    cp    " + left.lowbit + ", " + right.lowbit);
            this.output.println("    cpc   " + left.highbit + ", " + right.highbit);
        }

        this.output.println("    breq " + two);
        this.output.println();
        this.output.println("    # result is false");
        this.output.println(one + ":");
        this.output.println("    ldi     r24, 0");
        this.output.println("    jmp      " + three);
        this.output.println();
        this.output.println("    # result is true");
        this.output.println(two + ":");
        this.output.println("    ldi     r24, 1");
        this.output.println();
        this.output.println("    # store result of equal expression");
        this.output.println(three + ":");
        this.genStoreExp(equalExp, new AVRgenVisitor.RegPair("r24", (String)null));
    }

    public void outLtExp(LtExp ltExp) {
        Label two = new Label();
        Label three = new Label();
        Label four = new Label();
        this.output.println();
        this.output.println("    # less than expression");
        AVRgenVisitor.RegPair right = this.genLoadExp(ltExp.getRExp(), 18);
        AVRgenVisitor.RegPair left = this.genLoadExp(ltExp.getLExp(), 24);
        if (this.mCurrentST.getExpType(ltExp.getLExp()).getAVRTypeSize() == 1 && this.mCurrentST.getExpType(ltExp.getRExp()).getAVRTypeSize() == 1) {
            this.output.println("    cp    " + left.lowbit + ", " + right.lowbit);
        } else {
            if(this.mCurrentST.getExpType(ltExp.getLExp()).getAVRTypeSize() == 1) {
                this.promoteReg(left);
            }

            if(this.mCurrentST.getExpType(ltExp.getRExp()).getAVRTypeSize() == 1) {
                this.promoteReg(right);
            }

            this.output.println("    cp    " + left.lowbit + ", " + right.lowbit);
            this.output.println("    cpc   " + left.highbit + ", " + right.highbit);
        }
        this.output.println("    brlt " + three);
        this.output.println();
        this.output.println("    # load false");
        this.output.println(two + ":");
        this.output.println("    ldi     r24, 0");
        this.output.println("    jmp      " + four);
        this.output.println();
        this.output.println("    # load true");
        this.output.println(three + ":");
        this.output.println("    ldi    r24, 1");
        this.output.println();
        this.output.println("    # push result of less than");
        this.output.println(four + ":");
        this.genStoreExp(ltExp, new AVRgenVisitor.RegPair("r24", (String)null));

    }

    public void outNotExp(NotExp notExp) {
        this.output.println();
        this.output.println("    # not operation");
        AVRgenVisitor.RegPair var2 = this.genLoadExp(notExp.getExp(), 24);
        this.output.println("    ldi     r22, 1");
        this.output.println("    eor     " + var2.lowbit + ",r22");
        this.genStoreExp(notExp, var2);

    }

    public void outPlusExp(PlusExp plusExp) {
        AVRgenVisitor.RegPair var2 = this.genLoadExp(plusExp.getRExp(), 18);
        AVRgenVisitor.RegPair var3 = this.genLoadExp(plusExp.getLExp(), 24);
        if(this.mCurrentST.getExpType(plusExp.getLExp()) == Type.BYTE) {
            this.promoteReg(var3);
        }

        if(this.mCurrentST.getExpType(plusExp.getRExp()) == Type.BYTE) {
            this.promoteReg(var2);
        }

        this.output.println();
        this.output.println("    # Do add operation");
        this.output.println("    add    " + var3.lowbit + ", " + var2.lowbit);
        this.output.println("    adc    " + var3.highbit + ", " + var2.highbit);
        this.output.flush();
        this.genStoreExp(plusExp, var3);

    }

    public void outMinusExp(MinusExp minusExp) {
        AVRgenVisitor.RegPair right = this.genLoadExp(minusExp.getRExp(), 18);
        AVRgenVisitor.RegPair left = this.genLoadExp(minusExp.getLExp(), 24);
        if(this.mCurrentST.getExpType(minusExp.getLExp()) == Type.BYTE) {
            this.promoteReg(left);
        }

        if(this.mCurrentST.getExpType(minusExp.getRExp()) == Type.BYTE) {
            this.promoteReg(right);
        }

        this.output.println();
        this.output.println("    # Do INT sub operation");
        this.output.println("    sub    " + left.lowbit + ", " + right.lowbit);
        this.output.println("    sbc    " + left.highbit + ", " + right.highbit);
        this.output.println("    # push hi order byte first");
        this.output.flush();
        this.genStoreExp(minusExp, left);

    }

    public void outNegExp(NegExp negExp) {
        AVRgenVisitor.RegPair reg;
        AVRgenVisitor.RegPair reg2;
        if(this.mCurrentST.getExpType(negExp.getExp()) == Type.INT) {
            this.output.println();
            this.output.println("    # neg int");
            reg = this.genLoadExp(negExp.getExp(), 24);
            reg2 = new AVRgenVisitor.RegPair("r22", "r23");
            this.output.println("    ldi     " + reg2.lowbit + ", 0");
            this.output.println("    ldi     " + reg2.highbit + ", 0");
            this.output.println("    sub     " + reg2.lowbit + ", " + reg.lowbit);
            this.output.println("    sbc     " + reg2.highbit + ", " + reg.highbit);
            this.genStoreExp(negExp, reg2);
        } else {
            if(this.mCurrentST.getExpType(negExp.getExp()) != Type.BYTE) {
                throw new InternalException("Fatal Error: invalid type for Neg . This should have been caught during Type Checking.");
            }

            this.output.println();
            this.output.println("    # neg byte");
            reg = this.genLoadExp(negExp.getExp(), 24);
            reg2 = new AVRgenVisitor.RegPair("r22", "r23");
            this.promoteReg(reg);
            this.output.println("    ldi    " + reg2.lowbit + ", 0");
            this.output.println("    ldi    " + reg2.highbit + ", 0");
            this.output.println("    sub     " + reg2.lowbit + ", " + reg.lowbit);
            this.output.println("    sbc     " + reg2.highbit + ", " + reg.highbit);
            this.genStoreExp(negExp, reg2);
        }


    }

    public void outMulExp(MulExp mulExp) {
        this.output.println();
        this.output.println("    # MulExp");
        AVRgenVisitor.RegPair var2 = this.genLoadExp(mulExp.getRExp(), 18);
        AVRgenVisitor.RegPair var3 = this.genLoadExp(mulExp.getLExp(), 22);
        AVRgenVisitor.RegPair var4 = new AVRgenVisitor.RegPair("r24", (String)null);
        AVRgenVisitor.RegPair var5 = new AVRgenVisitor.RegPair("r26", (String)null);
        this.genMoveReg(var4, var2);
        this.genMoveReg(var5, var3);
        this.output.println();
        this.output.println("    # Do mul operation of two input bytes");
        this.output.println("    muls   " + var4.lowbit + ", " + var5.lowbit);
        this.genStoreExp(mulExp, new AVRgenVisitor.RegPair("r0", "r1"));
        this.output.println("    # clear r0 and r1, thanks Brendan!");
        this.output.println("    eor    r0,r0");
        this.output.println("    eor    r1,r1");
        this.output.flush();
    }

    public void outMeggySetPixel(MeggySetPixel meggySetPixel) {
        this.output.println();
        this.output.println("    ### Meggy.setPixel(x,y,color) call");
        AVRgenVisitor.RegPair pix = this.genLoadExp(meggySetPixel.getColor(), 20);
        this.genMoveReg(new AVRgenVisitor.RegPair("r20", (String)null), pix);
        pix = this.genLoadExp(meggySetPixel.getYExp(), 22);
        this.genMoveReg(new AVRgenVisitor.RegPair("r22", (String)null), pix);
        pix = this.genLoadExp(meggySetPixel.getXExp(), 24);
        this.genMoveReg(new AVRgenVisitor.RegPair("r24", (String)null), pix);
        this.output.println("    call   _Z6DrawPxhhh");
        this.output.println("    call   _Z12DisplaySlatev");
    }

    public void outMeggySetAuxLEDs(MeggySetAuxLEDs meggySetAuxLEDs) {
        this.output.println();
        this.output.println("    ### Meggy.setAuxLEDs(num) call");
        AVRgenVisitor.RegPair led = this.genLoadExp(meggySetAuxLEDs.getExp(), 24);
        this.genMoveReg(new AVRgenVisitor.RegPair("r24", "r25"), led);
        this.output.println("    call   _Z10SetAuxLEDsh");
    }


    public void outMeggyDelay(MeggyDelay meggyDelay) {
        this.output.println();
        this.output.println("    ### Meggy.delay() call");
        this.output.println("    # load delay parameter");
        AVRgenVisitor.RegPair del = this.genLoadExp(meggyDelay.getExp(), 24);
        this.genMoveReg(new AVRgenVisitor.RegPair("r24", "r25"), del);
        this.output.println("    call   _Z8delay_msj");
    }

    public void outMeggyGetPixel(MeggyGetPixel meggyGetPixel) {
        this.output.println();
        this.output.println("    ### Meggy.getPixel(x,y) call");
        AVRgenVisitor.RegPair pix = this.genLoadExp(meggyGetPixel.getYExp(), 22);
        this.genMoveReg(new AVRgenVisitor.RegPair("r22", (String)null), pix);
        pix = this.genLoadExp(meggyGetPixel.getXExp(), 24);
        this.genMoveReg(new AVRgenVisitor.RegPair("r24", (String)null), pix);
        this.output.println("    call   _Z6ReadPxhh");
        this.genStoreExp(meggyGetPixel, new AVRgenVisitor.RegPair("r24", (String)null));

    }

    public void outMeggyToneStart(MeggyToneStart meggyToneStart) {
        this.output.println();
        this.output.println("    ### Meggy.toneStart(tone, time_ms) call");
        AVRgenVisitor.RegPair tone = this.genLoadExp(meggyToneStart.getDurationExp(), 22);
        this.genMoveReg(new AVRgenVisitor.RegPair("r22", "r23"), tone);
        tone = this.genLoadExp(meggyToneStart.getToneExp(), 24);
        this.genMoveReg(new AVRgenVisitor.RegPair("r24", "r25"), tone);
        this.output.println("    call   _Z10Tone_Startjj");
    }

    public void visitIfStatement(IfStatement ifStatement) {

        this.output.println();
        this.output.println("    #### if statement");
        Label one = new Label();
        Label two = new Label();
        Label three = new Label();
        if (ifStatement.getExp()!=null) {
            ifStatement.getExp().accept(this);
        }
        this.output.println();
        this.output.println("    # load condition and branch if false");
        this.output.println("    # load a one byte expression off stack");
        this.output.println("    pop    r24");
        this.output.println("    #load zero into reg");
        this.output.println("    ldi    r25, 0");
        this.output.println();
        this.output.println("    #use cp to set SREG");
        this.output.println("    cp     r24, r25");
        this.output.println("    #WANT breq "+one);
        this.output.println("    brne   " + two);
        this.output.println("    jmp    " + one);
        this.output.println();
        this.output.println("    # then label for if");
        this.output.println(two + ":");
        if (ifStatement.getThenStatement() != null) {
            ifStatement.getThenStatement().accept(this);
        }
        this.output.println("    jmp    " + three);
        this.output.println();
        this.output.println("    # else label for if");
        this.output.println(one + ":");
        if (ifStatement.getElseStatement() != null) {
            ifStatement.getElseStatement().accept(this);
        }
        this.output.println();
        this.output.println("    # done label for if");
        this.output.println(three + ":");

    }

    public void visitWhileStatement(WhileStatement whileStatement) {
            Label two = new Label();
            Label three = new Label();
            Label four = new Label();
            this.output.println();
            this.output.println("    #### while statement");
            this.output.println(two + ":");
            if(whileStatement.getExp() != null) {
                whileStatement.getExp().accept(this);
            }

            this.output.println();
            this.output.println("    # if not(condition)");
            AVRgenVisitor.RegPair one = this.genLoadExp(whileStatement.getExp(), 24);
            this.output.println("    ldi    r25,0");
            this.output.println("    cp     " + one.lowbit + ", r25");
            this.output.println("    # WANT breq " + four);
            this.output.println("    brne   " + three);
            this.output.println("    jmp    " + four);
            this.output.println();
            this.output.println("    # while loop body");
            this.output.println(three + ":");
            if(whileStatement.getStatement() != null) {
                whileStatement.getStatement().accept(this);
            }

            this.output.println();
            this.output.println("    # jump to while test");
            this.output.println("    jmp    " + two);
            this.output.println();
            this.output.println("    # end of while");
            this.output.println(four + ":");

    }

    public void outByteCast(ByteCast byteCast) {
        if(this.mCurrentST.getExpType(byteCast.getExp()) == Type.INT && (this.mCurrentST.getExpType(byteCast.getExp()) == null || this.mCurrentST.getExpType(byteCast.getExp()) != this.mCurrentST.getExpType(byteCast))){
            if (this.mCurrentST.getExpType(byteCast) == null) {
                throw new InternalException("Expecting self to have no reg");
            }
        }
        if (mCurrentST.getExpType(byteCast.getExp()) == Type.BYTE)
            return;
        else {
            this.output.println();
            this.output.println("    # Casting int to byte by popping");
            this.output.println("    # 2 bytes off stack and only pushing low order bits");
            this.output.println("    # back on.  Low order bits are on top of stack.");
            this.output.println("    pop    r24");
            this.output.println("    pop    r25");
            this.output.println("    push   r24");
        }
    }

    public void outMeggyCheckButton(MeggyCheckButton meggyCheckButton) {
        this.output.println();
        this.output.println("    ### MeggyCheckButton");
        this.output.println("    call    _Z16CheckButtonsDownv");
        String button =  meggyCheckButton.getExp().toString();
        if (button.equals("Meggy.Button.B")) {
            this.output.println("    lds    r24, Button_B");
        } else if(button.equals("Meggy.Button.A")) {
            this.output.println("    lds    r24, Button_A");
        } else if(button.equals("Meggy.Button.Up")) {
            this.output.println("    lds    r24, Button_Up");
        } else if(button.equals("Meggy.Button.Down")) {
            this.output.println("    lds    r24, Button_Down");
        } else if(button.equals("Meggy.Button.Left")) {
            this.output.println("    lds    r24, Button_Left");
        } else {
            if(button.equals("Meggy.Button.Right")) {
                this.output.println("    lds    r24, Button_Right");
            }
        }

        Label one = new Label();
        Label two = new Label();
        Label three = new Label();
        this.output.println("    # if button value is zero, push 0 else push 1");
        this.output.println("    tst    r24");
        this.output.println("    breq   " + one);
        this.output.println(two + ":");
        this.output.println("    ldi    r24, 1");
        this.output.println("    jmp    " + three);
        this.output.println(one + ":");
        this.output.println(three + ":");
        this.output.println("    # push one byte expression onto stack");
        this.output.println("    push   r24");
    }

    public void outToneExp(ToneLiteral toneLiteral) {
        String tone = toneLiteral.getLexeme();
        int var3 = toneLiteral.getIntValue();
        this.output.println();
        this.output.println("    # Push " + tone + " onto the stack.");
        this.output.println("    ldi    r25, hi8(" + var3 + ")");
        this.output.println("    ldi    r24, lo8(" + var3 + ")");
        AVRgenVisitor.RegPair var4 = new AVRgenVisitor.RegPair("r24", "r25");
        this.genStoreExp(toneLiteral, var4);
    }

    public void outColorExp(ColorLiteral colorLiteral) {
        this.output.println();
        this.output.println("    # Color expression " + colorLiteral.getLexeme());
        this.output.println("    ldi    r22," + colorLiteral.getIntValue());
        AVRgenVisitor.RegPair reg = new AVRgenVisitor.RegPair("r22", (String)null);
        this.genStoreExp(colorLiteral, reg);
    }

    public void outTrueExp(TrueLiteral trueLiteral) {
        this.output.println();
        this.output.println("    # True/1 expression");
        this.output.println("    ldi     r22, 1");
        AVRgenVisitor.RegPair reg = new AVRgenVisitor.RegPair("r22", (String)null);
        this.genStoreExp(trueLiteral, reg);
    }

    public void outFalseExp(FalseLiteral falseLiteral) {
        this.output.println();
        this.output.println("    # False/0 expression");
        this.output.println("    ldi    r24,0");
        AVRgenVisitor.RegPair reg = new AVRgenVisitor.RegPair("r24", (String)null);
        this.genStoreExp(falseLiteral, reg);
    }





    public void outNewExp(NewExp newExp) {
        int size = 0;
        ClassSTE classSTE =(ClassSTE) this.mCurrentST.lookup(newExp.getId());
        HashMap<String, STE> dic = classSTE.getScope().dictionary;
        for (Map.Entry<String, STE> map:dic.entrySet()) {
            if (map.getValue() instanceof VarSTE) {
                VarSTE varSTE = (VarSTE) map.getValue();
                if (varSTE.isPara() == false)
                    size+=varSTE.getType().getAVRTypeSize();
            }

        }
        this.output.println();
        this.output.println("    # NewExp");
        this.output.println("    ldi    r24, lo8(" + size + ")");
        this.output.println("    ldi    r25, hi8(" + size + ")");
        this.output.println("    # allocating object of size "+ size + " on heap");
        this.output.println("    call    malloc");
        this.output.println("    # push object address");
        this.output.println("    # push two byte expression onto stack");
        this.output.println("    push   r25");
        this.output.println("    push   r24");
        this.output.flush();

    }

    public void outArrayExp(ArrayExp arrayExp) {
        this.output.println();
        this.output.println("    ### ArrayExp");
        this.getAddress(this.mCurrentST.getExpType(arrayExp.getExp()),arrayExp.getExp(),arrayExp.getIndex());
        this.output.println("    # load array element and push onto stack");
        AVRgenVisitor.RegPair regPair = new AVRgenVisitor.RegPair("r24","r25");
        if (this.mCurrentST.getExpType(arrayExp.getExp()) == Type.COLORARRAY) {
            this.output.println("    ldd    " + regPair.lowbit + ", Z+0");
        } else {
            if (this.mCurrentST.getExpType(arrayExp.getExp())!=Type.INTARRAY) {
                throw new InternalException("Unknown array type, " + this.mCurrentST.getExpType(arrayExp));
            }
            this.output.println("    ldd    " + regPair.lowbit + ", Z+0");
            this.output.println("    ldd    " + regPair.highbit + ", Z+1");
        }
        this.genStoreExp(arrayExp,regPair);
        this.output.flush();

    }

    private void getAddress(Type expType, IExp exp, IExp index) {
        this.output.println("    # calculate the array element address by first");
        this.output.println("    # loading index");
        AVRgenVisitor.RegPair regPair = this.genLoadExp(index, 18);
        if(this.mCurrentST.getExpType(index) == Type.BYTE) {
            this.promoteReg(regPair);
        }
        if (expType == Type.INTARRAY) {
            this.output.println("    # add size in elems to self to multiply by 2");
            this.output.println("    # complements of Jason Mealler");
            this.output.println("    add    " + regPair.lowbit + "," + regPair.lowbit);
            this.output.println("    adc    " + regPair.highbit + "," + regPair.highbit);
        }
        this.output.println("    # put index*(elem size in bytes) into r31:r30");
        this.output.println("    mov    r31, " + regPair.highbit);
        this.output.println("    mov    r30, " + regPair.lowbit);
        this.output.println("    # want result of addressing arithmetic ");
        this.output.println("    # to be in r31:r30 for access through Z");
        this.output.println("    # index over length");
        this.output.println("    ldi    r20, 2");
        this.output.println("    ldi    r21, 0");
        this.output.println("    add    r30, r20");
        this.output.println("    adc    r31, r21");
        this.output.println("    # loading array reference");
        AVRgenVisitor.RegPair ref = this.genLoadExp(exp, 22);
        this.output.println("    # add array reference to result of indexing arithmetic");
        this.output.println("    add    r30, " + ref.lowbit);
        this.output.println("    adc    r31, " + ref.highbit);
        this.output.flush();
    }


    private Type getFunctionCall(IExp iExp, String id,LinkedList<IExp> linkedList) {
        Type var = this.mCurrentST.getExpType(iExp);
        //System.out.println(var.className);
        //System.out.println(this.mCurrentST.getCurrentScope().getScopeID());
        ClassSTE classSTE = (ClassSTE) this.mCurrentST.lookup(var.className);
        //System.out.println(id);
        //System.out.println(classSTE);
        MethodSTE methodSTE = (MethodSTE) classSTE.getScope().lookup(id);
        Signature signature = methodSTE.getSignature();
        this.output.println();
        this.output.println("    #### function call");
        this.output.println("    # put parameter values into appropriate registers");
        ArrayList list = new ArrayList(linkedList);
        for (int i = list.size()-1; i >= 0; i--) {
            IExp para = (IExp)list.get(i);
            Type type = this.mCurrentST.getExpType(para);
            AVRgenVisitor.RegPair regPair = this.genLoadExp(para, 24 - 2 * (i+1));
            Type formalType = signature.getFormals().get(i);
            if (type == Type.BYTE && formalType == Type.INT) {
                this.promoteReg(regPair);
            }

        }
        this.output.println("    # receiver will be passed as first param");
        this.genLoadExp(iExp,24);
        this.output.println();
        this.output.println("    call    " +var.className + '_' + methodSTE.getName());
        return methodSTE.getSignature().getReturn_type();

    }
    public void outCallStatement(CallStatement callStatement) {
        this.getFunctionCall(callStatement.getExp(),callStatement.getId(),callStatement.getArgs());
        this.output.flush();

    }

    public void outCallExp(CallExp callExp) {
        this.getFunctionCall(callExp.getExp(),callExp.getId(),callExp.getArgs());
        this.output.println();
        this.output.println("    # handle return value");
        AVRgenVisitor.RegPair reg = new AVRgenVisitor.RegPair("r24", "r25");
        this.genStoreExp(callExp, reg);
        this.output.flush();

    }

    public void outNewArrayExp(NewArrayExp newArrayExp) {
        this.output.println();
        this.output.println("    ### NewArrayExp, allocating a new array");
        this.output.println("    # loading array size in elements");
        AVRgenVisitor.RegPair regPair = this.genLoadExp(newArrayExp.getExp(), 26);
        if(this.mCurrentST.getExpType(newArrayExp.getExp()) == Type.BYTE) {
            this.promoteReg(regPair);
        }
        this.output.println("    # since num elems might be in caller-saved registers");
        this.output.println("    # need to push num elems onto the stack around call to malloc");
        this.output.println("    # if had three-address code reg alloc could work around this");
        this.output.println("    push   " + regPair.highbit);
        this.output.println("    push   " + regPair.lowbit);
        if(this.mCurrentST.getExpType(newArrayExp) == Type.INTARRAY) {
            this.output.println("    # add size in elems to self to multiply by 2");
            this.output.println("    # complements of Jason Mealler");
            this.output.println("    add    " + regPair.lowbit+ "," + regPair.lowbit);
            this.output.println("    adc    " + regPair.highbit + "," + regPair.highbit);
        }
        this.output.println("    # need bytes for elems + 2 in bytes");
        this.output.println("    ldi    r24, 2");
        this.output.println("    ldi    r25, 0");
        this.output.println("    add    r24," + regPair.lowbit);
        this.output.println("    adc    r25," + regPair.highbit);
        this.output.println("    # call malloc, it expects r25:r24 as input");
        this.output.println("    call   malloc");
        this.output.println("    # set .length field to number of elements");
        this.output.println("    mov    r31, r25");
        this.output.println("    mov    r30, r24");
        this.output.println("    pop    " + regPair.lowbit);
        this.output.println("    pop    " + regPair.highbit);
        this.output.println("    std    Z+0," + regPair.lowbit);
        this.output.println("    std    Z+1," + regPair.highbit);
        this.output.println("    # store object address");
        //System.out.println(mCurrentST.getExpType(newArrayExp).getAVRTypeSize() + mCurrentST.getExpType(newArrayExp).toString());
        this.genStoreExp(newArrayExp, new AVRgenVisitor.RegPair("r30", "r31"));
        this.output.flush();

    }
    public void outAssignStatement(AssignStatement assignStatement) {
        VarSTE name = (VarSTE) this.mCurrentST.lookup(assignStatement.getId());
        this.output.println();
        this.output.println("    ### AssignStatement");
        this.output.println("    # load rhs exp");
        //System.out.println(this.mCurrentST.getExpType(assignStatement.getExp()).toString()+" " + name.getType().toString());
        AVRgenVisitor.RegPair regPair = this.genLoadExp(assignStatement.getExp(), 24);
        if (this.mCurrentST.getExpType(assignStatement.getExp()) == Type.BYTE && name.getType() == Type.INT )
            this.promoteReg(regPair);
        if (name.isMember()) {
            this.output.println();
            this.output.println("    # loading the implicit \"this\"");
            this.output.println();
            this.output.println("    # load a two byte variable from base+offset");
            this.output.println("    ldd    r31, Y + 2");
            this.output.println("    ldd    r30, Y + 1");
        }
        this.output.println("    # store rhs into var " + name.getName());
        this.genStoreVar(name, regPair);
        this.output.flush();
    }

    public void outArrayAssignStatement(ArrayAssignStatement arrayAssignStatement) {
        VarSTE varSTE = (VarSTE) this.mCurrentST.lookup(arrayAssignStatement.getIdLit().getLexeme());
        this.output.println();
        this.output.println("    ### ArrayAssignStatement");
        this.output.println("    # load rhs");
        RegPair regPair = this.genLoadExp(arrayAssignStatement.getExp(),24);
        if(this.mCurrentST.getExpType(arrayAssignStatement.getExp()) == Type.BYTE && varSTE.getType() == Type.INT) {
            this.promoteReg(regPair);
        }
        this.getAddress(varSTE.getType(),arrayAssignStatement.getIdLit(),arrayAssignStatement.getIndex());
        this.output.println("    # store rhs into memory location for array element");
        if (varSTE.getType() != Type.COLORARRAY) {
            this.output.println("    std    Z+0, " + regPair.lowbit);
            this.output.println("    std    Z+1, " + regPair.highbit);
        }
        else {
            this.output.println("    std    Z+0, " + regPair.lowbit);
        }

        this.output.flush();
    }

    public void outLengthExp(LengthExp lengthExp) {
        this.output.println();
        this.output.println("    # length of array\n" +
                "    # load a two byte expression off stack\n" +
                "    pop    r30\n" +
                "    pop    r31\n" +
                "    ldd   r24, Z+0\n" +
                "    ldd   r25, Z+1\n" +
                "    # push two byte expression onto stack\n" +
                "    push   r25\n" +
                "    push   r24");
        this.output.flush();
    }

    private void genStoreVar(VarSTE name, RegPair regPair) {
        if(name.getType().getAVRTypeSize() == 1) {
            this.output.println("    std    " + name.getBase() + " + " + name.getOffset() + ", " + regPair.lowbit);
        } else if(name.getType().getAVRTypeSize() == 2) {
            this.output.println("    std    " + name.getBase() + " + " + (name.getOffset() + 1) + ", " + regPair.highbit);
            this.output.println("    std    " + name.getBase() + " + " + name.getOffset() + ", " + regPair.lowbit);
        }
    }

    public void outIdLiteral(IdLiteral idLiteral) {
        VarSTE varSTE = (VarSTE) this.mCurrentST.lookup(idLiteral.getLexeme());
        this.output.println();
        this.output.println("    # IdExp");
        this.output.println("    # load value for variable " + idLiteral.getLexeme());
        if (varSTE.isMember()) {
            this.output.println();
            this.output.println("    # loading the implicit \"this\"");
            this.output.println();
            this.output.println("    # load a two byte variable from base+offset");
            this.output.println("    ldd    r31, Y + 2");
            this.output.println("    ldd    r30, Y + 1");
            this.output.println("    # variable is a member variable");
        } else {
            this.output.println("    # variable is a local or param variable");
        }
        AVRgenVisitor.RegPair regPair = this.genLoadVar(varSTE, 24);
        this.genStoreExp(idLiteral,regPair);
    }

    private void genLoadThis() {
        AVRgenVisitor.RegPair regPair = new AVRgenVisitor.RegPair("r30", "r31");
        this.output.println();
        this.output.println("    # loading the implicit \"this\"");
        this.output.println();
        this.output.println("    # load a two byte variable from base+offset");
        this.output.println("    ldd    r31, Y + 2");
        this.output.println("    ldd    r30, Y + 1");
        this.output.println("    # push two byte expression onto stack");
        this.output.println("    push   r31");
        this.output.println("    push   r30");

    }

    private RegPair genLoadVar(VarSTE varSTE, int i) {
        AVRgenVisitor.RegPair regPair = new AVRgenVisitor.RegPair("r" + i, "r" + (i + 1));
        this.output.println();
        if(varSTE.getType().getAVRTypeSize() == 2) {
            this.output.println("    # load a two byte variable from base+offset");
            this.output.println("    ldd    " + regPair.highbit +", "+ varSTE.getBase() + " + " + (varSTE.getOffset()+1));
            this.output.println("    ldd    " + regPair.lowbit + ", " + varSTE.getBase() + " + " + varSTE.getOffset());
        } else {
            this.output.println("    # load a one byte expression off stack");
            this.output.println("    ldd    " + regPair.lowbit + "," + varSTE.getBase() + " + " + varSTE.getOffset());
        }
        return regPair;
    }
    public void inMethodDecl(MethodDecl methodDecl) {
        this.output.println();
        MethodSTE methodSTE = (MethodSTE) this.mCurrentST.lookup(methodDecl.getName());
        this.mCurrentST.pushScope(methodDecl.getName());
        this.output.println("    .text");
        this.output.println(".global " + currentClass+"_"+methodDecl.getName());
        this.output.println("    .type  " + currentClass +"_" + methodDecl.getName() + ", @function");
        this.output.println(currentClass + "_" + methodDecl.getName() + ":");
        this.output.println("    push   r29");
        this.output.println("    push   r28");
        this.output.println("    # make space for locals and params");
        this.output.println("    ldi    r30, 0");
        int total = 0;
        HashMap<String, STE> dic = methodSTE.getScope().dictionary;
        for (Map.Entry<String, STE> entry: dic.entrySet()) {
            if (entry.getValue() instanceof VarSTE) {
                total += ((VarSTE) entry.getValue()).getType().getAVRTypeSize();
            }
        }
        for (int i = 0; i < total; i++) {
            this.output.println("    push   r30");
        }
        this.output.println();
        this.output.println("    # Copy stack pointer to frame pointer");
        this.output.println("    in     r28,__SP_L__");
        this.output.println("    in     r29,__SP_H__");
        this.output.println();
        this.output.println("    # save off parameters");
        this.output.println("    std    Y + 2, r25");
        this.output.println("    std    Y + 1, r24");
        int var = 24;
        int offset = 3;
        ArrayList<Formal> arrayList = new ArrayList(methodDecl.getFormals());
        for (int i = 0 ; i < arrayList.size();i++) {
            var -= 2;
            IType para = arrayList.get(i).getType();
            Type type = getType(para);
            if(type.getAVRTypeSize() == 2) {
                this.output.println("    std    Y + " +( offset+1) +", r" + (var+1));
            }
            this.output.println("    std    Y + " + offset + ", r" + var);
            offset += type.getAVRTypeSize();

        }
        this.output.println("/* done with function " + currentClass +"_" + methodDecl.getName() +" prologue */");
        this.output.println();

    }
    public void outMethodDecl(MethodDecl methodDecl) {
        this.output.println();
        this.output.println("/* epilogue start for "+currentClass+"_"+methodDecl.getName()+" */");
        if (methodDecl.getExp() != null) {
            this.output.println("    # handle return value");
            RegPair regPair = new RegPair("r24", "r25");
            this.genMoveReg(regPair,this.genLoadExp(methodDecl.getExp(),24));
            if (this.mCurrentST.getExpType(methodDecl.getExp()).getAVRTypeSize() == 1) {
                this.promoteReg(regPair);
            }
        } else {
            this.output.println("    # no return value");
        }

        this.output.println("    # pop space off stack for parameters and locals");

        int total = 0;
        MethodSTE methodSTE = (MethodSTE) this.mCurrentST.lookup(methodDecl.getName());
        HashMap<String, STE> dic = methodSTE.getScope().dictionary;
        for (Map.Entry<String, STE> entry: dic.entrySet()) {
            if (entry.getValue() instanceof VarSTE) {
                total += ((VarSTE) entry.getValue()).getType().getAVRTypeSize();
            }
        }
        for (int i = 0; i < total; i++) {
            this.output.println("    pop    r30");
        }

        this.output.println("    # restoring the frame pointer");
        this.output.println("    pop    r28");
        this.output.println("    pop    r29");
        this.output.println("    ret");
        this.output.println("    .size " + currentClass + "_" + methodDecl.getName() + ", .-" + currentClass + "_" + methodDecl.getName());
        this.output.println();
        this.output.flush();
        this.mCurrentST.popScope();


    }

    public void inTopClassDecl(TopClassDecl topClassDecl) {

        currentClass = topClassDecl.getName();
        this.mCurrentST.pushScope(topClassDecl.getName());
    }
    public void outTopClassDecl(TopClassDecl topClassDecl) {
        this.mCurrentST.popScope();
    }


    public String convertStreamToString(InputStream inputStream) {
        BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder buildString = new StringBuilder();
        String string = null;

        try {
            while((string = buffer.readLine()) != null) {
                buildString.append(string + "\n");
            }
        } catch (IOException ioexcept) {
            ioexcept.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException var) {
                var.printStackTrace();
            }

        }

        return buildString.toString();
    }

    private class RegPair {
        public String lowbit;
        public String highbit;

        public RegPair (String low, String high) {
            this.lowbit = low;
            this.highbit = high;
        }
    }

    public void outThisExp(ThisLiteral thisLiteral) {
        this.output.println();
            this.genLoadThis();
            this.output.flush();
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

}
