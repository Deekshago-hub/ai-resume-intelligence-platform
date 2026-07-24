import { Link } from "react-router-dom"

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"

import { LoginForm } from "@/features/auth/components/LoginForm"

export function LoginPage() {
  return (
    <main className="flex min-h-screen items-center justify-center bg-muted/40 px-4">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle className="text-2xl">
            Welcome back
          </CardTitle>

          <CardDescription>
            Sign in to your AI Resume Intelligence account.
          </CardDescription>
        </CardHeader>

        <CardContent>
          <LoginForm />

          <p className="mt-6 text-center text-sm text-muted-foreground">
            Don't have an account?{" "}
            <Link
              to="/register"
              className="font-medium text-foreground underline underline-offset-4"
            >
              Create account
            </Link>
          </p>
        </CardContent>
      </Card>
    </main>
  )
}