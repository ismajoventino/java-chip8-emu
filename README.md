```
░█████╗░██╗░░██╗██╗██████╗░░█████╗░
██╔══██╗██║░░██║██║██╔══██╗██╔══██╗
██║░░╚═╝███████║██║██████╔╝╚█████╔╝
██║░░██╗██╔══██║██║██╔═══╝░██╔══██╗
╚█████╔╝██║░░██║██║██║░░░░░╚█████╔╝
░╚════╝░╚═╝░░╚═╝╚═╝╚═╝░░░░░░╚════╝░
```

# chip8-java

A CHIP-8 emulator built from scratch in Java 21, with terminal-based ASCII rendering.  
No GUI frameworks. No external dependencies. Pure Java, pure terminal.

---

## Overview

| Property | Value |
|---|---|
| Language | Java 21 |
| Rendering | Terminal (ANSI escape codes + Unicode block chars) |
| Target OS | Linux (tested on Linux Mint) |
| Dependencies | None |
| ROM format | `.ch8` |
| Display resolution | 64 × 32 pixels |
| CPU speed | ~500Hz (configurable) |
| Timer frequency | 60Hz |

---

## Architecture

```
src/main/java/com/ismael/chip8/
├── core/
│   ├── CPU.java             # Fetch-decode-execute cycle, all opcodes
│   ├── Memory.java          # 4KB address space (0x000–0xFFF)
│   ├── Stack.java           # 16-level call stack with manual SP
│   └── Registers.java       # V0–VF, I, PC, SP, delay/sound timers
├── io/
│   ├── Display.java         # boolean[64][32] framebuffer + XOR draw
│   ├── Keyboard.java        # boolean[16] key state, QWERTY mapping
│   └── TerminalRenderer.java# ANSI rendering, █ / space mapping
├── rom/
│   └── RomLoader.java       # Reads .ch8 files into byte[]
└── Chip8.java               # Entry point, game loop, timing
```

### Component Responsibilities

- **CPU** — orchestrates everything. Owns the fetch-decode-execute cycle and holds references to all other components. Each opcode is a discrete method or switch case.
- **Memory** — exposes `read(int addr)` and `write(int addr, int val)` with bounds validation. The internal array is never public.
- **Registers** — encapsulates all CPU state: general-purpose registers, index register, program counter, stack pointer, and both timers.
- **Stack** — implemented as `int[]` with a manual `sp` pointer, faithful to the original hardware. Not `java.util.Stack`.
- **Display** — maintains the framebuffer and handles sprite drawing with XOR collision detection. Returns collision flag for `VF`.
- **TerminalRenderer** — isolated rendering layer. All ANSI escape codes live here, nowhere else.
- **Keyboard** — maps physical keys to CHIP-8's 16-key hex layout. Manages pressed/released state.

---

## Memory Map

```
0x000 ──────────── reserved (interpreter area)
0x050 ──────────── font sprites (0–F, 5 bytes each)
0x09F ──────────── end of font data
0x200 ──────────── ROM entry point  ← PC starts here
      ──────────── program data
0xEA0 ──────────── call stack area
0xF00 ──────────── display buffer
0xFFF ──────────── end of memory
```

---

## Keyboard Mapping

```
CHIP-8 Layout     →     QWERTY Mapping
┌───┬───┬───┬───┐       ┌───┬───┬───┬───┐
│ 1 │ 2 │ 3 │ C │       │ 1 │ 2 │ 3 │ 4 │
│ 4 │ 5 │ 6 │ D │       │ Q │ W │ E │ R │
│ 7 │ 8 │ 9 │ E │       │ A │ S │ D │ F │
│ A │ 0 │ B │ F │       │ Z │ X │ C │ V │
└───┴───┴───┴───┘       └───┴───┴───┴───┘
```

---

## Opcode Reference

