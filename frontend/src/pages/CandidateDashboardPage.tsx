import { useNavigate } from "react-router-dom"

import { Button } from "@/components/ui/button"
import { useAuth } from "@/features/auth/context/AuthContext"

export function CandidateDashboardPage() {
  const navigate = useNavigate()
  const { user, logout } = useAuth()

  const handleLogout = () => {
    logout()
    navigate("/login")
  }

  return (
    <main className="min-h-screen p-8">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold">
            Candidate Dashboard
          </h1>

          <p className="mt-2 text-muted-foreground">
            Welcome, {user?.name}.
          </p>
        </div>

        <Button
          variant="outline"
          onClick={handleLogout}
        >
          Logout
        </Button>
      </div>
    </main>
  )
}