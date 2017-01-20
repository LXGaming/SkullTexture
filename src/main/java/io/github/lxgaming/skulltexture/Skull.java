/*
 * Copyright 2017 Alex Thomson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
