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
			} else if(opcode == 0x00EE) {
				registers.setPc(stack.pop());
			} else {
				System.out.printf("Ignored opcode in category 0000: %04X\n", opcode);
			}
			break;
		case 0x1000:
			System.out.printf("Executing 1nn - Unconditional Jump to %03X\n", nnn);
			registers.setPc(nnn);
			break;
		case 0x2000:
			stack.push(registers.getPc());
			registers.setPc(nnn);
			break;
		case 0x3000: 
			if(registers.getV(x) == kk) {
				registers.setPc(registers.getPc() + 2);
			}
			break;
		case 0x4000: 
			if(registers.getV(x) != kk) {
				registers.setPc(registers.getPc() + 2);
			}
			break;
		case 0x5000:
			if(registers.getV(x) == registers.getV(y)) {
				registers.setPc(registers.getPc() + 2);
			}
			break;
		case 0x9000:
			if(registers.getV(x) != registers.getV(y)) {
				registers.setPc(registers.getPc() + 2);
			}
			break;
		case 0x6000:
			registers.setV(x, kk);
			break;
		case 0x7000:
			int result = (registers.getV(x) + kk) & 0xFF;
			registers.setV(x, result);
			break;
		case 0xA000:
			registers.setI(nnn);
			break;
		case 0xB000:
			registers.setPc(nnn + registers.getV(0));
			break;
		case 0xF000:
			switch (kk) {
				case 0x07:
					registers.setV(x, registers.getDelayTimer());
					break;
				case 0x15:
					registers.setDelayTimer(registers.getV(x));
					break;
				case 0x18:
					registers.setSoundTimer(registers.getV(x));
					break;
				case 0x1E:
					registers.setI(registers.getI() + registers.getV(x));
					break;
				case 0x29:
					registers.setI(0x050 + (registers.getV(x) * 5));
					break;
				case 0x33:
					int value = registers.getV(x);
					memory.write(registers.getI(), value / 100);
					memory.write(registers.getI() + 1, (value / 10) % 10);
					memory.write(registers.getI() + 2, value % 10);
					break;
				case 0x55:
					for (int i = 0; i <= x; i++) {
						memory.write(registers.getI() + i, registers.getV(i));
					}
					break;
				case 0x65:
					for (int i = 0; i <= x; i++) {
						registers.setV(i, memory.read(registers.getI() + i));
					}
					break;
				default:
					System.out.printf("Ignored or unknown opcode in F000: %04X\n", opcode);
					break;
			}
			break;
		case 0xC000:
			int rand = (int) (Math.random() * 256);
            registers.setV(x, rand & kk);
            break;
		case 0x8000:
            switch (n) {
                case 0x0: 
                    registers.setV(x, registers.getV(y));
                    break;
                case 0x1: 
                    registers.setV(x, registers.getV(x) | registers.getV(y));
                    break;
                case 0x2: 
                    registers.setV(x, registers.getV(x) & registers.getV(y));
                    break;
                case 0x3: 
                    registers.setV(x, registers.getV(x) ^ registers.getV(y));
                    break;
                case 0x4: 
                    int sum = registers.getV(x) + registers.getV(y);
                    registers.setV(0xF, (sum > 255) ? 1 : 0);
                    registers.setV(x, sum & 0xFF);
                    break;
                case 0x5: 
                    registers.setV(0xF, (registers.getV(x) >= registers.getV(y)) ? 1 : 0);
                    registers.setV(x, (registers.getV(x) - registers.getV(y)) & 0xFF);
                    break;
                case 0x6: 
                    registers.setV(0xF, registers.getV(x) & 0x1);
                    registers.setV(x, registers.getV(x) >> 1);
                    break;
                case 0x7: 
                    registers.setV(0xF, (registers.getV(y) >= registers.getV(x)) ? 1 : 0);
                    registers.setV(x, (registers.getV(y) - registers.getV(x)) & 0xFF);
                    break;
                case 0xE: 
                    registers.setV(0xF, (registers.getV(x) & 0x80) >> 7);
                    registers.setV(x, (registers.getV(x) << 1) & 0xFF);
                    break;
                default:
                    System.out.printf("Opcode 8000 desconhecido: %04X\n", opcode);
                    break;
            }
            break;
		default:
			System.out.printf("Ignored or unknown opcode: %04X\n", opcode);
			break;
		}
	}
}
