package de.doridian.yiffbukkit.listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.PluginManager;

import com.sk89q.worldedit.PlayerDirection;
import com.sk89q.worldedit.blocks.BlockType;

import de.doridian.yiffbukkit.YiffBukkit;
import de.doridian.yiffbukkit.mcbans.MCBans.BanType;
import de.doridian.yiffbukkit.permissions.YiffBukkitPermissionHandler;
import de.doridian.yiffbukkit.util.PlayerHelper;
import de.doridian.yiffbukkit.util.Utils;

/**
 * Handle events for all Block related events
 * @author Doridian
 */
public class YiffBukkitBlockListener extends BlockListener {
	private final YiffBukkit plugin;
	public static final Map<Material,String> blocklevels = new HashMap<Material,String>();
	public static final Set<Material> flammableBlocks = new HashSet<Material>();
	public static final BlockFace[] flameSpreadDirections = { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN };

	private static final int TORCH_BREAK_WINDOW = 8;
	private static final int TORCH_BREAK_TIMEOUT_MILLIS = 1000;

	static {
		blocklevels.put(Material.TNT, "yiffbukkit.place.block.tnt");
		blocklevels.put(Material.BEDROCK, "yiffbukkit.place.block.bedrock");

		blocklevels.put(Material.OBSIDIAN, "yiffbukkit.place.block.obsidian");

		blocklevels.put(Material.WATER, "yiffbukkit.place.block.water");
		blocklevels.put(Material.WATER_BUCKET, "yiffbukkit.place.block.water_bucket");
		blocklevels.put(Material.LAVA, "yiffbukkit.place.block.lava");
		blocklevels.put(Material.LAVA_BUCKET, "yiffbukkit.place.block.lava_bucket");

		blocklevels.put(Material.FLINT_AND_STEEL, "yiffbukkit.place.block.flint_and_steel");
		blocklevels.put(Material.FIRE, "yiffbukkit.place.block.fire");
		blocklevels.put(Material.PISTON_BASE, "yiffbukkit.place.block.piston.base");
		blocklevels.put(Material.PISTON_EXTENSION, "yiffbukkit.place.block.piston.extension");
		blocklevels.put(Material.PISTON_MOVING_PIECE, "yiffbukkit.place.block.piston.moving_piece");
		blocklevels.put(Material.PISTON_STICKY_BASE, "yiffbukkit.place.block.piston.sticky_base");

		flammableBlocks.add(Material.LOG);
		flammableBlocks.add(Material.WOOD);
		flammableBlocks.add(Material.WOOL);
		flammableBlocks.add(Material.BOOKSHELF);
		flammableBlocks.add(Material.LEAVES);
	}
	private PlayerHelper playerHelper;
	private YiffBukkitPermissionHandler permissionHandler;

	public YiffBukkitBlockListener(YiffBukkit instance) {
		plugin = instance;
		playerHelper = plugin.playerHelper;
		permissionHandler = plugin.permissionHandler;

		PluginManager pm = plugin.getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_PLACE, this, Priority.Normal, plugin);
		pm.registerEvent(Event.Type.BLOCK_CANBUILD, this, Priority.Normal, plugin);
		//pm.registerEvent(Event.Type.BLOCK_BREAK, this, Priority.Normal, plugin);
		pm.registerEvent(Event.Type.BLOCK_DAMAGE, this, Priority.Normal, plugin);
		pm.registerEvent(Event.Type.BLOCK_PHYSICS, this, Priority.Highest, plugin);
		pm.registerEvent(Event.Type.BLOCK_PISTON_EXTEND, this, Priority.Highest, plugin);
		pm.registerEvent(Event.Type.BLOCK_PISTON_RETRACT, this, Priority.Highest, plugin);

		playerHelper.registerMap(torchQueues);
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		Player ply = event.getPlayer();
		if (playerHelper.isPlayerDisabled(ply)) {
			playerHelper.sendDirectedMessage(ply, "You are not allowed to build right now.");
			event.setBuild(false);
			return;
		}

		final Block block = event.getBlock();
		Material material = block.getType();
		if (!permissionHandler.has(ply, "yiffbukkit.place")) {
			plugin.ircbot.sendToStaffChannel(ply.getName() + " is not allowed to build but tried tried to spawn " + material+".");
			playerHelper.sendServerMessage(ply.getName() + " is not allowed to build but tried tried to spawn " + material+".");
			event.setBuild(false);
			return;
		}

		final String permission = blocklevels.get(material);
		if (permission != null && !permissionHandler.has(ply, permission)) {
			plugin.ircbot.sendToStaffChannel(ply.getName() + " tried to spawn illegal block " + material+".");
			playerHelper.sendServerMessage(ply.getName() + " tried to spawn illegal block " + material+".");
			event.setBuild(false);
			return;
		}

