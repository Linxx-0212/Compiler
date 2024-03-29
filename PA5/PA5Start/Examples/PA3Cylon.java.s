    .file  "main.java"
__SREG__ = 0x3f
__SP_H__ = 0x3e
__SP_L__ = 0x3d
__tmp_reg__ = 0
__zero_reg__ = 1
    .global __do_copy_data
    .global __do_clear_bss
    .text
.global main
    .type   main, @function
main:
    push r29
    push r28
    in r28,__SP_L__
    in r29,__SP_H__
/* prologue: function */
    call _Z18MeggyJrSimpleSetupv 
    /* Need to call this so that the meggy library gets set up */


    #### while statement
MJ_L0:

    # True/1 expression
    ldi     r22, 1
    # push one byte expression onto stack
    push   r22

    # if not(condition)
    # load a one byte expression off stack
    pop    r24
    ldi    r25,0
    cp     r24, r25
    # WANT breq MJ_L2
    brne   MJ_L1
    jmp    MJ_L2

    # while loop body
MJ_L1:

    #### if statement

    #### short-circuited && operation
    # &&: left operand

    ### MeggyCheckButton
    call    _Z16CheckButtonDownv
    lds    r24, Button_Up
    # if button value is zero, push 0 else push 1
    tst    r24
    breq   MJ_L6
MJ_L7:
    ldi    r24, 1
    jmp    MJ_L8
MJ_L6:
MJ_L8:
    # push one byte expression onto stack
    push   r24

    # &&: if left operand is false do not eval right
    # load a one byte expression off stack
    pop    r24
    # push one byte expression onto stack
    push   r24
    # compare left exp with zero
    ldi r25, 0
    cp    r24, r25
    # Want this, breq MJ_L9
    brne  MJ_L10
    jmp   MJ_L9

MJ_L10:
    # right operand
    # load a one byte expression off stack
    pop    r24

    ### MeggyCheckButton
    call    _Z16CheckButtonDownv
    lds    r24, Button_Right
    # if button value is zero, push 0 else push 1
    tst    r24
    breq   MJ_L11
MJ_L12:
    ldi    r24, 1
    jmp    MJ_L13
MJ_L11:
MJ_L13:
    # push one byte expression onto stack
    push   r24

    # not operation
    # load a one byte expression off stack
    pop    r24
    ldi     r22, 1
    eor     r24,r22
    # push one byte expression onto stack
    push   r24
    # load a one byte expression off stack
    pop    r24
    # push one byte expression onto stack
    push   r24

MJ_L9:

    # load condition and branch if false
    # load a one byte expression off stack
    pop    r24
    #load zero into reg
    ldi    r25, 0

    #use cp to set SREG
    cp     r24, r25
    #WANT breq MJ_L3
    brne   MJ_L4
    jmp    MJ_L5

    # then label for if
MJ_L4:

    # Load constant int 0
    ldi    r24,lo8(0)
    ldi    r25,hi8(0)
    # push two byte expression onto stack
    push   r25
    push   r24

    # Casting int to byte by popping
    # 2 bytes off stack and only pushing low order bits
    # back on.  Low order bits are on top of stack.
    pop    r24
    pop    r25
    push   r24

    # Load constant int 0
    ldi    r24,lo8(0)
    ldi    r25,hi8(0)
    # push two byte expression onto stack
    push   r25
    push   r24

    # Casting int to byte by popping
    # 2 bytes off stack and only pushing low order bits
    # back on.  Low order bits are on top of stack.
    pop    r24
    pop    r25
    push   r24

    # Color expression Meggy.Color.DARK
    ldi     r22,0
    # push one byte expression onto stack
    push   r22

    ### Meggy.setPixel(x,y,color) call
    # load a one byte expression off stack
    pop    r20
    # load a one byte expression off stack
    pop    r22
    # load a one byte expression off stack
    pop    r24
    call   _Z6DrawPxhhh
    call   _Z12DisplaySlatev

    # Load constant int 256
    ldi    r24,lo8(256)
    ldi    r25,hi8(256)
    # push two byte expression onto stack
    push   r25
    push   r24

    ### Meggy.delay() call
    # load delay parameter
    # load a two byte expression off stack
    pop    r24
    pop    r25
    call   _Z8delay_msj
    jmp    MJ_L5

    # else label for if
