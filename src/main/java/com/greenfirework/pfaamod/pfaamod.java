package com.greenfirework.pfaamod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.greenfirework.pfaamod.blocks.Blocks;
import com.greenfirework.pfaamod.items.Items;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mod(modid = pfaamod.MODID, version = Tags.VERSION, name = "pfaamod", acceptedMinecraftVersions = "[1.7.10]")
public class pfaamod {

    public static final String MODID = "pfaamod";
    public static final Logger LOG = LogManager.getLogger(MODID);

    @Mod.Instance("pfaamod")
    public static pfaamod instance;
    
    @SidedProxy(clientSide = "com.greenfirework.pfaamod.ClientProxy", serverSide = "com.greenfirework.pfaamod.CommonProxy")
    public static CommonProxy proxy;

    public static final SimpleNetworkWrapper networkMessages = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
    
    @Mod.EventHandler
    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry." (Remove if not needed)
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);
        
        Blocks.init();
        Items.init();
    }

    @Mod.EventHandler
    // load "Do your mod setup. Build whatever data structures you care about. Register recipes." (Remove if not needed)
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    // postInit "Handle interaction with other mods, complete your setup based on this." (Remove if not needed)
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    // register server commands in this event handler (Remove if not needed)
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
    
    public static CreativeTabs creativeTab = new CreativeTabs(MODID)
	{
		@Override
		public Item getTabIconItem()
		{
			return null;
		}
		@Override
		public ItemStack getIconItemStack()
		{
			return new ItemStack(Blocks.ChannelAssembly,1,1);
		}
	};
}
