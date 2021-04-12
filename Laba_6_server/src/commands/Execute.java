package commands;

import collectionofflats.MyTreeMap;
import commands.exceptions.ExitException;
import commands.exceptions.IllegalCommandException;
import commands.exceptions.InvalidArgExcaption;
import commands.exceptions.RecursionException;

import java.util.Scanner;

/**
 * Class with execute all program and all command from console.
 */
public class Execute {
    public Execute(boolean isFromFile, MyTreeMap map, Scanner scanner) {
        execute(isFromFile, map, scanner);
    }

    /**
     * function for defining a command and its execution
     *
     * @param isFromFile - var to check about execute_script command
     * @param map        - MAP with objects
     * @param SCANNER    - mod of program
     * @throws ExitException
     */
    public static void execute(boolean isFromFile, MyTreeMap map, Scanner SCANNER)
            throws ExitException {
        String command, execCom;
        String[] commands;

        do {
            System.out.print("Enter the command: ");

            if (!SCANNER.hasNextLine()) {
                System.out.println("ending");
                return;
            }
            command = SCANNER.nextLine(); // ожидание новой команды
            if (isFromFile)
                System.out.println(command);

            commands = command.toLowerCase().split(" ");
            execCom = commands[0];

            switch (execCom) {

                case "help":
                    if (commands.length == 1) {
                        CommandHelp.helpCommand();
                    } else {
                        throw new IllegalCommandException("Unknown help_<...> command");
                    }
                    break;

                case "info":
                    if (commands.length == 1) {
                        CommandInfo.infoCommand(map);
                    } else {
                        throw new IllegalCommandException("Unknown info_<...> command");
                    }
                    break;

                case "show":
                    if (commands.length == 1) {
                        CommandShow.showCommand(map);
                    } else {
                        throw new IllegalCommandException("Unknown show_<...> command");
                    }
                    break;

                case "insert":
                    if (commands.length == 1) {
                        System.out.println("wait KEY");
                    } else if (commands.length >= 3) {
                        throw new IllegalCommandException("Wrong format. Need -> 'insert <key>'");
                    } else {
                        try {
                            Integer newKey = Integer.parseInt(commands[1]);
                            new CommandInsert(newKey, map, false, SCANNER);
                        } catch (NumberFormatException e) {
                            throw new InvalidArgExcaption("type key - Integer");
                        }
                    }
                    break;

                case "update":
                    if (commands.length == 1) {
                        System.out.println("wait id");
                    } else if (commands.length >= 3) {
                        throw new IllegalCommandException("Wrong format. Need -> 'update <id>'");
                    } else {
                        try {
                            int idUpd = Integer.parseInt(commands[1]);
                            new CommandUpdate(idUpd, map, SCANNER);
                        } catch (NumberFormatException e) {
                            throw new InvalidArgExcaption("type of id - int");
                        }
                    }
                    break;

                case "remove_key":
                    if (commands.length != 2) {
                        throw new IllegalCommandException("Wrong format. Need -> 'remove_key <KEY>'");
                    } else {
                        try {
                            Integer newKey = Integer.parseInt(commands[1]);
                            new RemoveByKey(map.getMyMap(), newKey);
                        } catch (NumberFormatException e) {
                            throw new InvalidArgExcaption("key of object - Integer");
                        }
                    }
                    break;
                case "remove_lower":
                    if (commands.length != 2) {
                        throw new IllegalCommandException("Wrong format 'remove lower <numeric>'");
                    } else {
                        try {
                            long comp = Long.parseLong(commands[1]);
                            new RemoveByLower(map.getMyMap(), comp);
                        } catch (NumberFormatException e) {
                            throw new InvalidArgExcaption("min square, type - long");
                        }
                    }
                    break;

                case "clear":
                    if (commands.length == 1) {
                        new ClearCommand(map.getMyMap());
                    } else {
                        throw new IllegalCommandException("Unknown clear_<...> command");
                    }
                    break;

                case "save":
                    if (commands.length == 1) {
                        SaveCommand.startSaveFile(map.getMyMap(), SCANNER);
                    } else {
                        throw new IllegalCommandException("Unknown save_<...> command");
                    }
                    break;

                case "execute_script":
                    if (commands.length == 2) {
                        try {
                            new ExecuteScript(map, commands[1]);
                        } catch (RecursionException e) {
                            System.out.println(e.getMessage());
                        }
                    } else {
                        throw new IllegalCommandException("Wrong format");
                    }
                    break;

                case "history":
                    if (commands.length == 1) {
                        HistoryCommand.printHistory();
                    } else {
                        throw new IllegalCommandException("Unknown history_<...> command");
                    }
                    break;

                case "replace_if_lowe":
                    if (commands.length == 3) {
                        try {
                            Integer newKey = Integer.parseInt(commands[1]);
                            long newArea = Long.parseLong(commands[2]);
                            new ReplaceByKeyLowe(map.getMyMap(), newKey, newArea);
                        } catch (NumberFormatException e) {
                            throw new InvalidArgExcaption("\tkey type - Integer,\n" + "\tarea type - long");
                        }
                    } else {
                        throw new IllegalCommandException("Wrong format. Need -> 'replace_if_lowe <key> <long>'");
                    }
                    break;

                case "min_by_id":
                    new MinById(map.getMyMap());
                    break;

                case "count_less_than_number_of_bathrooms":
                    if (commands.length == 2) {
                        try {
                            int numBath = Integer.parseInt(commands[1]);
                            new CountLess(map, numBath);
                        } catch (NumberFormatException e) {
                            throw new InvalidArgExcaption("number of bathrooms -> int");
                        }
                    } else {
                        throw new IllegalCommandException("Wrong format. Need -> 'count_less_than_number_of_bathrooms <NUMBER>'");
                    }
                    break;
                case "load":
                    if (commands.length == 2) {
                        String path = commands[1];
                        map.addToTree(ReadCommand.readTheCollection(path));
                    } else {
                        throw new IllegalCommandException("Wrong format. Need -> 'count_less_than_number_of_bathrooms <NUMBER>'");
                    }
                    break;
                case "filter_starts_by_name":
                    new FilterStartsByName(map, commands);
                    break;

                case "exit":
                    throw new ExitException();

                default:
                    throw new IllegalCommandException("Command not found");
            }
        }
        while (true);
    }
}
