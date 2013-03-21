package de.doridian.yiffbukkit.transmute;

import de.doridian.yiffbukkitsplit.util.PlayerHelper;
import net.minecraft.server.v1_5_R2.Block;
import net.minecraft.server.v1_5_R2.ItemStack;
import net.minecraft.server.v1_5_R2.Packet;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ItemShape extends VehicleShape {
	static {
		//yOffsets[1] = 1.62;
	}

	protected final ItemStack itemStack = new ItemStack(Block.CACTUS, 1, 0);

	public ItemShape(Transmute transmute, Entity entity, int mobType) {
		super(transmute, entity, mobType);

		dropping = true;
	}

	protected Packet createItemMetadataPacket() {
		return createMetadataPacket(10, itemStack.cloneItemStack());
	}

	protected void sendMetadataPacket() {
		sendPacketToPlayersAround(transmute.ignorePacket(createItemMetadataPacket()));
	}

	@Override
	public void createTransmutedEntity() {
		super.createTransmutedEntity();
		sendMetadataPacket();
	}

	@Override
	public void createTransmutedEntity(Player forPlayer) {
		super.createTransmutedEntity(forPlayer);
		PlayerHelper.sendPacketToPlayer(forPlayer, transmute.ignorePacket(createItemMetadataPacket()));
	}

	public int getType() {
		return itemStack.id;
	}

	public void setType(int type) {
		itemStack.id = type;

		sendMetadataPacket();
	}

	public int getDataValue() {
		return itemStack.getData();
	}

	public void setData(int data) {
		itemStack.setData(data);

		sendMetadataPacket();
	}

	public int getCount() {
		return itemStack.count;
	}

	public void setCount(int count) {
		itemStack.count = count;

		sendMetadataPacket();
	}

	public void setType(int type, int data) {
		itemStack.id = type;
		itemStack.setData(data);

		sendMetadataPacket();
	}

	public void setType(int type, int data, int count) {
		itemStack.id = type;
		itemStack.setData(data);
		itemStack.count = count;

		sendMetadataPacket();
	}
}
