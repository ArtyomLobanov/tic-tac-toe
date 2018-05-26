//package ru.spbau.mit.game.client.gui;
//
//import ru.spbau.mit.game.common.api.units.Room;
//
//import javax.swing.*;
//import javax.swing.table.AbstractTableModel;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.NoSuchElementException;
//
//public class RoomsTable extends JTable {
//    private final List<Room> rooms = new ArrayList<>();
//
//    private class RoomsTableModel extends AbstractTableModel {
//
//        @Override
//        public int getRowCount() {
//            return rooms.size();
//        }
//
//        @Override
//        public String getColumnName(int column) {
//            switch (column) {
//                case 0 : return "Name";
//                case 1 : return "Host";
//                case 2 : return "Guest";
//                case 3 : return "Size";
//                case 4 : return "Action";
//                default:
//                    throw new NoSuchElementException();
//            }
//        }
//
//        @Override
//        public Class<?> getColumnClass(int columnIndex) {
//            return columnIndex == 4 ? Room.class : String.class;
//        }
//
//        @Override
//        public boolean isCellEditable(int rowIndex, int columnIndex) {
//            return ;
//        }
//
//        @Override
//        public int getColumnCount() {
//            return 5;
//        }
//
//        @Override
//        public Object getValueAt(int rowIndex, int columnIndex) {
//            final Room room = rooms.get(rowIndex);
//            switch (columnIndex) {
//                case 0:
//                    return room.name;
//                case 1:
//                    return room.host;
//                case 2:
//                    return room.guest;
//                case 3:
//                    return Integer.toString(room.size);
//                case 4:
//                    return room;
//                default:
//                    throw new NoSuchElementException();
//            }
//        }
//    }
//}
