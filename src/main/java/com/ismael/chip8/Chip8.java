package com.ismael.chip8;

import com.ismael.chip8.core.CPU;
import com.ismael.chip8.core.Memory;
import com.ismael.chip8.core.Registers;
import com.ismael.chip8.core.Stack;
import com.ismael.chip8.io.Display;
import com.ismael.chip8.io.TerminalRenderer;
import com.ismael.chip8.rom.RomLoader;

public class Chip8 {

	public static void main(String[] args) {
		
		/*Display display = new Display();
		TerminalRenderer renderer = new TerminalRenderer(display);
		
		renderer.clearScreen();
		
		display.setPixel(0, 0, true);
		display.setPixel(63, 0, true);
		display.setPixel(0, 31, true);
		display.setPixel(63, 31, true);
		
		display.setPixel(31, 15, true);
		display.setPixel(32, 15, true);
		display.setPixel(31, 16, true);
		display.setPixel(32, 16, true);
		
		renderer.render();*/
		
		Memory memory = new Memory();
		Registers registers = new Registers();
		Stack stack = new Stack();
		Display display = new Display();
		
		TerminalRenderer renderer = new TerminalRenderer(display);
		CPU cpu = new CPU(memory, registers, stack, display);
		
		RomLoader loader = new RomLoader();
		
		try {
			loader.loadRom("test_opcode.ch8", memory);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			return; 
		}
		
		renderer.clearScreen();
		
		for (int i = 0; i < 300; i++) {
			int opcode = cpu.fetch();
			cpu.decode(opcode);
			cpu.execute();
			
			renderer.render();
			
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		

	}

}
