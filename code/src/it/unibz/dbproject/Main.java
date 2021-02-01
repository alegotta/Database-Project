package it.unibz.dbproject;

import it.unibz.dbproject.exceptions.DbException;
import it.unibz.dbproject.exceptions.NonFatalDbException;

import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try {
            System.out.println("****** Introduction to Databases project ******\n");

            System.out.print("Connecting... ");
            Map<String, String> properties = new PropertiesReader("database.properties").getContent();
            Db.connect(properties.getOrDefault("dbms", "postgresql"),
                    properties.getOrDefault("host", "localhost"),
                    properties.getOrDefault("port", "5432"),
                    properties.get("db"),
                    properties.get("username"),
                    properties.get("password"));
            System.out.println("successfully connected to the database");

            GuiManager menu = new GuiManager();
            Scanner in = new Scanner(System.in);
            int index = -1;

            while(index!=menu.getExitIndex()) {
                System.out.println("\n\n"+menu.getMenu());
                System.out.print("Please choose an item: ");
                index = tryConvert(in.nextLine());

                if (index<0 || index>menu.getLastIndex()) {
                    System.out.println("Please insert a valid value");
                } else {
                    try {
                        menu.chooseOperation(index);
                        while(menu.hasNextSubOperation()) {
                            System.out.println("\n" + menu.getOperationMenu());

                            String input = "";
                            while (!input.equalsIgnoreCase("q") && menu.hasNextInput()) {
                                System.out.print("Enter " + menu.getInputName() + ": ");
                                input = in.nextLine();
                                menu.setOperationInput(input);
                            }
                            if (!input.equalsIgnoreCase("q")) {
                                String result = menu.executeOperation();
                                System.out.println("\n" + result);
                                if (result.startsWith("No result found"))
                                    break;

                                while (menu.hasNextFilter()) {
                                    System.out.print(menu.getFilterName() + ": ");
                                    menu.setOperationFilter(in.nextLine());
                                }
                            }
                        }
                    } catch (NonFatalDbException e) {
                        System.out.print(e.getMessage() + " - ");
                        System.out.println(e.getDetails());
                    }
                }
            }
            Db.disconnect();
        } catch (DbException e) {
            System.err.println("Fatal error (" + e.getSqlState() + "): " + e.getMessage());
            System.err.println(e.getDetails());
            //e.printStackTrace();
            System.out.println("Terminating...");
            System.exit(1);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            //e.printStackTrace();
            System.out.println("Terminating...");
            System.exit(1);
        }
    }

    private static int tryConvert(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
