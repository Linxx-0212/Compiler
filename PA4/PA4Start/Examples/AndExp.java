import meggy.Meggy;

class AndExp{
    public static void main(String[] whatever) {
        if( (byte)(2+3) < 1 && 1+1 == 2)
            Meggy.setPixel((byte)3, (byte)2, Meggy.Color.DARK);
    }
}