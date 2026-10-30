# Applied Math AI & 3D Interactive Lab Suite

Transforming the Class 12 Applied Mathematics tracker into a high-performance 100/100 exam preparation hub powered by Gemini AI tutoring, real-time voice audio coaching, Google Search grounded syllabus updates, Firebase cloud synchronization, and interactive Three.js 3D mathematical visualizations.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following specifications synthesize your clarifying answers and feature requirements. Please review before proceeding to execution.

- **Confirmed Decision 1 (AI Capabilities)**:
  - **Gemini Math Chatbot**: Multi-turn chat interface using `gemini-3.8-flash` (and `gemini-4-argon` for advanced calculus/matrix proofs) with LaTeX rendering and step-by-step problem breakdowns.
  - **Live Voice Conversations**: Audio-based interactive tutoring powered by `gemini-3.8-live` for hands-free voice Q&A and mental math practice.
  - **Google Search Grounding**: Enabled via Gemini with `googleSearch` tool to retrieve up-to-date CBSE Class 12 exam dates, sample question papers (SQP), and marking scheme trends.
  - **Firebase Cloud Sync & Auth**: Google Sign-In via Jetpack `CredentialManager` and Firestore database to synchronize syllabus progress, streaks, XP, and study logs across devices while preserving offline Room SQLite caching.
- **Confirmed Decision 2 (AI Persona & Pedagogy)**:
  - **100/100 CBSE Exam Strategist & Vedic Speed Coach**: Focused on achieving top marks in Class 12 Applied Mathematics (CBSE Code 241). Delivers crystal-clear conceptual explanations, step-by-step derivations, and Vedic Math speed shortcuts (e.g., Urdhva Tiryagbhyam for matrix multiplication, Nikhilam for large numerical powers and modulo arithmetic, shortcut formulas for EMI and sinking funds).
- **Confirmed Decision 3 (3D Visualizations Dual Integration)**:
  - **Dedicated 3D Math Lab**: Full-screen 3D interactive viewer for Linear Programming feasible regions (bounded/unbounded polyhedrons, constraint planes, corner-point evaluation), 3D coordinate planes, and matrix transformations.
  - **Embedded Topic 3D Cards**: Direct interactive 3D model previews embedded within relevant chapter topic detail cards (e.g., Matrices, Linear Programming, Derivatives slopes).
- **Confirmed Decision 4 (3D Graphics Delivery in Android)**:
  - High-performance, hardware-accelerated **Three.js / WebGL scene container** running locally through an optimized Compose `AndroidView` (WebView loading bundled local HTML5/Three.js assets) with zero external network dependencies, 60fps orbit controls, studio PBR lighting, and bi-directional JavaScript-Kotlin bridge.

---

## 1. Overview & Core Concept

The enhanced application equips Class 12 Applied Mathematics students with a comprehensive, modern exam prep companion:
- **Concept & Speed Coaching**: Bridges theoretical understanding with competitive speed using Vedic calculation shortcuts and CBSE-tailored step breakdowns.
- **Multi-Modal AI Interaction**: Students can type complex math problems, inspect LaTeX solutions, talk via voice during Pomodoro focus sessions, and query current CBSE sample papers.
- **Spatial 3D Intuition**: Visualizes abstract 3D planes, vectors, and linear programming feasible polygons that are traditionally hard to visualize in textbooks.
- **Reliable Progress & Sync**: Seamless transition between offline Room SQLite storage and authenticated Firebase Firestore cloud backup.

---

## 2. User Experience & Visual Design

### Key User Flows
1. **AI Chatbot & Vedic Solver**:
   - Tap the floating AI Spark button or bottom bar navigation to open the **Math AI Mentor**.
   - Select quick prompt chips: *"Explain Leontief Input-Output Model"*, *"Vedic shortcut for 3x3 Determinant"*, *"Solve Modulo Congruence step-by-step"*, or *"Latest CBSE 2026 Sample Paper Pattern"*.
   - View streaming responses with formatted equations, highlighted speed tricks, and collapsible step explanations.
2. **Live Voice Audio Coach**:
   - Switch to the **Voice Tutoring** mode. Tap the glowing audio waveform mic to initiate real-time conversational practice.
   - Hands-free voice dialogues: Practice mental math, formula recall, and rapid question answering while studying.
3. **3D Interactive Math Lab**:
   - Access from Home dashboard or through topic cards (e.g., *13.1 Linear Programming Problems* or *3.1 Matrices*).
   - Touch to rotate, pan, and zoom 3D constraint planes, inspect vertices of feasible regions, and toggle bounding boundaries in real-time.
