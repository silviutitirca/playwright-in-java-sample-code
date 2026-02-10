package com.serenitydojo.playwright.todomvc;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import com.serenitydojo.playwright.fixtures.ChromeHeadlessOptions;
import com.serenitydojo.playwright.todomvc.pageobjects.TodoMvcAppPage;
import io.qameta.allure.Feature;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Editing todo items")
@UsePlaywright(ChromeHeadlessOptions.class)
@Feature("Editing todo items")
class EditingTodoItemsTest {

    private TodoMvcAppPage todoMvcApp;

    @BeforeEach
    void openApp(Page page) {
        todoMvcApp = new TodoMvcAppPage(page);
        todoMvcApp.open();
    }

    @DisplayName("Editing an item saves the new text")
    @Test
    void editItemSavesNewText() {
        todoMvcApp.addItem("Old name");
        todoMvcApp.editItem("Old name", "New name");
        Assertions.assertThat(todoMvcApp.todoItemsDisplayed()).containsExactly("New name");
    }

    @DisplayName("Canceling an edit keeps the original text")
    @Test
    void cancelEditKeepsOriginalText() {
        todoMvcApp.addItem("Stay same");
        todoMvcApp.cancelEditItem("Stay same");
        Assertions.assertThat(todoMvcApp.todoItemsDisplayed()).containsExactly("Stay same");
    }
}
