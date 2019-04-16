import meggy.Meggy;

class goodTestCase2 {

    public static void main(String[] whatever){
      new aClass().setColor(Meggy.Color.BLUE);
    }
}

class aClass {
    int[] b;
    Meggy.Color[] color;

    public void setColor(Meggy.Color c){
     color = new Meggy.Color[2];
     color[1] = c;
    }

}
