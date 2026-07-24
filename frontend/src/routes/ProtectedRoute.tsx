import { Navigate, Outlet } from "react-router-dom"

import { useAuth } from "@/features/auth/context/AuthContext"
import type { UserRole } from "@/features/auth/types/auth.types"

interface ProtectedRouteProps {
  allowedRole?: UserRole
}

export function ProtectedRoute({
  allowedRole,
}: ProtectedRouteProps) {
  const { user, token } = useAuth()

  if (!user || !token) {
    return <Navigate to="/login" replace />
  }

  if (allowedRole && user.role !== allowedRole) {
    const destination =
      user.role === "CANDIDATE"
        ? "/candidate/dashboard"
        : "/recruiter/dashboard"

    return <Navigate to={destination} replace />
  }

  return <Outlet />
}