/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package B_servlets;

import CommonInfrastructure.AccountManagement.AccountManagementBeanLocal;
import HelperClasses.Member;
import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
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
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author adamk
 */
@WebServlet(name = "ECommerce_MemberEditProfileServlet", urlPatterns = {"/ECommerce_MemberEditProfileServlet"})
public class ECommerce_MemberEditProfileServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
           
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
    @EJB
    private AccountManagementBeanLocal accountManagementBean;
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //processRequest(request, response);
        
        Client client = ClientBuilder.newClient();
           HttpSession session = request.getSession();
           String email = (String)session.getAttribute("memberEmail");
           String name = request.getParameter("name");
           String phone = request.getParameter("phone");
           String city = request.getParameter("country");
           String address = request.getParameter("address");
           int securityQuestion = Integer.parseInt(request.getParameter("securityQuestion"));
           String securityAnswer = request.getParameter("securityAnswer");
           int age = Integer.parseInt(request.getParameter("age"));
           int income = Integer.parseInt(request.getParameter("income"));
           String password = request.getParameter("password");
           String repassword = request.getParameter("repassword");
           
           WebTarget target = client.target("http://localhost:8080/IS3102_WebService-Student/webresources/getAndUpdateMember")
                   .path("updateProfile")
                   .queryParam("email", email)
                   .queryParam("name", name)
                   .queryParam("phone", phone)
                   .queryParam("city",city)
                   .queryParam("address",address)
                   .queryParam("securityQuestion", securityQuestion)
                   .queryParam("securityAnswer", securityAnswer)
                   .queryParam("age",age)
                   .queryParam("income",income);
           
           if((!password.equals("") && !repassword.equals("")) && (password.equals(repassword))){
                accountManagementBean.resetMemberPassword(email, password);
                System.out.println("Changed password");
            }
                 Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
                 Response res = invocationBuilder.post(null);
        //System.out.println("status: " + response.getStatus());

        if (res.getStatus() == Response.Status.OK.getStatusCode()) {
        
           response.sendRedirect("ECommerce_GetMember");
            
        } else {
            //response.sendRedirect("/IS3102_Project-war/B/SG/memberLogin.jsp");
            System.out.println("failed");
        }
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