MJ_L3:

    #### if statement

    #### short-circuited && operation
    # &&: left operand

    ### MeggyCheckButton
    call    _Z16CheckButtonDownv
    lds    r24, Button_Up
    # if button value is zero, push 0 else push 1
    tst    r24
    breq   MJ_L17
MJ_L18:
    ldi    r24, 1
    jmp    MJ_L19
MJ_L17:
MJ_L19:
    # push one byte expression onto stack
    push   r24

    # &&: if left operand is false do not eval right
    # load a one byte expression off stack
    pop    r24
    # push one byte expression onto stack
    push   r24
    # compare left exp with zero
    ldi r25, 0
    cp    r24, r25
    # Want this, breq MJ_L20
    brne  MJ_L21
    jmp   MJ_L20

MJ_L21:
    # right operand
    # load a one byte expression off stack
    pop    r24

    ### MeggyCheckButton
    call    _Z16CheckButtonDownv
    lds    r24, Button_Right
    # if button value is zero, push 0 else push 1
    tst    r24
    breq   MJ_L22
MJ_L23:
    ldi    r24, 1
    jmp    MJ_L24
MJ_L22:
MJ_L24:
    # push one byte expression onto stack
    push   r24
    # load a one byte expression off stack
    pop    r24
    # push one byte expression onto stack
    push   r24

MJ_L20:

    # load condition and branch if false
    # load a one byte expression off stack
    pop    r24
    #load zero into reg
    ldi    r25, 0

    #use cp to set SREG
    cp     r24, r25
    #WANT breq MJ_L14
    brne   MJ_L15
    jmp    MJ_L16

    # then label for if
MJ_L15:

    # Load constant int 0
    ldi    r24,lo8(0)
    ldi    r25,hi8(0)
    # push two byte expression onto stack
    push   r25
    push   r24

    # Casting int to byte by popping
    # 2 bytes off stack and only pushing low order bits
    # back on.  Low order bits are on top of stack.
    pop    r24
    pop    r25
    push   r24

    # Load constant int 0
    ldi    r24,lo8(0)
    ldi    r25,hi8(0)
    # push two byte expression onto stack
    push   r25
    push   r24

    # Casting int to byte by popping
    # 2 bytes off stack and only pushing low order bits
    # back on.  Low order bits are on top of stack.
    pop    r24
    pop    r25
    push   r24

    # Color expression Meggy.Color.DARK
    ldi     r22,0
    # push one byte expression onto stack
    push   r22

    ### Meggy.setPixel(x,y,color) call
    # load a one byte expression off stack
    pop    r20
    # load a one byte expression off stack
    pop    r22
    # load a one byte expression off stack
    pop    r24
    call   _Z6DrawPxhhh
    call   _Z12DisplaySlatev

    # Load constant int 256
    ldi    r24,lo8(256)
    ldi    r25,hi8(256)
    # push two byte expression onto stack
    push   r25
    push   r24

    ### Meggy.delay() call
    # load delay parameter
    # load a two byte expression off stack
    pop    r24
    pop    r25
    call   _Z8delay_msj
    jmp    MJ_L16

    # else label for if
MJ_L14:

    #### if statement

    ### MeggyCheckButton
    call    _Z16CheckButtonDownv
    lds    r24, Button_Down
    # if button value is zero, push 0 else push 1
    tst    r24
    breq   MJ_L28
MJ_L29:
    ldi    r24, 1
    jmp    MJ_L30
MJ_L28:
MJ_L30:
    # push one byte expression onto stack
    push   r24

    # load condition and branch if false
    # load a one byte expression off stack
    pop    r24
    #load zero into reg
    ldi    r25, 0

    #use cp to set SREG
    cp     r24, r25
    #WANT breq MJ_L25
    brne   MJ_L26
    jmp    MJ_L27

    # then label for if
