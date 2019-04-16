package ast_visitors;


/** 
 * CheckTypes
 * 
 * This AST visitor traverses a MiniJava Abstract Syntax Tree and checks
 * for a number of type errors.  If a type error is found a SymanticException
 * is thrown
 * 
 * CHANGES to make next year (2012)
 *  - make the error messages between *, +, and - consistent <= ??
 *
 * Bring down the symtab code so that it only does get and set Type
 *  for expressions
 */

import ast.node.*;
import ast.visitor.DepthFirstVisitor;
import java.util.*;


import symtable.*;
import exceptions.InternalException;
import exceptions.SemanticException;

public class CheckTypes extends DepthFirstVisitor
{
    
   private SymTable mCurrentST;
   private String cuurentClass;
   
   public CheckTypes(SymTable st) {
     if(st==null) {
          throw new InternalException("unexpected null argument");
      }
      mCurrentST = st;
     this.cuurentClass = null;
   }
   
   //========================= Overriding the visitor interface

   public void defaultOut(Node node) {
       //System.err.println("Node not implemented in CheckTypes, " + node.getClass());
   }


   
   public void outAndExp(AndExp node)
   {
     if(this.mCurrentST.getExpType(node.getLExp()) != Type.BOOL) {
       throw new SemanticException(
         "Invalid left operand type for operator &&",
         node.getLExp().getLine(), node.getLExp().getPos());
     }

     if(this.mCurrentST.getExpType(node.getRExp()) != Type.BOOL) {
       throw new SemanticException(
         "Invalid right operand type for operator &&",
         node.getRExp().getLine(), node.getRExp().getPos());
     }

     this.mCurrentST.setExpType(node, Type.BOOL);
   }
  
   public void outPlusExp(PlusExp node)
   {
       Type lexpType = this.mCurrentST.getExpType(node.getLExp());
       Type rexpType = this.mCurrentST.getExpType(node.getRExp());
       if ((lexpType==Type.INT  || lexpType==Type.BYTE) &&
           (rexpType==Type.INT  || rexpType==Type.BYTE)
          ){
           this.mCurrentST.setExpType(node, Type.INT);
       } else {
           throw new SemanticException(
                   "Operands to + operator must be INT or BYTE",
                   node.getLExp().getLine(),
                   node.getLExp().getPos());
       }

   }
   public void outEqualExp(EqualExp node) {
   	Type lexpType = this.mCurrentST.getExpType(node.getLExp());
   	Type rexpType = this.mCurrentST.getExpType(node.getRExp());
   	if (lexpType == rexpType||((lexpType == Type.BYTE||lexpType == Type.INT)&&(rexpType == Type.BYTE || rexpType == Type.INT))) {
   		this.mCurrentST.setExpType(node,Type.BOOL);
   	} else {
   		throw new SemanticException(
   				"Operands to == operator must be equal",
   				node.getLExp().getLine(),
   				node.getLExp().getPos());
   	}
   }

   public void outMinusExp(MinusExp node) {
   	Type lexpType = this.mCurrentST.getExpType(node.getLExp());
   	Type rexpType = this.mCurrentST.getExpType(node.getRExp());
   	if ((lexpType == Type.INT || lexpType == Type.BYTE) &&
   		(rexpType == Type.INT || rexpType == Type.BYTE)
   		) {
   		this.mCurrentST.setExpType(node, Type.INT);
   	} else {
   		throw new SemanticException(
   				"Operands to - operator must be INT or BYTE",
   				node.getLExp().getLine(),
   				node.getLExp().getPos());
   	  }
   }

   public void outMulExp(MulExp node) {
   	Type lexpType = this.mCurrentST.getExpType(node.getLExp());
   	Type rexpType = this.mCurrentST.getExpType(node.getRExp());
   	if ((lexpType == Type.INT || lexpType == Type.BYTE) &&
   		(rexpType == Type.INT || rexpType == Type.BYTE)
   		) {
   		this.mCurrentST.setExpType(node, Type.INT);
   	} else {
   		throw new SemanticException(
   				"Operands to * operator must be INT or BYTE",
   				node.getLExp().getLine(),
   				node.getLExp().getPos());
   	  }
   }

