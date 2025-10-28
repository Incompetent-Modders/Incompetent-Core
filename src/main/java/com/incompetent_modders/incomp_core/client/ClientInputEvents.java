package com.incompetent_modders.incomp_core.client;

import com.incompetent_modders.incomp_core.IncompCore;
import com.incompetent_modders.incomp_core.api.class_type.ability.AbilityWheelOverlay;
import com.incompetent_modders.incomp_core.client.util.KeyState;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

import java.util.ArrayList;
import java.util.Optional;

import static com.incompetent_modders.incomp_core.client.ClientModEvents.ACTIVATE_CLASS_ABILITY;

@EventBusSubscriber(modid = IncompCore.MODID, value = Dist.CLIENT)
public class ClientInputEvents {
    private static final ArrayList<KeyState> KEY_STATES = new ArrayList<>();

    private static final KeyState ABILITY_WHEEL_STATE = register(ACTIVATE_CLASS_ABILITY.get());

    private static int useKeyId = Integer.MIN_VALUE;
    public static boolean isUseKeyDown;
    public static boolean hasReleasedSinceCasting;
    public static boolean isShiftKeyDown;

    @SubscribeEvent
    public static void clientMouseScrolled(InputEvent.MouseScrollingEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player == null)
            return;
        if (Minecraft.getInstance().screen == null) {

        }
    }

    @SubscribeEvent
    public static void onUseInput(InputEvent.InteractionKeyMappingTriggered event) {
        if (event.isUseItem()) {

        } else if (event.isAttack()) {

        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        handleInputEvent(event.getKey(), event.getAction());
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton.Pre event) {
        handleInputEvent(event.getButton(), event.getAction());
    }

    private static void handleInputEvent(int button, int action) {
        var minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) {
            return;
        }
        handleRightClickSuppression(button, action);
        if (button == InputConstants.KEY_LSHIFT) {
            isShiftKeyDown = action >= InputConstants.PRESS;
        }

        if (ABILITY_WHEEL_STATE.wasPressed()) {
            if (minecraft.screen == null) {
                AbilityWheelOverlay.instance.open();
            }
        }
        if (ABILITY_WHEEL_STATE.wasReleased()) {
            if (minecraft.screen == null && AbilityWheelOverlay.instance.active) {
                AbilityWheelOverlay.instance.close();
            }
        }

    }

    private static void handleRightClickSuppression(int button, int action) {
        if (useKeyId == Integer.MIN_VALUE) {
            useKeyId = Minecraft.getInstance().options.keyUse.getKey().getValue();
        }

        if (button == useKeyId) {
            if (action == InputConstants.RELEASE) {
                isUseKeyDown = false;
                hasReleasedSinceCasting = true;
            } else if (action == InputConstants.PRESS) {
                isUseKeyDown = true;
            }
        }
    }

    private static void update() {
        for (KeyState k : KEY_STATES) {
            k.update();
        }
    }

    private static KeyState register(KeyMapping key) {
        var k = new KeyState(key);
        KEY_STATES.add(k);
        return k;
    }
}