		if (flammableBlocks.contains(material)) {
			if (!permissionHandler.has(ply, "yiffbukkit.place.flammablenearfire")) {
				for (BlockFace face : flameSpreadDirections) {
					Material neighborMaterial = block.getRelative(face).getType();
					if (neighborMaterial == Material.FIRE) {
						plugin.ircbot.sendToStaffChannel(ply.getName() + " tried to spawn flammable block " + material.toString() + " near fire.");
						playerHelper.sendServerMessage(ply.getName() + " tried to spawn flammable block " + material.toString() + " near fire.");
						event.setBuild(false);
					}
				}
			}
		}
	}

	Map<Player, Queue<Long>> torchQueues = new HashMap<Player, Queue<Long>>();
	@Override
	public void onBlockDamage(BlockDamageEvent event) {
		Player ply = event.getPlayer();
		if(playerHelper.isPlayerDisabled(ply)) {
			event.setCancelled(true);
			return;
		}

		if(playerHelper.getPlayerLevel(ply) < 0 && event.getInstaBreak()) {
			plugin.ircbot.sendToStaffChannel(ply.getName() + " tried to illegaly break a block!");
			playerHelper.sendServerMessage(ply.getName() + " tried to illegaly break a block!");
			event.setCancelled(true);
		}

		final int typeId = event.getBlock().getTypeId();
		if (typeId == 50 || typeId == 76) {
			Queue<Long> torchQueue = torchQueues.get(ply);
			if (torchQueue == null)
				torchQueues.put(ply, torchQueue = new ArrayBlockingQueue<Long>(TORCH_BREAK_WINDOW+1));

			final long currentTimeMillis = System.currentTimeMillis();
			torchQueue.offer(currentTimeMillis);

			if (torchQueue.size() > TORCH_BREAK_WINDOW) {
				final long timeSinceStart = currentTimeMillis - torchQueue.poll();
				if (timeSinceStart < TORCH_BREAK_TIMEOUT_MILLIS) {
					playerHelper.sendServerMessage(ply.getName() + " was autokicked for breaking "+TORCH_BREAK_WINDOW+" torches in "+timeSinceStart+"ms.", "yiffbukkit.opchat");
					plugin.mcbans.ban(new ConsoleCommandSender(plugin.getServer()), ply, "[AUTOMATED] Torchbreak", BanType.GLOBAL);
					event.setCancelled(true);
					ply.kickPlayer("[YB AUTOMATED] Torchbreak");
				}
			}
		}
	}

	@Override
	public void onBlockPhysics(BlockPhysicsEvent event) {
		if (event.getChangedType() == Material.PORTAL)
			event.setCancelled(true);
	}

	@Override
	public void onBlockCanBuild(BlockCanBuildEvent event)
	{
		if (event.isBuildable() == false) {
			if (event.getMaterial() == Material.FENCE) {
				event.setBuildable(true);
			}
		}
	}

	@Override
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		if (event.isCancelled())
			return;

		handlePistons(event.getBlocks(), event.getDirection());
	}

	@Override
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		if (event.isCancelled())
			return;

		if (!event.isSticky())
			return;

		final Block block = event.getRetractLocation().getBlock();
		final BlockFace pistonDirection = event.getDirection();
		BlockFace pushDirection = pistonDirection.getOppositeFace();

		final List<State> states = new ArrayList<State>();

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

	private static final Map<Integer, BlockFace> dataAttachments = new HashMap<Integer, BlockFace>();
	private static final Map<Integer, BlockFace> nonDataAttachments = new HashMap<Integer, BlockFace>();
	private static final Map<BlockFace, BlockFace[]> faces = new HashMap<BlockFace, BlockFace[]>();
	static {
		Map<Integer, PlayerDirection> weDataAttachments = Utils.getPrivateValue(BlockType.class, null, "dataAttachments");
		Map<Integer, PlayerDirection> weNonDataAttachments = Utils.getPrivateValue(BlockType.class, null, "nonDataAttachments");

		for (Entry<Integer, PlayerDirection> foo : weDataAttachments.entrySet()) {
			dataAttachments.put(foo.getKey(), BlockFace.valueOf(foo.getValue().name()));
		}
		for (Entry<Integer, PlayerDirection> foo : weNonDataAttachments.entrySet()) {
			nonDataAttachments.put(foo.getKey(), BlockFace.valueOf(foo.getValue().name()));
		}

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
		private Block targetBlock;
		private BlockState state;

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
		blocks = new ArrayList<Block>(blocks);
		Collections.sort(blocks, new Comparator<Block>() { public int compare(Block lhs, Block rhs) {
			return
			(rhs.getX()-lhs.getX())*modX +
			(rhs.getY()-lhs.getY())*modY +
			(rhs.getZ()-lhs.getZ())*modZ;
		}});

		final List<State> states = new ArrayList<State>();

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
