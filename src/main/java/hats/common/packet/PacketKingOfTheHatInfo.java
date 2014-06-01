package hats.common.packet;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import hats.common.Hats;
import hats.common.core.HatHandler;
import ichun.common.core.network.AbstractPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class PacketKingOfTheHatInfo extends AbstractPacket
{
    public String currentKing;
    public String hatsList;

    public PacketKingOfTheHatInfo(){}

    public PacketKingOfTheHatInfo(String name, String hats)
    {
        currentKing = name;
        hatsList = hats;
    }

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
        ByteBufUtils.writeUTF8String(buffer, currentKing);
        ByteBufUtils.writeUTF8String(buffer, hatsList);
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
        Hats.config.updateSession("currentKing", ByteBufUtils.readUTF8String(buffer));

        hatsList = ByteBufUtils.readUTF8String(buffer);
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        if(!hatsList.isEmpty())
        {
            HatHandler.populateHatsList(hatsList);
        }
    }
}
