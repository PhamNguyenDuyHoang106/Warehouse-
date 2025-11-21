<footer id="footer" class="mt-5">
    <style>
        #footer {
            width: 100%;
            max-width: 100%;
            background: linear-gradient(135deg, #2c3e50 0%, #34495e 100%);
            color: #ecf0f1;
            padding: 50px 0 20px;
            position: relative;
            overflow: hidden;
            margin: 0;
            box-sizing: border-box;
        }
        
        #footer::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, #DEAD6F 0%, #cfa856 100%);
        }
        
        #footer h6 {
            color: #DEAD6F;
            font-weight: 600;
            font-size: 16px;
            margin-bottom: 20px;
            text-transform: uppercase;
            letter-spacing: 1px;
            position: relative;
            padding-bottom: 10px;
        }
        
        #footer h6::after {
            content: '';
            position: absolute;
            bottom: 0;
            left: 0;
            width: 40px;
            height: 2px;
            background: #DEAD6F;
        }
        
        #footer ul {
            list-style: none;
            padding: 0;
        }
        
        #footer ul li {
            margin-bottom: 12px;
            transition: transform 0.3s ease;
        }
        
        #footer ul li:hover {
            transform: translateX(5px);
        }
        
        #footer a {
            color: #bdc3c7;
            text-decoration: none;
            transition: all 0.3s ease;
            display: inline-flex;
            align-items: center;
            gap: 8px;
        }
        
        #footer a:hover {
            color: #DEAD6F;
            transform: translateX(3px);
        }
        
        #footer a i {
            font-size: 12px;
        }
        
        #footer .footer-logo {
            margin-bottom: 20px;
            transition: transform 0.3s ease;
        }
        
        #footer .footer-logo:hover {
            transform: scale(1.05);
        }
        
        #footer .social-icons {
            display: flex;
            gap: 12px;
            margin-top: 20px;
        }
        
        #footer .social-icon {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: rgba(222, 173, 111, 0.1);
            border: 2px solid rgba(222, 173, 111, 0.3);
            display: flex;
            align-items: center;
            justify-content: center;
            color: #bdc3c7;
            transition: all 0.3s ease;
            text-decoration: none;
        }
        
        #footer .social-icon:hover {
            background: #DEAD6F;
            border-color: #DEAD6F;
            color: white;
            transform: translateY(-3px) rotate(5deg);
            box-shadow: 0 5px 15px rgba(222, 173, 111, 0.4);
        }
        
        #footer .contact-info {
            color: #bdc3c7;
            line-height: 1.8;
        }
        
        #footer .contact-info strong {
            color: #DEAD6F;
            display: block;
            margin-bottom: 8px;
        }
        
        #footer .contact-info i {
            color: #DEAD6F;
            margin-right: 8px;
            width: 20px;
        }
        
        #footer .footer-divider {
            border: none;
            border-top: 1px solid rgba(222, 173, 111, 0.2);
            margin: 30px 0 20px;
        }
        
        #footer .footer-bottom {
            text-align: center;
            padding-top: 20px;
            color: #95a5a6;
            font-size: 14px;
        }
        
        #footer .footer-bottom p {
            margin: 0;
        }
        
        @media (max-width: 768px) {
            #footer {
                text-align: center;
            }
            
            #footer .social-icons {
                justify-content: center;
            }
        }
    </style>
    
    <div class="container-fluid py-4">
        <div class="row">
            <!-- Quick Links -->
            <div class="col-md-3 col-sm-6 mb-4 mb-md-0">
                <h6><i class="fas fa-link me-2"></i>Quick Links</h6>
                <ul>
                    <li><a href="home"><i class="fas fa-chevron-right"></i>Dashboard</a></li>
                    <li><a href="dashboardmaterial"><i class="fas fa-chevron-right"></i>Materials</a></li>
                    <li><a href="ImportMaterial"><i class="fas fa-chevron-right"></i>Import</a></li>
                    <li><a href="ExportMaterial"><i class="fas fa-chevron-right"></i>Export</a></li>
                    <li><a href="ExportRequestList"><i class="fas fa-chevron-right"></i>Requests</a></li>
                </ul>
            </div>

            <!-- Help Center -->
            <div class="col-md-3 col-sm-6 mb-4 mb-md-0">
                <h6><i class="fas fa-question-circle me-2"></i>Help Center</h6>
                <ul>
                    <li><a href="#"><i class="fas fa-chevron-right"></i>User Guide</a></li>
                    <li><a href="#"><i class="fas fa-chevron-right"></i>Policies</a></li>
                    <li><a href="#"><i class="fas fa-chevron-right"></i>Return Materials</a></li>
                    <li><a href="#"><i class="fas fa-chevron-right"></i>Report Issue</a></li>
                    <li><a href="#"><i class="fas fa-chevron-right"></i>Technical Support</a></li>
                </ul>
            </div>

            <!-- Contact -->
            <div class="col-md-3 col-sm-6 mb-4 mb-md-0">
                <h6><i class="fas fa-envelope me-2"></i>Contact</h6>
                <div class="contact-info">
                    <strong><i class="fas fa-building"></i>Material Department</strong>
                    <p><i class="fas fa-envelope"></i>support@materials.company.com</p>
                    <p><i class="fas fa-phone"></i>+1 234 567 890</p>
                    <p><i class="fas fa-map-marker-alt"></i>123 Company Street, District 1, HCMC</p>
                </div>
            </div>

            <!-- Logo & Social -->
            <div class="col-md-3 col-sm-6 mb-4 mb-md-0 text-md-end text-center">
                <div class="footer-logo">
                    <img src="images/AdminLogo.png" alt="Logo" width="180px" style="filter: brightness(0) invert(1);">
                </div>
                <p class="mb-3" style="color: #bdc3c7; font-size: 13px;">
                    Internal Materials Management System
                </p>
                <div class="social-icons">
                    <a href="#" class="social-icon" title="Facebook">
                        <i class="fab fa-facebook-f"></i>
                    </a>
                    <a href="#" class="social-icon" title="LinkedIn">
                        <i class="fab fa-linkedin-in"></i>
                    </a>
                    <a href="#" class="social-icon" title="GitHub">
                        <i class="fab fa-github"></i>
                    </a>
                </div>
            </div>
        </div>
        
        <hr class="footer-divider">
        
        <div class="footer-bottom">
            <p>&copy; 2024 Material Management System. All rights reserved.</p>
        </div>
    </div>
</footer>
</html>
