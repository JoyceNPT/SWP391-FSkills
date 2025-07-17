/**
 * Transition effects for sidebar and header interactions
 * This script handles the smooth transitions between sidebar and header
 * for various pages including test pages and admin pages
 */

document.addEventListener('DOMContentLoaded', function() {
    // Get elements
    const sidebar = document.querySelector('.sidebar');
    const header = document.querySelector('header');
    const mainContent = document.querySelector('.main') || 
                        document.querySelector('.main-content') || 
                        document.querySelector('.admin-content') ||
                        document.querySelector('.profile-edit-container');
    
    if (!sidebar || !header) return; // Exit if elements don't exist
    
    // Detect page type for specific transitions
    const body = document.body;
    const path = window.location.pathname;
    
    // Add appropriate class to body based on current page
    if (path.includes('/test') && path.includes('/take')) {
        body.classList.add('test-page');
    } else if (path.includes('/test') && path.includes('/detail')) {
        body.classList.add('test-detail-page');
    } else if (path.includes('/test') && path.includes('/submit')) {
        body.classList.add('test-submit-page');
    } else if (path.includes('/admin') && !path.includes('/')) {
        body.classList.add('admin-dashboard-page');
    } else if (path.includes('/admin/voucher')) {
        body.classList.add('admin-voucher-page');
    } else if (path.includes('/admin/announcement')) {
        body.classList.add('admin-announcement-page');
    } else if (path.includes('/admin/degree')) {
        body.classList.add('admin-degree-page');
    } else if (path.includes('/admin/accounts')) {
        body.classList.add('admin-accounts-page');
    } else if (path.includes('/admin/report')) {
        body.classList.add('admin-report-page');
    } else if (path.includes('/admin/feedback')) {
        body.classList.add('admin-feedback-page');
    }
    
    // Add role class to body
    const userRole = getUserRole();
    if (userRole) {
        body.classList.add(userRole + '-role');
    }
    
    // Event handler for sidebar mouseenter
    function handleSidebarMouseEnter() {
        const windowWidth = window.innerWidth;
        
        if (windowWidth <= 768) {
            // Mobile layout
            if (header) {
                header.style.left = '250px';
                header.style.width = 'calc(100% - 266px)';
            }
            if (mainContent) {
                mainContent.style.marginLeft = '250px';
                mainContent.style.width = 'calc(100% - 250px)';
            }
        } else {
            // Desktop layout
            if (header) {
                header.style.left = '250px';
                header.style.width = 'calc(100% - 266px)';
            }
            if (mainContent) {
                mainContent.style.marginLeft = '250px';
                mainContent.style.width = 'calc(100% - 250px)';
            }
        }
    }
    
    // Event handler for sidebar mouseleave
    function handleSidebarMouseLeave() {
        const windowWidth = window.innerWidth;
        
        if (windowWidth <= 768) {
            // Mobile layout
            if (header) {
                header.style.left = '0';
                header.style.width = 'calc(100% - 16px)';
            }
            if (mainContent) {
                mainContent.style.marginLeft = '0';
                mainContent.style.width = '100%';
            }
        } else {
            // Desktop layout
            if (header) {
                header.style.left = '80px';
                header.style.width = 'calc(100% - 96px)';
            }
            if (mainContent) {
                mainContent.style.marginLeft = '80px';
                mainContent.style.width = 'calc(100% - 80px)';
            }
        }
    }
    
    // Add event listeners
    sidebar.addEventListener('mouseenter', handleSidebarMouseEnter);
    sidebar.addEventListener('mouseleave', handleSidebarMouseLeave);
    
    // Set initial state based on window width
    handleSidebarMouseLeave();
    
    // Add resize event listener to handle window size changes
    window.addEventListener('resize', function() {
        // Update layout based on current sidebar state
        if (sidebar.matches(':hover')) {
            handleSidebarMouseEnter();
        } else {
            handleSidebarMouseLeave();
        }
    });
    
    // Helper function to get user role from session
    function getUserRole() {
        // Check for instructor role
        if (document.querySelector('.instructor-indicator') || 
            path.includes('/instructor')) {
            return 'instructor';
        }
        // Check for admin role
        else if (document.querySelector('.admin-indicator') || 
                path.includes('/admin')) {
            return 'admin';
        }
        // Default to learner role
        else {
            return 'learner';
        }
    }
});