   public void outNegExp(NegExp node) {
   	Type expType = this.mCurrentST.getExpType(node.getExp());
   	if (expType == Type.INT || expType == Type.BYTE) {
   		this.mCurrentST.setExpType(node, Type.INT);
   	} else {
   		throw new SemanticException(
   				"Operand to - operator must be INT or BYTE",
   				node.getExp().getLine(),
   				node.getExp().getPos());
   	}
   }

   public void outLtExp(LtExp node) {
   	Type lexpType = this.mCurrentST.getExpType(node.getLExp());
   	Type rexpType = this.mCurrentST.getExpType(node.getRExp());
   	if ((lexpType == Type.INT || lexpType == Type.BYTE) &&
   		(rexpType == Type.INT || rexpType == Type.BYTE)
   		) {
   		this.mCurrentST.setExpType(node, Type.BOOL);
   	} else {
   		throw new SemanticException(
   				"Operands to < operator must be INT or BYTE",
   				node.getLExp().getLine(),
   				node.getLExp().getPos());
   	}
   }

   public void outNotExp(NotExp node) {
   	Type expType = this.mCurrentST.getExpType(node.getExp());
   	if (expType == Type.BOOL) {
   		this.mCurrentST.setExpType(node, Type.BOOL);
   	} else {
   		throw new SemanticException(
   				"Operand to - operator must be BOOL",
   				node.getExp().getLine(),
   				node.getExp().getPos());
   	}
   }

   public void outCallExp(CallExp node) {
	   Scope global = this.mCurrentST.getGlobalScope();
	   String methodName = node.getId();
	   String className = "";
	   STE classSTE = null;
	   if (node.getExp() instanceof  ThisLiteral) {
		   className = this.cuurentClass;
		   classSTE = this.mCurrentST.getGlobalScope().lookup(className);
	   }
	   else {
		   if (node.getExp() instanceof NewExp) {
			   className = ((NewExp) node.getExp()).getId();
			   classSTE = this.mCurrentST.getGlobalScope().lookup(className);
		   } else {
			   //System.out.println(callStatement.getId());
			   //System.out.println(callStatement.getExp());
			   className = mCurrentST.getExpType(node.getExp()).toString();
			   className = className.replace("class_","");
			   //VarSTE var = (VarSTE) this.mCurrentST.lookup(mCurrentST.getExpType(callStatement.getExp()));
			   // System.out.println(var.getName());
			   classSTE = this.mCurrentST.lookup(className);

		   }
	   }
	   STE methodSTE = null;
	   if(classSTE != null) {
		   methodSTE = ((ClassSTE)classSTE).getMethodSTE(methodName);
	   }
	   if(methodSTE != null) {

		   List<Type> ParamList = ((MethodSTE)methodSTE).getSignature().getFormals();
		   List<IExp> callParam = node.getArgs();
		   if (ParamList.size() != callParam.size()) {
			   throw new SemanticException(
					   "Wrong Args number",
					   node.getLine(),
					   node.getPos()
			   );
		   }
		   Iterator<Type> args = ParamList.iterator();
		   Iterator<IExp> param = callParam.iterator();
		   while(args.hasNext()&&param.hasNext()) {
			   String rexpType = "";
			   Type lexp = args.next();
			   IExp rexp = param.next();
			   rexpType = this.mCurrentST.getExpType(rexp).toString();
			   if (!rexpType.equalsIgnoreCase(lexp.toString())) {
				   throw new SemanticException(
						   "Wrong Args Type",
						   node.getLine(),
						   node.getPos()
				   );
			   }
		   }
	   }
	   else {
		   throw new SemanticException(
				   "Method:"+methodName+" is not Found",
				   node.getLine(),
				   node.getPos()
		   );
	   }

	   this.mCurrentST.setExpType(node,((MethodSTE)methodSTE).getSignature().getReturn_type());

   }

