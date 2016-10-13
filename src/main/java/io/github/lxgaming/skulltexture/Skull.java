package io.github.lxgaming.skulltexture;

import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

public class Skull {
	
	public static ItemStack getCustomSkull(String id, String texture) {
		GameProfile profile = new GameProfile(UUID.fromString(id), null);
		PropertyMap propertyMap = profile.getProperties();
		if (propertyMap == null) {
			throw new IllegalStateException("Profile doesn't contain a property map");
		}
		propertyMap.put("textures", new Property("textures", new String(texture)));
		ItemStack skullItem = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		ItemMeta skullMeta = skullItem.getItemMeta();
		Class<?> skullMetaClass = skullMeta.getClass();
		Reflections.getField(skullMetaClass, "profile", GameProfile.class).set(skullMeta, profile);
		skullItem.setItemMeta(skullMeta);
		return skullItem;
	}
}