| Opcode | Mnemonic | Description |
|---|---|---|
| `00E0` | CLS | Clear display |
| `00EE` | RET | Return from subroutine |
| `1nnn` | JP addr | Jump to address |
| `2nnn` | CALL addr | Call subroutine |
| `3xkk` | SE Vx, byte | Skip if Vx == kk |
| `4xkk` | SNE Vx, byte | Skip if Vx != kk |
| `5xy0` | SE Vx, Vy | Skip if Vx == Vy |
| `6xkk` | LD Vx, byte | Vx = kk |
| `7xkk` | ADD Vx, byte | Vx += kk |
| `8xy0` | LD Vx, Vy | Vx = Vy |
| `8xy1` | OR Vx, Vy | Vx \|= Vy |
| `8xy2` | AND Vx, Vy | Vx &= Vy |
| `8xy3` | XOR Vx, Vy | Vx ^= Vy |
| `8xy4` | ADD Vx, Vy | Vx += Vy, VF = carry |
| `8xy5` | SUB Vx, Vy | Vx -= Vy, VF = !borrow |
| `8xy6` | SHR Vx | Vx >>= 1, VF = LSB |
| `8xy7` | SUBN Vx, Vy | Vx = Vy - Vx, VF = !borrow |
| `8xyE` | SHL Vx | Vx <<= 1, VF = MSB |
| `9xy0` | SNE Vx, Vy | Skip if Vx != Vy |
| `Annn` | LD I, addr | I = nnn |
| `Bnnn` | JP V0, addr | PC = V0 + nnn |
| `Cxkk` | RND Vx, byte | Vx = random() & kk |
| `Dxyn` | DRW Vx, Vy, n | Draw n-byte sprite at (Vx, Vy) |
| `Ex9E` | SKP Vx | Skip if key Vx is pressed |
| `ExA1` | SKNP Vx | Skip if key Vx is NOT pressed |
| `Fx07` | LD Vx, DT | Vx = delay timer |
| `Fx0A` | LD Vx, K | Block until keypress, store in Vx |
| `Fx15` | LD DT, Vx | delay timer = Vx |
| `Fx18` | LD ST, Vx | sound timer = Vx |
| `Fx1E` | ADD I, Vx | I += Vx |
| `Fx29` | LD F, Vx | I = address of digit Vx sprite |
| `Fx33` | LD B, Vx | BCD of Vx → mem[I], mem[I+1], mem[I+2] |
| `Fx55` | LD [I], Vx | Dump V0..Vx to memory |
| `Fx65` | LD Vx, [I] | Load V0..Vx from memory |

---

## Development Checklist

### Phase 1 — Memory & Registers
- [x] `Memory.java` — 4096-byte array with `read()`/`write()` and bounds validation
- [x] `Registers.java` — V0–VF, I, PC (init `0x200`), SP, delay timer, sound timer
- [x] `Stack.java` — `int[16]` with manual `push()`/`pop()` and SP
- [x] Font sprites loaded into memory at `0x050`
- [x] `RomLoader.java` — reads `.ch8` file into `byte[]`, writes from `0x200`
- [x] Smoke test: load ROM, print first bytes as hex

### Phase 2 — Fetch-Decode-Execute
- [x] `fetch()` — reads 2 bytes from PC, advances PC by 2
- [x] `decode()` — extracts `nnn`, `n`, `x`, `y`, `kk` from opcode
- [x] `execute()` — main `switch` dispatching on `opcode & 0xF000`
- [x] Opcode logger (hex output to console)
- [x] `00E0` (CLS) and `1nnn` (JP) implemented as first working instructions

