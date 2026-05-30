package com.ismael.chip8.core;

public class CPU {

	private final Memory memory;
	private final Registers registers;
	private final Stack stack;
	
	public CPU(Memory memory, Registers registers, Stack stack) {
		super();
		this.memory = memory;
		this.registers = registers;
		this.stack = stack;
	}
	
	public int fetch() {
		int pc = registers.getPc();
		
		int byte1 = memory.read(pc);
		int byte2 = memory.read(pc + 1);
	}
}
