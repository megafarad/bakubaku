object FrontendCommands {
  val dependencyInstall = "npm install"
  val prettier = "npx prettier --write ."
  val prettierCheck = "npx prettier --check ."
  val eslint = "npx eslint --max-warnings 0 --fix ."
  val eslintCheck = "npx eslint --max-warnings 0 ."
  val test: String = "npm run test"
  val serve: String = "npm run dev"
  val build: String = "npm run build"
  val kill: String = "npx kill-port 5173"
}
