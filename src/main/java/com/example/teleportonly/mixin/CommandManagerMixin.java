package com.example.teleportonly.mixin;

import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.brigadier.ParseResults;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private void teleportOnly$blockCommands(ParseResults<ServerCommandSource> parseResults, String command, CallbackInfoReturnable<Integer> cir) {
        ServerCommandSource source = parseResults.getContext().getSource();
        if (!(source.getEntity() instanceof ServerPlayerEntity)) {
            return;
        }

        if (source.hasPermissionLevel(2)) {
            return;
        }

        String trimmed = command.trim();
        if (trimmed.startsWith("/")) {
            trimmed = trimmed.substring(1);
        }

        String base = trimmed.split("\\s+", 2)[0];
        if (base.equalsIgnoreCase("tp") || base.equalsIgnoreCase("teleport")) {
            return;
        }

        source.sendError(Text.literal("You can only use /tp or /teleport on this server.").formatted(Formatting.RED));
        cir.setReturnValue(0);
    }
}
