# ShambaBora - Role-Based Access Control Guide

## Overview
ShambaBora now implements a comprehensive role-based access control system that provides tailored experiences for different user types.

## User Roles

### 1. FARMER 🌾
**Full Access User**
- **Registration**: Self-registration available
- **Features Access**:
  - ✅ Dashboard (Home)
  - ✅ Marketplace
  - ✅ Community
  - ✅ Farm Records (Activities, Expenses, Yields)
  - ✅ AI Assistant
- **Special Flow**: Must create farmer profile after registration
- **Use Case**: Farmers managing their maize farms

### 2. BUYER 🛒
**Limited Access User**
- **Registration**: Self-registration available
- **Features Access**:
  - ✅ Marketplace (Browse and purchase products)
  - ✅ Community (Interact with farmers and agro dealers)
  - ❌ Dashboard
  - ❌ Farm Records
  - ❌ AI Assistant
- **Special Flow**: Direct access to marketplace after registration
- **Use Case**: Customers buying agricultural products

### 3. EXTENSION_OFFICER (Agro Dealer) 👨‍🌾
**Educational Access User**
- **Registration**: ❌ Cannot self-register (Admin creates account)
- **Features Access**:
  - ✅ Marketplace (Sell agricultural products and inputs)
  - ✅ Community (Teach and interact with farmers)
  - ✅ AI Assistant (Provide expert assistance)
  - ❌ Dashboard
  - ❌ Farm Records
- **Special Flow**: Login-only access
- **Use Case**: Agricultural extension officers and agro dealers providing expertise

## Registration Flow

### Farmer Registration
```
1. Open App → Login Screen
2. Click "Register"
3. Fill Form:
   - Full Name
   - Username
   - Email
   - Phone (optional)
   - Select Role: "Farmer" ✓
   - Password
   - Confirm Password
4. Click "Register"
5. → Redirected to Farmer Profile Creation
6. Fill Farmer Profile:
   - Farm Name
   - Farm Size
   - Location
   - County
   - Primary Crops
   - etc.
7. Click "Save"
8. → Redirected to Dashboard
```

### Buyer Registration
```
1. Open App → Login Screen
2. Click "Register"
3. Fill Form:
   - Full Name
   - Username
   - Email
   - Phone (optional)
   - Select Role: "Buyer" ✓
   - Password
   - Confirm Password
4. Click "Register"
5. → Redirected to Marketplace
6. Start browsing products!
```

### Agro Dealer (Extension Officer)
```
1. Admin creates account in backend
2. User receives credentials
3. Open App → Login Screen
4. Enter credentials
5. → Redirected to Marketplace
6. Access to Marketplace, Community, and AI Assistant
```

## Navigation Structure

### Bottom Navigation by Role

#### Farmer (5 tabs)
```
┌─────────────────────────────────────────┐
│  Home  │ Market │ Community │ Records │ AI │
└─────────────────────────────────────────┘
```

#### Buyer (2 tabs)
```
┌──────────────────────────┐
│  Marketplace  │ Community │
└──────────────────────────┘
```

#### Agro Dealer (3 tabs)
```
┌──────────────────────────────────────┐
│  Marketplace  │ Community │ AI Assistant │
└──────────────────────────────────────┘
```

## Farmer Profile Management

### Profile Creation Flow
1. **After Registration**: Farmers are automatically redirected to create their profile
2. **Required Fields**:
   - Farm Name (Required)
   - Farm Size (Optional)
   - Location (Optional)
   - County (Optional)
3. **Optional Fields**:
   - Farm Description
   - Alternate Phone
   - Postal Address
   - Primary Crops
   - Farming Experience
   - Certifications

### Dashboard Banner
If a farmer hasn't created their profile, a prominent banner appears on the dashboard:

```
┌──────────────────────────────────────────────────────────┐
│ ⚠️  Complete Your Farmer Profile                        │
│                                                           │
│ Please create your farmer profile to access all          │
│ features and get personalized recommendations.           │
│                                                    →      │
└──────────────────────────────────────────────────────────┘
```

**Features**:
- Eye-catching error container color
- Clear call-to-action message
- Click anywhere to navigate to profile creation
- Disappears after profile is created

## Login Behavior

### Smart Routing
The app automatically routes users to the appropriate screen based on their role:

| Role | Login Destination |
|------|------------------|
| FARMER | Dashboard (Home) |
| BUYER | Marketplace |
| EXTENSION_OFFICER | Marketplace |

