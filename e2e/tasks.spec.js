// @ts-check
const { test, expect } = require("@playwright/test");
const { randomUUID } = require("node:crypto");

test("user can create, complete and delete a task", async ({ page }) => {
  const title = `E2E task ${randomUUID()}`;

  await page.goto("/");

  // Vänta tills frontend har laddat data från API:et.
  await expect(page.locator("#todo-counter")).toContainText("left");

  await page.getByRole("textbox", { name: "Add To-Do" }).fill(title);
  await page.getByRole("button", { name: "Add Task", exact: true }).click();

  const task = page.getByRole("listitem").filter({ hasText: title });

  await expect(task).toBeVisible();
  await task.getByRole("button", { name: "Done", exact: true }).click();
  await expect(
    task.getByRole("button", { name: "Undo", exact: true }),
  ).toBeVisible();

  // Kontrollera att uppgiften fortfarande är klar efter omladdning.
  await page.reload();
  await expect(page.locator("#todo-counter")).toContainText("left");
  await expect(
    task.getByRole("button", { name: "Undo", exact: true }),
  ).toBeVisible();

  await task.getByRole("button", { name: "Delete", exact: true }).click();
  await expect(task).toHaveCount(0);

  // Kontrollera att borttagningen också sparats.
  await page.reload();
  await expect(page.locator("#todo-counter")).toContainText("left");
  await expect(task).toHaveCount(0);
});
