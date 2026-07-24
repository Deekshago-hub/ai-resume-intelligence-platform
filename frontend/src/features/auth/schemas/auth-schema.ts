import { z } from "zod"

export const loginSchema = z.object({
  email: z
    .string()
    .email("Enter a valid email address"),

  password: z
    .string()
    .min(1, "Password is required"),
})

export const registerSchema = z.object({
  name: z
    .string()
    .min(2, "Name must contain at least 2 characters")
    .max(100, "Name is too long"),

  email: z
    .string()
    .email("Enter a valid email address"),

  password: z
    .string()
    .min(8, "Password must contain at least 8 characters")
    .max(72, "Password must not exceed 72 characters"),

  role: z.enum(["CANDIDATE", "RECRUITER"]),
})

export type LoginFormValues = z.infer<typeof loginSchema>
export type RegisterFormValues = z.infer<typeof registerSchema>