4. **Google Sign-In & Cloud Sync**:
   - Open Profile / Settings to sign in with Google.
   - Instantly back up syllabus progress, streak shield state, and study logs to Firestore with visual sync status indicators.

### Visual Identity & Theme
- **Theme Palette**: Deep Dark Slate `#0F172A` background, Card surfaces `#1E293B`, glowing Streak Green `#22C55E`, vibrant Cyan Accent `#38BDF8`, and Flame Orange `#F97316`.
- **Typography**: Clean, highly readable system typography paired with monospace/LaTeX mathematical rendering.
- **Components & Motion**: Material Design 3 cards with subtle glow borders, animated audio waveforms during voice sessions, smooth bottom sheets, and responsive WebGL touch controls.

---

## 3. Key Product Decisions & Trade-Offs

| Decision | Chosen Approach | Rationale & Trade-offs |
| :--- | :--- | :--- |
| **3D Rendering Engine** | Three.js via local bundled WebGL Compose Bridge | Allows rich 3D procedural geometries, PBR lighting, and OrbitControls while maintaining lightweight APK footprint without heavy native game engines. |
| **Data Persistence** | Dual Hybrid: Room SQLite (Local Cache) + Firestore (Cloud Sync) | Guarantees instant zero-latency offline usage and 100% functionality without internet, with seamless cloud backup on Google Sign-In. |
| **AI Architecture** | Firebase AI Logic (`gemini-3.8-flash` & `gemini-3.8-live`) with App Check | Zero-trust enterprise security, server-side credential isolation, App Check integrity, and official Google SDK support. |
| **Exam Grounding** | Gemini Search Tool (`googleSearch`) | Directly fetches accurate CBSE Code 241 official blueprints, marking schemes, and syllabus clarifications in real time. |

---

## 4. Technical Architecture & Data Strategy

### System Component Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Jetpack Compose UI Layer                        │
├───────────────┬────────────────┬────────────────┬──────────────────────┤
│ HomeScreen    │ MathAIMentor   │ VoiceCoach     │ ThreeJsMathLab       │
│ • Progress    │ • Multi-turn   │ • Live Audio   │ • Feasible Polyhedr. │
│ • 5 Targets   │ • Vedic Tricks │ • Hands-free   │ • 3D Matrix Planes   │
│ • Streak 0    │ • Search Tool  │ • Real-time    │ • Orbit / Pan touch  │
└───────┬───────┴────────┬───────┴────────┬───────┴──────────┬───────────┘
        │                │                │                  │
        ▼                ▼                ▼                  ▼
┌────────────────────────────────────────────────────────────────────────┐
│                   MainViewModel & StudyRepository                      │
├────────────────────────────────┬───────────────────────────────────────┤
│ • Daily Engine & Backlog Shift │ • Gemini AI Logic Service             │
│ • Streak & Freeze Shield Logic │ • Live Audio Session Controller       │
│ • Progress Reset Orchestrator  │ • JavaScript-Kotlin WebGL Bridge      │
└───────────────┬────────────────┴────────────────┬──────────────────────┘
                │                                 │
                ▼                                 ▼
┌───────────────────────────────┐ ┌──────────────────────────────────────┐
│      Local Room Database      │ │      Firebase Cloud Services         │
│  (13 Chapters, 93 Topics,     │ │  • Firebase Auth (Google Sign-In)    │
│   Streaks, Study Sessions)    │ │  • Firestore (User Sync / Backup)    │
│                               │ │  • Firebase AI Logic (Gemini Models) │
└───────────────────────────────┘ └──────────────────────────────────────┘
```

### Phased Execution Strategy
1. **Firebase Provisioning & App Check Setup**: Execute `set_up_firebase` for `firestore` and `ai_logic`, configure App Check debug tokens and Google Sign-In via `CredentialManager`.
2. **AI Logic Service & Gemini Models**: Implement `AiStudyMentorService` integrating `gemini-3.8-flash`, search grounding, structured math output, and Vedic math prompt tuning.
3. **Live Voice Audio Coach**: Implement real-time voice conversational tutor interface with animated audio visualizer.
4. **Three.js 3D Math Lab**: Build bundled local WebGL Three.js assets and Compose `AndroidView` bridge for interactive 3D linear programming feasible regions and matrix planes.
5. **Firestore Sync & Cloud Backup**: Implement bidirectional synchronization between Room SQLite entities and Firestore user collections.
6. **UI Integration & Polish**: Add navigation tabs/sheets for the AI Mentor, Voice Coach, and 3D Lab, verifying compilation and unit tests.
