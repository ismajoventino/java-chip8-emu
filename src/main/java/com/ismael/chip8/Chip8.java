package com.ismael.chip8;

import com.ismael.chip8.io.Display;
import com.ismael.chip8.io.TerminalRenderer;

public class Chip8 {

	public static void main(String[] args) {
		
		Display display = new Display();
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
		
		renderer.render();
		

	}

}
