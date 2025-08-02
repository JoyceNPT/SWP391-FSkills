# Test Plan for Non-Logged-In User Course Access

## Requirements Verification

Based on the issue description: "chưa login vẫn xem đc AllCourses, xem đc course detail, khi nhấn mua hoặc giỏ hàng check nếu chưa login sẽ chuyển hướng tới trang login"

Translation: "Not logged in users can still view AllCourses, view course details, but when clicking buy or cart, check if not logged in, redirect to login page"

## Changes Made

### 1. CourseDetailServlet.java
- **Modified**: Added null check for user session in doGet method (lines 95-108)
- **Result**: Non-logged-in users can now view course details without NullPointerException
- **Default values set**: isInCart=false, isBought=false, isEnroll=false, studyProgress=0, hasReviewed=false

### 2. courseListLearner.jsp
- **Modified**: Removed login check that redirected to login page (lines 7-12 → 8-9)
- **Result**: Non-logged-in users can now view the course list

### 3. courseDetailsLearner.jsp
- **Modified**: Removed login check that redirected to login page (lines 6-14 → 7-8)
- **Modified**: Added login prompt UI for non-logged-in users (lines 211-223)
- **Result**: Non-logged-in users see course details but get login prompt instead of purchase buttons

### 4. CartServlet.java
- **Modified**: Added explicit null check for user in doPost method (lines 119-122)
- **Result**: Non-logged-in users are redirected to login when trying to add to cart

### 5. CheckoutServlet.java
- **Already had**: Proper login checks in both doGet and doPost methods
- **Result**: Non-logged-in users are redirected to login when trying to purchase

## Test Scenarios

### ✅ Scenario 1: View All Courses (Non-logged-in)
- **URL**: `/AllCourses`
- **Expected**: Course list displays without login requirement
- **Status**: IMPLEMENTED

### ✅ Scenario 2: View Course Details (Non-logged-in)
- **URL**: `/courseDetail?id=<courseId>`
- **Expected**: Course details display with login prompt instead of purchase buttons
- **Status**: IMPLEMENTED

### ✅ Scenario 3: Try to Purchase (Non-logged-in)
- **Action**: Click "Buy Now" button (should show login prompt)
- **Expected**: Redirect to `/login`
- **Status**: IMPLEMENTED (via login prompt UI)

### ✅ Scenario 4: Try to Add to Cart (Non-logged-in)
- **Action**: POST to `/cart` with CartAction=Add
- **Expected**: Redirect to `/login`
- **Status**: IMPLEMENTED

### ✅ Scenario 5: Try to Checkout (Non-logged-in)
- **Action**: Access `/checkout`
- **Expected**: Redirect to `/login`
- **Status**: IMPLEMENTED

## Implementation Summary

All requirements have been successfully implemented:

1. ✅ Non-logged-in users can view AllCourses
2. ✅ Non-logged-in users can view course details
3. ✅ Purchase actions redirect to login page
4. ✅ Cart actions redirect to login page
5. ✅ No server errors for non-logged-in users

The implementation follows the exact requirements specified in the issue description.