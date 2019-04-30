/**
 * PA4bluedot.java
 * 
 * Set two blue pixels but first call another function and have 
 * that function do one.
 * Also passing parameters.  Want to test that we are cleaning up in 
 * function epilogues correctly.
 *   
 * MMS, 2/9/11
 */

import meggy.Meggy;

class test1 {

    public static void main(String[] whatever){
        {
            new Simple().bluedot((byte)3, (byte)7);    
        }
    }
}

class Simple {
    
    public void bluedot(int x, int y) {
        Meggy.setPixel((byte)x, (byte)y, Meggy.Color.BLUE );
    }

}
