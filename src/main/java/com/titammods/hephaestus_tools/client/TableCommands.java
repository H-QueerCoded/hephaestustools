package com.titammods.hephaestus_tools.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.titammods.hephaestus_tools.config.HephaestusConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class TableCommands {
    private TableCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("hephaestus")
                .then(Commands.literal("visual")
                        .executes(ctx -> setVisual(ctx, !HephaestusConfig.modernInterface()))
                        .then(Commands.literal("modern").executes(ctx -> setVisual(ctx, true)))
                        .then(Commands.literal("classic").executes(ctx -> setVisual(ctx, false))))
                .then(Commands.literal("modern")
                        .then(Commands.literal("anim")
                                .executes(ctx -> setAnim(ctx, HephaestusConfig.skipCinematics()))
                                .then(Commands.literal("on").executes(ctx -> setAnim(ctx, true)))
                                .then(Commands.literal("off").executes(ctx -> setAnim(ctx, false))))));
    }

    private static int setVisual(CommandContext<CommandSourceStack> ctx, boolean modern) {
        HephaestusConfig.setModernInterface(modern);
        TableStyleClient.send();
        Component value = Component.literal(modern ? "MODERN" : "CLASSIC").withStyle(ChatFormatting.YELLOW);
        ctx.getSource().sendSuccess(() -> Component.translatable("hephaestus_tools.configuration.table_interface")
                .append(": ").append(value), false);
        return 1;
    }

    private static int setAnim(CommandContext<CommandSourceStack> ctx, boolean on) {
        HephaestusConfig.setSkipCinematics(!on);
        Component value = Component.translatable(on ? "options.off" : "options.on")
                .withStyle(on ? ChatFormatting.RED : ChatFormatting.GREEN);
        ctx.getSource().sendSuccess(() -> Component.translatable("hephaestus_tools.configuration.skip_cinematics")
                .append(": ").append(value), false);
        return 1;
    }
}
