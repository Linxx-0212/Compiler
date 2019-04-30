import meggy.Meggy;

class Test {

    public static void main(String[] whatever){
        {
            new A().call();
        }
    }
}

class A {
	int a;
	Meggy.Color[] array;
	int[] int_array;
	public int call(){
		a=new B().call();
		array=new Meggy.Color[3];
		int_array=new int[30];
		array[2]=Meggy.Color.BLUE;
		a=int_array.length;
		int_array[20]=3;
		int_array[array.length]=int_array.length;
		return 1;
	}
}
class B{
	int a;
	public int call(){
		C c;
		c=new C();
		c.init();
		// return c.call().call();
		return new E().call(c.call()).getE().kall(c.call()).call();
		// return new E().kall(new D()).call();

	}
}
class C{
	D d;
	public void init(){
		d=new D();
	}
	public D call(){
		return d;
	}
}
class D{
	public int call(){
		return 4;
	}
	public E getE(){
		return new E();
	}
}
class E{
	public D call(D d){
		int[] index_array;
		Meggy.Color[] color_array;
		int index;
		index=0;
		index_array=new int[10];
		color_array=new Meggy.Color[10];
		index_array[0]=0;
		index_array[1]=1;
		index_array[2]=2;
		while(index<index_array.length){
			if(index_array[index]==0){
				color_array[index]=Meggy.Color.BLUE;
			}else if(index_array[index]==1){
				color_array[index]=Meggy.Color.ORANGE;
			}else if(index_array[index]==2){
				color_array[index]=Meggy.Color.YELLOW;
			}else{
				color_array[index]=Meggy.Color.WHITE;
			}
			index=index+1;
		}	
		return d;
	}
	public D kall(D d){
		int[] index_array;
		Meggy.Color[] color_array;
		int index;
		index=0;
		index_array=new int[10];
		color_array=new Meggy.Color[10];
		color_array[0]=Meggy.Color.BLUE;
		color_array[1]=Meggy.Color.ORANGE;
		color_array[2]=Meggy.Color.YELLOW;
		while(index<index_array.length){
			if(color_array[index]==Meggy.Color.BLUE){
				index_array[index]=0;
			}else if(color_array[index]==Meggy.Color.ORANGE){
				index_array[index]=1;
			}else if(color_array[index]==Meggy.Color.YELLOW){
				index_array[index]=2;
			}else{
				index_array[index]=3;
			}
			index=index+1;
		}	
		return d;
	}
}