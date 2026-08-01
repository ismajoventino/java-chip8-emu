package com.ismael.chip8.core;

public class Registers {

		private final int[] v;
		private int i;
		private int pc;
		private int sp;
		private int delayTimer;
		private int soundTimer;
		
		public Registers() {
			this.v = new int[16];
			this.pc = 0x200;
		}

		public int getI() {
			return i;
		}

		public void setI(int i) {
			this.i = i & 0xFFF;
		}

		public int getPc() {
			return pc;
		}

		public void setPc(int pc) {
			this.pc = pc & 0xFFFF;
		}
		
		public void incrementPc() {
			this.pc = (this.pc + 2) & 0xFFFF;
		}

		public int getSp() {
			return sp;
		}

		public void setSp(int sp) {
			this.sp = sp & 0xFFFF;
		}

		public int getDelayTimer() {
			return delayTimer;
		}

		public void setDelayTimer(int delayTimer) {
			this.delayTimer = delayTimer & 0xFF;
		}

		public int getSoundTimer() {
			return soundTimer;
		}

		public void setSoundTimer(int soundTimer) {
			this.soundTimer = soundTimer & 0xFF;
		}

		public int getV(int index) {
			return v[index];
		}
		
		public void setV(int index, int value) {
			this.v[index] = value & 0xFF;
		}
		
		
		
}
