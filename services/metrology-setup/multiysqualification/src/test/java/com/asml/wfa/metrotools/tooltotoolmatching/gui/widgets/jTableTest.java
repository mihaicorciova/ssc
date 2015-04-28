package com.asml.wfa.metrotools.tooltotoolmatching.gui.widgets;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.DefaultListSelectionModel;
import javax.swing.table.DefaultTableModel;

/** @see http://stackoverflow.com/questions/4526779 */
public class jTableTest extends JPanel {

    private static final int CHECK_COL = 1;
    private static final Object[][] DATA = { { "One", Boolean.TRUE }, { "Two", Boolean.FALSE }, { "Three", Boolean.TRUE },
            { "Four", Boolean.FALSE }, { "Five", Boolean.TRUE }, { "Six", Boolean.FALSE }, { "Seven", Boolean.TRUE }, { "Eight", Boolean.FALSE },
            { "Nine", Boolean.TRUE }, { "Ten", Boolean.FALSE } };
    private static final String[] COLUMNS = { "Number", "CheckBox" };
    private final DataModel dataModel = new DataModel(DATA, COLUMNS);
    private final JTable table = new JTable(dataModel);
    private final DefaultListSelectionModel selectionModel;

    public jTableTest() {
        super(new BorderLayout());
        this.add(new JScrollPane(table));
        this.add(new ControlPanel(), BorderLayout.SOUTH);
        table.setPreferredScrollableViewportSize(new Dimension(250, 175));
        selectionModel = (DefaultListSelectionModel) table.getSelectionModel();
    }

    private class DataModel extends DefaultTableModel {

        public DataModel(final Object[][] data, final Object[] columnNames) {
            super(data, columnNames);
        }

        @Override
        public Class<?> getColumnClass(final int columnIndex) {
            if (columnIndex == CHECK_COL) {
                return getValueAt(0, CHECK_COL).getClass();
            }
            return super.getColumnClass(columnIndex);
        }

        @Override
        public boolean isCellEditable(final int row, final int column) {
            return column == CHECK_COL;
        }
    }

    private class ControlPanel extends JPanel {

        public ControlPanel() {
            this.add(new JLabel("Selection:"));
            this.add(new JButton(new SelectionAction("Clear", false)));
            this.add(new JButton(new SelectionAction("Check", true)));
        }
    }

    private class SelectionAction extends AbstractAction {

        boolean value;

        public SelectionAction(final String name, final boolean value) {
            super(name);
            this.value = value;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            for (int i = 0; i < dataModel.getRowCount(); i++) {
                if (selectionModel.isSelectedIndex(i)) {
                    dataModel.setValueAt(value, i, CHECK_COL);
                }
            }
        }
    }

    private static void createAndShowUI() {
        final JFrame frame = new JFrame("CheckABunch");
        frame.add(new jTableTest());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(final String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                createAndShowUI();
            }
        });
    }
}