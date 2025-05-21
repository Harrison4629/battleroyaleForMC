package net.harrison.battleroyale.items;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.items.custom.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Battleroyale.MODID);

    public static final RegistryObject<Item> MEDKIT = ITEMS.register("medkit",
            () -> new MedkitItem(new Item.Properties().stacksTo(5)));
    public static final RegistryObject<Item> BANDAGE = ITEMS.register("bandage",
            () -> new BandageItem(new Item.Properties().stacksTo(8)));
    public static final RegistryObject<Item> REGENERATION_SYRINGE = ITEMS.register("regeneration_syringe",
            () -> new RegenerationSyringeItem(new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> CHAMELEON = ITEMS.register("chameleon",
            () -> new ChameleonItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ARMOR_PLATE_1 = ITEMS.register("armor_plate_1",
            () -> new ArmorPlateItem1(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ARMOR_PLATE_2 = ITEMS.register("armor_plate_2",
            () -> new ArmorPlateItem2(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ARMOR_PLATE_3 = ITEMS.register("armor_plate_3",
            () -> new ArmorPlateItem3(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ARMOR_PLATE_4 = ITEMS.register("armor_plate_4",
            () -> new ArmorPlateItem4(new Item.Properties().stacksTo(1)));


}
