package com.ismael.chip8.io;

public class Display {

	private final boolean[][] framebuffer;
	
	public Display() {
		this.framebuffer = new boolean[64][32];
	}
	
	public boolean getPixel(int x, int y) {
		return framebuffer[x][y];
	}
	
	public void setPixel(int x, int y, boolean value) {
		framebuffer[x][y] = value;
	}
	
	public void clear() {
		for(int x = 0; x < 64; x++) {
			for(int y = 0; y < 32; y++) {
				framebuffer[x][y] = false;
			}
		}
	}
	
}
