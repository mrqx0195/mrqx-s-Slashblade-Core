package net.mrqx.sbr_core.utils;

import mods.flammpfeil.slashblade.util.InputCommand;
import net.minecraft.world.entity.Entity;

import java.util.*;
import java.util.function.Predicate;

/**
 * 按键输入流管理器。
 * <p>
 * 为每个实体维护一个历史按键输入队列（{@link KeyInput}），支持按指令类型、时间范围、
 * 以及复合按键条件对历史输入进行查询，可用于连段/搓招系统。
 */
public class InputStream {
    private static final Map<UUID, InputStream> INPUT_STREAM_MAP = new HashMap<>();
    
    /**
     * 获取或创建指定实体对应的输入流实例。
     */
    public static InputStream getOrCreateInputStream(Entity entity) {
        InputStream inputStream = INPUT_STREAM_MAP.get(entity.getUUID());
        if (inputStream == null) {
            inputStream = new InputStream(entity);
            INPUT_STREAM_MAP.put(entity.getUUID(), inputStream);
        }
        return inputStream;
    }
    
    /**
     * 递增指定实体的 tick 计数，用于超时判定。
     */
    public static void tick(UUID uuid) {
        if (INPUT_STREAM_MAP.containsKey(uuid)) {
            INPUT_STREAM_MAP.get(uuid).tickCount++;
        }
    }
    
    private final LinkedList<KeyInput> keyStream = new LinkedList<>();
    public final UUID uuid;
    public final Entity entity;
    public long tickCount = 0;
    private static final long INPUT_TIMEOUT = 20 * 30;
    
    /**
     * 构造一个与指定实体绑定的输入流。
     */
    public InputStream(Entity entity) {
        this.entity = entity;
        this.uuid = entity.getUUID();
    }
    
    /**
     * 清理超出 {@link #INPUT_TIMEOUT} 的过期输入记录。
     */
    private void cleanTimeOutInput() {
        long time = this.tickCount;
        while (!keyStream.isEmpty()) {
            KeyInput lastKey = keyStream.getLast();
            if (lastKey.time < time - INPUT_TIMEOUT) {
                keyStream.removeLast();
            } else {
                break;
            }
        }
        while (!keyStream.isEmpty()) {
            KeyInput firstKey = keyStream.getFirst();
            if (firstKey.time > time) {
                keyStream.removeFirst();
            } else {
                break;
            }
        }
    }
    
    /**
     * 向输入流头部添加一条按键记录。
     */
    public void addInput(InputCommand inputCommand, EnumSet<InputCommand> commands, InputType type) {
        keyStream.addFirst(new KeyInput(this.tickCount, inputCommand, commands, type));
    }
    
    /**
     * 使用谓词检查输入流中是否存在匹配的按键记录。
     */
    public boolean checkInputWithPredicate(Predicate<KeyInput> predicate) {
        cleanTimeOutInput();
        return keyStream.stream().anyMatch(predicate);
    }
    
    /**
     * 检查指定指令和类型的按键是否在输入流中。
     */
    public boolean checkInput(InputCommand targetCommand, InputType type) {
        return checkInputWithPredicate(key -> key.inputCommand.equals(targetCommand)
            && key.type.equals(type)
        );
    }
    
    /**
     * 检查指定指令和类型是否在指定时间范围内出现过。
     */
    public boolean checkInputWithTime(InputCommand targetCommand, InputType type, long timeLimit) {
        return checkInputWithPredicate(key -> key.inputCommand.equals(targetCommand)
            && (key.time + timeLimit >= this.tickCount)
            && key.type.equals(type)
        );
    }
    
    /**
     * 检查指定指令和类型是否在双边界时间范围内出现过。
     */
    public boolean checkInputWithRangedTime(InputCommand targetCommand, InputType type, long startTimeLimit, long endTimeLimit) {
        return checkInputWithPredicate(key -> key.inputCommand.equals(targetCommand)
            && (key.time + startTimeLimit >= this.tickCount)
            && (key.time + endTimeLimit <= this.tickCount)
            && key.type.equals(type)
        );
    }
    
    /**
     * 检查输入流中是否存在指定指令，且满足附加按键状态。
     */
    public boolean checkInputWithCommands(InputCommand targetCommand, InputType type, EnumSet<InputCommand> commands) {
        return checkInputWithPredicate(key -> key.inputCommand.equals(targetCommand)
            && key.commands.containsAll(commands)
            && key.type.equals(type)
        );
    }
    
    /**
     * 检查输入流中是否存在指定指令、时间范围和附加按键状态。
     */
    public boolean checkInputWithCommandsAndTime(InputCommand targetCommand, InputType type, long timeLimit, EnumSet<InputCommand> commands) {
        return checkInputWithPredicate(key -> key.inputCommand.equals(targetCommand)
            && (key.time + timeLimit >= this.tickCount)
            && key.commands.containsAll(commands)
            && key.type.equals(type)
        );
    }
    
    /**
     * 检查输入流中是否在双边界时间范围内存在指定指令与附加按键状态。
     */
    public boolean checkInputWithCommandsAndRangedTime(InputCommand targetCommand, InputType type, long startTimeLimit, long endTimeLimit, EnumSet<InputCommand> commands) {
        return checkInputWithPredicate(key -> key.inputCommand.equals(targetCommand)
            && (key.time + startTimeLimit >= this.tickCount)
            && (key.time + endTimeLimit <= this.tickCount)
            && key.commands.containsAll(commands)
            && key.type.equals(type)
        );
    }
    
    /**
     * 按时间线顺序检查输入流中是否依次匹配给定的按键序列。
     * <p>
     * 用于复杂连段的判定——要求每个时间段的按键按顺序先后出现。
     */
    public boolean checkTimeLineInput(LinkedList<TimeLineKeyInput> inputTimeLine) {
        cleanTimeOutInput();
        
        if (inputTimeLine.isEmpty()) {
            return true;
        }
        
        long currentReferenceTime = this.tickCount;
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
        /**
         * 按下按键
         */
        START,
        /**
         * 弹起按键
         */
        END
    }
}
