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

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class MainTest {
    @Test
    public void testHelp() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        PrintStream printWriter = new PrintStream(bos);
        Main.mainStream(new String[0], printWriter);
        printWriter.flush();
        String help = baos.toString();
        System.out.println(help);
        assert(help.contains("USAGE"));
        assert(help.contains("OPTIONS"));
        assert(help.contains("EXAMPLES"));
    }

    @Test
    public void testBOMFactory() {
        BOMFactory bomFactory = new BOMFactory();
        assertEquals(BOMFactory.OutputType.DEFAULT, bomFactory.getOutputType());
        bomFactory.setOutputType(BOMFactory.OutputType.MARKDOWN);
        assertEquals(BOMFactory.OutputType.MARKDOWN, bomFactory.getOutputType());
    }
}
