import {
  BrowserRouter,
  Navigate,
  Route,
  Routes,
} from "react-router-dom"

import { LoginPage } from "@/pages/LoginPage"
import { RegisterPage } from "@/pages/RegisterPage"
import { CandidateDashboardPage } from "@/pages/CandidateDashboardPage"
import { RecruiterDashboardPage } from "@/pages/RecruiterDashboardPage"
import { ProtectedRoute } from "@/routes/ProtectedRoute"

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/"
          element={<Navigate to="/login" replace />}
        />

        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        <Route
          element={<ProtectedRoute allowedRole="CANDIDATE" />}
        >
          <Route
            path="/candidate/dashboard"
            element={<CandidateDashboardPage />}
          />
        </Route>

        <Route
          element={<ProtectedRoute allowedRole="RECRUITER" />}
        >
          <Route
            path="/recruiter/dashboard"
            element={<RecruiterDashboardPage />}
          />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App