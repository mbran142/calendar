//Brandon Mazur - CSCI230 Final Project

import javax.swing.*;
import java.awt.*;
import java.util.StringTokenizer;

public class CalendarDate extends CalendarEvent {

    private boolean yearly;

    public CalendarDate(ModificationPanel inref, StringTokenizer st, int type) {
        super(inref, st);
        yearly = type == Consts.YEARLY_DATE;
    }

    public CalendarDate(ModificationPanel inref, int year, int month, int day, String title, String description, boolean yearly) {
        super(inref, year, month, day, title, description);
        this.yearly = yearly;
    }

    public String getCalendarText() {
        if (yearly)
            return "<font color='#008000'><i>" + title + "</i></font>";
        else
            return "<font color='#008000'>" + title + "</font>";
    }

    public int createCalendarPanel(JPanel contentPane) {

        //prepare a jpanel for the weekly calendar display
        JPanel output = new JPanel();
        output.setLayout(new BoxLayout(output, BoxLayout.Y_AXIS));
        output.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        output.setOpaque(false);

        JLabel label;
        //italics for yearly events
        if (yearly)
            label = new JLabel("<html><p><i><u>" + title + "</u>" + (description.length() == 0 ? "" : " - " + description) + "</i></p></html>");
        else
            label = new JLabel("<html><p><u>" + title + "</u>" + (description.length() == 0 ? "" : " - " + description) + "</p></html>");
        label.setForeground(new Color(0x008000));

        //calculate the necessary size dimension of the label
        Dimension d = label.getPreferredSize();
        int paneWidth = contentPane.getPreferredSize().width;
        int labelWidth = d.width;
        int labelHeight = (d.height + 3) * ((labelWidth / paneWidth) + 3);
        label.setPreferredSize(new Dimension(paneWidth, labelHeight));
        output.add(label);

        output.add(Box.createRigidArea(new Dimension(0,10)));
        output.addMouseListener(this);
        contentPane.add(output);

        //output height
        return labelHeight + 15;
    }

    public JPanel getTodoPanel(JPanel contentPane) {

        //prepare panel for the to-do list
        JPanel output = new JPanel();
        output.setLayout(new BoxLayout(output, BoxLayout.X_AXIS));
        output.setOpaque(false);
        output.setAlignmentX(JComponent.LEFT_ALIGNMENT);

        JLabel titleLabel;
        //italics for yearly events
        if (yearly)
            titleLabel = new JLabel("<html><p><font size='4'>" + month + '/' + day + ": "
                    + "<font color='#008000'><u>" + title + "</u>"
                    + (description.length() == 0 ? "" : " - " + description) + "</font></p></html>");
        else
            titleLabel = new JLabel("<html><p><font size='4'>" + month + '/' + day + ": "
                    + "<font color='#008000'><u>" + title + "</u>"
                    + (description.length() == 0 ? "" : " - " + description) + "</font></p></html>");
        titleLabel.setHorizontalAlignment(JLabel.LEFT);
        titleLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        titleLabel.addMouseListener(this);

        output.add(titleLabel);

        //keep track of its height
        int width = contentPane.getPreferredSize().width;
        int height = titleLabel.getPreferredSize().height;
        titleLabel.setPreferredSize(new Dimension(width, height * (titleLabel.getPreferredSize().width / width + 1)));
        output.setPreferredSize(new Dimension(width, height * (titleLabel.getPreferredSize().width / width + 1)));

        return output;
    }

    public void prepareModPanel() {
        //prepares modification panel for a modification
        int[] spec = new int[] { year, month, day, yearly ? Consts.YEARLY_DATE : Consts.ONETIME_DATE };
        modref.clear();
        modref.setFields(spec);
        modref.setEventToModify(this);
        modref.setTitleDesc(title, description);
    }

    public int getType() {
        return yearly ? Consts.YEARLY_DATE : Consts.ONETIME_DATE;
    }
}
