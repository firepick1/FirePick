package org.firepick.firebom.rest;

import org.firepick.firebom.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.io.*;

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
        while (br.ready()) {
            String line = br.readLine();
            if (line.contains("<!--PART-->")) {
                for (Part part : partFactory) {
                    if (partMatchesQuery(part, query)) {
                        psHtml.print("<tr>");
                        psHtml.print("<td class='firebom_td'>");
                        psHtml.print(part.getId());
                        psHtml.print("</td>");
                        psHtml.print("<td class='firebom_td firebom_longtext'>");
                        psHtml.print(part.getTitle());
                        psHtml.print("</td>");
                        if (!part.isFresh()) {
                            psHtml.print("<td class='firebom_td firebom_stale'>");
                        } else {
                            psHtml.print("<td class='firebom_td firebom_fresh'>");
                        }
                        psHtml.print(part.getExpectedRefreshMillis());
                        psHtml.print("</td>");
                        psHtml.print("</tr>");
                    }
                }
            } else {
                psHtml.println(line);
            }
        }

        return bosHtml.toString();
    }

    private boolean partMatchesQuery(Part part, String query) {
        return true;
    }
}