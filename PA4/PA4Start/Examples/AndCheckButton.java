import meggy.Meggy;

class AndCheckButton {
    public static void main(String[] whatever) {
        if (Meggy.checkButton(Meggy.Button.Up)
                && (Meggy.checkButton(Meggy.Button.Right)))
        {
            Meggy.setPixel((byte)0, (byte)0, Meggy.Color.DARK);

            Meggy.delay(256);
        }
    }

}
