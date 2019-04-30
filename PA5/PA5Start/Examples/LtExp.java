import meggy.Meggy;

class LtExp{
    public static void main(String[] whatever) {
        if( (byte)(2+3) < 1)
            Meggy.setPixel((byte)3, (byte)2, Meggy.Color.DARK);
    }
}