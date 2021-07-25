package moe.plushie.armourers_workshop.common.init.items;

import net.minecraft.inventory.EquipmentSlotType;

public class ItemArmourContainer extends AbstractModItemArmour {

    public ItemArmourContainer(String name, EquipmentSlotType armourType) {
        super(name, ArmorMaterial.IRON, armourType, false);
        setCreativeTab(null);
    }
}
