package de.doridian.yiffbukkit.commands;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import net.minecraft.server.Packet;
import net.minecraft.server.Packet53BlockChange;
import net.minecraft.server.Packet54PlayNoteBlock;
import net.minecraft.server.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;

import de.doridian.yiffbukkit.YiffBukkitCommandException;
import de.doridian.yiffbukkit.commands.ICommand.*;
import de.doridian.yiffbukkit.util.Utils;

@Names("play")
@Help("Plays notes (currently qbasic format)")
@Permission("yiffbukkit.experimental.play")
public class PlayCommand extends ICommand {
	public class Packet53BlockChangeExpress extends Packet53BlockChange {
		{
			Map<Class<? extends Packet>, Integer> classToId = Utils.getPrivateValue(Packet.class, null, "c");
			classToId.put(Packet53BlockChangeExpress.class, 53);
		}
		public Packet53BlockChangeExpress() {
			k = false;
		}

		public Packet53BlockChangeExpress(int x, int y, int z, World world) {
			super(x, y, z, world);
			k = false;
		}
	}

	enum Sharp {
		FLAT(-1), REGULAR(0), SHARP(1);

		public int value;

		Sharp(int value) {
			this.value = value;
		}
	}

	public class Note {
		public int number;
		public double length;

		public Note(int value, double length) {
			this.number = value;
			this.length = length;
		}
	}

