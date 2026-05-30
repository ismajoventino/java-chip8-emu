package com.ismael.chip8.core;

public class Stack {

	private final int[] stack;
	private int sp;
	
	public Stack() {
		this.stack = new int[16];
		this.sp = 0;
	}
	
	public void push(int address) {
		if(sp < stack.length) {
			stack[sp] = address & 0xFFFF;
			sp++;
		}
	}
	
	public int pop() {
		if (sp > 0) {
			sp--;
			return stack[sp];
		}
		return 0;
	}
	
}
