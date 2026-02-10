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

@DisplayName("UI Visibility and Footer Display")
@UsePlaywright(ChromeHeadlessOptions.class)
@Feature("UI Visibility and Footer Display")
class UIVisibilityTest {

    TodoMvcAppPage todoMvcApp;

    @BeforeEach
    void openApp(Page page) {
        todoMvcApp = new TodoMvcAppPage(page);
        todoMvcApp.open();
    }

    @DisplayName("Footer visibility tests")
    @Nested
    class FooterVisibility {

        @DisplayName("Footer should be hidden when list is empty")
        @Test
        void footerShouldBeHiddenWhenListIsEmpty() {
            Assertions.assertThat(todoMvcApp.isFooterVisible()).isFalse();
        }

        @DisplayName("Footer should be visible when items exist")
        @Test
        void footerShouldBeVisibleWhenItemsExist() {
            todoMvcApp.addItem("Feed the cat");
            Assertions.assertThat(todoMvcApp.isFooterVisible()).isTrue();
        }
    }

    @DisplayName("Clear completed button visibility tests")
    @Nested
    class ClearCompletedButtonVisibility {

        @DisplayName("Clear completed button should be hidden when no completed items exist")
        @Test
        void clearCompletedButtonHiddenWhenNoCompletedItems() {
            todoMvcApp.addItems("Feed the cat", "Walk the dog");
            Assertions.assertThat(todoMvcApp.isClearCompletedButtonVisible()).isFalse();
        }

        @DisplayName("Clear completed button should be visible when completed items exist")
        @Test
        void clearCompletedButtonVisibleWhenCompletedItemsExist() {
            todoMvcApp.addItems("Feed the cat", "Walk the dog");
            todoMvcApp.completeItem("Feed the cat");
            Assertions.assertThat(todoMvcApp.isClearCompletedButtonVisible()).isTrue();
        }

        @DisplayName("Clear completed button should hide after clearing all completed items")
        @Test
        void clearCompletedButtonHidesAfterClearing() {
            todoMvcApp.addItems("Feed the cat", "Walk the dog");
            todoMvcApp.completeItem("Feed the cat");
            todoMvcApp.completeItem("Walk the dog");
            todoMvcApp.clearCompletedItems();
            Assertions.assertThat(todoMvcApp.isClearCompletedButtonVisible()).isFalse();
        }
    }

    @DisplayName("Item count display tests")
    @Nested
    class ItemCountDisplay {

        @DisplayName("Should display singular 'item' for 1 item left")
        @Test
        void shouldDisplaySingularForOneItem() {
            todoMvcApp.addItem("Feed the cat");
            Assertions.assertThat(todoMvcApp.todoCount()).isEqualTo("1 item left");
        }

        @DisplayName("Should display plural 'items' for multiple items left")
        @Test
        void shouldDisplayPluralForMultipleItems() {
            todoMvcApp.addItems("Feed the cat", "Walk the dog", "Buy milk");
            Assertions.assertThat(todoMvcApp.todoCount()).isEqualTo("3 items left");
        }

        @DisplayName("Item count should update correctly when completing items sequentially")
        @Test
        void itemCountUpdatesSequentially() {
            todoMvcApp.addItems("Feed the cat", "Walk the dog", "Buy milk");

            todoMvcApp.completeItem("Feed the cat");
            Assertions.assertThat(todoMvcApp.todoCount()).isEqualTo("2 items left");

            todoMvcApp.completeItem("Walk the dog");
            Assertions.assertThat(todoMvcApp.todoCount()).isEqualTo("1 item left");

            todoMvcApp.completeItem("Buy milk");
            Assertions.assertThat(todoMvcApp.todoCount()).isEqualTo("0 items left");
        }
    }
}

