import { useMutation } from "@tanstack/react-query"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { useNavigate } from "react-router-dom"
import { useAuth } from "../context/AuthContext"

import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"

import { loginUser } from "../api/auth-api"
import {
  loginSchema,
  type LoginFormValues,
} from "../schemas/auth-schema"

export function LoginForm() {
  
  const navigate = useNavigate()
  
const { login } = useAuth()

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
  })

  const loginMutation = useMutation({
    mutationFn: loginUser,

   onSuccess: (data) => {
  const user = {
    id: data.userId,
    name: data.name,
    email: data.email,
    role: data.role,
  }

  login(data.token, user)

  if (data.role === "CANDIDATE") {
    navigate("/candidate/dashboard")
  } else {
    navigate("/recruiter/dashboard")
  }
},
  })

  const onSubmit = (data: LoginFormValues) => {
    loginMutation.mutate(data)
  }

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className="space-y-5"
    >
      <div className="space-y-2">
        <Label htmlFor="email">Email</Label>

        <Input
          id="email"
          type="email"
          placeholder="you@example.com"
          {...register("email")}
        />

        {errors.email && (
          <p className="text-sm text-destructive">
            {errors.email.message}
          </p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="password">Password</Label>

        <Input
          id="password"
          type="password"
          placeholder="Enter your password"
          {...register("password")}
        />

        {errors.password && (
          <p className="text-sm text-destructive">
            {errors.password.message}
          </p>
        )}
      </div>

      {loginMutation.isError && (
        <p className="text-sm text-destructive">
          {loginMutation.error.message}
        </p>
      )}

      <Button
        type="submit"
        className="w-full"
        disabled={loginMutation.isPending}
      >
        {loginMutation.isPending
          ? "Signing in..."
          : "Sign in"}
      </Button>
    </form>
  )
}