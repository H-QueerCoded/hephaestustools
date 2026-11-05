package com.titammods.hephaestus_tools.materials;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record Material(
        MaterialId id,
        MaterialIngredient ingredient,
        int color,
        MaterialStats headStats,
        MaterialStats handleStats,
        MaterialStats bindingStats,
        Optional<MaterialAnimation> animation
) {
    public record MaterialIngredient(ResourceLocation id, boolean tag) {

        public static final MaterialIngredient EMPTY =
                new MaterialIngredient(ResourceLocation.withDefaultNamespace("air"), false);

        private static final Codec<MaterialIngredient> STRING_CODEC = Codec.STRING.xmap(
                text -> text.startsWith("#")
                        ? new MaterialIngredient(ResourceLocation.parse(text.substring(1)), true)
                        : new MaterialIngredient(ResourceLocation.parse(text), false),
                value -> value.tag() ? "#" + value.id() : value.id().toString()
        );

        private static final Codec<MaterialIngredient> OBJECT_CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        ResourceLocation.CODEC.optionalFieldOf("item")
                                .forGetter(value -> value.tag() ? Optional.<ResourceLocation>empty() : Optional.of(value.id())),
                        ResourceLocation.CODEC.optionalFieldOf("tag")
                                .forGetter(value -> value.tag() ? Optional.of(value.id()) : Optional.<ResourceLocation>empty())
                ).apply(instance, (item, tag) -> tag
                        .map(found -> new MaterialIngredient(found, true))
                        .orElseGet(() -> new MaterialIngredient(item.orElse(EMPTY.id()), false)))
        );

        public static final Codec<MaterialIngredient> CODEC =
                Codec.either(STRING_CODEC, OBJECT_CODEC)
                        .xmap(either -> either.map(left -> left, right -> right), Either::left);

        public static final StreamCodec<RegistryFriendlyByteBuf, MaterialIngredient> STREAM_CODEC =
                StreamCodec.of(
                        (buf, value) -> {
                            buf.writeResourceLocation(value.id());
                            buf.writeBoolean(value.tag());
                        },
                        buf -> new MaterialIngredient(buf.readResourceLocation(), buf.readBoolean())
                );

        public boolean test(ItemStack stack) {
            if (stack.isEmpty()) return false;
            return tag
                    ? stack.is(TagKey.create(Registries.ITEM, id))
                    : stack.is(BuiltInRegistries.ITEM.get(id));
        }
    }

    public static final Codec<Material> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    MaterialId.CODEC.fieldOf("id").forGetter(Material::id),
                    MaterialIngredient.CODEC.optionalFieldOf("ingredient", MaterialIngredient.EMPTY)
                            .forGetter(Material::ingredient),
                    Codec.INT.optionalFieldOf("color", 0xFFFFFF).forGetter(Material::color),
                    MaterialStats.HEAD_CODEC.optionalFieldOf("head_stats", MaterialStats.EMPTY).forGetter(Material::headStats),
                    MaterialStats.HANDLE_CODEC.optionalFieldOf("handle_stats", MaterialStats.EMPTY_HANDLE).forGetter(Material::handleStats),
                    MaterialStats.BINDING_CODEC.optionalFieldOf("binding_stats", MaterialStats.EMPTY_BINDING).forGetter(Material::bindingStats),
                    MaterialAnimation.CODEC.optionalFieldOf("animation").forGetter(Material::animation)
            ).apply(instance, Material::new)
    );

    private static final StreamCodec<ByteBuf, Optional<MaterialAnimation>> ANIMATION_STREAM_CODEC =
            ByteBufCodecs.optional(MaterialAnimation.STREAM_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, Material> STREAM_CODEC =
            StreamCodec.of(
                    (buf, m) -> {
                        MaterialId.STREAM_CODEC.encode(buf, m.id());
                        MaterialIngredient.STREAM_CODEC.encode(buf, m.ingredient());
                        buf.writeInt(m.color());
                        MaterialStats.STREAM_CODEC.encode(buf, m.headStats());
                        MaterialStats.STREAM_CODEC.encode(buf, m.handleStats());
                        MaterialStats.STREAM_CODEC.encode(buf, m.bindingStats());
                        ANIMATION_STREAM_CODEC.encode(buf, m.animation());
                    },
                    buf -> new Material(
                            MaterialId.STREAM_CODEC.decode(buf),
                            MaterialIngredient.STREAM_CODEC.decode(buf),
                            buf.readInt(),
                            MaterialStats.STREAM_CODEC.decode(buf),
                            MaterialStats.STREAM_CODEC.decode(buf),
                            MaterialStats.STREAM_CODEC.decode(buf),
                            ANIMATION_STREAM_CODEC.decode(buf)
                    )
            );

    public MaterialStats getStatsForSlot(int slot) {
        return switch (slot) {
            case 0  -> headStats;
            case 1  -> handleStats;
            case 2  -> bindingStats;
            default -> MaterialStats.emptyFor(slot);
        };
    }
}