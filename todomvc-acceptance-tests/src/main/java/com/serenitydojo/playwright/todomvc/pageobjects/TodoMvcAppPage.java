package com.serenitydojo.playwright.todomvc.pageobjects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Page Object for the TodoMVC application.
 * This class provides methods to interact with the TodoMVC application using Playwright.
 */
public class TodoMvcAppPage {

    private final Page page;
    private final String baseUrl;

    // Locators defined as fields for better maintainability and reuse
    private final Locator todoField;
    private final Locator todoItems;
    private final Locator todoCount;
    private final Locator footer;
    private final Locator toggleAll;
    private final Locator clearCompletedButton;

    public TodoMvcAppPage(Page page) {
        this.page = page;
        this.baseUrl = (StringUtils.isEmpty(System.getenv("APP_HOST_URL")))
                ? "https://demo.playwright.dev/todomvc/#/"
                : System.getenv("APP_HOST_URL");

        // Initialize locators
        this.todoField = page.locator(".new-todo");
        this.todoItems = page.getByTestId("todo-item");
        this.todoCount = page.locator(".todo-count");
        this.footer = page.locator(".footer");
        this.toggleAll = page.locator(".toggle-all");
        this.clearCompletedButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Clear completed"));
    }

    /**
     * Opens the TodoMVC application.
     */
    public TodoMvcAppPage open() {
        page.navigate(baseUrl);
        return this;
    }

    /**
     * @return a list of all todo item texts currently displayed.
     */
    public List<String> todoItemsDisplayed() {
        return todoItems.allTextContents();
    }

    /**
     * @return the locator for the main input field.
     */
    public Locator todoField() {
        return todoField;
    }

    /**
     * Adds a new todo item to the list.
     *
     * @param itemText the text of the item to add.
     */
    public void addItem(String itemText) {
        todoField.fill(itemText);
        todoField.press("Enter");
    }

    /**
     * Adds multiple todo items to the list.
     *
     * @param items the items to add.
     */
    public void addItems(String... items) {
        for (String item : items) {
            addItem(item);
        }
    }

    /**
     * Deletes a todo item by its text.
     *
     * @param itemName the text of the item to delete.
     */
    public void deleteItem(String itemName) {
        Locator itemRow = itemRow(itemName).first();
        itemRow.hover();
        itemRow.getByRole(AriaRole.BUTTON, new Locator.GetByRoleOptions().setName("Delete")).click();
    }

    /**
     * Returns the locator for a specific todo item row.
     * Uses exact text match to avoid strict mode violations when item names overlap (e.g., "Task-1" and "Task-10").
     * @param itemName the text of the item.
     */
    public Locator itemRow(String itemName) {
        return todoItems.filter(new Locator.FilterOptions().setHas(page.getByText(itemName, new Page.GetByTextOptions().setExact(true))));
    }

    /**
     * Completes a todo item by clicking its checkbox.
     *
     * @param itemName the text of the item to complete.
     */
    public void completeItem(String itemName) {
        itemRow(itemName).getByRole(AriaRole.CHECKBOX, new Locator.GetByRoleOptions().setName("Toggle Todo")).first().click();
    }

    /**
     * @return the text content of the item count display (e.g., "1 item left").
     */
    public String todoCount() {
        return todoCount.textContent();
    }

    /**
     * Clears all completed items from the list.
     */
    public void clearCompletedItems() {
        clearCompletedButton.click();
    }

    /**
     * @return the text of the currently selected filter.
     */
    public String currentFilter() {
        return footer.locator(".selected").textContent();
    }

    /**
     * Filters the todo items by clicking one of the filter links in the footer.
     *
     * @param filterName the name of the filter (e.g., "All", "Active", "Completed").
     */
    public void filterItemsBy(String filterName) {
        footer.locator(".filters")
                .getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName(filterName).setExact(true))
                .click();
    }

    /**
     * @return true if the footer is visible.
     */
    public boolean isFooterVisible() {
        return footer.isVisible();
    }

    /**
     * @return true if the "Clear completed" button is visible.
     */
    public boolean isClearCompletedButtonVisible() {
        return clearCompletedButton.isVisible();
    }

    /**
     * Edits an existing todo item.
     *
     * @param oldName the current text of the item.
     * @param newName the new text for the item.
     */
    public void editItem(String oldName, String newName) {
        Locator item = itemRow(oldName);
        item.locator("label").dblclick();
        Locator editField = item.locator(".edit");
        editField.fill(newName);
        editField.press("Enter");
    }

    /**
     * Cancels the editing of an item.
     *
     * @param itemName the text of the item being edited.
     */
    public void cancelEditItem(String itemName) {
        Locator item = itemRow(itemName);
        item.locator("label").dblclick();
        item.locator(".edit").press("Escape");
    }

    /**
     * Reloads the page and waits for the application to be ready.
     */
    public void refreshPage() {
        page.reload();
        try {
            page.waitForSelector(".todoapp", new Page.WaitForSelectorOptions().setTimeout(3000));
        } catch (Exception ignored) {
            // ignore timeouts; caller can handle assertions
        }
    }

    /**
     * Checks if a specific item is marked as completed.
     * @param itemName the text of the item.
     * @return true if the item has the "completed" class.
     */
    public boolean isItemCompleted(String itemName) {
        String classAttribute = itemRow(itemName).getAttribute("class");
        return classAttribute != null && classAttribute.contains("completed");
    }

    /**
     * Toggles all items as completed or active.
     */
    public void toggleAllItems() {
        toggleAll.click();
    }

    /**
     * @return true if the "Toggle All" checkbox is visible.
     */
    public boolean toggleAllCheckboxExists() {
        return toggleAll.isVisible();
    }
}
