import meggy.Meggy;

class LengthExp{
    public static void main(String[] whatever){
        new callClass().callMethod();
    }
}

class callClass{
    public void callMethod() {
        int[] arr;
        byte ret;
        Meggy.Color[] colors;
        arr = new int[3];
        ret = (byte)(arr.length);
        colors = new Meggy.Color[5];
        colors[0] = Meggy.Color.DARK;
    }
}
