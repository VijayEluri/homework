		.ORIG x3000
		LEA R1, ST1
		LEA R6, PST
		STR R1, R6, #0
		LEA R1, ST2
		STR R1, R6, #1
		LEA R1, ST3
		STR R1, R6, #2
		LEA R6, MAIN_1
		JSR PUSH
		JSR READ
		ST R6, HEIGHT
		ADD R3, R6, #0
		ADD R6, R6, #-15
		BRnz BEGIN
		LEA R0, sz_ER
		PUTS
		HALT
BEGIN	ADD R1, R3, #0
		AND R2, R2, #0
INIT	LEA R6, ST1
		ADD R6, R6, R2
		STR R1, R6, #0
		ADD R2, R2, #1
		ADD R1, R1, #-1
		BRp INIT
		LEA R6, S_TOP
		STR R3, R6, #0
		ADD R5, R5, #2
		AND R6, R6, #0
		JSR PUSH
		JSR PUSH
		JSR PUSH
		LEA R6, WORK
		JMP R6
MAIN_1	HALT

WORK	ADD R3, R3, #0
		BRp WORKP
		LEA R6, RET_1
		JMP R6
WORKP	LEA R6, MOVE
		JSR PUSH					; push EIP
		ADD R6, R3, #0
		JSR PUSH					; push i
		ADD R6, R4, #0
		JSR PUSH					; push src
		ADD R6, R5, #0
		JSR PUSH					; push dst
		ADD R3, R3, #-1				; i -= 1
		NOT R1, R4
		ADD R1, R1, #1
		NOT R2, R5
		ADD R2, R2, #1
		ADD R5, R1, #3
		ADD R5, R5, R2
		LEA R6, WORK
		JMP R6						; call work(i - 1, src, 3 - src - dst)
MOVE	LEA R0, sz1
		PUTS
		LD R0, LETTERA
		ADD R0, R0, R4
		OUT							; wait
		LEA R0, sz2
		PUTS
		LD R0, LETTERA
		ADD R0, R0, R5
		OUT
		LD R0, ENTER
		OUT
		LEA R6, AFT_MOV
		JSR PUSH
		ADD R6, R3, #0
		JSR PUSH
		ADD R6, R4, #0
		JSR PUSH
		ADD R6, R5, #0
		JSR PUSH
		LEA R6, T_MOV
		JMP R6						; END OF MOVE
AFT_MOV	LEA R6, RET_1
		JSR PUSH					; push EIP
		ADD R6, R3, #0
		JSR PUSH					; push i
		ADD R6, R4, #0
		JSR PUSH					; push src
		ADD R6, R5, #0
		JSR PUSH					; push dst
		ADD R3, R3, #-1				; i - 1
		NOT R1, R4
		ADD R1, R1, #1
		NOT R2, R5
		ADD R2, R2, #1
		ADD R4, R1, #3
		ADD R4, R4, R2
		LEA R6, WORK
		JMP R6						; call work(i - 1, 3 - src - dst, dst)

RET_1	JSR POP
		ADD R5, R6, #0
		JSR POP
		ADD R4, R6, #0
		JSR POP
		ADD R3, R6, #0
		JSR POP
		JMP R6

READ	AND R6, R6, #0
		ADD R2, R7, #0
READ_1	GETC
		OUT
		LD R1, nNINE
		ADD R0, R0, R1  
		BRp READ_1
		ADD R0, R0, #9
		BRn READ_1
READ_2	ADD R6, R6, R6			;begin mul 10
		ADD R1, R6, #0
		ADD R1, R1, R1
		ADD R1, R1, R1
		ADD R6, R6, R1
		ADD R6, R6, R0			;R6 = R6 * 10 + R0
		GETC
		OUT
		LD R1, nNINE
		ADD R0, R0, R1
		BRzp READ_3
		ADD R0, R0, #9
		BRzp READ_2
READ_3	ADD R7, R2, #0
		RET

T_MOV	AND R0, R0, #0
		LEA R6, S_TOP
		ADD R6, R6, R4
		LDR R2, R6, #0		; R2 = TOP
		LEA R3, PST
		ADD R3, R3, R4
		LDR R3, R3, #0
		ADD R2, R2, #-1
		ADD R3, R3, R2
		LDR R1, R3, #0		; R1 = S[TOP]
		STR R0, R3, #0
		STR R2, R6, #0

		LEA R6, S_TOP
		ADD R6, R6, R5
		LDR R2, R6, #0
		LEA R3, PST
		ADD R3, R3, R5
		LDR R3, R3, #0
		ADD R3, R3, R2
		STR R1, R3, #0
		ADD R2, R2, #1
		STR R2, R6, #0
		;PUTSTAT
		LEA R6, RET_1
		JMP R6

PUSH	LD R1, TOP
		LEA R2, STACK
		ADD R2, R2, R1
		STR R6, R2, #0
		ADD R1, R1, #1
		ST R1, TOP
		RET

POP		LD R1, TOP
		ADD R1, R1, #-1
		LEA R2, STACK
		ADD R2, R2, R1
		LDR R6, R2, #0
		ST R1, TOP
		RET

HEIGHT	.FILL x0000
TOP		.FILL x0000
RESULT	.FILL x0000
LETTERA	.FILL x0041
ENTER	.FILL x000a
BLANK	.FILL x0020
nNINE	.FILL xFFC7
sz1		.STRINGZ "After move from "
sz2		.STRINGZ " to "
sz_ER	.STRINGZ "Size must be no more then 15"
PST		.BLKW 3
S_TOP	.BLKW 3
ST1		.BLKW 15
ST2		.BLKW 15
ST3		.BLKW 15
STACK	.BLKW 70
		.END
