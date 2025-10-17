package utils;

import entity.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/*"})
public class AuthenticationFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Lấy đường dẫn request
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());
        
        // Loại trừ các URL không cần authentication
        if (path.equals("/LoginServlet") || path.equals("/Login.jsp") || 
            path.equals("/logout") || path.equals("/LogoutServlet") || 
            path.equals("/ForgotPassword") || path.equals("/ForgotPasswordServlet") ||
            path.equals("/ForgotPassword.jsp") || path.equals("/VerifyUserServlet") ||
            path.equals("/error.jsp") || path.equals("/") || path.equals("") ||
            path.equals("/search") || path.equals("/searchbycode") || path.equals("/searchPrice") ||
            path.equals("/sortbymaterialname") || path.equals("/filter") ||
            path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/") ||
            path.startsWith("/assets/") || path.startsWith("/META-INF/") || 
            path.startsWith("/WEB-INF/") || path.endsWith(".ico") || path.endsWith(".png") ||
            path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".gif") ||
            path.endsWith(".svg") || path.endsWith(".woff") || path.endsWith(".woff2") ||
            path.endsWith(".ttf") || path.endsWith(".eot")) {
            chain.doFilter(request, response);
            return;
        }
        
        HttpSession session = httpRequest.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;
        
        if (user == null) {
            // Lưu URL hiện tại để redirect sau khi đăng nhập
            String currentURL = httpRequest.getRequestURL().toString();
            String queryString = httpRequest.getQueryString();
            if (queryString != null && !queryString.isEmpty()) {
                currentURL += "?" + queryString;
            }
            
            // Tạo session mới nếu chưa có
            if (session == null) {
                session = httpRequest.getSession();
            }
            session.setAttribute("redirectURL", currentURL);
            
            // Redirect đến Login.jsp
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/Login.jsp");
            return;
        }
        
        // Nếu đã đăng nhập, tiếp tục xử lý request
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        // Cleanup code if needed
    }
} 