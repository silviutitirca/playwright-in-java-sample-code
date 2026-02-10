package com.serenitydojo.playwright.todomvc.pageobjects;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class TodoMvcAppPage {

    private final Page page;
    private final String baseUrl;
    private final Locator todoItems;
    private final Locator todoField;

    public TodoMvcAppPage(Page page) {
        this.page = page;
        baseUrl = (StringUtils.isEmpty(System.getenv("APP_HOST_URL"))) ? "https://demo.playwright.dev/todomvc/#/" : System.getenv("APP_HOST_URL");
        //baseUrl = (StringUtils.isEmpty(System.getenv("APP_HOST_URL"))) ? "http://localhost:7002" : System.getenv("APP_HOST_URL");
        todoItems = page.getByTestId("todo-item");
        todoField = page.locator(".new-todo");
    }

    public void open() {
        page.navigate(baseUrl);
    }

    public List<String> todoItemsDisplayed() {
        return todoItems.allTextContents();
    }


    public Locator todoField() {
        return todoField;
    }

    public void addItem(String itemText) {
        todoField.fill(itemText);
        todoField.press("Enter");
    }

    public void addItems(String... todoItems) {
        for(String todoItem : todoItems){
            addItem(todoItem);
        }
    }

    public void deleteItem(String itemName) {
        Locator itemRow = itemRow(itemName);
        Locator deleteButton = itemRow.getByLabel("Delete");
        itemRow.hover();
        deleteButton.click();
    }

    public Locator itemRow(String itemName) {
        return page.getByTestId("todo-item")
                .filter(new Locator.FilterOptions().setHasText(itemName));
    }

    public void completeItem(String itemName) {
        itemRow(itemName).getByLabel("Toggle Todo").first().click();
    }

    public String todoCount() {
        return page.locator(".todo-count").textContent();
    }

    public void clearCompletedItems() {
        page.getByText("Clear completed").click();
    }

    public String currentFilter() {
        return page.locator(".footer").locator(".selected").textContent();
    }

    public void filterItemsBy(String filter) {
        page.locator(".footer .filters")
                .getByText(filter, new Locator.GetByTextOptions().setExact(true))
                .click();
    }

    public boolean isFooterVisible() {
        return page.locator(".footer").isVisible();
    }

    public boolean isClearCompletedButtonVisible() {
        return page.locator(".footer").getByText("Clear completed").isVisible();
    }

    public void editItem(String oldName, String newName) {
        Locator item = itemRow(oldName);
        item.locator("label").dblclick();
        item.locator(".edit").fill(newName);
        item.locator(".edit").press("Enter");
    }

    public void cancelEditItem(String itemName) {
        Locator item = itemRow(itemName);
        item.locator("label").dblclick();
        item.locator(".edit").press("Escape");
    }

    public void refreshPage() {
        page.reload();
        // Wait for the application root to be attached and visible before proceeding
        try {
            page.waitForSelector(".todoapp", new Page.WaitForSelectorOptions().setTimeout(3000));
        } catch (Exception ignored) {
            // ignore timeouts; caller can handle assertions
        }
    }

    public boolean isItemCompleted(String itemName) {
        String classAttribute = itemRow(itemName).getAttribute("class");
        return classAttribute != null && classAttribute.contains("completed");
    }

    public void toggleAllItems() {
        page.locator(".toggle-all").click();
    }

    public boolean toggleAllCheckboxExists() {
        return page.locator(".toggle-all").isVisible();
    }
}
