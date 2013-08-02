package org.firepick.firebom.rest;

import org.firepick.firebom.Main;
import org.firepick.firebom.part.Part;
import org.firepick.firebom.part.PartFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.io.*;
import java.util.TreeSet;

@Path("/part")
public class PartFactoryResource {

    @GET
    @Path("/find")
    @Produces("text/html; charset=UTF-8")
    public String findPart(@QueryParam("query") String query) throws IOException, InterruptedException {
        PartFactory partFactory = PartFactory.getInstance();

        ByteArrayOutputStream bosHtml = new ByteArrayOutputStream();
        PrintStream psHtml = new PrintStream(bosHtml);
        InputStream is = Main.class.getResourceAsStream("/part.html");
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        int maxRows = 100;
        while (br.ready()) {
            String line = br.readLine();
            if (line.contains("<!--PART-->")) {
                printParts(query, partFactory, psHtml, maxRows);
            } else {
                psHtml.println(line);
            }
        }

        return bosHtml.toString();
    }

    private void printParts(String query, PartFactory partFactory, PrintStream psHtml, int maxRows) {
        TreeSet<Part> parts = new TreeSet<Part>();
        for (Part part : partFactory) {
            if (partMatchesQuery(part, query)) {
                parts.add(part);
                if (parts.size() >= maxRows) {
                    break;
                }
            }
        }

        psHtml.println("<table cellpadding=0 cellspacing=0 class='firebom_table'>");
        psHtml.println("<tr>");
        psHtml.println("<th class='firebom_th'>#</th>");
        psHtml.println("<th class='firebom_th'>OBJECT</th>");
        psHtml.println("<th class='firebom_th'>ID</th>");
        psHtml.println("<th class='firebom_th firebom_longtext'>TITLE</th>");
        psHtml.println("<th class='firebom_th'>AGE@REFRESH</th>");
        psHtml.println("</tr>");

        int row = 1;
        for (Part part : parts) {
            psHtml.print("<tr>");
            psHtml.print("<td class='firebom_td'>");
            psHtml.print(row++);
            psHtml.print("</td>");
            psHtml.print("<td class='firebom_td firebom_longtext'>");
            psHtml.print(part.hashCode());
            psHtml.print("</td>");
            psHtml.print("<td class='firebom_td' title='");
            psHtml.print(part.getUrl());
            psHtml.print("'>");
            psHtml.print(part.getId());
            psHtml.print("</td>");
            psHtml.print("<td class='firebom_td firebom_longtext' title='");
            psHtml.print(part.getSourceUrl());
            psHtml.print("'>");
            psHtml.print(part.getTitle());
            psHtml.print("</td>");
            if (part.getRefreshException() != null) {
                psHtml.print("<td class='firebom_td firebom_error' title='");
                psHtml.print(part.getRefreshException().getMessage());
                psHtml.print("'>");
            } else if (!part.isResolved()) {
                psHtml.print("<td class='firebom_td firebom_unresolved'>");
            } else if (!part.isFresh()) {
                psHtml.print("<td class='firebom_td firebom_stale'>");
            } else {
                psHtml.print("<td class='firebom_td firebom_fresh'>");
            }
            psHtml.print((part.getAge() + 500) / 1000);
            psHtml.print("@");
            psHtml.print((part.getRefreshInterval() + 500) / 1000);
            psHtml.print("</td>");
            psHtml.print("</tr>");
        }
        psHtml.print("</table>");

        psHtml.print("Refresh queue: ");
        psHtml.println("<ol>");
        for (Part part : partFactory.getRefreshQueue()) {
            psHtml.println("<li>");
            psHtml.println(part.getUrl());
            psHtml.println("</li>");
        }
        psHtml.println("</ol>");

        psHtml.println("<script>setTimeout(function() {location.reload();}, 5000)</script>");

    }

    private boolean partMatchesQuery(Part part, String query) {
        return true;
    }
}