MJ_L26:

    # Load constant int 0
    ldi    r24,lo8(0)
    ldi    r25,hi8(0)
    # push two byte expression onto stack
    push   r25
    push   r24

    # Casting int to byte by popping
    # 2 bytes off stack and only pushing low order bits
    # back on.  Low order bits are on top of stack.
    pop    r24
    pop    r25
    push   r24

    # Load constant int 4
    ldi    r24,lo8(4)
    ldi    r25,hi8(4)
    # push two byte expression onto stack
    push   r25
    push   r24

    # Casting int to byte by popping
    # 2 bytes off stack and only pushing low order bits
    # back on.  Low order bits are on top of stack.
    pop    r24
    pop    r25
    push   r24

    # Color expression Meggy.Color.DARK
    ldi     r22,0
    # push one byte expression onto stack
    push   r22

    ### Meggy.setPixel(x,y,color) call
    # load a one byte expression off stack
    pop    r20
    # load a one byte expression off stack
    pop    r22
    # load a one byte expression off stack
    pop    r24
    call   _Z6DrawPxhhh
    call   _Z12DisplaySlatev

    # Load constant int 256
    ldi    r24,lo8(256)
    ldi    r25,hi8(256)
    # push two byte expression onto stack
    push   r25
    push   r24

    ### Meggy.delay() call
    # load delay parameter
    # load a two byte expression off stack
    pop    r24
    pop    r25
    call   _Z8delay_msj
    jmp    MJ_L27

    # else label for if
MJ_L25:

    #### if statement

    # Load constant int 0
    ldi    r24,lo8(0)
    ldi    r25,hi8(0)
    # push two byte expression onto stack
    push   r25
    push   r24

    # Casting int to byte by popping
    # 2 bytes off stack and only pushing low order bits
    # back on.  Low order bits are on top of stack.
    pop    r24
    pop    r25
    push   r24

    # Load constant int 0
    ldi    r24,lo8(0)
    ldi    r25,hi8(0)
    # push two byte expression onto stack
    push   r25
    push   r24

    # Casting int to byte by popping
    # 2 bytes off stack and only pushing low order bits
    # back on.  Low order bits are on top of stack.
    pop    r24
    pop    r25
    push   r24

    ### Meggy.getPixel(x,y) call
    # load a one byte expression off stack
    pop    r22
    # load a one byte expression off stack
    pop    r24
    call   _Z6ReadPxhh
    # push one byte expression onto stack
    push   r24

    # Color expression Meggy.Color.RED
    ldi     r22,1
    # push one byte expression onto stack
    push   r22

    # equality check expression
    # load a one byte expression off stack
    pop    r18
    # load a one byte expression off stack
    pop    r24
    cp    r24, r18
    breq MJ_L35

    # result is false
MJ_L34:
    ldi     r24, 0
    jmp      MJ_L36

    # result is true
MJ_L35:
    ldi     r24, 1

    # store result of equal expression
MJ_L36:
    # push one byte expression onto stack
    push   r24

    # load condition and branch if false
    # load a one byte expression off stack
    pop    r24
    #load zero into reg
    ldi    r25, 0

    #use cp to set SREG
    cp     r24, r25
    #WANT breq MJ_L31
    brne   MJ_L32
    jmp    MJ_L33

    # then label for if