   public void outCallStatement(CallStatement callStatement) {
	   Scope global = this.mCurrentST.getGlobalScope();
	   String methodName = callStatement.getId();
	   String className = "";
	   STE classSTE = null;
	   if (callStatement.getExp() instanceof  ThisLiteral) {
		   className = this.cuurentClass;
		   classSTE = this.mCurrentST.getGlobalScope().lookup(className);
	   }
	   else {
		   if (callStatement.getExp() instanceof NewExp) {
			   className = ((NewExp) callStatement.getExp()).getId();
			   classSTE = this.mCurrentST.getGlobalScope().lookup(className);
		   } else {
			   //System.out.println(callStatement.getId());
			   //System.out.println(callStatement.getExp());
			   className = mCurrentST.getExpType(callStatement.getExp()).toString();
			   className = className.replace("class_","");
			   //VarSTE var = (VarSTE) this.mCurrentST.lookup(mCurrentST.getExpType(callStatement.getExp()));
			   //System.out.println(className);
			   classSTE = this.mCurrentST.getGlobalScope().lookup(className);


		   }
	   }
	   STE methodSTE = null;
	   if(classSTE != null) {
		   methodSTE = ((ClassSTE)classSTE).getMethodSTE(methodName);
	   }
	   if(methodSTE != null) {

		   List<Type> ParamList = ((MethodSTE)methodSTE).getSignature().getFormals();
		   List<IExp> callParam = callStatement.getArgs();
		   if (ParamList.size() != callParam.size()) {
			   throw new SemanticException(
					   "Wrong Args number",
					   callStatement.getLine(),
					   callStatement.getPos()
			   );
		   }
		   Iterator<Type> args = ParamList.iterator();
		   Iterator<IExp> param = callParam.iterator();
		   while(args.hasNext()&&param.hasNext()) {
			   String rexpType = "";
			   Type lexp = args.next();
			   IExp rexp = param.next();
			   rexpType = this.mCurrentST.getExpType(rexp).toString();
			   if (!rexpType.equalsIgnoreCase(lexp.toString())) {
				   throw new SemanticException(
						   "Wrong Args Type",
						   callStatement.getLine(),
						   callStatement.getPos()
				   );
			   }
		   }
	   }
	   else {
		   throw new SemanticException(
				   "Method:"+methodName+" is not Found",
				   callStatement.getLine(),
				   callStatement.getPos()
		   );
	   }

	   this.mCurrentST.setExpType(callStatement,((MethodSTE)methodSTE).getSignature().getReturn_type());

   }


   public void outByteCast(ByteCast node) {
   	Type expType = this.mCurrentST.getExpType(node.getExp());
   	//System.out.println(expType.toString());
   	if (expType.toString() == Type.INT.toString() || expType.toString() == Type.BYTE.toString()) {
   		this.mCurrentST.setExpType(node, Type.BYTE);
	}
	else {
   		throw new SemanticException(
   				"Operands to Byte operator must be INT or BYTE",
				node.getExp().getLine(),
				node.getExp().getPos()
		);
	}
   }

   public void outMeggyDelay(MeggyDelay node) {
   	String expType = this.mCurrentST.getExpType(node.getExp()).toString();

   	if (expType == Type.INT.toString()) {
   		this.mCurrentST.setExpType(node, Type.VOID);
	}
	else {
   		throw new SemanticException(
   				"Operands to Delay must be INT",
				node.getExp().getLine(),
				node.getExp().getPos()
		);
	}
   }
   public void outMeggyToneStart(MeggyToneStart node) {
   	String toneExpType = this.mCurrentST.getExpType(node.getToneExp()).toString();
   	String durationExpType = this.mCurrentST.getExpType(node.getDurationExp()).toString();

   	if ((toneExpType.equalsIgnoreCase(Type.TONE.toString()))&&(durationExpType.equalsIgnoreCase(Type.INT.toString()))) {
   		this.mCurrentST.setExpType(node, Type.VOID);
	}
	else {
   		throw new SemanticException(
   				"Operands to ToneStart must b TOne,INT",
				node.getToneExp().getLine(),
				node.getToneExp().getPos()
		);
	}

   }

   public void outToneExp(ToneLiteral tonLiteral) {
   	this.mCurrentST.setExpType((Node)tonLiteral,Type.TONE);
   }

   public void outMeggySetPixel(MeggySetPixel node) {
   	String lexpType = this.mCurrentST.getExpType(node.getYExp()).toString();
   	String rexpType = this.mCurrentST.getExpType(node.getXExp()).toString();
   	String colorexpType = this.mCurrentST.getExpType(node.getColor()).toString();

   	if ((lexpType == Type.INT.toString()|| lexpType == Type.BYTE.toString())&&
			(rexpType == Type.INT.toString()||rexpType == Type.BYTE.toString())&&
			colorexpType == Type.COLOR.toString()) {
   		this.mCurrentST.setExpType(node, Type.VOID);
	} else {
   		throw new SemanticException(
   				"Operands to MeggySetPixel must be INT or BYTE,INT or BYTE,COLOR",
				node.getXExp().getLine(),
				node.getXExp().getPos()
		);
	}
   }

