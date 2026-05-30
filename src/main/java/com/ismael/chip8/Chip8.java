package com.ismael.chip8;

import com.ismael.chip8.core.CPU;
import com.ismael.chip8.core.Memory;
import com.ismael.chip8.core.Registers;
import com.ismael.chip8.core.Stack;
import com.ismael.chip8.rom.RomLoader;

public class Chip8 {

	public static void main(String[] args) {
		Memory memory = new Memory();
		Registers registers = new Registers();
		Stack stack = new Stack();
		CPU cpu = new CPU(memory, registers, stack);
		RomLoader romLoader = new RomLoader();
		
		romLoader.loadFonts(memory);
		
		try {
			romLoader.loadRom("test_opcode.ch8", memory);
			
			for(int i = 0; i < 50; i++) {
				int opcode = cpu.fetch();
				cpu.decode(opcode);
				cpu.execute();
			}
		}
		catch(Exception e) {
            e.printStackTrace();
		}
		

	}

}