MJ_L32:

    # Load constant int 0
    ldi    r24,lo8(0)
    ldi    r25,hi8(0)
    # push two byte expression onto stack
    push   r25
    push   r24

    # Casting int to byte by popping
    # 2 bytes off stack and only pushing low order bits
    # back on.  Low order bits are on top of stack.
    pop    r24
    pop    r25
    push   r24

    # Load constant int 0
    ldi    r24,lo8(0)
    ldi    r25,hi8(0)
    # push two byte expression onto stack
    push   r25
    push   r24

    # Casting int to byte by popping
    # 2 bytes off stack and only pushing low order bits
    # back on.  Low order bits are on top of stack.
    pop    r24
    pop    r25
    push   r24

    # Color expression Meggy.Color.RED
    ldi     r22,1
    # push one byte expression onto stack
    push   r22

    ### Meggy.setPixel(x,y,color) call
    # load a one byte expression off stack
    pop    r20
    # load a one byte expression off stack
    pop    r22
    # load a one byte expression off stack
    pop    r24
    call   _Z6DrawPxhhh
    call   _Z12DisplaySlatev

    # Load constant int 256
    ldi    r24,lo8(256)
    ldi    r25,hi8(256)
    # push two byte expression onto stack
    push   r25
    push   r24

    ### Meggy.delay() call
    # load delay parameter
    # load a two byte expression off stack
    pop    r24
    pop    r25
    call   _Z8delay_msj
    jmp    MJ_L33

    # else label for if
MJ_L31:

    #### if statement

    # Load constant int 0
    ldi    r24,lo8(0)
    ldi    r25,hi8(0)
    # push two byte expression onto stack
    push   r25
    push   r24

    # Casting int to byte by popping
    # 2 bytes off stack and only pushing low order bits
    # back on.  Low order bits are on top of stack.
    pop    r24
    pop    r25
    push   r24

    # Load constant int 4
    ldi    r24,lo8(4)
    ldi    r25,hi8(4)
    # push two byte expression onto stack
    push   r25
    push   r24

    # Casting int to byte by popping
    # 2 bytes off stack and only pushing low order bits
    # back on.  Low order bits are on top of stack.
    pop    r24
    pop    r25
    push   r24

    ### Meggy.getPixel(x,y) call
    # load a one byte expression off stack
    pop    r22
    # load a one byte expression off stack
    pop    r24
    call   _Z6ReadPxhh
    # push one byte expression onto stack
    push   r24

    # Color expression Meggy.Color.RED
    ldi     r22,1
    # push one byte expression onto stack
    push   r22

    # equality check expression
    # load a one byte expression off stack
    pop    r18
    # load a one byte expression off stack
    pop    r24
    cp    r24, r18
    breq MJ_L41

    # result is false
MJ_L40:
    ldi     r24, 0
    jmp      MJ_L42

    # result is true
MJ_L41:
    ldi     r24, 1

    # store result of equal expression
MJ_L42:
    # push one byte expression onto stack
    push   r24

    # load condition and branch if false
    # load a one byte expression off stack
    pop    r24
    #load zero into reg
    ldi    r25, 0

    #use cp to set SREG
    cp     r24, r25
    #WANT breq MJ_L37
    brne   MJ_L38
    jmp    MJ_L39

    # then label for if
MJ_L38:

    # Load constant int 0
    ldi    r24,lo8(0)
    ldi    r25,hi8(0)
    # push two byte expression onto stack
    push   r25
    push   r24

    # Casting int to byte by popping
    # 2 bytes off stack and only pushing low order bits
    # back on.  Low order bits are on top of stack.
    pop    r24
    pop    r25
    push   r24

    # Load constant int 4
    ldi    r24,lo8(4)
    ldi    r25,hi8(4)
    # push two byte expression onto stack
    push   r25
    push   r24

    # Casting int to byte by popping
    # 2 bytes off stack and only pushing low order bits
    # back on.  Low order bits are on top of stack.
    pop    r24
    pop    r25
    push   r24

    # Color expression Meggy.Color.RED
    ldi     r22,1
    # push one byte expression onto stack
    push   r22

    ### Meggy.setPixel(x,y,color) call
    # load a one byte expression off stack
    pop    r20
    # load a one byte expression off stack
    pop    r22
    # load a one byte expression off stack
    pop    r24
    call   _Z6DrawPxhhh
    call   _Z12DisplaySlatev

    # Load constant int 256
    ldi    r24,lo8(256)
    ldi    r25,hi8(256)
    # push two byte expression onto stack
    push   r25
    push   r24

    ### Meggy.delay() call
    # load delay parameter
    # load a two byte expression off stack
    pop    r24
    pop    r25
    call   _Z8delay_msj
    jmp    MJ_L39

    # else label for if