   public void outMeggyCheckButton(MeggyCheckButton node) {
   	String expType = this.mCurrentST.getExpType(node.getExp()).toString();

   	if(expType == Type.BUTTON.toString()) {
   		this.mCurrentST.setExpType(node, Type.BOOL);
	} else {
   		throw new SemanticException(
   				"Operands to MeggyCheckButton must be ButtonLiteral",
				node.getExp().getLine(),
				node.getExp().getPos()
		);
	}
   }
   public void outMeggyGetPixel(MeggyGetPixel node) {
   	String lexpType = this.mCurrentST.getExpType(node.getXExp()).toString();
   	String rexpType = this.mCurrentST.getExpType(node.getYExp()).toString();

   	if((lexpType == Type.INT.toString()|| lexpType == Type.BYTE.toString())&&
			(rexpType == Type.INT.toString() || rexpType == Type.BYTE.toString()))
	   {
	   	this.mCurrentST.setExpType(node,Type.COLOR);
	   }
	   else
	   	throw new SemanticException(
	   			"Operands to MeggyGetPixel must be INT or BYTE",
				node.getXExp().getLine(),
				node.getXExp().getPos()
		);
   }

   public void outNewExp(NewExp newExp) {
   	STE ste = this.mCurrentST.getGlobalScope().lookup(newExp.getId());
   	if (ste == null) {
   		throw new SemanticException(
   				"Undeclared class type",
				newExp.getLine(),
				newExp.getPos()
		);
	}
	if(ste instanceof ClassSTE)
		this.mCurrentST.setExpType(newExp, Type.getClassType(newExp.getId()));
   	else
   		throw new SemanticException(
   				"wrong new exp",
				newExp.getLine(),
				newExp.getPos()
		);
   }

   public void outNewArrayExp(NewArrayExp newArrayExp) {
   	Type type = mCurrentST.getExpType(newArrayExp.getExp());
   	if (type != Type.INT) {
   		throw new SemanticException("Length should be INT",
		newArrayExp.getExp().getLine(),
		newArrayExp.getExp().getPos());
	}
   	if (getIType(newArrayExp.getType()).toString()==Type.COLOR.toString())
   		mCurrentST.setExpType(newArrayExp,Type.COLORARRAY);
   	else
   		mCurrentST.setExpType(newArrayExp,Type.INTARRAY);
   }


   public void outColorExp(ColorLiteral colorLiteral) {
   	this.mCurrentST.setExpType((Node)colorLiteral,Type.COLOR);
   }

   public void outButtonExp(ButtonLiteral buttonLiteral) {
   	this.mCurrentST.setExpType((Node)buttonLiteral,Type.BUTTON);
   }

   public void outIdLiteral(IdLiteral idLiteral) {
   	STE ste = this.mCurrentST.lookup(idLiteral.getLexeme());

   	if (ste == null) {
   		throw new SemanticException(
   				"Undeclared variable"+idLiteral.getLexeme(),
				idLiteral.getLine(),
				idLiteral.getPos()
		);
	}
	if (ste instanceof  VarSTE) {
   		VarSTE varSTE = (VarSTE) ste;
   		this.mCurrentST.setExpType((Node)idLiteral,varSTE.getType());
 	}

   }

   public void outThisExp(ThisLiteral thisLiteral) {
   	if (this.cuurentClass == null) {
   		throw new SemanticException(
   				"This: CurrntClass is null",
				thisLiteral.getLine(),
				thisLiteral.getPos()
		);
	}
	else
		this.mCurrentST.setExpType((Node)thisLiteral,Type.getClassType(this.cuurentClass));
   }

   public void inTopClassDecl(TopClassDecl topClassDecl) {
   	this.cuurentClass = topClassDecl.getName();
   	this.mCurrentST.pushScope(this.cuurentClass);
   }

   public void outTopClassDecl(TopClassDecl topClassDecl) {
   	this.mCurrentST.popScope();
   }

   public void inMethodDecl(MethodDecl methodDecl) {
   	this.mCurrentST.pushScope(methodDecl.getName());
   }

