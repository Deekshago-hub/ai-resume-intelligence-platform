export type UserRole = "CANDIDATE" | "RECRUITER"

export interface RegisterRequest {
  name: string
  email: string
  password: string
  role: UserRole
}

export interface RegisterResponse {
  id: number
  name: string
  email: string
  role: UserRole
}

export interface LoginRequest {
  email: string
  password: string
}

export interface LoginResponse {
  token: string
  userId: number
  name: string
  email: string
  role: UserRole
}

export interface AuthUser {
  id: number
  name: string
  email: string
  role: UserRole
}