MJ_L37:

    #### if statement

    # Load constant int 0
    ldi    r24,lo8(0)
    ldi    r25,hi8(0)
    # push two byte expression onto stack
    push   r25
    push   r24

    # Casting int to byte by popping
    # 2 bytes off stack and only pushing low order bits
    # back on.  Low order bits are on top of stack.
    pop    r24
    pop    r25
    push   r24

    # Load constant int 7
    ldi    r24,lo8(7)
    ldi    r25,hi8(7)
    # push two byte expression onto stack
    push   r25
    push   r24

    # Casting int to byte by popping
    # 2 bytes off stack and only pushing low order bits
    # back on.  Low order bits are on top of stack.
    pop    r24
    pop    r25
    push   r24

    ### Meggy.getPixel(x,y) call
    # load a one byte expression off stack
    pop    r22
    # load a one byte expression off stack
    pop    r24
    call   _Z6ReadPxhh
    # push one byte expression onto stack
    push   r24

    # Color expression Meggy.Color.RED
    ldi     r22,1
    # push one byte expression onto stack
    push   r22

    # equality check expression
    # load a one byte expression off stack
    pop    r18
    # load a one byte expression off stack
    pop    r24
    cp    r24, r18
    breq MJ_L47

    # result is false
MJ_L46:
    ldi     r24, 0
    jmp      MJ_L48

    # result is true
MJ_L47:
    ldi     r24, 1

    # store result of equal expression
MJ_L48:
    # push one byte expression onto stack
    push   r24

    # load condition and branch if false
    # load a one byte expression off stack
    pop    r24
    #load zero into reg
    ldi    r25, 0

    #use cp to set SREG
    cp     r24, r25
    #WANT breq MJ_L43
    brne   MJ_L44
    jmp    MJ_L45

    # then label for if
MJ_L44:

    # Load constant int 0
    ldi    r24,lo8(0)
    ldi    r25,hi8(0)
    # push two byte expression onto stack
    push   r25
    push   r24

    # Casting int to byte by popping
    # 2 bytes off stack and only pushing low order bits
    # back on.  Low order bits are on top of stack.
    pop    r24
    pop    r25
    push   r24

    # Load constant int 7
    ldi    r24,lo8(7)
    ldi    r25,hi8(7)
    # push two byte expression onto stack
    push   r25
    push   r24

    # Casting int to byte by popping
    # 2 bytes off stack and only pushing low order bits
    # back on.  Low order bits are on top of stack.
    pop    r24
    pop    r25
    push   r24

    # Color expression Meggy.Color.RED
    ldi     r22,1
    # push one byte expression onto stack
    push   r22

    ### Meggy.setPixel(x,y,color) call
    # load a one byte expression off stack
    pop    r20
    # load a one byte expression off stack
    pop    r22
    # load a one byte expression off stack
    pop    r24
    call   _Z6DrawPxhhh
    call   _Z12DisplaySlatev

    # Load constant int 256
    ldi    r24,lo8(256)
    ldi    r25,hi8(256)
    # push two byte expression onto stack
    push   r25
    push   r24

    ### Meggy.delay() call
    # load delay parameter
    # load a two byte expression off stack
    pop    r24
    pop    r25
    call   _Z8delay_msj
    jmp    MJ_L45

    # else label for if
MJ_L43:

    # done label for if
MJ_L45:

    # done label for if
MJ_L39:

    # done label for if
MJ_L33:

    # done label for if
MJ_L27:

    # done label for if
MJ_L16:

    # Load constant int 1000
    ldi    r24,lo8(1000)
    ldi    r25,hi8(1000)
    # push two byte expression onto stack
    push   r25
    push   r24

    ### Meggy.delay() call
    # load delay parameter
    # load a two byte expression off stack
    pop    r24
    pop    r25
    call   _Z8delay_msj

    # done label for if
MJ_L5:

    # jump to while test
    jmp    MJ_L0

    # end of while
MJ_L2:


/* epilogue start */
    endLabel:
    jmp endLabel
    ret
    .size   main, .-main


