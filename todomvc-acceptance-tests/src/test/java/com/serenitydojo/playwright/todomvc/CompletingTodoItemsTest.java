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

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@DisplayName("Completing todo items to the list")
@UsePlaywright(ChromeHeadlessOptions.class)
@Feature("Completing todo items to the list")
class CompletingTodoItemsTest {

    TodoMvcAppPage todoMvcApp;

    @BeforeEach
    void openApp(Page page) {
        todoMvcApp = new TodoMvcAppPage(page);
        todoMvcApp.open();
    }

    @DisplayName("Completed items should be marked as completed")
    @Test
    void completedItemsShouldBeMarkedAsCompleted() {
        todoMvcApp.addItems("Feed the cat", "Walk the dog", "Buy some milk");
        todoMvcApp.completeItem("Feed the cat");
        assertThat(todoMvcApp.itemRow("Feed the cat")).hasClass("completed");
    }

    @DisplayName("Completing an item should update the number of items left count")
    @Test
    void shouldUpdateNumberOfItemsLeftCount() {
        todoMvcApp.addItems("Feed the cat", "Walk the dog", "Buy some milk");
        todoMvcApp.completeItem("Feed the cat");
        Assertions.assertThat(todoMvcApp.todoCount()).isEqualTo("2 items left!");
    }

    @DisplayName("Should be able to clear completed items")
    @Test
    void shouldBeAbleToClearCompletedItems() {
        todoMvcApp.addItems("Feed the cat", "Walk the dog", "Buy some milk");
        todoMvcApp.completeItem("Walk the dog");
        todoMvcApp.clearCompletedItems();
        Assertions.assertThat(todoMvcApp.todoItemsDisplayed()).containsExactly("Feed the cat", "Buy some milk");
    }
}
