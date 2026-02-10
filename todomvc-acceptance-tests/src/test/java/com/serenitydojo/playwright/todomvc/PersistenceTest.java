package com.serenitydojo.playwright.todomvc;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import com.serenitydojo.playwright.fixtures.ChromeHeadlessOptions;
import com.serenitydojo.playwright.todomvc.pageobjects.TodoMvcAppPage;
import io.qameta.allure.Feature;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Todo Items Persistence")
@UsePlaywright(ChromeHeadlessOptions.class)
@Feature("Todo Items Persistence")
class PersistenceTest {

    private TodoMvcAppPage todoMvcApp;

    @BeforeEach
    void openApp(Page page) {
        todoMvcApp = new TodoMvcAppPage(page);
        todoMvcApp.open();
    }

    @DisplayName("Data persistence after page refresh")
    @Nested
    class DataPersistenceAfterRefresh {

        @DisplayName("Items should persist after page refresh")
        @Test
        void itemsShouldPersistAfterPageRefresh() {
            todoMvcApp.addItems("Feed the cat", "Walk the dog", "Buy milk");

            todoMvcApp.refreshPage();

            Assertions.assertThat(todoMvcApp.todoItemsDisplayed())
                    .containsExactly("Feed the cat", "Walk the dog", "Buy milk");
        }

        @DisplayName("Completed items should persist after page refresh")
        @Test
        void completedItemsShouldPersistAfterPageRefresh() {
            todoMvcApp.addItems("Feed the cat", "Walk the dog", "Buy milk");
            todoMvcApp.completeItem("Feed the cat");
            todoMvcApp.completeItem("Buy milk");

            todoMvcApp.refreshPage();

            Assertions.assertThat(todoMvcApp.isItemCompleted("Feed the cat")).isTrue();
            Assertions.assertThat(todoMvcApp.isItemCompleted("Buy milk")).isTrue();
        }

        @DisplayName("Item count should persist after page refresh")
        @Test
        void itemCountShouldPersistAfterPageRefresh() {
            todoMvcApp.addItems("Feed the cat", "Walk the dog", "Buy milk");
            todoMvcApp.completeItem("Feed the cat");

            String countBeforeRefresh = todoMvcApp.todoCount();
            todoMvcApp.refreshPage();
            String countAfterRefresh = todoMvcApp.todoCount();

            Assertions.assertThat(countAfterRefresh).isEqualTo(countBeforeRefresh);
        }

        @DisplayName("Filter state should persist after page refresh")
        @Test
        void filterStateShouldPersistAfterPageRefresh() {
            todoMvcApp.addItems("Feed the cat", "Walk the dog", "Buy milk");
            todoMvcApp.completeItem("Feed the cat");

            todoMvcApp.filterItemsBy("Active");
            String filterBeforeRefresh = todoMvcApp.currentFilter();

            todoMvcApp.refreshPage();
            String filterAfterRefresh = todoMvcApp.currentFilter();

            Assertions.assertThat(filterAfterRefresh).isEqualTo(filterBeforeRefresh).isEqualTo("Active");
        }

        @DisplayName("Empty list should persist as empty after page refresh")
        @Test
        void emptyListShouldPersistAsEmpty() {
            todoMvcApp.refreshPage();

            Assertions.assertThat(todoMvcApp.todoItemsDisplayed()).isEmpty();
        }
    }

    @DisplayName("Toggle-all checkbox and persistence")
    @Nested
    class ToggleAllAndPersistence {

        @DisplayName("Toggle-all checkbox exists and toggles all items")
        @Test
        void toggleAllTogglesAllItems() {
            todoMvcApp.addItems("A", "B", "C");
            Assertions.assertThat(todoMvcApp.toggleAllCheckboxExists()).isTrue();

            todoMvcApp.toggleAllItems();

            Assertions.assertThat(todoMvcApp.isItemCompleted("A")).isTrue();
            Assertions.assertThat(todoMvcApp.isItemCompleted("B")).isTrue();
            Assertions.assertThat(todoMvcApp.isItemCompleted("C")).isTrue();
        }

        @DisplayName("Completed state persists after page refresh")
        @Test
        void completedStatePersistsAfterRefresh() {
            todoMvcApp.addItems("Persist1", "Persist2");
            todoMvcApp.completeItem("Persist2");

            // sanity before refresh
            Assertions.assertThat(todoMvcApp.isItemCompleted("Persist2")).isTrue();

            todoMvcApp.refreshPage();

            // after reload the completed item should still be completed
            Assertions.assertThat(todoMvcApp.todoItemsDisplayed()).contains("Persist1", "Persist2");
            Assertions.assertThat(todoMvcApp.isItemCompleted("Persist2")).isTrue();
        }
    }
}

