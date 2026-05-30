package com.ismael.chip8.core;

public class Memory {

	private final byte[] ram;
	
	public Memory() {
		this.ram = new byte[4096];
	}
	
	public void write(int address, int value) {
		if(address >= 0 && address < ram.length) {
			ram[address] = (byte) (value & 0xFF);
		}
	}
	
	public int read(int address) {
		if (address >= 0 && address < ram.length) {
			return ram[address] & 0xFF;
		}
		
		return 0;
	}
	
}
