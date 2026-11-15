// @ts-check
const { defineConfig, devices } = require("@playwright/test");

module.exports = defineConfig({
  testDir: "./e2e",
  workers: 1,
  forbidOnly: !!process.env.CI,
  retries: 0,
  reporter: "html",

  use: {
    baseURL: "http://127.0.0.1:8080",
    trace: "retain-on-failure",
    httpCredentials: {
      username: process.env.E2E_USER ?? "dev",
      password: process.env.E2E_PASSWORD ?? "dev",
    },
  },

  projects: [
    {
      name: "firefox",
      use: { ...devices["Desktop Firefox"] },
    },
  ],
});
