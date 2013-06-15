package org.firepick.firebom;
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

import org.firepick.relation.IRelation;
import org.firepick.relation.IRow;
import org.firepick.relation.RelationPrinter;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class BOMMarkdownPrinter extends RelationPrinter {

    public BOMMarkdownPrinter() {
        super.setPrintTitleRow(false);
        super.setPrintTotalRow(false);
    }

    @Override
    public RelationPrinter print(IRelation relation, PrintStream printStream) {
        BOM bom = (BOM)  relation;
        printStream.print("#### Bill Of Materials (");
        DecimalFormat currencyFormat = (DecimalFormat) NumberFormat.getCurrencyInstance();
        printStream.print(currencyFormat.format(bom.totalCost()));
        printStream.print("; ");
        printStream.print(bom.partCount());
        printStream.println(" parts)");

        return super.print(relation, printStream);
    }

    @Override
    protected void printRow(PrintStream printStream, IRow row, int iRow) {
        BOMRow bomRow = (BOMRow) row;
        BOM bom = (BOM) bomRow.getRelation();
        printStream.print(iRow);
        printStream.print(". ");
        printColumnValue(printStream, bom.getColumn(BOMColumn.COST), row);
        printStream.print(" [");
        printColumnValue(printStream, bom.getColumn(BOMColumn.ID), row);
        printStream.print("](");
        printColumnValue(printStream, bom.getColumn(BOMColumn.URL), row);
        printStream.print(") ");
        printColumnValue(printStream, bom.getColumn(BOMColumn.QUANTITY), row);
        printStream.print(" [");
        printColumnValue(printStream, bom.getColumn(BOMColumn.VENDOR), row);
        printStream.print("](");
        printColumnValue(printStream, bom.getColumn(BOMColumn.SOURCE), row);
        printStream.print(") ");
        printColumnValue(printStream, bom.getColumn(BOMColumn.TITLE), row);
        printStream.println();
    }

    @Override
    protected void printTotalRow(PrintStream printStream, IRelation relation) {
        BOM bom = (BOM)  relation;
        printStream.print("1. Total cost:");
        DecimalFormat currencyFormat = (DecimalFormat) NumberFormat.getCurrencyInstance();
        printStream.print(currencyFormat.format(bom.totalCost()));
        printStream.print(" parts:");
        printStream.print(bom.partCount());
        printStream.println();
    }
}
