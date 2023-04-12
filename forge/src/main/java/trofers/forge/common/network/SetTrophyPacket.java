package trofers.forge.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import trofers.forge.common.block.entity.TrophyBlockEntity;
import trofers.forge.common.trophy.Trophy;
import trofers.forge.common.trophy.TrophyManager;

import java.util.function.Supplier;

public class SetTrophyPacket {

    private final Trophy trophy;
    private final BlockPos blockPos;

    @SuppressWarnings("unused")
    public SetTrophyPacket(FriendlyByteBuf buffer) {
        this.trophy = TrophyManager.get(buffer.readResourceLocation());
        this.blockPos = buffer.readBlockPos();
    }

    public SetTrophyPacket(Trophy trophy, BlockPos blockPos) {
        this.trophy = trophy;
        this.blockPos = blockPos;
    }

    @SuppressWarnings("unused")
    void encode(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(trophy.id());
        buffer.writeBlockPos(blockPos);
    }

    void handle(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player != null) {
            context.get().enqueueWork(() -> {
                if (player.isCreative()
                        && player.level.isLoaded(blockPos)
                        && player.level.getBlockEntity(blockPos) instanceof TrophyBlockEntity blockEntity
                ) {
                    blockEntity.setTrophy(trophy);
                }
            });
        }
        context.get().setPacketHandled(true);
    }
}
