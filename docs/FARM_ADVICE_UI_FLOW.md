# 🎨 Farm Advice UI/UX Flow Diagram

## Screen 1: Initial State
```
┌─────────────────────────────────┐
│                                 │
│   ShambaBora AI Chatbot         │
│                                 │
│   💬 Chat with AI...            │
│                                 │
│                                 │
│   [Info icon] [Add icon]        │ ← Get Advice button (Info icon)
│                                 │
├─────────────────────────────────┤
│                                 │
│   Message input here...         │
│                   [Send Button] │
│                                 │
└─────────────────────────────────┘

User taps Info icon ("Get Advice")
         ↓
```

---

## Screen 2: Loading Dialog (Normal Speed)
```
┌────────────────────────────────────┐
│  ⊙ Getting Farm Advice             │
├────────────────────────────────────┤
│                                    │
│  Analyzing your farm data...       │
│                                    │
│  ⚙️ Retrieving farm data           │
│  ⚙️ Analyzing conditions           │
│  ⚙️ Generating recommendations    │
│                                    │
│                 [Keep waiting] [×]│
└────────────────────────────────────┘

(Wait 2-5 seconds if backend is fast)
         ↓
```

---

## Screen 3: Loading Dialog (Slow Request - User Option A: Wait)
```
┌────────────────────────────────────┐
│  ⊙ Getting Farm Advice             │
├────────────────────────────────────┤
│                                    │
│  Still processing...              │
│  Please wait a little more        │
│                                    │
│  ⚙️ Retrieving farm data           │
│  ⚙️ Analyzing conditions           │
│  ⚙️ Generating recommendations    │
│                                    │
│         [Processing in           │
│          background] [Keep       │
│                    waiting] [×]  │
└────────────────────────────────────┘

(Background fade - shows farm advice when ready)
         ↓
```

---

## Screen 4: Slow Request - User Option B: Continue Using App
```
┌─────────────────────────────────┐
│                                 │
│   ShambaBora AI Chatbot         │
│                                 │
│   💬 Chat with AI...            │  ← User can continue chatting
│                                 │
│   Previous: What's best pH...   │  ← App is responsive
│   Assistant: For maize, ideal   │
│            pH is...             │
│                                 │
├─────────────────────────────────┤
│                                 │
│   Message input here...         │
│                   [Send Button] │
│                                 │
└─────────────────────────────────┘

(Processing continues in background)
         ↓
(After 5-20 seconds)
         ↓
```

---

## Screen 5: Notification - Advice Ready
```
┌─────────────────────────────────┐
│                                 │
│   ShambaBora AI Chatbot         │
│                                 │
│   💬 Chat continues...          │
│                                 │
├─────────────────────────────────┤
│┌─────────────────────────────────┐ ← Snackbar Notification
││✓ Your farm advice is ready! │View││
│└─────────────────────────────────┘│
│                                 │
│   Message input here...         │
│                   [Send Button] │
│                                 │
└─────────────────────────────────┘

User taps "View"
         ↓
```

---

## Screen 6: Farm Advice Full Dialog
```
┌────────────────────────────────────┐
│  ℹ️ Farm Advice                     │
├────────────────────────────────────┤
│                                    │
│  ┌──────────────────────────────┐  │
│  │ General Advice               │  │
│  │                              │  │
│  │ Based on your farm data...  │  │
│  │ recommendations are:         │  │
│  │                              │  │
│  │ • Apply N-P-K at these      │  │
│  │ • Best planting time is     │  │
│  │ • Ensure adequate drainage  │  │
│  └──────────────────────────────┘  │
│                                    │
│  Fertilizer Recommendations        │
│  • DAP 20kg per hectare           │
│  • Urea 30kg per hectare          │
│                                    │
│  Priority Actions                  │
│  1. Prepare soil now               │
│  2. Source quality seeds           │
│                                    │
│  Risk Warnings ⚠️                  │
│  • Watch for drought stress        │
│  • Monitor pest activity           │
│                                    │
│                            [Close] │
└────────────────────────────────────┘
```

---

## Screen 7: Error Dialog (with Timeout Message)
```
┌────────────────────────────────────┐
│  Farm Advice                       │
├────────────────────────────────────┤
│                                    │
│  Farm advice is taking longer      │
│  than expected. You can close      │
│  this and we'll notify you when    │
│  it's ready.                       │
│                                    │
│  (Background processing continues) │
│                                    │
│                     [OK] [Retry]   │
└────────────────────────────────────┘
```

---

## Timeline Diagrams

### Case 1: Fast Backend (✅ Best Case)
```
T=0s    [User taps Get Advice]
        Dialog opens
        
T=2s    ✅ Advice loaded
        Dialog shows full advice

Total time: ~2 seconds
User sees: Loading → Advice (smooth experience)
```

