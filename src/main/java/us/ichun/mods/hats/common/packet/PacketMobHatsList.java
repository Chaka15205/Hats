package us.ichun.mods.hats.common.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import us.ichun.mods.hats.common.Hats;
import us.ichun.mods.hats.common.core.HatInfo;
import us.ichun.mods.hats.common.entity.EntityHat;
import us.ichun.mods.ichunutil.common.core.network.AbstractPacket;

import java.util.ArrayList;

public class PacketMobHatsList extends AbstractPacket
{
    public ArrayList<Integer> mobIds = new ArrayList<Integer>();
    public ArrayList<String> hatNames = new ArrayList<String>();

    public PacketMobHatsList(){}

    public PacketMobHatsList(ArrayList<Integer> ids, ArrayList<String> names)
    {
        mobIds = ids;
        hatNames = names;
    }

    @Override
    public void writeTo(ByteBuf buffer, Side side)
    {
        try
        {
            for(int i = 0; i < mobIds.size(); i++)
            {
                buffer.writeInt(mobIds.get(i));
                if(mobIds.get(i) != -2)
                {
                    ByteBufUtils.writeUTF8String(buffer, hatNames.get(i));
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        buffer.writeInt(-2);
    }

    @Override
    public void readFrom(ByteBuf buffer, Side side)
    {
        int id = buffer.readInt();
        while(id != -2)
        {
            mobIds.add(id);
            hatNames.add(ByteBufUtils.readUTF8String(buffer));
            id = buffer.readInt();
        }
    }

    @Override
    public void execute(Side side, EntityPlayer player)
    {
        handleClient(side, player);
    }

    @SideOnly(Side.CLIENT)
    public void handleClient(Side side, EntityPlayer player)
    {
        for(int i = 0; i < Math.min(mobIds.size(), hatNames.size()); i++)
        {
            Entity ent = Minecraft.getMinecraft().theWorld.getEntityByID(mobIds.get(i));
            if(ent != null && ent instanceof EntityLivingBase)
            {
                HatInfo hatInfo = new HatInfo(hatNames.get(i));
                EntityHat hat = new EntityHat(ent.worldObj, (EntityLivingBase)ent, hatInfo);
                Hats.proxy.tickHandlerClient.mobHats.put(ent.getEntityId(), hat);
                ent.worldObj.spawnEntityInWorld(hat);
            }
        }
    }
}