### Session Persistence
- Roles are stored in SharedPreferences
- Navigation persists across app restarts
- Users stay logged in until they explicitly logout

## API Integration

### Registration Endpoint
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_farmer",
  "email": "john@example.com",
  "password": "SecurePass123",
  "fullName": "John Doe",
  "phoneNumber": "+254712345678",
  "role": "FARMER"  // or "BUYER"
}
```

### Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "username": "john_farmer",
  "email": "john@example.com",
  "roles": ["FARMER"],
  "userId": 123,
  "message": "Registration successful"
}
```

## Security Considerations

### Frontend Protection
- Bottom navigation hides restricted tabs
- Conditional rendering based on roles
- Role checks before navigation

### Backend Protection (Required)
⚠️ **Important**: The backend must validate user roles for all protected endpoints:

```java
// Example: Protect farm records endpoints
@PreAuthorize("hasRole('FARMER')")
@GetMapping("/farm-activities")
public ResponseEntity<?> getFarmActivities() {
    // Only accessible by farmers
}

@PreAuthorize("hasAnyRole('FARMER', 'BUYER', 'EXTENSION_OFFICER')")
@GetMapping("/marketplace/products")
public ResponseEntity<?> getProducts() {
    // Accessible by all authenticated users
}
```

## Testing Guide

### Test Scenarios

#### 1. Farmer Registration & Profile Creation
```
✓ Register as farmer
✓ Redirected to profile creation
✓ Create profile with required fields
✓ Redirected to dashboard
✓ All 5 tabs visible in bottom navigation
✓ No banner on dashboard (profile created)
```

#### 2. Buyer Registration & Access
```
✓ Register as buyer
✓ Redirected to marketplace
✓ Only 2 tabs visible (Marketplace, Community)
✓ Cannot access Dashboard or Records
✓ Can browse and purchase products
```

#### 3. Agro Dealer Login
```
✓ Login with admin-created credentials
✓ Redirected to marketplace
✓ 3 tabs visible (Marketplace, Community, AI)
✓ Can access AI Assistant
✓ Cannot access Dashboard or Records
```

#### 4. Incomplete Farmer Profile
```
✓ Register as farmer
✓ Skip profile creation (back button)
✓ Navigate to dashboard
✓ Banner appears: "Complete Your Farmer Profile"
✓ Click banner
✓ Redirected to profile creation
```

#### 5. Role Persistence
```
✓ Login as farmer
✓ Close app
✓ Reopen app
✓ Still logged in as farmer
✓ Dashboard shown (correct start destination)
✓ All 5 tabs still visible
```

## Troubleshooting

### Issue: Bottom navigation shows wrong tabs
**Solution**: Check that roles are being saved correctly in PreferenceManager after login/registration

### Issue: Farmer not redirected to profile creation
**Solution**: Verify that the role is set to "FARMER" during registration and saved in SharedPreferences

### Issue: Banner doesn't appear for incomplete profile
**Solution**: Check that the farmer profile API returns an error when no profile exists

### Issue: Agro dealer can self-register
**Solution**: Remove EXTENSION_OFFICER from the registration dropdown (already done)

## Future Enhancements

### Planned Features
1. **Multi-Role Support**: Users with multiple roles (e.g., FARMER + BUYER)
2. **Admin Dashboard**: Separate interface for admins
3. **Role-Based Permissions**: Fine-grained permissions per feature
4. **Dynamic Role Assignment**: Admins can change user roles
5. **Role Analytics**: Track user behavior by role

### Backend Requirements
1. Admin panel to create EXTENSION_OFFICER accounts
2. Role validation on all protected endpoints
3. Role update API for admins
4. Audit logging for role changes

## Summary

✅ **3 User Roles**: FARMER, BUYER, EXTENSION_OFFICER
✅ **Dynamic Navigation**: Bottom nav adapts to user role
✅ **Smart Routing**: Users land on appropriate screen
✅ **Farmer Profile Flow**: Seamless post-registration profile creation
✅ **Dashboard Banner**: Reminds farmers to complete profile
✅ **Session Persistence**: Roles persist across app restarts
✅ **Professional UI**: Material 3 design with role-based customization
✅ **API Compliant**: Follows swagger documentation

The ShambaBora app now provides a tailored, professional experience for each user type! 🎉
