/**
 * This file is part of FoxBukkit.
 *
 * FoxBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FoxBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FoxBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.doridian.foxbukkit.core.listeners;

import com.sk89q.worldedit.PlayerDirection;
import com.sk89q.worldedit.blocks.BlockType;
import de.doridian.foxbukkit.bans.Bans.BanType;
import de.doridian.foxbukkit.bans.commands.KickCommand;
import de.doridian.foxbukkit.core.util.AutoCleanup;
import de.doridian.foxbukkit.core.util.PlayerHelper;
import de.doridian.foxbukkit.main.listeners.BaseListener;
import de.doridian.foxbukkit.main.util.Utils;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Handle events for all Block related events
 * @author Doridian
 */
public class FoxBukkitBlockListener extends BaseListener {
	public static final Map<Material,String> blocklevels = new EnumMap<>(Material.class);
	public static final Set<Material> flammableBlocks = EnumSet.noneOf(Material.class);
	public static final BlockFace[] flameSpreadDirections = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN };

	private static final int TORCH_BREAK_WINDOW = 8;
	private static final int TORCH_BREAK_TIMEOUT_MILLIS = 1000;

	static {
		blocklevels.put(Material.TNT, "foxbukkit.place.block.tnt");
		blocklevels.put(Material.BEDROCK, "foxbukkit.place.block.bedrock");

		blocklevels.put(Material.OBSIDIAN, "foxbukkit.place.block.obsidian");

		blocklevels.put(Material.WATER, "foxbukkit.place.block.water");
		blocklevels.put(Material.WATER_BUCKET, "foxbukkit.place.block.water_bucket");
		blocklevels.put(Material.LAVA, "foxbukkit.place.block.lava");
		blocklevels.put(Material.LAVA_BUCKET, "foxbukkit.place.block.lava_bucket");

		blocklevels.put(Material.FLINT_AND_STEEL, "foxbukkit.place.block.flint_and_steel");
		blocklevels.put(Material.FIRE, "foxbukkit.place.block.fire");
		//blocklevels.put(Material.PISTON_BASE, "foxbukkit.place.block.piston.base");
		//blocklevels.put(Material.PISTON_EXTENSION, "foxbukkit.place.block.piston.extension");
		//blocklevels.put(Material.PISTON_MOVING_PIECE, "foxbukkit.place.block.piston.moving_piece");
		//blocklevels.put(Material.PISTON_STICKY_BASE, "foxbukkit.place.block.piston.sticky_base");

		flammableBlocks.add(Material.LOG);
		flammableBlocks.add(Material.WOOD);
		flammableBlocks.add(Material.WOOL);
		flammableBlocks.add(Material.BOOKSHELF);
		flammableBlocks.add(Material.LEAVES);
	}

	public FoxBukkitBlockListener() {
		AutoCleanup.registerPlayerMap(torchQueues);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player ply = event.getPlayer();
		if (playerHelper.isPlayerDisabled(ply)) {
			PlayerHelper.sendDirectedMessage(ply, "You are not allowed to build right now.");
			event.setBuild(false);
			return;
		}

		final Block block = event.getBlock();
		Material material = block.getType();
		if (!ply.hasPermission("foxbukkit.place")) {
			PlayerHelper.sendServerMessage(ply.getName() + " is not allowed to build but tried tried to spawn " + material + ".");
			event.setBuild(false);
			return;
		}

		final String permission = blocklevels.get(material);
		if (permission != null && !ply.hasPermission(permission)) {
			PlayerHelper.sendServerMessage(ply.getName() + " tried to spawn illegal block " + material + ".");
			event.setBuild(false);
			return;
		}

		if (flammableBlocks.contains(material)) {
			if (!ply.hasPermission("foxbukkit.place.flammablenearfire")) {
				for (BlockFace face : flameSpreadDirections) {
					Material neighborMaterial = block.getRelative(face).getType();
					if (neighborMaterial == Material.FIRE) {
						PlayerHelper.sendServerMessage(ply.getName() + " tried to spawn flammable block " + material.toString() + " near fire.");
						event.setBuild(false);
					}
				}
			}
		}
	}

	Map<Player, Queue<Long>> torchQueues = new HashMap<>();
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockDamage(BlockDamageEvent event) {
		Player ply = event.getPlayer();
		if(playerHelper.isPlayerDisabled(ply)) {
			event.setCancelled(true);
			return;
		}

		if(PlayerHelper.getPlayerLevel(ply) < 0 && event.getInstaBreak()) {
			PlayerHelper.sendServerMessage(ply.getName() + " tried to illegaly break a block!");
			event.setCancelled(true);
		}

		final int typeId = event.getBlock().getTypeId();
		if (typeId == 50 || typeId == 76) {
			Queue<Long> torchQueue = torchQueues.get(ply);
			if (torchQueue == null)
				torchQueues.put(ply, torchQueue = new ArrayBlockingQueue<>(TORCH_BREAK_WINDOW+1));

			final long currentTimeMillis = System.currentTimeMillis();
			torchQueue.offer(currentTimeMillis);

			if (torchQueue.size() > TORCH_BREAK_WINDOW) {
				final long timeSinceStart = currentTimeMillis - torchQueue.poll();
				if (timeSinceStart < TORCH_BREAK_TIMEOUT_MILLIS) {
					PlayerHelper.sendServerMessage(ply.getName() + " was autokicked for breaking " + TORCH_BREAK_WINDOW + " torches in " + timeSinceStart + "ms.", "foxbukkit.opchat");
					plugin.bans.ban(plugin.getServer().getConsoleSender(), ply, "[AUTOMATED] Torchbreak", BanType.LOCAL);
					event.setCancelled(true);
					KickCommand.kickPlayer(ply, "[FB AUTOMATED] Torchbreak");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (event.getChangedType() == Material.PORTAL)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockCanBuild(BlockCanBuildEvent event)
	{
		if (!event.isBuildable()) {
			if (event.getMaterial() == Material.FENCE) {
				event.setBuildable(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		handlePistons(event.getBlocks(), event.getDirection());
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		if (!event.isSticky())
			return;

		final Block block = event.getRetractLocation().getBlock();
		final BlockFace pistonDirection = event.getDirection();
		BlockFace pushDirection = pistonDirection.getOppositeFace();

		final List<State> states = new ArrayList<>();

		for (BlockFace face : faces.get(pushDirection)) {
			handlePistonBlock(block, face, pushDirection, states);
		}

		do {
			final Block attachedBlock = block.getRelative(pistonDirection);
			final PlayerDirection attachment = BlockType.getAttachment(attachedBlock.getTypeId(), attachedBlock.getData());
			if (attachment == null)
				break;

			if (!attachment.name().equals(pistonDirection.getOppositeFace().name()))
				break;

			final Block targetBlock = attachedBlock.getRelative(pushDirection);

			states.add(new State(targetBlock, attachedBlock.getState()));

			attachedBlock.setTypeIdAndData(0, (byte) 0, false);
		} while (false);

		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { public void run() {
			for (State state : states) {
				state.apply();
			}
		}}, 3);
	}

	private static final TIntObjectMap<BlockFace> dataAttachments = new TIntObjectHashMap<>();
	private static final TIntObjectMap<BlockFace> nonDataAttachments = new TIntObjectHashMap<>();
	private static final Map<BlockFace, BlockFace[]> faces = new HashMap<>();
	static {
		HashMap<Integer, PlayerDirection> weDataAttachments = Utils.getPrivateValue(BlockType.class, null, "dataAttachments");
		HashMap<Integer, PlayerDirection> weNonDataAttachments = Utils.getPrivateValue(BlockType.class, null, "nonDataAttachments");

		for(Map.Entry<Integer, PlayerDirection> entry : weDataAttachments.entrySet()) {
			dataAttachments.put(entry.getKey(), BlockFace.valueOf(entry.getValue().name()));
		}

		for(Map.Entry<Integer, PlayerDirection> entry : weNonDataAttachments.entrySet()) {
			nonDataAttachments.put(entry.getKey(), BlockFace.valueOf(entry.getValue().name()));
		}

		/*weDataAttachments.forEachEntry(new TIntObjectProcedure<PlayerDirection>() {
			@Override
			public boolean execute(int i, PlayerDirection dir) {
				dataAttachments.put(i, BlockFace.valueOf(dir.name()));
				return true;
			}
		});

		weNonDataAttachments.forEachEntry(new TIntObjectProcedure<PlayerDirection>() {
			@Override
			public boolean execute(int i, PlayerDirection dir) {
				nonDataAttachments.put(i, BlockFace.valueOf(dir.name()));
				return true;
			}
		});*/

		final BlockFace[] xArray = new BlockFace[] { BlockFace.EAST , BlockFace.WEST , BlockFace.UP  , BlockFace.DOWN };
		final BlockFace[] yArray = new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST };
		final BlockFace[] zArray = new BlockFace[] { BlockFace.NORTH, BlockFace.SOUTH, BlockFace.UP  , BlockFace.DOWN };

		faces.put(BlockFace.NORTH, xArray);
		faces.put(BlockFace.SOUTH, xArray);
		faces.put(BlockFace.UP, yArray);
		faces.put(BlockFace.DOWN, yArray);
		faces.put(BlockFace.EAST, zArray);
		faces.put(BlockFace.WEST, zArray);
	}

	public static BlockFace getAttachment(int type, int data) {
		BlockFace direction = nonDataAttachments.get(type);
		if (direction != null) return direction;

		return dataAttachments.get((type << 4) | (data & 0xf));
	}

	private class State {
		private final Block targetBlock;
		private final BlockState state;

		public State(Block targetBlock, BlockState state) {
			this.targetBlock = targetBlock;
			this.state = state;
		}

		public void apply() {
			if (!targetBlock.isEmpty()) {
				if (state.getBlock().isEmpty())
					state.update(true);

				return;
			}

			switch (state.getTypeId()) {
			case 70: // STONE_PLATE
			case 72: // WOOD_PLATE
				targetBlock.setTypeIdAndData(state.getTypeId(), (byte) 0, false);
				break;

			case 77: // STONE_BUTTON
				targetBlock.setTypeIdAndData(state.getTypeId(), (byte) (state.getRawData() & ~0x4), false);
				break;

			case 63: // SIGN_POST
			case 68: // WALL_SIGN
				Sign newState = (Sign)targetBlock.getState();
				for (int i = 0; i < 4; ++i) {
					newState.setLine(i, ((Sign)state).getLine(i));
				}
				/* FALL-THROUGH */

			default:
				targetBlock.setTypeIdAndData(state.getTypeId(), state.getRawData(), false);
			}
		}
	}

	private void handlePistons(List<Block> blocks, BlockFace pushDirection) {
		if (blocks.isEmpty())
			return;

		final int modX = pushDirection.getModX();
		final int modY = pushDirection.getModY();
		final int modZ = pushDirection.getModZ();
		blocks = new ArrayList<>(blocks);
		Collections.sort(blocks, new Comparator<Block>() { public int compare(Block lhs, Block rhs) {
			return
			(rhs.getX()-lhs.getX())*modX +
			(rhs.getY()-lhs.getY())*modY +
			(rhs.getZ()-lhs.getZ())*modZ;
		}});

		final List<State> states = new ArrayList<>();

		final BlockFace[] blockFaces = faces.get(pushDirection);
		for (Block block : blocks) {
			for (BlockFace face : blockFaces) {
				handlePistonBlock(block, face, pushDirection, states);
			}
		}

		handlePistonBlock(blocks.get(0), pushDirection, pushDirection, states);

		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() { public void run() {
			for (State state : states) {
				state.apply();
			}
		}}, 2);
	}

	private void handlePistonBlock(Block block, BlockFace face, BlockFace pushDirection, final List<State> states) {
		final Block attachedBlock = block.getRelative(face);
		final BlockFace attachment = getAttachment(attachedBlock.getTypeId(), attachedBlock.getData());
		if (attachment == null)
			return;

		if (attachment != face.getOppositeFace())
			return;

		final Block targetBlock = attachedBlock.getRelative(pushDirection);
		if (!targetBlock.isEmpty())
			return;

		states.add(new State(targetBlock, attachedBlock.getState()));

		attachedBlock.setTypeIdAndData(0, (byte) 0, false);
	}
}
