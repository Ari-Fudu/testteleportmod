package com.example.teleportonly.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandManager.class)
public class CommandManagerMixin {
    @Shadow
    private CommandDispatcher<ServerCommandSource> dispatcher;

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
            try {
                int result = dispatcher.execute(command, source.withLevel(2));
                cir.setReturnValue(result);
            } catch (CommandSyntaxException exception) {
                source.sendError(Text.literal(exception.getMessage()).formatted(Formatting.RED));
                cir.setReturnValue(0);
            }
            return;
        }

        source.sendError(Text.literal("Error: You do not have the privileges for that command!").formatted(Formatting.RED));
        cir.setReturnValue(0);
    }
}
