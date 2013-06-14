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

import java.text.*;

public class FixedWidthFormat extends Format {
    private Format format;
    private int width;

    public FixedWidthFormat(int width, Format format) {
        this.format = format;
        this.setWidth(width);
    }

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        int length = toAppendTo.length();
        if (obj instanceof String) {
            toAppendTo.append(obj);
        } else if (obj == null) {
            toAppendTo.append("null");
        } else if (format != null) {
                format.format(obj, toAppendTo, pos);
        } else {
            toAppendTo.append(obj.toString());
        }
        int padding = getWidth() - (toAppendTo.length() - length);
        for (int iPad = 0; iPad < padding; iPad++) {
//            if (format instanceof NumberFormat) {
//                toAppendTo.insert(0, "\u2007");
//            } else {
                toAppendTo.insert(0, " ");
//            }
        }
        return toAppendTo;
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return format.parseObject(source, pos);
    }

    public Format getFormat() {
        return format;
    }

    public int getWidth() {
        return width;
    }

    public FixedWidthFormat setWidth(int width) {
        this.width = width;
        return this;
    }
}
