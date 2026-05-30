package com.ismael.chip8.core;

public class CPU {

	private final Memory memory;
	private final Registers registers;
	private final Stack stack;
	
	private int opcode;
	private int nnn;
	private int n;
	private int x;
	private int y;
	private int kk;
	
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
		
		registers.incrementPc();
		
		return byte1 << 8 | byte2;
	}
	
	public void decode(int opcode) {
		this.opcode = opcode;
		this.nnn = opcode & 0x0FFF;
		this.n = opcode & 0x000F;
		this.x = (opcode & 0x0F00) >> 8;
		this.y = (opcode & 0x00F0) >> 4;
		this.kk = opcode & 0x00FF;
	}
	
	public void execute() {
		switch(opcode & 0xF000) {
		case 0x0000:
			if(opcode == 0x00E0) {
				System.out.println("Executing: 00E0");
			} else {
				System.out.printf("Ignored opcode in category 0000: %04X\n", opcode);
			}
			break;
		case 0x1000:
			System.out.printf("Executing 1nn - Unconditional Jump to %03X\n", nnn);
			registers.setPc(nnn);
			break;
		default:
			System.out.printf("Ignored or unknown opcode: %04Z\n", opcode);
			break;
		}
	}
}
