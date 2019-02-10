/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package B_servlets;

import HelperClasses.ShoppingCartLineItem;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author adamk
 */
@WebServlet(name = "ECommerce_AddFurnitureToListServlet", urlPatterns = {"/ECommerce_AddFurnitureToListServlet"})
public class ECommerce_AddFurnitureToListServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    /*done by adam*/
    ArrayList<ShoppingCartLineItem> shoppingCart;
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
   HttpSession session = request.getSession();
            //can only order from queenstown
            String storeId = "59";
            Long longStoreId = Long.parseLong(storeId);
            
            
            String SKU = request.getParameter("SKU");
            String id = request.getParameter("id");
            double price = Double.parseDouble(request.getParameter("price"));
            String name = request.getParameter("name");
            String img = request.getParameter("imageURL");
            
            //Singapore country Id
            String countryId = "25";
            Long longCountryId = Long.parseLong(countryId);

            //get Quantity 
            int itemQty = getQuantity(longStoreId, SKU);
            ShoppingCartLineItem oneShopCartItem = new ShoppingCartLineItem(id, SKU, name, img, price, 1, longCountryId);
            
            shoppingCart = (ArrayList<ShoppingCartLineItem>) session.getAttribute("shoppingCart");

            if (itemQty > 0){
             
                if (shoppingCart == null) {
                shoppingCart= new ArrayList<ShoppingCartLineItem>();
                shoppingCart.add(oneShopCartItem);
                }
                else if (!shoppingCart.contains(oneShopCartItem)) {   
                shoppingCart.add(oneShopCartItem);
                } else {
                if (shoppingCart.contains(oneShopCartItem)) {
                    for (ShoppingCartLineItem oneLineItem : shoppingCart) {
                        if (oneLineItem.equals(oneShopCartItem)) {
                            int newQuantity = oneLineItem.getQuantity() + 1;
                            if (itemQty > newQuantity) {
                                oneLineItem.setQuantity(newQuantity);
                                
                            } else {
                                response.sendRedirect("/IS3102_Project-war/B/SG/" + 
                                        "shoppingCart.jsp?errMsg=Not enough stock");
                                return;
                                
                            }
                            break;
                        }
                    }
                }
            }            
            session.setAttribute("shoppingCart", shoppingCart);
            response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp?goodMsg=Successfully added item to cart!");                 
            }else{
            out.println("<script type=\"text/javascript\">");
            out.println("alert('Not enough item found in the warehouse!');");
            out.println("location='/IS3102_Project-war/B/SG/furnitureProductDetails.jsp?sku="+SKU+"';");
            out.println("</script>");
            }    
        }
        
}
      public int getQuantity(Long storeID, String SKU) {
        try {
            System.out.println("getQuantity() SKU: " + SKU);
            Client client = ClientBuilder.newClient();
            WebTarget target = client
                    .target("http://localhost:8080/IS3102_WebService-Student/webresources/entity.storeentity")
                    .path("getQuantity")
                    .queryParam("storeID", storeID)
                    .queryParam("SKU", SKU);
            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
            Response response = invocationBuilder.get();
            System.out.println("status: " + response.getStatus());
            if (response.getStatus() != 200) {
                return 0;
            }
            String result = (String) response.readEntity(String.class);
            System.out.println("Result returned from ws: " + result);
            return Integer.parseInt(result);

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
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