### Phase 3 — Opcodes
- [ ] **Group A — Control flow:** `00E0`, `00EE`, `1nnn`, `2nnn`, `Bnnn`
- [ ] **Group B — Conditionals:** `3xkk`, `4xkk`, `5xy0`, `9xy0`
- [ ] **Group C — Load & assign:** `6xkk`, `7xkk`, `Annn`, `Cxkk`
- [ ] **Group D — ALU (8xy_):** `8xy0` through `8xyE` (8 instructions)
- [ ] **Group E — Memory & misc (Fx__):** `Fx07`, `Fx15`, `Fx18`, `Fx1E`, `Fx29`, `Fx33`, `Fx55`, `Fx65`
- [ ] Passes [Timendus chip8-test-suite](https://github.com/Timendus/chip8-test-suite): `test_opcode.ch8`

### Phase 4 — Terminal Display
- [ ] `Display.java` — `boolean[64][32]` framebuffer
- [ ] `drawSprite(x, y, sprite[])` — XOR draw with collision detection, returns `VF` flag
- [ ] `TerminalRenderer.java` — ANSI cursor reset (`\033[H`), no full clear (no flicker)
- [ ] Pixel mapping: `█` (U+2588) for ON, space for OFF
- [ ] Optional: `▀`/`▄` half-block chars for 2× vertical density
- [ ] Border rendering with `─ │ ┌ ┐ └ ┘`
- [ ] `Dxyn` opcode implemented
- [ ] Refresh capped at 60Hz (independent of CPU speed)
- [ ] Visual test: Pong or Space Invaders renders correctly

### Phase 5 — Keyboard Input
- [ ] `Keyboard.java` — `boolean[16]` key state array
- [ ] QWERTY → CHIP-8 key mapping implemented
- [ ] Raw terminal input (via `stty` + `ProcessBuilder` or JLine3)
- [ ] `Ex9E` (SKP) implemented
- [ ] `ExA1` (SKNP) implemented
- [ ] `Fx0A` (LD Vx, K) — blocking wait for keypress implemented
- [ ] Pong playable end-to-end

### Phase 6 — Game Loop & Timing
- [ ] Main loop: N CPU cycles per frame → decrement timers → render → sleep
- [ ] Timing with `System.nanoTime()` for frame precision
- [ ] `CYCLES_PER_FRAME` configurable (default: 10, range: 8–20)
- [ ] Stable 60Hz render loop with no drift
- [ ] Multiple ROMs tested at different cycle speeds

---

## Technical Notes

**Unsigned bytes in Java**  
Java `byte` is signed. Always mask with `0xFF` when reading from memory:
```java
int val = memory[addr] & 0xFF;
```
Missing this causes silent arithmetic bugs that are very hard to trace.

**SUPER-CHIP vs original CHIP-8**  
Opcodes `8xy6`, `8xyE`, `Fx55`, and `Fx65` have different behavior between the original CHIP-8 and SUPER-CHIP. Most modern ROMs expect SUPER-CHIP behavior. This implementation follows **SUPER-CHIP** semantics.

**Sprite draw algorithm**
```
for i in 0..n-1:
  byte = mem[I + i]
  for j in 7..0:
    px = (Vx + (7 - j)) % 64
    py = (Vy + i) % 32
    if bit j of byte == 1:
      if framebuffer[px][py] == true → VF = 1
      framebuffer[px][py] ^= true
```

**Recommended test ROMs**
- [`test_opcode.ch8`](https://github.com/Timendus/chip8-test-suite) — opcode-by-opcode validation (use this first)
- `BC_test.ch8` — general compatibility test
- `pong.ch8` — good end-to-end integration test
- `Space Invaders.ch8` — stress test for display and input

---

## Running

```bash
# Compile
javac -d target/classes src/main/java/com/ismael/chip8/**/*.java src/main/java/com/ismael/chip8/*.java

# Run
java -cp target/classes com.ismael.chip8.Chip8 test_opcode.ch8
```

---

## References

- [Cowgod's CHIP-8 Technical Reference](http://devernay.free.fr/hacks/chip8/C8TECH10.HTM)
- [Tobias V. Langhoff's CHIP-8 Guide](https://tobiasvl.github.io/blog/write-a-chip-8-emulator/)
- [Timendus chip8-test-suite](https://github.com/Timendus/chip8-test-suite)
