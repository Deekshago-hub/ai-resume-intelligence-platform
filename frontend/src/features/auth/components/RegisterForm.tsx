import { useMutation } from "@tanstack/react-query"
import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { useNavigate } from "react-router-dom"

import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"

import { registerUser } from "../api/auth-api"
import {
  registerSchema,
  type RegisterFormValues,
} from "../schemas/auth-schema"

export function RegisterForm() {
  const navigate = useNavigate()

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      role: "CANDIDATE",
    },
  })

  const registerMutation = useMutation({
    mutationFn: registerUser,

    onSuccess: () => {
      navigate("/login")
    },
  })

  const onSubmit = (data: RegisterFormValues) => {
    registerMutation.mutate(data)
  }

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className="space-y-5"
    >
      <div className="space-y-2">
        <Label htmlFor="name">Name</Label>

        <Input
          id="name"
          placeholder="Your name"
          {...register("name")}
        />

        {errors.name && (
          <p className="text-sm text-destructive">
            {errors.name.message}
          </p>
        )}
      </div>

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
          placeholder="Minimum 8 characters"
          {...register("password")}
        />

        {errors.password && (
          <p className="text-sm text-destructive">
            {errors.password.message}
          </p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="role">I am a</Label>

        <select
          id="role"
          {...register("role")}
          className="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-xs outline-none"
        >
          <option value="CANDIDATE">
            Candidate
          </option>

          <option value="RECRUITER">
            Recruiter
          </option>
        </select>

        {errors.role && (
          <p className="text-sm text-destructive">
            {errors.role.message}
          </p>
        )}
      </div>

      {registerMutation.isError && (
        <p className="text-sm text-destructive">
          {registerMutation.error.message}
        </p>
      )}

      <Button
        type="submit"
        className="w-full"
        disabled={registerMutation.isPending}
      >
        {registerMutation.isPending
          ? "Creating account..."
          : "Create account"}
      </Button>
    </form>
  )
}