package com.github.imdmk.spenttime.feature.gui;

import com.github.imdmk.spenttime.gui.IdentifiableGui;
import com.github.imdmk.spenttime.task.TaskScheduler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages registration and opening of GUIs identified by their unique identifiers.
 * Responsible for running GUI open operations synchronously on the main server thread.
 */
public class GuiManager {

    private final Map<String, IdentifiableGui> identifiableGuis = new ConcurrentHashMap<>();

    private final TaskScheduler taskScheduler;

    /**
     * Constructs a GuiManager with the given task scheduler.
     *
     * @param taskScheduler used to schedule synchronous tasks on the main server thread
     * @throws NullPointerException if taskScheduler is null
     */
    public GuiManager(@NotNull TaskScheduler taskScheduler) {
        this.taskScheduler = Objects.requireNonNull(taskScheduler, "taskScheduler cannot be null");
    }

    /**
     * Registers a GUI instance with its unique identifier.
     * If a GUI with the same identifier already exists, it will be replaced.
     *
     * @param gui the GUI instance to register
     * @throws NullPointerException if gui or its identifier is null
     */
    public void registerGui(@NotNull IdentifiableGui gui) {
        Objects.requireNonNull(gui, "gui cannot be null");
        Objects.requireNonNull(gui.getIdentifier(), "gui identifier cannot be null");

        this.identifiableGuis.put(gui.getIdentifier(), gui);
    }

    /**
     * Opens a simple GUI for the specified player.
     *
     * @param identifier the unique identifier of the GUI
     * @param player the player to open the GUI for
     * @throws IllegalArgumentException if no GUI with the given identifier is registered,
     *                                  or if the registered GUI is not of type SimpleGui
     * @throws NullPointerException if identifier or player is null
     */
    public void openGui(@NotNull String identifier, @NotNull Player player) {
        Objects.requireNonNull(identifier, "identifier cannot be null");
        Objects.requireNonNull(player, "player cannot be null");

        IdentifiableGui gui = this.getGuiOrThrow(identifier);
        if (gui instanceof SimpleGui simpleGui) {
            this.taskScheduler.runSync(() -> simpleGui.open(player));
        }
        else {
            throw new IllegalArgumentException("GUI with identifier '" + identifier + "' is not a SimpleGui");
        }
    }

    /**
     * Opens a parameterized GUI for the specified player with an additional parameter.
     *
     * @param identifier the unique identifier of the GUI
     * @param viewer the player to open the GUI for
     * @param parameter the parameter to pass to the GUI
     * @param <T> the type of the parameter
     * @throws IllegalArgumentException if no GUI with the given identifier is registered,
     *                                  or if the registered GUI is not of type ParameterizedGui
     * @throws NullPointerException if identifier, viewer, or parameter is null
     */
    @SuppressWarnings("unchecked")
    public <T> void openGui(@NotNull String identifier, @NotNull Player viewer, @NotNull T parameter) {
        Objects.requireNonNull(identifier, "identifier cannot be null");
        Objects.requireNonNull(viewer, "viewer cannot be null");
        Objects.requireNonNull(parameter, "parameter cannot be null");

        IdentifiableGui gui = this.getGuiOrThrow(identifier);
        if (gui instanceof ParameterizedGui<?> paramGui) {
            this.taskScheduler.runSync(() -> ((ParameterizedGui<T>) paramGui).open(viewer, parameter));
        }
        else {
            throw new IllegalArgumentException("GUI with identifier '" + identifier + "' is not a ParameterizedGui");
        }
    }

    /**
     * Retrieves the GUI by identifier or throws if not found.
     *
     * @param identifier unique GUI identifier
     * @return the registered GUI instance
     * @throws IllegalArgumentException if no GUI with the given identifier is registered
     */
    private IdentifiableGui getGuiOrThrow(@NotNull String identifier) {
        IdentifiableGui gui = this.identifiableGuis.get(identifier);
        if (gui == null) {
            throw new IllegalArgumentException("No GUI registered with identifier '" + identifier + "'");
        }

        return gui;
    }

}
