package net.mrqx.sbr_core.utils;

import mods.flammpfeil.slashblade.util.InputCommand;
import net.minecraft.world.entity.Entity;

import java.util.*;
import java.util.function.Predicate;

public class InputStream {
    private static final Map<UUID, InputStream> INPUT_STREAM_MAP = new HashMap<>();

    public static InputStream getOrCreateInputStream(Entity entity) {
        InputStream inputStream = INPUT_STREAM_MAP.get(entity.getUUID());
        if (inputStream == null) {
            inputStream = new InputStream(entity);
            INPUT_STREAM_MAP.put(entity.getUUID(), inputStream);
        }
        return inputStream;
    }

    private final LinkedList<KeyInput> keyStream = new LinkedList<>();
    public final UUID uuid;
    public final Entity entity;
    private static final long INPUT_TIMEOUT = 20 * 30;

    public InputStream(Entity entity) {
        this.entity = entity;
        this.uuid = entity.getUUID();
    }

    private void cleanTimeOutInput() {
        long time = entity.level().getGameTime();
        while (!keyStream.isEmpty()) {
            KeyInput firstKey = keyStream.getLast();
            if (firstKey.time < time - INPUT_TIMEOUT) {
                keyStream.removeLast();
            } else {
                break;
            }
        }
    }

    public void addInput(InputCommand inputCommand, EnumSet<InputCommand> commands, InputType type) {
        keyStream.addFirst(new KeyInput(entity.level().getGameTime(), inputCommand, commands, type));
    }

    public boolean checkInputWithPredicate(Predicate<KeyInput> predicate) {
        cleanTimeOutInput();
        return keyStream.stream().anyMatch(predicate);
    }

    public boolean checkInput(InputCommand targetCommand, InputType type) {
        return checkInputWithPredicate(key -> key.inputCommand.equals(targetCommand)
                && key.type.equals(type)
        );
    }

    public boolean checkInputWithTime(InputCommand targetCommand, InputType type, long timeLimit) {
        return checkInputWithPredicate(key -> key.inputCommand.equals(targetCommand)
                && (key.time + timeLimit >= entity.level().getGameTime())
                && key.type.equals(type)
        );
    }

    public boolean checkInputWithRangedTime(InputCommand targetCommand, InputType type, long startTimeLimit, long endTimeLimit) {
        return checkInputWithPredicate(key -> key.inputCommand.equals(targetCommand)
                && (key.time + startTimeLimit >= entity.level().getGameTime())
                && (key.time + endTimeLimit <= entity.level().getGameTime())
                && key.type.equals(type)
        );
    }

    public boolean checkInputWithCommands(InputCommand targetCommand, InputType type, EnumSet<InputCommand> commands) {
        return checkInputWithPredicate(key -> key.inputCommand.equals(targetCommand)
                && key.commands.containsAll(commands)
                && key.type.equals(type)
        );
    }

    public boolean checkInputWithCommandsAndTime(InputCommand targetCommand, InputType type, long timeLimit, EnumSet<InputCommand> commands) {
        return checkInputWithPredicate(key -> key.inputCommand.equals(targetCommand)
                && (key.time + timeLimit >= entity.level().getGameTime())
                && key.commands.containsAll(commands)
                && key.type.equals(type)
        );
    }

    public boolean checkInputWithCommandsAndRangedTime(InputCommand targetCommand, InputType type, long startTimeLimit, long endTimeLimit, EnumSet<InputCommand> commands) {
        return checkInputWithPredicate(key -> key.inputCommand.equals(targetCommand)
                && (key.time + startTimeLimit >= entity.level().getGameTime())
                && (key.time + endTimeLimit <= entity.level().getGameTime())
                && key.commands.containsAll(commands)
                && key.type.equals(type)
        );
    }

    public boolean checkTimeLineInput(LinkedList<TimeLineKeyInput> inputTimeLine) {
        cleanTimeOutInput();

        if (inputTimeLine.isEmpty()) {
            return true;
        }

        long currentReferenceTime = entity.level().getGameTime();
        Iterator<KeyInput> inputIterator = keyStream.iterator();

        for (TimeLineKeyInput timelineKey : inputTimeLine) {
            long startTime = currentReferenceTime - timelineKey.startBeforeTime;
            long endTime = currentReferenceTime - timelineKey.endBeforeTime;
            boolean found = false;

            while (inputIterator.hasNext()) {
                KeyInput nextInput = inputIterator.next();
                if (nextInput.time > endTime) {
                    continue;
                }
                if (nextInput.time < startTime) {
                    break;
                }

                if (nextInput.inputCommand.equals(timelineKey.inputCommand)
                        && nextInput.type.equals(timelineKey.type)
                        && nextInput.commands.containsAll(timelineKey.commands)) {
                    currentReferenceTime = nextInput.time;
                    found = true;
                    break;
                }
            }

            if (!found) {
                return false;
            }
        }
        return true;
    }

    public record KeyInput(long time, InputCommand inputCommand, EnumSet<InputCommand> commands, InputType type) {
    }

    public record TimeLineKeyInput(long startBeforeTime, long endBeforeTime, InputCommand inputCommand,
                                   EnumSet<InputCommand> commands, InputType type) {
    }

    public enum InputType {
        START,
        END
    }
}