	@Override
	public void Run(final Player ply, String[] args, final String argStr) throws YiffBukkitCommandException {
		final CraftWorld cworld = (CraftWorld) ply.getWorld();
		final World notchWorld = cworld.getHandle();

		final Location loc = ply.getLocation();
		final int x = loc.getBlockX(), y = loc.getBlockY(), z = loc.getBlockZ();

		final long startTime = System.currentTimeMillis()+1000;

		final Packet53BlockChange p53 = new Packet53BlockChangeExpress(x, y, z, notchWorld);
		p53.material = Material.NOTE_BLOCK.getId();
		playerHelper.sendPacketToPlayersAround(loc, 64, p53);

		new Runnable() {
			// Note length fractions in the the modes ML, MN and MS
			private static final double mode_legato = 1.0;
			private static final double mode_normal = 7.0/8.0;
			private static final double mode_staccato = 3.0/4.0;

			final int taskID = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this, 0, 1);
			long nextTime = startTime;

			final Queue<Note> notes = parse(argStr);

			Queue<Note> parse(String input) throws YiffBukkitCommandException {
				int note_octave = 4;
				double note_length = 4.0;
				double note_tempo  = 120.0;
				@SuppressWarnings("unused")
				double note_mode   = mode_normal;
				
				Queue<Note> ret = new ArrayBlockingQueue<Note>(50);
				StringReader iss = new StringReader(input);

				// Temporary value for parameters.
				int tmp;
				try {
					while(-1 != (tmp = iss.read())) {
						char this_char = Character.toLowerCase((char)tmp);
						switch(this_char) {
						case '<':
							if (note_octave > 0) note_octave--;
							break;
						case '>':
							if (note_octave < 6) note_octave++;
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
						case 'g':
							Sharp note_sharp = Sharp.REGULAR;
							switch(peek(iss)) {
							case '#':
							case '+':
								if (this_char == 'e' || this_char == 'b') break;
								iss.skip(1);
								note_sharp = Sharp.SHARP;
								break;

							case '-':
								if (this_char == 'f' || this_char == 'c') break;
								iss.skip(1);
								note_sharp = Sharp.FLAT;
								break;
							}
							double current_note_length = note_length;

							char next = peek(iss);
							if (next >= '1' && next <= '9') {
								tmp = readnum(iss);
								if (tmp != -1) current_note_length = tmp;
							}

							while (peek(iss) == '.') {
								iss.skip(1);
								current_note_length /= 1.5;
								break; // wtf?
							}
							int noteNumber = get_note_number(note_octave, this_char, note_sharp);
							double noteLength = real_note_length(note_tempo, current_note_length);
							ret.add(new Note(noteNumber, noteLength));
							break; // tested
						case 'l':
							tmp = readnum(iss);
							if (tmp < 1 || tmp > 64) throw new YiffBukkitCommandException("play: invalid note length (allowed: 1-64)");
							note_length = tmp;
							break; // tested
						case 'm':
							char mode_char= (char) iss.read();
							switch(Character.toLowerCase(mode_char)) {
							case 'l':
								note_mode = mode_legato;
								break;
							case 'n':
								note_mode = mode_normal;
								break;
							case 's':
								note_mode = mode_staccato;
								break;
							case 'b':
							case 'f':
								break; // not implemented, ignored
							default:
								throw new YiffBukkitCommandException("play: invalid mode '"+this_char+"'(allowed: l, n, s)");
							}
							break; // untested

						case 'n':
							tmp = readnum(iss);
							if (/*tmp < 0 || */tmp > 84) throw new YiffBukkitCommandException("play: invalid note number (allowed: 0-84)");
							if (tmp == 0) {
								//append_silence(real_note_length(note_tempo, note_length));
							}
							else {
								ret.add(new Note(tmp, real_note_length(note_tempo, note_length)));
							}
							break; // untested

						case 'o':
							tmp = readnum(iss);
							if (/*tmp < 0 || */tmp > 6) throw new YiffBukkitCommandException("play: invalid octave (allowed: 0-6)");
							note_octave = tmp;
							break; // tested

						case 'p':
							tmp = readnum(iss);
							if (tmp < 1 || tmp > 64) throw new YiffBukkitCommandException("play: invalid pause duration (allowed: 1-64)");
							//append_silence(real_note_length(note_tempo, tmp));
							break; // untested

						case 't':
							tmp = readnum(iss);
							if (tmp < 32 || tmp > 255) throw new YiffBukkitCommandException("play: invalid tempo (allowed: 32-255)");
							note_tempo = tmp;
							break; // tested
						case ' ':
						case '\t':
							break;
						default:
							throw new YiffBukkitCommandException("play: invalid command/note '"+this_char+"' (allowed: a-g, l-p, t)");
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				return ret;
			}

			private int readnum(StringReader iss) throws IOException {
				try {
					StringBuilder s = new StringBuilder();
					while (Character.isDigit(peek(iss))) {
						s.append((char)iss.read());
					}
					return Integer.parseInt(s.toString());
				} catch (NumberFormatException e) {
					return -1;
				}
			}

			private double real_note_length(double bpm, double note_length) {
				return (60/bpm)*(4/note_length);
			}

			private int get_note_number(int octave, char name, Sharp sharp) {
				final int baseValue = octave*12+sharp.value;
				switch(name) {
				case 'a': return baseValue+10;
				case 'b': return baseValue+12;
				case 'c': return baseValue+ 1;
				case 'd': return baseValue+ 3;
				case 'e': return baseValue+ 5;
				case 'f': return baseValue+ 6;
				case 'g': return baseValue+ 8;
				default: return 0;
				}
			}

			private char peek(StringReader iss) throws IOException {
				iss.mark(1);
				int ret = iss.read();
				iss.reset();
				return (char)ret;
			}

			@Override
			public void run() {
				while (System.currentTimeMillis() >= nextTime) {

					Note note = notes.poll();

					if (note == null) {
						playerHelper.sendPacketToPlayersAround(loc, 64, new Packet53BlockChangeExpress(x, y, z, notchWorld));
						plugin.getServer().getScheduler().cancelTask(taskID);
						return;
					}

					nextTime += note.length*1000;

					final int noteValue = note.number-4*12+5;
					//System.out.println(String.format("%d/%d/%d i=%d n=%d", x, y, z, 0, noteValue));
					playerHelper.sendPacketToPlayersAround(loc, 64, new Packet54PlayNoteBlock(x, y, z, 0, noteValue));
				}
			}
		};
	}
}
