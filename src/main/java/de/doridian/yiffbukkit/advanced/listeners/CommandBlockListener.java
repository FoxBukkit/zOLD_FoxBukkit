package de.doridian.yiffbukkit.advanced.listeners;

import de.doridian.yiffbukkit.main.listeners.BaseListener;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.CommandBlockRunEvent;

import java.lang.ref.SoftReference;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandBlockListener extends BaseListener {
    private static final Pattern argumentValuePattern = Pattern.compile("\\G(\\w{1,2})=(-?\\w+)(?:$|,)");

    private static final HashMap<String, SoftReference<ParsedCommand>> parsedCommandMap = new HashMap<>();

    private interface ParsedArgument {
        public List<String> getValue(Block block);
    }

    enum ParsedArgumentPlayerType {
        ALL, RANDOM, CLOSEST
    }

    private class ParsedArgumentPlayer implements ParsedArgument {
        private final ParsedArgumentPlayerType type;

        private int x = Integer.MIN_VALUE;
        private int y = Integer.MIN_VALUE;
        private int z = Integer.MIN_VALUE;

        private int minRadius = Integer.MIN_VALUE;
        private int maxRadius = Integer.MIN_VALUE;

        private int maxCount = Integer.MIN_VALUE;

        private int minLevel = Integer.MIN_VALUE;
        private int maxLevel = Integer.MIN_VALUE;

        private GameMode gameMode = null;

        private ParsedArgumentPlayer(ParsedArgumentPlayerType type) {
            this.type = type;
        }

        public List<String> getValue(Block block) {
            final Location useLoc = block.getLocation();
            if(this.x != Integer.MIN_VALUE) {
                useLoc.setX(this.x);
            }
            if(this.y != Integer.MIN_VALUE) {
                useLoc.setY(this.y);
            }
            if(this.z != Integer.MIN_VALUE) {
                useLoc.setZ(this.z);
            }
            int useCount = maxCount;

            List<Player> availablePlayers = Arrays.asList(plugin.getServer().getOnlinePlayers());

            switch(type) {
                case ALL:
                    //Don't do anything, just match
                    break;
                case RANDOM:
                    if(useCount == Integer.MIN_VALUE) {
                        useCount = 1; //By default use a count of only one here!
                    }
                    Collections.shuffle(availablePlayers); //Just randomize the order
                    break;
                case CLOSEST:
                    if(useCount == Integer.MIN_VALUE) {
                        useCount = 1; //By default use a count of only one here!
                    }
                    Collections.sort(availablePlayers, new Comparator<Player>() {
                        @Override
                        public int compare(final Player o1, final Player o2) {
                            final double d1 = o1.getLocation().distanceSquared(useLoc);
                            final double d2 = o2.getLocation().distanceSquared(useLoc);
                            if(d1 < d2) {
                                return -1;
                            } else if(d1 > d2) {
                                return 1;
                            } else {
                                return 0;
                            }
                        }
                    });
                    break;
            }

            if(useCount < 0 && useCount != Integer.MIN_VALUE) {
                useCount = -useCount;
                Collections.reverse(availablePlayers);
            }

            ArrayList<String> ret = new ArrayList<>();
            int count = 0;

            for(Player ply : availablePlayers) {
                double dist = ply.getLocation().distanceSquared(useLoc);
                if(minRadius != Integer.MIN_VALUE && dist < minRadius)
                    continue;
                if(maxRadius != Integer.MIN_VALUE && dist > maxRadius)
                    continue;
                if(minLevel != Integer.MIN_VALUE && ply.getLevel() < minLevel)
                    continue;
                if(maxLevel != Integer.MIN_VALUE && ply.getLevel() > maxLevel)
                    continue;
                if(gameMode != null && !ply.getGameMode().equals(gameMode))
                    continue;

                ret.add(ply.getName());

                count++;
                if(useCount != Integer.MIN_VALUE && count >= useCount)
                    break;
            }

            return ret;
        }
    }

    private class ParsedArgumentString implements ParsedArgument {
        private final List<String> value;

        private ParsedArgumentString(String value) {
            this.value = new ArrayList<>();
            this.value.add(value);
        }

        private ParsedArgumentString(List<String> value) {
            this.value = value;
        }

        @Override
        public List<String> getValue(Block block) {
            return value;
        }
    }

    private class ParsedCommand {
        private final ParsedArgument[] parsedArguments;

        protected ParsedCommand(String originalCommand) {
            String[] args = originalCommand.split(" ");
            parsedArguments = new ParsedArgument[args.length];

            for(int i = 0; i < args.length; i++) {
                final String arg = args[i];
                if(arg.charAt(0) == '@') {
                    ParsedArgumentPlayer parsedArgumentPlayer = null;
                    switch(arg.charAt(1)) {
                        case 'a':
                            parsedArgumentPlayer = new ParsedArgumentPlayer(ParsedArgumentPlayerType.ALL);
                            break;
                        case 'p':
                            parsedArgumentPlayer = new ParsedArgumentPlayer(ParsedArgumentPlayerType.CLOSEST);
                            break;
                        case 'r':
                            parsedArgumentPlayer = new ParsedArgumentPlayer(ParsedArgumentPlayerType.RANDOM);
                            break;
                        default:
                            parsedArguments[i] = new ParsedArgumentString(arg);
                            break;
                    }

                    if(parsedArgumentPlayer != null) {
                        final int argLen = arg.length();
                        if(argLen >= 3) {
                            if(arg.charAt(2) == '[' && arg.charAt(argLen - 1) == ']') {
                                Matcher matcher = argumentValuePattern.matcher(arg.substring(3, argLen - 2));
                                while (matcher.find()) {
                                    final String key = matcher.group(1);
                                    final String value = matcher.group(2);
                                    if(key.equals("x")) {
                                        parsedArgumentPlayer.x = Integer.parseInt(value);
                                    } else if(key.equals("y")) {
                                        parsedArgumentPlayer.y = Integer.parseInt(value);
                                    } else if(key.equals("z")) {
                                        parsedArgumentPlayer.z = Integer.parseInt(value);
                                    } else if(key.equals("r")) {
                                        parsedArgumentPlayer.maxRadius = Integer.parseInt(value);
                                    } else if(key.equals("rm")) {
                                        parsedArgumentPlayer.minRadius = Integer.parseInt(value);
                                    } else if(key.equals("l")) {
                                        parsedArgumentPlayer.maxLevel = Integer.parseInt(value);
                                    } else if(key.equals("lm")) {
                                        parsedArgumentPlayer.minLevel = Integer.parseInt(value);
                                    } else if(key.equals("m")) {
                                        parsedArgumentPlayer.gameMode = GameMode.getByValue(Integer.valueOf(value));
                                    } else if(key.equals("c")) {
                                        parsedArgumentPlayer.maxCount = Integer.valueOf(value);
                                    }
                                }
                            } else {
                                parsedArguments[i] = new ParsedArgumentString(arg);
                                continue;
                            }
                        }

                        parsedArguments[i] = parsedArgumentPlayer;
                    }
                } else {
                    parsedArguments[i] = new ParsedArgumentString(arg);
                }
            }
        }

        public List<StringBuilder> getCommandsRunBy(Block block) {
            ArrayList<StringBuilder> currentCommands = new ArrayList<>();
            for(int i = 0; i < parsedArguments.length; i++) {
                List<String> argVals = parsedArguments[i].getValue(block);
                int argValSize = argVals.size();
                if(argValSize < 1) {
                    return new ArrayList<>(); // x * 0 = 0!
                } else if(argValSize == 1) {
                    String argVal = argVals.get(0);
                    for(StringBuilder stringBuilder : currentCommands) {
                        stringBuilder.append(argVal);
                        stringBuilder.append(' ');
                    }
                } else {
                    String argVal = argVals.get(0);
                    int kmax = currentCommands.size();
                    for(int k = 0; k < kmax; k++) {
                        StringBuilder stringBuilderMain = currentCommands.get(k);
                        for(int j = 1; j < argValSize; j++) {
                            StringBuilder newStringBuilder = new StringBuilder(stringBuilderMain);
                            newStringBuilder.append(argVals.get(j));
                            newStringBuilder.append(' ');
                            currentCommands.add(newStringBuilder);
                        }
                        for(StringBuilder stringBuilder : currentCommands) {
                            stringBuilder.append(argVal);
                            stringBuilder.append(' ');
                        }
                    }
                }
            }

            return currentCommands;
        }

        public void runBy(Block block) {
            for(StringBuilder currentCommand : getCommandsRunBy(block)) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), currentCommand.toString());
            }
        }
    }

    public CommandBlockListener() {
        /*try {
            String cmd = "";
            Block block = plugin.getServer().getWorlds().get(0).getHighestBlockAt(0, 0);
            ParsedCommand parsedCommand = new ParsedCommand(cmd);
            for(StringBuilder stringBuilder : parsedCommand.getCommandsRunBy(block)) {
                System.out.println(stringBuilder.toString());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }*/
    }

    //@EventHandler
    public void onCommandBlockRan(CommandBlockRunEvent event) {
        final String command = event.getCommand();
        final Block block = event.getBlock();
        event.setCancelled(true);

        ParsedCommand parsedCommand = null;
        if(parsedCommandMap.containsKey(command)) {
            SoftReference<ParsedCommand> parsedCommandSoftReference = parsedCommandMap.get(command);
            parsedCommand = parsedCommandSoftReference.get();
        }

        if(parsedCommand == null) {
            parsedCommand = new ParsedCommand(command);
            parsedCommandMap.put(command, new SoftReference<>(parsedCommand));
        }

        parsedCommand.runBy(block);
    }
}
