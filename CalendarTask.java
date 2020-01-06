//Brandon Mazur - CSCI230 Final Project

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

public class CalendarTask extends CalendarEvent implements ActionListener {

    private boolean complete;

    public CalendarTask(ModificationPanel inref, StringTokenizer st, int type) {
        super(inref, st);
        complete = type == Consts.COMPLETED_TASK;
    }

    public CalendarTask(ModificationPanel inref, int year, int month, int day, String title, String description, boolean complete) {
        super(inref, year, month, day, title, description);
        this.complete = complete;
    }

    public String getCalendarText() {
        return "<font color='#800000'>" + title + "</font>";
    }

    public int createCalendarPanel(JPanel contentPane) {

        //prepare a jpanel for the weekly calendar display
        JPanel output = new JPanel();
        output.setLayout(new BoxLayout(output, BoxLayout.Y_AXIS));
        output.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        output.setOpaque(false);

        JLabel label = new JLabel("<html><p><u>" + title + "</u>" + (description.length() == 0 ? "" : " - " + description) + "</p></html>");
        label.setForeground(new Color(0x800000));

        //calculate the necessary size dimension of the label
        Dimension d = label.getPreferredSize();
        int paneWidth = contentPane.getPreferredSize().width;
        int labelWidth = d.width;
        int labelHeight = (d.height + 3) * ((labelWidth / paneWidth) + 3);
        label.setPreferredSize(new Dimension(paneWidth, labelHeight));
        output.add(label);

        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(complete);
        checkBox.setPreferredSize(new Dimension(12,12));
        checkBox.setOpaque(false);
        checkBox.addActionListener(this);
        output.add(checkBox);

        output.add(Box.createRigidArea(new Dimension(0,10)));
        output.addMouseListener(this);
        contentPane.add(output);

        //output height
        return labelHeight + 30;
    }

    public JPanel getTodoPanel(JPanel contentPane) {

        //prepare panel for the to-do list
        JPanel output = new JPanel();
        output.setLayout(new BoxLayout(output, BoxLayout.X_AXIS));
        output.setOpaque(false);
        output.setAlignmentX(JComponent.LEFT_ALIGNMENT);

        JCheckBox checkBox = new JCheckBox();
        checkBox.setOpaque(false);
        checkBox.addActionListener(this);
        checkBox.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        output.add(checkBox);
        output.add(Box.createRigidArea(new Dimension(10,0)));

        JLabel titleLabel = new JLabel("<html><p><font size='4'>" + month + '/' + day + ": "
                + "<font color='#800000'><u>" + title + "</u>"
                + (description.length() == 0 ? "" : " - " + description) + "</font></p></html>");
        titleLabel.setHorizontalAlignment(JLabel.LEFT);
        titleLabel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        titleLabel.addMouseListener(this);

        //keep track of its height
        int width = contentPane.getPreferredSize().width;
        int height = titleLabel.getPreferredSize().height;
        titleLabel.setPreferredSize(new Dimension(width, height * (titleLabel.getPreferredSize().width / width + 1)));
        output.add(titleLabel);

        output.setPreferredSize(new Dimension(width + 26,
                height * (titleLabel.getPreferredSize().width / width + 1) + 15));

        return output;
    }

    public void prepareModPanel() {
        //prepares modification panel for a modification
        int[] spec = new int[] { year, month, day, complete ? Consts.COMPLETED_TASK : Consts.UNCOMPLETED_TASK };
        modref.clear();
        modref.setFields(spec);
        modref.setEventToModify(this);
        modref.setTitleDesc(title, description);
    }

    public int getType() {
        return complete ? Consts.COMPLETED_TASK : Consts.UNCOMPLETED_TASK;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        complete = ((JCheckBox)e.getSource()).isSelected();
    }
}
