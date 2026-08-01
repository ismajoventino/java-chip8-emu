package com.ismael.chip8.io;

public class TerminalRenderer {

	private final Display display;
	
	public TerminalRenderer(Display display) {
		this.display = display;
	}
	
	public void render() {
	    StringBuilder sb = new StringBuilder();

	    for (int y = 0; y < 32; y++) {
	        sb.append("\033[").append(y + 1).append(";1H");
	        for (int x = 0; x < 64; x++) {
	            sb.append(display.getPixel(x, y) ? "█" : " ");
	        }
	    }

	    System.out.print(sb);
	    System.out.flush();
	}
	
	public void clearScreen() {
		//System.out.print("\033[2J\033[H");
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}
	
}
