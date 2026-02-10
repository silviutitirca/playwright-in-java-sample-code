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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Input Validation and Edge Cases")
@UsePlaywright(ChromeHeadlessOptions.class)
@Feature("Input Validation and Edge Cases")
class InputValidationTest {

    private TodoMvcAppPage todoMvcApp;

    @BeforeEach
    void openApp(Page page) {
        todoMvcApp = new TodoMvcAppPage(page);
        todoMvcApp.open();
    }

    @DisplayName("Whitespace handling tests")
    @Nested
    class WhitespaceHandling {

        @DisplayName("Items with leading whitespace should be trimmed")
        @Test
        void itemsWithLeadingWhitespaceShouldBeTrimmed() {
            todoMvcApp.addItem("   Feed the cat");
            Assertions.assertThat(todoMvcApp.todoItemsDisplayed())
                    .contains("Feed the cat");
        }

        @DisplayName("Items with trailing whitespace should be trimmed")
        @Test
        void itemsWithTrailingWhitespaceShouldBeTrimmed() {
            todoMvcApp.addItem("Feed the cat   ");
            Assertions.assertThat(todoMvcApp.todoItemsDisplayed())
                    .contains("Feed the cat");
        }

        @DisplayName("Items with leading and trailing whitespace should be trimmed")
        @Test
        void itemsWithLeadingAndTrailingWhitespaceShouldBeTrimmed() {
            todoMvcApp.addItem("   Feed the cat   ");
            Assertions.assertThat(todoMvcApp.todoItemsDisplayed())
                    .contains("Feed the cat");
        }

        @DisplayName("Items with only whitespace should not be added")
        @Test
        void itemsWithOnlyWhitespaceShouldNotBeAdded() {
            todoMvcApp.addItem("   ");
            Assertions.assertThat(todoMvcApp.todoItemsDisplayed()).isEmpty();
        }
    }

    @DisplayName("Long input handling tests")
    @Nested
    class LongInputHandling {

        @DisplayName("Very long item names should be handled correctly")
        @Test
        void veryLongItemNamesShouldBeHandledCorrectly() {
            String longItemName = "This is a very long todo item name that should be handled gracefully by the application without breaking the UI or causing performance issues. It contains multiple words and spaces and should display properly in the list.";
            todoMvcApp.addItem(longItemName);
            Assertions.assertThat(todoMvcApp.todoItemsDisplayed()).contains(longItemName);
        }

        @DisplayName("Items with repeated text should be handled correctly")
        @Test
        void itemsWithRepeatedTextShouldBeHandledCorrectly() {
            String repeatedItem = "Task task task task task task task task task task task task";
            todoMvcApp.addItem(repeatedItem);
            Assertions.assertThat(todoMvcApp.todoItemsDisplayed()).contains(repeatedItem);
        }
    }

    @DisplayName("Special characters handling tests")
    @Nested
    class SpecialCharactersHandling {

        @ParameterizedTest
        @ValueSource(strings = {
                "Item with 'single quotes'",
                "Item with \"double quotes\"",
                "Item with <angle> brackets",
                "Item with [square] brackets",
                "Item with {curly} braces",
                "Item with & ampersand",
                "Item with @ at symbol",
                "Item with # hash symbol",
                "Item with $ dollar sign",
                "Item with % percent sign",
                "Item with * asterisk",
                "Item with ! exclamation",
                "Item with ? question mark",
                "Item with ~ tilde",
                "Item with ` backtick",
                "Item with | pipe",
                "Item with \\ backslash",
                "Item with / forward slash",
                "Item with : colon",
                "Item with ; semicolon",
                "Item with = equals",
                "Item with + plus",
                "Item with - minus",
                "Item with newline\ncharacter"
        })
        @DisplayName("Items with special characters should be handled correctly")
        void itemsWithSpecialCharactersShouldBeHandledCorrectly(String itemWithSpecialChar) {
            todoMvcApp.addItem(itemWithSpecialChar);
            Assertions.assertThat(todoMvcApp.todoItemsDisplayed()).contains(itemWithSpecialChar);
        }

        @DisplayName("Items with emojis should be handled correctly")
        @Test
        void itemsWithEmojisShouldBeHandledCorrectly() {
            String itemWithEmoji = "Feed the cat 🐱";
            todoMvcApp.addItem(itemWithEmoji);
            Assertions.assertThat(todoMvcApp.todoItemsDisplayed()).contains(itemWithEmoji);
        }
    }

    @DisplayName("Unicode and multilingual tests")
    @Nested
    class UnicodeAndMultilingualHandling {

        @ParameterizedTest
        @ValueSource(strings = {
                "Привет мир",                    // Russian
                "你好世界",                        // Simplified Chinese
                "こんにちは世界",                   // Japanese
                "안녕하세요 세계",                  // Korean
                "Γεια σας κόσμε",                // Greek
                "שלום עולם",                      // Hebrew
                "مرحبا بالعالم",                  // Arabic
                "Olá mundo",                     // Portuguese
                "สวัสดีชาวโลก",                    // Thai
                "नमस्ते दुनिया",                  // Hindi
        })
        @DisplayName("Items with various Unicode scripts should be handled correctly")
        void itemsWithUnicodeShouldBeHandledCorrectly(String unicodeItem) {
            todoMvcApp.addItem(unicodeItem);
            Assertions.assertThat(todoMvcApp.todoItemsDisplayed()).contains(unicodeItem);
        }
    }

    @DisplayName("Multiple items edge cases")
    @Nested
    class MultipleItemsEdgeCases {

        @DisplayName("Can add and manage large number of items (100+)")
        @Test
        void canHandleLargeNumberOfItems() {
            for (int i = 1; i <= 50; i++) {
                todoMvcApp.addItem("Task-" + i);
            }
            Assertions.assertThat(todoMvcApp.todoItemsDisplayed()).hasSize(50);
        }

        @DisplayName("Can complete and delete items efficiently with many items in list")
        @Test
        void canManageItemsWithLargeList() {
            for (int i = 1; i <= 20; i++) {
                todoMvcApp.addItem("Task-" + i);
            }

            todoMvcApp.completeItem("Task-1");
            todoMvcApp.completeItem("Task-10");
            todoMvcApp.deleteItem("Task-5");

            Assertions.assertThat(todoMvcApp.todoItemsDisplayed()).hasSize(19);
        }
    }
}

