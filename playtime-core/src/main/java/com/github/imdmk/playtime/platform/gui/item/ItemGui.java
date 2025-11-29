package com.github.imdmk.playtime.platform.gui.item;

import com.github.imdmk.playtime.shared.Validator;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable data model representing a GUI item definition.
 * <p>
 * Pure data – no logic, no rendering.
 * Used to describe items in configuration and GUI assembly layers.
 */
public record ItemGui(
        @NotNull Material material,
        @NotNull Component name,
        @NotNull List<Component> lore,

        @Nullable Integer slot,
        @Nullable Map<Enchantment, Integer> enchantments,
        @Nullable List<ItemFlag> flags,
        @Nullable String requiredPermission) {

    public ItemGui {
        Validator.notNull(material, "material cannot be null");
        Validator.notNull(name, "name cannot be null");
        Validator.notNull(lore, "lore cannot be null");

        lore = List.copyOf(lore);

        if (enchantments != null) {
            enchantments = Map.copyOf(enchantments);
        }

        if (flags != null) {
            flags = List.copyOf(flags);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Material material;
        private Component name;
        private List<Component> lore = List.of();

        private Integer slot;
        private Map<Enchantment, Integer> enchantments = Map.of();
        private List<ItemFlag> flags = List.of();
        private String requiredPermission;

        @CheckReturnValue
        @Contract(value = "_ -> this", mutates = "this")
        public Builder material(@NotNull Material material) {
            this.material = Validator.notNull(material, "material cannot be null");
            return this;
        }

        @CheckReturnValue
        @Contract(value = "_ -> this", mutates = "this")
        public Builder name(@NotNull Component name) {
            this.name = Validator.notNull(name, "name cannot be null");
            return this;
        }

        @CheckReturnValue
        @Contract(value = "_ -> this", mutates = "this")
        public Builder lore(@NotNull List<Component> lore) {
            Validator.notNull(lore, "lore cannot be null");
            this.lore = List.copyOf(lore);
            return this;
        }

        @CheckReturnValue
        @Contract(value = "_ -> this", mutates = "this")
        public Builder slot(@Nullable Integer slot) {
            this.slot = slot;
            return this;
        }

        @CheckReturnValue
        @Contract(value = "_ -> this", mutates = "this")
        public Builder enchantments(@Nullable Map<Enchantment,Integer> enchantments) {
            this.enchantments = (enchantments == null) ? Map.of() : Map.copyOf(enchantments);
            return this;
        }

        @CheckReturnValue
        @Contract(value = "_ -> this", mutates = "this")
        public Builder flags(@Nullable List<ItemFlag> flags) {
            this.flags = (flags == null) ? List.of() : List.copyOf(flags);
            return this;
        }

        @CheckReturnValue
        @Contract(value = "_ -> this", mutates = "this")
        public Builder requiredPermission(@Nullable String permission) {
            this.requiredPermission = (permission == null || permission.isBlank()) ? null : permission.trim();
            return this;
        }

        @CheckReturnValue
        @Contract(value = "_, _ -> this", mutates = "this")
        public Builder addEnchantment(@NotNull Enchantment enchantment, @NotNull Integer level) {
            Validator.notNull(enchantment, "enchantment cannot be null");
            Validator.notNull(level, "level cannot be null");

            Map<Enchantment, Integer> newEnchantments = new HashMap<>(this.enchantments);
            newEnchantments.put(enchantment, level);
            this.enchantments = Map.copyOf(newEnchantments);
            return this;
        }

        @CheckReturnValue
        @Contract(value = "_ -> this", mutates = "this")
        public Builder addFlags(@NotNull ItemFlag... toAdd) {
            Validator.notNull(toAdd, "flags cannot be null");

            List<ItemFlag> newFlags = new ArrayList<>(this.flags);
            Collections.addAll(newFlags, toAdd);
            this.flags = List.copyOf(newFlags);
            return this;
        }

        @CheckReturnValue
        @Contract(value = "_ -> this", mutates = "this")
        public Builder appendLore(@NotNull Component... lines) {
            Validator.notNull(lines, "lines cannot be null");

            List<Component> newLore = new ArrayList<>(this.lore);
            Collections.addAll(newLore, lines);
            this.lore = List.copyOf(newLore);
            return this;
        }

        @NotNull
        public ItemGui build() {
            return new ItemGui(
                    this.material,
                    this.name,
                    this.lore,
                    this.slot,
                    this.enchantments,
                    this.flags,
                    this.requiredPermission
            );
        }
    }


}
