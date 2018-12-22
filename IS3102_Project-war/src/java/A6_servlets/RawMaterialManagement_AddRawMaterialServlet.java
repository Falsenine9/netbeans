package A6_servlets;

import CorporateManagement.ItemManagement.ItemManagementBeanLocal;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RawMaterialManagement_AddRawMaterialServlet extends HttpServlet {

    @EJB
    private ItemManagementBeanLocal itemManagementBean;
    String result;
/* done by: Josh(1749139)
    Shi Han(1728080) 
    Adam (1749171)
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String SKU = request.getParameter("SKU");
            String name = request.getParameter("name");
            String category = request.getParameter("category");
            String description = request.getParameter("description");

            Integer _length = Integer.parseInt(request.getParameter("length"));
            Integer width = Integer.parseInt(request.getParameter("width"));
            Integer height = Integer.parseInt(request.getParameter("height"));
            String source = request.getParameter("source");
            System.out.println("source is " + source);
            Pattern p = Pattern.compile("RM[1-9]\\d*");
            Pattern p2 = Pattern.compile("[1-9]\\d*");
            Matcher m = p.matcher(SKU);
            Matcher m2 = p2.matcher(_length.toString());
            Matcher m3 = p2.matcher(width.toString());
            Matcher m4 = p2.matcher(height.toString());
            
            
            
            if (m.matches()) {
                if (m2.matches()) {
                    if (m3.matches()) {
                        if (m4.matches()) {
                            if (!itemManagementBean.checkSKUExists(SKU)) {
                                itemManagementBean.addRawMaterial(SKU, name, category, description, _length, width, height);
                                result = "?goodMsg=Raw Material with SKU: " + SKU + " has been created successfully.";
                                response.sendRedirect("RawMaterialManagement_RawMaterialServlet" + result);
                            } else {
                                result = "?errMsg=Failed to add raw material, SKU: " + SKU + " already exist.";
                                response.sendRedirect(source + result);
                            }
                        } else {
                            result = "?errMsg=Invalid height input, enter a value greater than 0";
                            response.sendRedirect(source + result);
                        }
                    } else {
                        result = "?errMsg=Invalid width input, enter a value greater than 0";
                        response.sendRedirect(source + result);
                    }
                } else {
                    result = "?errMsg=Invalid length input, enter a value greater than 0";
                    response.sendRedirect(source + result);
                }
            } else {
                result = "?errMsg=Invalid SKU input, please follow the following format. Format %28RM%2BInteger greater than 0%29";
                response.sendRedirect(source + result);
            }

        } catch (Exception ex) {
            out.println(ex);
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