   public void outMethodDecl(MethodDecl methodDecl) {
   	this.mCurrentST.popScope();
   }

   public void outLengthExp(LengthExp lengthExp) {
   	Type type = mCurrentST.getExpType(lengthExp.getExp());
   	if(type.toString() == Type.INTARRAY.toString() || type.toString() == Type.COLORARRAY.toString()) {
   		this.mCurrentST.setExpType(lengthExp,Type.INT);
	}
	else
		throw new SemanticException(
				"operands near length is not an array",
				lengthExp.getLine(),
				lengthExp.getPos()
		);

   }

   public void outAssignStatement(AssignStatement node) {
   	ClassSTE classSTE = (ClassSTE)this.mCurrentST.lookup(this.cuurentClass);
   	VarSTE var = (VarSTE)this.mCurrentST.lookup(node.getId());
	//System.out.println(node.getId());
	if(var != null) {
   		String LType = var.getType().toString();
   		String RType = this.mCurrentST.getExpType(node.getExp()).toString();

   		if(!LType.equalsIgnoreCase(RType)) {
   			throw new SemanticException(
   					"Variable type error",
					node.getExp().getLine(),
					node.getExp().getPos()
			);
		}
		this.mCurrentST.setExpType(node,var.getType());
	}

   else {
		throw new SemanticException(
				"Variable is undecalared",
				node.getExp().getLine(),
				node.getExp().getPos()
		);
	}
	}

	public void outArrayExp(ArrayExp arrayExp)
	{
		Type type = this.mCurrentST.getExpType(arrayExp.getExp());
		Type index = this.mCurrentST.getExpType(arrayExp.getIndex());
		if (index != Type.INT) {
			throw new SemanticException("index should be INT",
					arrayExp.getExp().getLine(),
					arrayExp.getExp().getPos());
		}
		if (type.toString() == Type.INT.toString())
			mCurrentST.setExpType(arrayExp,Type.INTARRAY);
		else
			mCurrentST.setExpType(arrayExp,Type.COLORARRAY);
	}

	public void outIfStatement(IfStatement ifStatement) {
		Type type = this.mCurrentST.getExpType(ifStatement.getExp());
		if (type.toString() != Type.BOOL.toString()) {
			throw new SemanticException(
					"ifstatement should be bool",
					ifStatement.getLine(),
					ifStatement.getPos()
			);
		}
		else {
			ifStatement.setExp(ifStatement.getExp());
		}

	}

	public void outWhileStatement(WhileStatement whileStatement) {

   	Type type = this.mCurrentST.getExpType(whileStatement.getExp());
   	if (type.toString() != Type.BOOL.toString()) {
   		throw new SemanticException(
   				"whilestatement should be bool",
				whileStatement.getLine(),
				whileStatement.getPos()
		);
	}

	}

	public void outArrayAssignStatement(ArrayAssignStatement node) {

   	ClassSTE classSTE = (ClassSTE) this.mCurrentST.lookup(this.cuurentClass);
   	VarSTE var = (VarSTE) this.mCurrentST.lookup(node.getIdLit().getLexeme());

   	if (var == null) {
   		var = (VarSTE) classSTE.getScope().lookup(node.getIdLit().getLexeme());
	}

	if (var != null) {
   		String LType = var.getType().toString();
   		String RType = this.mCurrentST.getExpType(node.getExp()).toString();
   		String index = this.mCurrentST.getExpType(node.getIndex()).toString();

   		if (index  != Type.INT.toString()) {
   			throw new SemanticException(
   					"Index is not INT",
					node.getExp().getLine(),
					node.getExp().getPos()
			);
		}
		else {
   			if (!(LType == RType || (LType == Type.COLORARRAY.toString() && RType == Type.COLOR.toString())||(LType == Type.INTARRAY.toString() && RType == Type.INT.toString()))) {
   				throw new SemanticException(
   						"Type is not match",
						node.getExp().getLine(),
						node.getExp().getPos()
				);
			}
		}
		this.mCurrentST.setExpType(node,var.getType());
	}
	else {
   		throw new SemanticException(
   				"Variable is undecalared",
				node.getExp().getLine(),
				node.getExp().getPos()
		);
	}

	}

	public Type getIType(IType node){
		if(node instanceof BoolType){
			return Type.BOOL;
		}
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

		return Type.VOID;
	}

}
