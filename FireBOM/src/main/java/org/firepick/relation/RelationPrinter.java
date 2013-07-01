package org.firepick.relation;
/*
    Copyright (C) 2013 Karl Lew <karl@firepick.org>. All rights reserved.
    DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
    
    This file is part of FirePick Software.
    
    FirePick Software is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    FirePick Software is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with FirePick Software.  If not, see <http://www.gnu.org/licenses/>.
    
    For more information about FirePick Software visit http://firepick.org
 */

import java.io.PrintStream;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;

public class RelationPrinter {
    private List<IColumnDescription> columnDescriptionList = new ArrayList<IColumnDescription>();
    private String columnSeparator = ", ";
    private boolean printTotalRow = true;
    private boolean printTitleRow = true;

    public RelationPrinter print(IRelation relation, PrintStream printStream, IRowVisitor rowVisitor) {
        if (columnDescriptionList.size() == 0) {
            columnDescriptionList = new ArrayList<IColumnDescription>(relation.describeColumns());
        }

        if (printTitleRow) {
            printColumnTitles(printStream, relation);
        }
        synchronized (columnDescriptionList) {
            printRows(relation, printStream, rowVisitor);
        }
        return this;
    }

    private void printRows(IRelation relation, PrintStream printStream, IRowVisitor rowVisitor) {
        for (IColumnDescription columnDescription : columnDescriptionList) {
            columnDescription.getAggregator().clear();
        }

        int iRow = 1;
        for (IRow row : relation) {
            printRow(printStream, row, iRow++, rowVisitor);
        }

        if (printTotalRow) {
            printTotalRow(printStream, relation);
        }
    }

    protected void printTotalRow(PrintStream printStream, IRelation relation) {
        int columns = 0;
        for (IColumnDescription columnDescription : columnDescriptionList) {
            if (columns++ > 0) {
                printStream.print(columnSeparator);
            }
            Object aggregate = columnDescription.getAggregator().getAggregate();
            printValue(printStream, columnDescription, aggregate);
        }
        printStream.println();
    }

    protected void printRow(PrintStream printStream, IRow row, int iRow, IRowVisitor rowVisitor) {
        if (rowVisitor != null) {
            rowVisitor.visit(row);
        }

        int columns = 0;
        for (IColumnDescription columnDescription : columnDescriptionList) {
            if (columns++ > 0) {
                printStream.print(columnSeparator);
            }
            Object value = printColumnValue(printStream, columnDescription, row);
            if (printTotalRow) {
                columnDescription.getAggregator().aggregate(value);
            }
        }
        printStream.println();
    }

    protected Object printColumnValue(PrintStream printStream, IColumnDescription columnDescription, IRow row) {
        Object value = row.item(columnDescription.getItemIndex());
        printValue(printStream, columnDescription, value);
        return value;
    }

    protected void printValue(PrintStream printStream, IColumnDescription columnDescription, Object value) {
        Format format = columnDescription.getFormat();
        if (format == null) {
            printStream.print(value);
        } else {
            printStream.print(format.format(value));
        }
    }

    private void printColumnTitles(PrintStream printStream, IRelation relation) {
        int columns = 0;
        for (IColumnDescription columnDescription : columnDescriptionList) {
            if (columns++ > 0) {
                printStream.print(columnSeparator);
            }
            String title = columnDescription.getTitle();
            printValue(printStream, columnDescription, title);
        }
        printStream.println();
    }

    public List<IColumnDescription> getColumnDescriptionList() {
        return columnDescriptionList;
    }

    public RelationPrinter setColumnDescriptionList(List<IColumnDescription> columnDescriptionList) {
        this.columnDescriptionList = columnDescriptionList;
        return this;
    }

    public String getColumnSeparator() {
        return columnSeparator;
    }

    public RelationPrinter setColumnSeparator(String columnSeparator) {
        this.columnSeparator = columnSeparator;
        return this;
    }

    public boolean isPrintTotalRow() {
        return printTotalRow;
    }

    public RelationPrinter setPrintTotalRow(boolean printTotalRow) {
        this.printTotalRow = printTotalRow;
        return this;
    }

    public boolean isPrintTitleRow() {
        return printTitleRow;
    }

    public RelationPrinter setPrintTitleRow(boolean printTitleRow) {
        this.printTitleRow = printTitleRow;
        return this;
    }
}
