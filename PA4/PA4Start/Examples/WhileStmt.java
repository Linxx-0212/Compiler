import meggy.Meggy;

class WhileStmt {
    public static void main (String [] args) {
        while (Meggy.getPixel((byte) 0, (byte) 0) == Meggy.Color.GREEN){
            if(Meggy.checkButton(Meggy.Button.A)) {
                Meggy.setPixel((byte) 1, (byte) 3, Meggy.Color.ORANGE);
            }else {
                if (Meggy.checkButton(Meggy.Button.B)) {
                    Meggy.setPixel((byte) 1, (byte) 3, Meggy.Color.RED);
                }
            }
        }
    }
}