### Case 2: Slow Backend with Background Option (✅ Good Case)
```
T=0s    [User taps Get Advice]
        Dialog opens, shows loading
        
T=5s    "Still processing..." message appears
        
T=8s    [User taps "Processing in background"]
        Dialog closes
        User continues using app
        
T=15s   📲 Notification appears: "Ready!"
        
T=16s   [User taps "View"]
        Full advice dialog opens

Total time: ~16 seconds (but only 2 seconds of blocked UI)
User experience: Non-blocking, responsive
```

### Case 3: Very Slow Backend (✅ Auto-Retry Case)
```
T=0s    [User taps Get Advice]
        Dialog opens
        
T=30s   ⏱️ Timeout detected
        "Still processing..." message shown
        
T=33s   🔄 Automatic retry starts
        
T=40s   ✅ Retry succeeds
        Notification: "Ready!"

Total time: ~40 seconds
Important: No error shown to user!
Result: Either advice loads OR notification appears
```

### Case 4: Network Error (⚠️ Error Case)
```
T=0s    [User taps Get Advice]
        Dialog opens
        
T=30s   ⏱️ Timeout detected
        "Still processing..." message
        
T=33s   🔄 Retry attempt
        
T=35s   ❌ Retry fails
        Error dialog with helpful message
        
T=36s   [User can click "Retry" or dismiss]
        Can try again manually

Total time: ~36 seconds
Important: Timeout handled gracefully, not shown as error
```

---

## State Machine Diagram

```
                    ┌─────────────────┐
                    │   Idle/Ready    │
                    │  (No request)   │
                    └────────┬────────┘
                             │ User taps "Get Advice"
                             ↓
                    ┌─────────────────┐
                    │    Loading      │
                    │ (0-30 seconds)  │
                    └────────┬────────┘
                   /         |         \
                  /          |          \
         Success /   ⏱️Timeout  \  Error (auth, etc)
               /        |        \
              ↓         ↓         ↓
        ┌──────────┐ ┌──────────┐ ┌──────────┐
        │ Success  │ │Background│ │  Error   │
        │ Dialog   │ │Loading   │ │ Dialog   │
        └──────────┘ └────┬─────┘ └──────────┘
                          │
                    [After 3s retry]
                          ↓
                      Success/Error
                          │
                ┌─────────┴──────────┐
               ↓                    ↓
          ┌─────────┐         ┌──────────┐
          │Notify   │         │ Error    │
          │ Ready   │         │ Dialog   │
          └─────────┘         └──────────┘
```

---

## Component Hierarchy

```
EnhancedChatbotScreen
├── ChatHeader
│   ├── Menu icon → Opens sidebar
│   └── Info icon → Triggers getFarmAdvice()
│
├── MessageArea
│   ├── MessageList (with pending messages)
│   └── WelcomeScreen (if empty)
│
├── ChatInput
│   └── Send button → askQuestion()
│
├── FarmAdviceLoadingDialog (if farmAdvice is Loading)
│   ├── Title with spinner
│   ├── Message (includes "Still processing..." if timeout)
│   └── Buttons: [Keep waiting] [Processing in background]
│
├── FarmAdviceErrorDialog (if farmAdvice is Error)
│   ├── Error message with tips
│   └── Buttons: [OK] [Retry]
│
├── FarmAdviceDialog (if farmAdvice is Success)
│   ├── GeneralAdvice section
│   ├── FertilizerRecommendations
│   ├── SeedRecommendations
│   ├── PrioritizedActions
│   └── RiskWarnings
│
└── FarmAdviceNotification (Snackbar)
    ├── Success icon + message
    └── "View" action button
```

---

## Data Flow

```
User Action          ViewModel                 UI
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
                                              
Tap Get Advice  →    getFarmAdvice()
                     setLoading()        →   Show Dialog
                                             
                     makeRequest()
                     (30s timeout)
                                            
                                     ↓ (5-30s)
                                             
Detecting Timeout → Show "Still                 
                    processing..."
                                             
                    delay(3000)
                                             
Retry Request   →    makeRequest()
                                             
                ↓    Success Response
                     setSuccess()        →   Show advice
                     notify!             →   Show Notification
                                             
                ↓    Timeout/Error
                     setError()          →   Show Error Dialog
```

---

## Color & Visual Indicators

```
STATE                 COLOR              ICON              ANIMATION
──────────────────────────────────────────────────────────────────
Loading              Primary (Blue)      ⊙ Spinner         Continuous rotation
Slow (timeout)       Orange Warning      ⏱️ Hourglass       Blinking
Success              Green               ✓ Checkmark       Fade in
Error                Red                 ✗ Close           Shake
Processing BG        Primary (Blue)      ⚙️ Gear            Gentle pulse
Ready Notification   Green               ✓ Check circle    Slide in from bottom
```

---

## Accessibility Considerations

✅ Loading dialogs have accessible labels  
✅ Progress steps read in order  
✅ Color + icon used (not color alone)  
✅ Buttons have clear text labels  
✅ Notification auto-dismisses after 5 seconds  
✅ Users can manually close/dismiss dialogs  

---

**Result**: Professional, responsive, user-friendly experience! 🚀
