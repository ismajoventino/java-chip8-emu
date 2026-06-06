package com.ismael.chip8.io;

public class TerminalRenderer {

	private final Display display;
	
	public TerminalRenderer(Display display) {
		this.display = display;
	}
	
	public void render() {
		StringBuilder sb = new StringBuilder();
		sb.append("\033[H");
		
		for(int y = 0; y < 32; y++) {
			for(int x = 0; x < 64; x++) {
				if(display.getPixel(x, y)) {
					sb.append("█");
				} else {
					sb.append(" ");
				}
			}
			sb.append("\n");
		}
		System.out.println(sb.toString());
	}
	
	public void clearScreen() {
		System.out.print("\033[2J\033[H");
		System.out.flush();
	}
	
}
