/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package B_servlets;

import HelperClasses.Member;
import HelperClasses.ShoppingCartLineItem;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author adamk
 */
@WebServlet(name = "ECommerce_PaymentServlet", urlPatterns = {"/ECommerce_PaymentServlet"})
public class ECommerce_PaymentServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
   /* done by Adam and Shi Han*/
    ArrayList<ShoppingCartLineItem> shoppingCart;
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
           HttpSession session;
            session = request.getSession();
            Pattern p1 = Pattern.compile("(5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}");
            Matcher m = p1.matcher(request.getParameter("cardNo"));
            if (m.matches()) {

            shoppingCart = (ArrayList<ShoppingCartLineItem>) session.getAttribute("shoppingCart");
            if (shoppingCart != null && shoppingCart.size() > 0) {
                double totalPrice = calculateTotalAmount(shoppingCart);
                Long selectedStore = Long.parseLong("59");
                int storeQty = checkQuantity(selectedStore, shoppingCart);
                if (storeQty > shoppingCart.get(0).getQuantity()) {
                    String memberEmail = (String) session.getAttribute("memberEmail");
                   
                    Member member = getMember(memberEmail);
                    Long memberId = member.getId();
                    int transactionRecordId = createECommerceTransactionRecord(totalPrice, memberId,selectedStore);
                    if (transactionRecordId != 0) {
                        for (ShoppingCartLineItem oneItem : shoppingCart) {
                            int result = createECommerceLineItemRecord(oneItem, transactionRecordId,selectedStore);
                            System.out.println(result);
                            System.out.println(transactionRecordId);
                            if (result == 1) {
                                if (shoppingCart.get(shoppingCart.size() - 1).equals(oneItem)) {
                                 
                                    session.setAttribute("shoppingCart", null);
                                    response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp?goodMsg=Payment Success!");
                                }
                            } else {
                                session.setAttribute("shoppingCart", null);
                                response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp?errMsg=Error in inserting into line item record.");
                            }

                        }
                    } else {
                        
                        session.setAttribute("shoppingCart", null);
                        response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp?errMsg=Error in inserting into transaction record.");
                    }
                }
                else
                {
                   
                    response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp?errMsg=Not enough quantity in stock");
                }

            }
            }
            else {
                response.sendRedirect("/IS3102_Project-war/B/SG/shoppingCart.jsp?errMsg=Please enter a valid Card number");
            }

        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }
    

    
     public int checkQuantity(Long storeId, ArrayList<ShoppingCartLineItem> shoppingCart) {
        ShoppingCartLineItem s = shoppingCart.get(0);
        storeId = Long.parseLong("59");
        try {
            Client client = ClientBuilder.newClient();
            WebTarget target = client
                    .target("http://localhost:8080/IS3102_WebService-Student/webresources/entity.storeentity")
                    .path("getQuantity")
                    .queryParam("storeID", storeId)
                    .queryParam("SKU", s.getSKU());
            
            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
            Response response = invocationBuilder.get();
            if (response.getStatus() != 200) {
                return 0;
            }
            String result = (String) response.readEntity(String.class);
            return Integer.parseInt(result);

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
     
     
    public Member getMember(String email) {
        try {

            Client client = ClientBuilder.newClient();
            WebTarget target = client
                    .target("http://localhost:8080/IS3102_WebService-Student/webresources/getAndUpdateMember").path("getMember")
                    .queryParam("email", email);
            Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
            Response response = invocationBuilder.get();

            if (response.getStatus() != 200) {
                return null;
            }

            return response.readEntity(Member.class);
        } catch (Exception ex) {
            return null;
        }
    }
      public int createECommerceTransactionRecord(double totalPrice, Long memberId,Long storeId) {
        try {
            Client client = ClientBuilder.newClient();
            WebTarget target = client
                    .target("http://localhost:8080/IS3102_WebService-Student/webresources/entity.commerce")
                    .path("createECommerceTransactionRecord")
                    .queryParam("totalPrice", totalPrice)
                    .queryParam("memberID", memberId)
                    .queryParam("storeId",storeId);

            Invocation.Builder invocationBuilder = target.request();
            Response response = invocationBuilder.put(Entity.entity("", "application/json"));

            if (response.getStatus() != 201) {
                return 0;
            }
            else
            {
                String result = (String) response.readEntity(String.class);
                System.out.println("createECommerceTransactionRecord result : " + result);
                return Integer.parseInt(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int createECommerceLineItemRecord(ShoppingCartLineItem cartItem, int transactionRecordId,Long storeId) {

        try {
            Client client = ClientBuilder.newClient();
            WebTarget target = client
                    
                    .target("http://localhost:8080/IS3102_WebService-Student/webresources/entity.commerce/")
                    .path("createECommerceLineItemRecord")
                    .queryParam("quantity", cartItem.getQuantity())
                    .queryParam("itemId", cartItem.getId())
                    .queryParam("SKU", cartItem.getSKU())
                    .queryParam("transactionRecordId", transactionRecordId)
                    .queryParam("storeId",storeId);

            Invocation.Builder invocationBuilder = target.request();
            Response response = invocationBuilder.put(Entity.entity("", "application/json"));
            if (response.getStatus() != 200) {
                return 0;
            }
            else
            {
                return 1;
            }
            

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public double calculateTotalAmount(ArrayList<ShoppingCartLineItem> shoppingCart) {
        double totalPrice = 0.0;
        for (ShoppingCartLineItem oneItem : shoppingCart) {
            totalPrice += oneItem.getQuantity() * oneItem.getPrice();
        }
        return totalPrice;
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
