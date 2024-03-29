package symtable;


import exceptions.*;

import java.util.*;

public class Type
{
  public static final Type BOOL = new Type();
  public static final Type INT = new Type();
  public static final Type BYTE = new Type();
  public static final Type COLOR = new Type();
  public static final Type BUTTON = new Type();
  public static final Type VOID = new Type();
  public static final Type TONE = new Type();
  public static final Type COLORARRAY = new Type();
  public static final Type INTARRAY = new Type();
  public static final Type CLASS = new Type();
  public static final HashMap<String, Type> classTypes = new HashMap<>();
  public final String className;

  public Type(String className) {
    this.className = className;
  }

  private Type() {
    this.className = null;
  }

    
/*
*/

  public String toString()
  {
    if(this == INT)
    {
      return "INT";
    }

    if(this == BOOL)
    {
      return "BOOL";
    }

    if(this == BYTE)
    {
      return "BYTE";
    }

    if(this == COLOR)
    {
      return "COLOR";
    }

    if(this == BUTTON)
    {
      return "BUTTON";
    }
    if(this == TONE)
    {
      return "TONE";
    }

    if(this == VOID)
    {
      return "VOID";
    }
    if(this == COLORARRAY)
    {
      return "COLORARRAY";
    }

    if(this == INTARRAY)
    {
      return "INTARRAY";
    }
    
/*
*/
    return "class_"+this.className;
  }
  
  public int getAVRTypeSize() {
      if(this == INT) { return 2; }
      if(this == BOOL) { return 1; }
      if(this == BYTE) { return 1; }
      if(this == COLOR) { return 1; }
      if(this == BUTTON) { return 1; }
      if(this == VOID) { return 0; }

      return 2; // class references are 2 bytes
  }

  public static Type getClassType(String className) {
    Type type = classTypes.get(className);
    if(type == null) {
      type = new Type(className);
      if(!classTypes.containsKey(className))
        classTypes.put(className,type);
    }
    return type;
  }

    
/*  
*/

}
