//Brandon Mazur - CSCI230 Final Project

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.io.PrintWriter;

class PlannerDisplay extends JPanel {

    public CalendarDisplay calendarDisplay;
    public ModificationPanel modificationPanel;
    public TodoPanel todoPanel;
    public ArrayList<CalendarEvent> data;
    public boolean backToCalendar = true;

    private final String calendarCard = "calcard";
    private final String modificationCard = "modcard";
    private final String todoCard = "todocard";
    private final String manualCard = "mancard";
    public String currentPanel;

    public PlannerDisplay() {

        //gets current date in day/weekday/month/year format
        String current_date = (new SimpleDateFormat("dd/uu/MM/yyyy")).format(new Date());
        this.setBackground(Consts.bg);

        //loadData() requires a valid reference to the modificationPanel, instantiating that must come first
        modificationPanel = new ModificationPanel(current_date, this);
        this.loadData();
        calendarDisplay = new CalendarDisplay(current_date, this);
        todoPanel = new TodoPanel(this, current_date);
        UserManual manual = new UserManual(this);

        this.setLayout(new CardLayout());
        this.add(calendarDisplay, calendarCard);
        this.add(modificationPanel, modificationCard);
        this.add(todoPanel, todoCard);
        this.add(manual, manualCard);
        currentPanel = calendarCard;
        ((CardLayout)this.getLayout()).show(this, calendarCard);
    }

    public void loadData() {

        data = new ArrayList<>();
        Scanner fileScan = null;

        //if there are any errors that come about loading the data, the file is incorrectly formatted
        try {

            fileScan = new Scanner(new File("data.cld"));
            StringTokenizer st;
            int type;

            //load file into program
            while (fileScan.hasNextLine()) {

                st = new StringTokenizer(fileScan.nextLine(),"@");

                if (st.countTokens() != 5 && st.countTokens() != 6)
                    throw new Exception();

                type = Integer.parseInt(st.nextToken());

                if (type == Consts.UNCOMPLETED_TASK || type == Consts.COMPLETED_TASK)
                    data.add(new CalendarTask(modificationPanel, st, type));

                else data.add(new CalendarDate(modificationPanel, st, type));
            }

            if (data.size() == 0)
                throw new Exception();

            fileScan.close();

        } catch (Exception e) {
            //if file doesn't exist or is malformatted, offer to create a new default file
            if (fileScan != null)
                fileScan.close();

            data.clear();
            int selectedOption = JOptionPane.showConfirmDialog(null,
                    "Generate new default data.cld?",
                    "Error loading \'data.cld\'",
                    JOptionPane.YES_NO_OPTION);
            if (selectedOption == JOptionPane.YES_OPTION) {
                this.generateDefaultFile();
                data.add(new CalendarDate(modificationPanel, Consts.YEARLESS, 1, 1, "New Years Day", "", true));
                data.add(new CalendarDate(modificationPanel, Consts.YEARLESS, 2, 14, "Valentine's Day", "", true));
                data.add(new CalendarDate(modificationPanel, Consts.YEARLESS, 4, 1, "April Fools' Day", "", true));
                data.add(new CalendarDate(modificationPanel, Consts.YEARLESS, 7, 4, "Independence Day", "", true));
                data.add(new CalendarDate(modificationPanel, Consts.YEARLESS, 10, 31, "Halloween", "", true));
                data.add(new CalendarDate(modificationPanel, Consts.YEARLESS, 12, 25, "Christmas Day", "", true));
                data.add(new CalendarDate(modificationPanel, Consts.YEARLESS, 12, 31, "New Years Eve", "", true));
            }
            else {
            	System.exit(1);
            }
        }
    }

    public JMenuBar getMenuBar() {

        JMenuBar menuBar;
        JMenu file, help;
        JMenuItem exit, nosave, manual;

        exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        nosave = new JMenuItem("Exit Without Saving");
        nosave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Event_Calendar.noSave();
                System.exit(0);
            }
        });

        file = new JMenu("File");
        file.add(exit);
        file.add(nosave);

        manual = new JMenuItem("User Manual");
        manual.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showManual();
            }
        });

        help = new JMenu("Help");
        help.add(manual);

        menuBar = new JMenuBar();
        menuBar.add(file);
        menuBar.add(help);

        return menuBar;
    }

    private void showManual() {
        ((CardLayout)this.getLayout()).show(this, manualCard);
    }

    public void toModCard(int[] spec) {

        //clears and switches to modification card
        modificationPanel.clear();
        modificationPanel.setFields(spec);
        ((CardLayout)this.getLayout()).show(this, modificationCard);
    }

    public String getCalendarCard() {
        return calendarCard;
    }

    public String getModificationCard() {
        return modificationCard;
    }

    public String getTodoCard() {
        return todoCard;
    }

    private void generateDefaultFile() {

        PrintWriter out = null; //to satisfy compiler

        try {
            out = new PrintWriter("data.cld");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error generating file", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        out.println("@3@-1@1@1@New Years Day@");
        out.println("@3@-1@2@14@Valentine's Day@");
        out.println("@3@-1@4@1@April Fools' Day@");
        out.println("@3@-1@7@4@Independence Day@");
        out.println("@3@-1@10@31@Halloween@");
        out.println("@3@-1@12@25@Christmas Day@");
        out.println("@3@-1@12@31@New Years Eve@");
        out.close();
    }
}

public class Event_Calendar {

    private static ArrayList<CalendarEvent> data;
    public static boolean save = true;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
            	if (data != null && save)
                	saveData();
            }
        }));
    }

    private static void createAndShowGUI() {

        JFrame frame = new JFrame("Virtual Planner");
        frame.setSize(1100, 650);

        PlannerDisplay display = new PlannerDisplay();
        data = display.data;

        frame.setJMenuBar(display.getMenuBar());
        frame.add(display);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private static void saveData() {

        PrintWriter out = null; //to satisfy compiler

        try {
            out = new PrintWriter("data.cld");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error saving file", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        Iterator<CalendarEvent> itr = data.iterator();
        CalendarEvent event;
        StringBuilder outStr;

        //save data in the proper format:
        //@ type @ year @ month @ day @ description @
        while (itr.hasNext()) {

            event = itr.next();
            outStr = new StringBuilder();

            outStr.append('@');
            outStr.append(event.getType());
            outStr.append('@');
            outStr.append(event.getYear());
            outStr.append('@');
            outStr.append(event.getMonth());
            outStr.append('@');
            outStr.append(event.getDay());
            outStr.append('@');
            outStr.append(event.getTitle());
            outStr.append('@');
            if (event.getDescription().length() != 0) {
                outStr.append(event.getDescription());
                outStr.append('@');
            }

            out.println(outStr.toString());
        }

        out.close();
    }

    public static void noSave() {
        save = false;
    }
}
